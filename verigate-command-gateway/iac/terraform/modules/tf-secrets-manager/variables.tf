variable "prefix" {
  description = "Prefix for secret names"
  type        = string
}

variable "default_recovery_window_in_days" {
  description = "Number of days to retain the secret after deletion"
  type        = number
  default     = 30
}

variable "secrets" {
  description = "Map of secrets to create"
  type = map(object({
    description = string
    value       = string
  }))
}
