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
