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
  value       = local.effective_cert_arn
}

output "portal_domain" {
  description = "Portal domain name"
  value       = local.has_custom_domain ? local.portal_domain : aws_cloudfront_distribution.website.domain_name
}

output "wildcard_domain" {
  description = "Wildcard domain pattern for tenant subdomains"
  value       = local.has_custom_domain ? local.wildcard_domain : null
}

output "cloudfront_cname_target" {
  description = "CloudFront domain name to use as CNAME target at your DNS provider"
  value       = aws_cloudfront_distribution.website.domain_name
}
