variable "complete_stack_name" {
  description = "Complete stack name prefix for resource naming"
  type        = string
}

variable "table_name" {
  description = "Name of the DynamoDB table"
  type        = string
}

variable "hash_key" {
  description = "Hash key for the DynamoDB table"
  type = object({
    name = string
    type = string
  })
}

variable "range_key" {
  description = "Range key for the DynamoDB table"
  type = object({
    name = string
    type = string
  })
  default = null
}

variable "attributes" {
  description = "List of attribute definitions"
  type = list(object({
    name = string
    type = string
  }))
}

variable "global_secondary_indexes" {
  description = "List of global secondary indexes"
  type = list(object({
    name            = string
    hash_key        = string
    range_key       = optional(string)
    projection_type = string
  }))
  default = []
}

variable "fis_az_failure_ready" {
  description = "Enable point-in-time recovery for AZ failure readiness"
  type        = bool
  default     = false
}

variable "default_tags" {
  description = "Default tags to apply to resources"
  type        = map(string)
  default     = {}
}
