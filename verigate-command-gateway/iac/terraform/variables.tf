#----------------------------------------------------------------------------------------------------------------
# Environment variables
#----------------------------------------------------------------------------------------------------------------
variable "environment_name" {
  type    = string
}

# AWS
variable "aws_region" {
  default = "eu-west-1"
  type    = string
}


#----------------------------------------------------------------------------------------------------------------
# Stack variables
#----------------------------------------------------------------------------------------------------------------
variable "stack_name" {
  description = "Name of the stack"
  type        = string
}

variable "project_name" {
  description = "Name of the project"
  type        = string
}

variable "environment_shortname" {
  description = "Short name for the environment (e.g., dev, sbx, ppe, prd)"
  type        = string
}

variable "stack_version" {
  description = "Version of the stack"
  type        = string
  default     = ""
}

variable "developer_stack_name" {
  description = "Developer name for sandbox environments"
  type        = string
  default     = ""
}

#----------------------------------------------------------------------------------------------------------------
# Log Groups Variables
#----------------------------------------------------------------------------------------------------------------

variable "log_retention_days" {
  description = "Number of days to retain Lambda logs"
  type        = number
  default     = 14
}


#---------------------------------------------------------------------------------------------------------------
# Secrets manager
#---------------------------------------------------------------------------------------------------------------
variable "recovery_window_in_days"{
  description = "Number of days to retain the secret after deletion"
  type        = number
  default     = 30
}

variable "secret_prefix" {
  description = "Prefix for the secrets in AWS Secrets Manager"
  type        = string
  default     = "/verifications"
}

#----------------------------------------------------------------------------------------------------------------
# QLINK
#----------------------------------------------------------------------------------------------------------------

variable "qlink_username" {
  description = "QLINK username for the password grant flow"
  type        = string
}

variable "qlink_password" {
  description = "QLINK password for the password grant flow"
  type        = string
  sensitive   = true # Mark as sensitive to prevent accidental exposure in logs
}

variable "qlink_client_id" {
  description = "QLINK client id for the password grant flow"
  type        = string
}

variable "qlink_api_url" {
  description = "Qlink API URL"
  type        = string
}

#----------------------------------------------------------------------------------------------------------------
# Worldcheck
#----------------------------------------------------------------------------------------------------------------

variable "worldcheck_client_id" {
  description = "Worldcheck client id for the password grant flow"
  type        = string
}

variable "worldcheck_client_secret" {
  description = "Worldcheck client secret for the password grant flow"
  type        = string
}

variable "worldcheck_scope" {
  description = "Worldcheck scope for the password grant flow"
  type        = string
  sensitive   = true # Mark as sensitive to prevent accidental exposure in logs
}

variable "worldcheck_subscription_key" {
  description = "Worldcheck subscription key for the password grant flow"
  type        = string
  sensitive   = true # Mark as sensitive to prevent accidental exposure in logs
}

variable "worldcheck_basic_authorization" {
  description = "Worldcheck basic authorization for the password grant flow"
  type        = string
  sensitive   = true # Mark as sensitive to prevent accidental exposure in logs
}

variable "worldcheck_authentication_url" {
  description = "Worldcheck authentication URL for the password grant flow"
  type        = string
}

variable "worldcheck_qt_api_url" {
  description = "Worldcheck QT API URL"
  type        = string
}

#----------------------------------------------------------------------------------------------------------------
# World Check One
#----------------------------------------------------------------------------------------------------------------

variable "worldcheck_api_key" {
  description = "World Check One API key"
  type        = string
  sensitive   = true
}

variable "worldcheck_api_secret" {
  description = "World Check One API secret"
  type        = string
  sensitive   = true
}

variable "worldcheck_user_id" {
  description = "World Check One user ID"
  type        = string
}

variable "worldcheck_default_group_id" {
  description = "World Check One default group ID"
  type        = string
}

variable "worldcheck_api_base_url" {
  description = "World Check One API base URL"
  type        = string
}

#----------------------------------------------------------------------------------------------------------------
# Datadog
#----------------------------------------------------------------------------------------------------------------
variable "datadog_monitor_notify_all" {
  description = "Datadog monitor notification target for all alerts"
  type        = string
  default     = ""
}

variable "datadog_monitor_notify_warning" {
  description = "Datadog monitor notification target for warnings"
  type        = string
  default     = ""
}

variable "datadog_monitor_notify_alert" {
  description = "Datadog monitor notification target for critical alerts"
  type        = string
  default     = ""
}





