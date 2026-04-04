variable "role_name" {
  description = "Name of the IAM role"
  type        = string
}

variable "policy_name" {
  description = "Name of the IAM policy"
  type        = string
}

variable "policy_description" {
  description = "Description of the IAM policy"
  type        = string
  default     = ""
}

variable "complete_stack_name" {
  description = "Complete stack name for resource naming"
  type        = string
}

variable "ssm_prefix" {
  description = "Prefix for SSM parameter paths (aligned with SAM stack name)"
  type        = string
}

variable "assume_role_policy" {
  description = "JSON string for the assume role policy document"
  type        = string
}

variable "policy_file" {
  description = "Name of the policy file"
  type        = string
}

variable "policies_path" {
  description = "Path to the policies directory"
  type        = string
}

variable "template_policy" {
  description = "Whether to use templatefile for the policy"
  type        = bool
  default     = false
}

variable "template_vars" {
  description = "Variables for template policy rendering"
  type        = map(string)
  default     = {}
}
