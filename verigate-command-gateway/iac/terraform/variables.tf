#----------------------------------------------------------------------------------------------------------------
# Environment variables
#----------------------------------------------------------------------------------------------------------------
variable "environment_name" {
  type    = string
}

# AWS
variable "aws_region" {
  default = "af-south-1"
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
  description = "Short name for the environment (dev, prod)"
  type        = string
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
# OpenSanctions
#----------------------------------------------------------------------------------------------------------------

variable "opensanctions_api_key" {
  description = "OpenSanctions API key"
  type        = string
  sensitive   = true
}

variable "opensanctions_api_url" {
  description = "OpenSanctions API base URL"
  type        = string
  default     = "https://api.opensanctions.org"
}

#----------------------------------------------------------------------------------------------------------------
# DHA
#----------------------------------------------------------------------------------------------------------------

variable "dha_api_url" {
  description = "DHA API URL"
  type        = string
  default     = "NOT_CONFIGURED"
}

variable "hanis_site_id" {
  description = "HANIS site ID"
  type        = string
  default     = "NOT_CONFIGURED"
}

variable "hanis_workstation_id" {
  description = "HANIS workstation ID"
  type        = string
  default     = "NOT_CONFIGURED"
}

variable "hanis_primary_url" {
  description = "HANIS primary URL"
  type        = string
  default     = "NOT_CONFIGURED"
}

variable "hanis_failover_url" {
  description = "HANIS failover URL"
  type        = string
  default     = "NOT_CONFIGURED"
}

#----------------------------------------------------------------------------------------------------------------
# CIPC
#----------------------------------------------------------------------------------------------------------------

variable "cipc_api_key" {
  description = "CIPC API key"
  type        = string
  sensitive   = true
  default     = "NOT_CONFIGURED"
}

#----------------------------------------------------------------------------------------------------------------
# Adapter API URLs
#----------------------------------------------------------------------------------------------------------------

variable "deedsweb_api_url" {
  description = "DeedsWeb SOAP endpoint base URL (read by the verify-property-ownership Lambda as DEEDSWEB_BASE_URL)"
  type        = string
  default     = "http://deedssoap.deeds.gov.za:80/deeds-registration-soap/"
}

variable "employment_api_url" {
  description = "Employment verification API URL"
  type        = string
  default     = "NOT_CONFIGURED"
}

variable "negativenews_api_url" {
  description = "Negative news screening API URL"
  type        = string
  default     = "https://newsapi.org"
}

variable "negativenews_api_key" {
  description = "NewsAPI API key for negative news screening"
  type        = string
  sensitive   = true
  default     = "NOT_CONFIGURED"
}

variable "fraudwatchlist_api_url" {
  description = "Fraud watchlist screening API URL"
  type        = string
  default     = "NOT_CONFIGURED"
}

variable "document_api_url" {
  description = "Document verification API URL"
  type        = string
  default     = "NOT_CONFIGURED"
}

variable "saqa_api_url" {
  description = "SAQA qualification verification API URL"
  type        = string
  default     = "NOT_CONFIGURED"
}

variable "creditbureau_api_url" {
  description = "Credit bureau API URL"
  type        = string
  default     = "NOT_CONFIGURED"
}

variable "sars_api_url" {
  description = "SARS tax compliance API URL"
  type        = string
  default     = "NOT_CONFIGURED"
}

variable "sars_vat_endpoint_url" {
  description = "SARS VAT Vendor Search SOAP endpoint URL"
  type        = string
  default     = "https://secure.sarsefiling.co.za/VATVendorSearch/application/VendorService.asmx"
}

variable "sars_efiling_login_name" {
  description = "SARS eFiling login name for VAT vendor search"
  type        = string
  sensitive   = true
  default     = "NOT_CONFIGURED"
}

variable "sars_efiling_password" {
  description = "SARS eFiling password for VAT vendor search"
  type        = string
  sensitive   = true
  default     = "NOT_CONFIGURED"
}

variable "income_api_url" {
  description = "Income verification API URL"
  type        = string
  default     = "NOT_CONFIGURED"
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

#----------------------------------------------------------------------------------------------------------------
# Bedrock AI
#----------------------------------------------------------------------------------------------------------------

variable "bedrock_model_id" {
  description = "AWS Bedrock model ID for AI features"
  type        = string
  default     = "us.anthropic.claude-sonnet-4-5-20250929-v1:0"
}

variable "bedrock_region" {
  description = "AWS region for Bedrock API calls"
  type        = string
  default     = "us-east-1"
}





