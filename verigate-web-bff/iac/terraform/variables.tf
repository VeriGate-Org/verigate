variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "af-south-1"
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

variable "container_port" {
  description = "Port the container listens on"
  type        = number
  default     = 8080
}

variable "cpu" {
  description = "Fargate task CPU units"
  type        = number
  default     = 512
}

variable "memory" {
  description = "Fargate task memory in MiB"
  type        = number
  default     = 1024
}

variable "desired_count" {
  description = "Desired number of ECS tasks"
  type        = number
  default     = 1
}

variable "cognito_enabled" {
  description = "Enable Cognito authentication"
  type        = string
  default     = "false"
}

variable "enable_custom_domain" {
  description = "Enable custom domain for API Gateway"
  type        = bool
  default     = false
}

variable "api_domain" {
  description = "Custom domain name for API Gateway (e.g. api.dev.verigate.co.za)"
  type        = string
  default     = ""
}

variable "acm_certificate_arn" {
  description = "Pre-validated ACM certificate ARN for the API domain (must be in the deploy region)"
  type        = string
  default     = ""
}

# ── Health Check URLs ────────────────────────────────────────────────

variable "health_deedsweb_url" {
  description = "DeedsWeb health check URL"
  type        = string
  default     = "NOT_CONFIGURED"
}

variable "health_dha_url" {
  description = "DHA health check URL"
  type        = string
  default     = "NOT_CONFIGURED"
}

variable "health_worldcheck_url" {
  description = "World-Check health check URL"
  type        = string
  default     = "NOT_CONFIGURED"
}

variable "health_experian_url" {
  description = "Experian health check URL"
  type        = string
  default     = "NOT_CONFIGURED"
}

variable "health_transunion_url" {
  description = "TransUnion health check URL"
  type        = string
  default     = "NOT_CONFIGURED"
}

variable "health_xds_url" {
  description = "XDS health check URL"
  type        = string
  default     = "NOT_CONFIGURED"
}

variable "health_cipc_url" {
  description = "CIPC health check URL"
  type        = string
  default     = "NOT_CONFIGURED"
}
