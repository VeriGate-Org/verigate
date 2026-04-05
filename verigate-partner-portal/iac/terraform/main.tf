locals {
  bucket_name = "${var.stack_name}-partner-portal-${var.environment_shortname}"

  # Subdomain pattern: PROD uses root domain, non-PROD uses env subdomain
  # PROD: *.verigate.co.za   Non-PROD: *.dev.verigate.co.za
  portal_domain   = var.environment_shortname == "prod" ? var.root_domain : "${var.environment_shortname}.${var.root_domain}"
  wildcard_domain = "*.${local.portal_domain}"

  # Use external cert if provided, otherwise use terraform-managed cert
  create_cert        = var.acm_certificate_arn == "" && var.enable_wildcard_subdomain
  effective_cert_arn = var.acm_certificate_arn != "" ? var.acm_certificate_arn : (var.enable_wildcard_subdomain ? aws_acm_certificate.wildcard[0].arn : null)
  has_custom_domain  = var.enable_wildcard_subdomain || var.acm_certificate_arn != ""
}

# ── S3 Bucket for static site ──────────────────────────────────────

resource "aws_s3_bucket" "website" {
  bucket = local.bucket_name
}

resource "aws_s3_bucket_public_access_block" "website" {
  bucket = aws_s3_bucket.website.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_versioning" "website" {
  bucket = aws_s3_bucket.website.id
  versioning_configuration {
    status = "Enabled"
  }
}

# ── CloudFront Origin Access Control ───────────────────────────────

resource "aws_cloudfront_origin_access_control" "website" {
  name                              = local.bucket_name
  origin_access_control_origin_type = "s3"
  signing_behavior                  = "always"
  signing_protocol                  = "sigv4"
}

# ── ACM Certificate (wildcard, us-east-1 for CloudFront) ──────────

resource "aws_acm_certificate" "wildcard" {
  count    = local.create_cert ? 1 : 0
  provider = aws.us_east_1

  domain_name               = local.portal_domain
  subject_alternative_names = [local.wildcard_domain]
  validation_method         = "DNS"

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_route53_record" "cert_validation" {
  for_each = local.create_cert && var.hosted_zone_id != "" ? {
    for dvo in aws_acm_certificate.wildcard[0].domain_validation_options : dvo.domain_name => {
      name   = dvo.resource_record_name
      record = dvo.resource_record_value
      type   = dvo.resource_record_type
    }
  } : {}

  zone_id = var.hosted_zone_id
  name    = each.value.name
  type    = each.value.type
  ttl     = 300
  records = [each.value.record]

  allow_overwrite = true
}

resource "aws_acm_certificate_validation" "wildcard" {
  count    = local.create_cert && var.hosted_zone_id != "" ? 1 : 0
  provider = aws.us_east_1

  certificate_arn         = aws_acm_certificate.wildcard[0].arn
  validation_record_fqdns = [for record in aws_route53_record.cert_validation : record.fqdn]
}

# ── CloudFront Distribution ───────────────────────────────────────

resource "aws_cloudfront_distribution" "website" {
  enabled             = true
  default_root_object = "index.html"
  price_class         = "PriceClass_100"

  # Wildcard subdomain aliases
  aliases = local.has_custom_domain ? [local.portal_domain, local.wildcard_domain] : []

  origin {
    domain_name              = aws_s3_bucket.website.bucket_regional_domain_name
    origin_id                = "S3-${local.bucket_name}"
    origin_access_control_id = aws_cloudfront_origin_access_control.website.id
  }

  default_cache_behavior {
    allowed_methods        = ["GET", "HEAD", "OPTIONS"]
    cached_methods         = ["GET", "HEAD"]
    target_origin_id       = "S3-${local.bucket_name}"
    viewer_protocol_policy = "redirect-to-https"
    compress               = true

    forwarded_values {
      query_string = false
      headers      = []
      cookies {
        forward = "none"
      }
    }

    min_ttl     = 0
    default_ttl = 3600
    max_ttl     = 86400
  }

  # SPA fallback - redirect 404/403 to index.html for client-side routing
  custom_error_response {
    error_code         = 404
    response_code      = 200
    response_page_path = "/index.html"
  }

  custom_error_response {
    error_code         = 403
    response_code      = 200
    response_page_path = "/index.html"
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  viewer_certificate {
    cloudfront_default_certificate = local.has_custom_domain ? false : true
    acm_certificate_arn            = local.effective_cert_arn
    ssl_support_method             = local.has_custom_domain ? "sni-only" : null
    minimum_protocol_version       = local.has_custom_domain ? "TLSv1.2_2021" : null
  }
}

# ── Route 53 DNS Records ──────────────────────────────────────────

resource "aws_route53_record" "portal" {
  count   = local.has_custom_domain && var.hosted_zone_id != "" ? 1 : 0
  zone_id = var.hosted_zone_id
  name    = local.portal_domain
  type    = "A"

  alias {
    name                   = aws_cloudfront_distribution.website.domain_name
    zone_id                = aws_cloudfront_distribution.website.hosted_zone_id
    evaluate_target_health = false
  }
}

resource "aws_route53_record" "wildcard" {
  count   = local.has_custom_domain && var.hosted_zone_id != "" ? 1 : 0
  zone_id = var.hosted_zone_id
  name    = local.wildcard_domain
  type    = "A"

  alias {
    name                   = aws_cloudfront_distribution.website.domain_name
    zone_id                = aws_cloudfront_distribution.website.hosted_zone_id
    evaluate_target_health = false
  }
}

# ── S3 Bucket Policy for CloudFront OAC ───────────────────────────

data "aws_iam_policy_document" "website_bucket_policy" {
  statement {
    effect = "Allow"
    principals {
      type        = "Service"
      identifiers = ["cloudfront.amazonaws.com"]
    }
    actions   = ["s3:GetObject"]
    resources = ["${aws_s3_bucket.website.arn}/*"]
    condition {
      test     = "StringEquals"
      variable = "AWS:SourceArn"
      values   = [aws_cloudfront_distribution.website.arn]
    }
  }
}

resource "aws_s3_bucket_policy" "website" {
  bucket = aws_s3_bucket.website.id
  policy = data.aws_iam_policy_document.website_bucket_policy.json
}
