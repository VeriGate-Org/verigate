variable "complete_stack_name" {
  description = "Complete stack name prefix for resource naming"
  type        = string
}

variable "bucket_name" {
  description = "Name suffix for the S3 bucket (appended to complete_stack_name)"
  type        = string
}

variable "versioning_enabled" {
  description = "Enable versioning on the bucket"
  type        = bool
  default     = true
}

variable "force_destroy" {
  description = "Allow bucket to be destroyed even if it contains objects"
  type        = bool
  default     = false
}

variable "cors_allowed_origins" {
  description = "Allowed origins for CORS configuration"
  type        = list(string)
  default     = []
}

variable "lifecycle_rules" {
  description = "Lifecycle rules for the bucket"
  type = list(object({
    id                          = string
    enabled                     = bool
    prefix                      = optional(string, "")
    transition_days             = optional(number)
    transition_storage_class    = optional(string)
    expiration_days             = optional(number)
    noncurrent_transition_days  = optional(number)
    noncurrent_storage_class    = optional(string)
    noncurrent_expiration_days  = optional(number)
  }))
  default = []
}

variable "default_tags" {
  description = "Default tags to apply to resources"
  type        = map(string)
  default     = {}
}
