variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "eu-west-1"
}

variable "environment_shortname" {
  description = "Short name for the environment (sbx, dev, ppe, prd)"
  type        = string
}

variable "stack_name" {
  description = "Stack name prefix"
  type        = string
  default     = "verigate"
}

variable "domain_name" {
  description = "Custom domain name for CloudFront (optional)"
  type        = string
  default     = ""
}

variable "root_domain" {
  description = "Root domain for the partner portal (e.g. verigate.co.za)"
  type        = string
  default     = "verigate.co.za"
}

variable "hosted_zone_id" {
  description = "Route 53 hosted zone ID for the root domain"
  type        = string
  default     = ""
}

variable "enable_wildcard_subdomain" {
  description = "Enable wildcard subdomain support for whitelabelling"
  type        = bool
  default     = false
}
