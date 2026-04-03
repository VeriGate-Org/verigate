variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "eu-west-1"
}

variable "environment_shortname" {
  description = "Short name for the environment (dev, prod)"
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
