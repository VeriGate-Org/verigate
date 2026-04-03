output "s3_bucket_name" {
  description = "S3 bucket name for the static site"
  value       = aws_s3_bucket.website.bucket
}

output "cloudfront_distribution_id" {
  description = "CloudFront distribution ID"
  value       = aws_cloudfront_distribution.website.id
}

output "cloudfront_domain_name" {
  description = "CloudFront distribution domain name"
  value       = aws_cloudfront_distribution.website.domain_name
}

output "acm_certificate_arn" {
  description = "ACM wildcard certificate ARN (if enabled)"
  value       = var.enable_wildcard_subdomain ? aws_acm_certificate.wildcard[0].arn : null
}

output "portal_domain" {
  description = "Portal domain name"
  value       = var.enable_wildcard_subdomain ? local.portal_domain : aws_cloudfront_distribution.website.domain_name
}

output "wildcard_domain" {
  description = "Wildcard domain pattern for tenant subdomains"
  value       = var.enable_wildcard_subdomain ? local.wildcard_domain : null
}
