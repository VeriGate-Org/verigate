variable "STACK_NAME" {
  description = "Name of the stack"
  type        = string
}

variable "ENVIRONMENT_SHORTNAME" {
  description = "Short name for the environment"
  type        = string
}

variable "ENVIRONMENT_NAME" {
  description = "Full environment name"
  type        = string
}

variable "POD_NAME" {
  description = "Pod name for Datadog"
  type        = string
}

variable "TEAM_NAME" {
  description = "Team name"
  type        = string
}

variable "GITHUB_PIPELINE_NAME" {
  description = "GitHub pipeline name"
  type        = string
}

variable "DATADOG_TEAM_NAME" {
  description = "Datadog team name"
  type        = string
}

variable "DATADOG_MONITOR_NOTIFY_ALL" {
  description = "Datadog monitor notification target for all alerts"
  type        = string
  default     = ""
}

variable "DATADOG_MONITOR_NOTIFY_WARNING" {
  description = "Datadog monitor notification target for warnings"
  type        = string
  default     = ""
}

variable "DATADOG_MONITOR_NOTIFY_ALERT" {
  description = "Datadog monitor notification target for critical alerts"
  type        = string
  default     = ""
}
