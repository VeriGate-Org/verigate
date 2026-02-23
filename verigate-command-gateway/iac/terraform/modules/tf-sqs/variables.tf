variable "complete_stack_name" {
  description = "Complete stack name prefix for resource naming"
  type        = string
}

variable "queue_name" {
  description = "Name of the SQS queue"
  type        = string
}

variable "max_receive_count" {
  description = "Maximum number of receives before sending to DLQ"
  type        = number
  default     = 3
}
