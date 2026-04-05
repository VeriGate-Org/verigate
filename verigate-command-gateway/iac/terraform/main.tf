
# Datadog monitoring
#----------------------------------------------------------------------------------------------------------------

module "datadog_monitoring" {
  count  = var.environment_shortname == "prod" ? 1 : 0
  source = "./modules/shared/datadog-monitoring"

  STACK_NAME                     = var.stack_name
  ENVIRONMENT_SHORTNAME          = var.environment_shortname
  ENVIRONMENT_NAME               = var.environment_name
  POD_NAME                       = "verigate"
  TEAM_NAME                      = "sds"
  GITHUB_PIPELINE_NAME           = "verigate"
  DATADOG_TEAM_NAME              = "verigate"
  DATADOG_MONITOR_NOTIFY_ALL     = var.datadog_monitor_notify_all
  DATADOG_MONITOR_NOTIFY_WARNING = var.datadog_monitor_notify_warning
  DATADOG_MONITOR_NOTIFY_ALERT   = var.datadog_monitor_notify_alert
}

#----------------------------------------------------------------------------------------------------------------
# DynamoDb
#----------------------------------------------------------------------------------------------------------------

module "verification_dynamodb_commandstore" {
  source = "./modules/tf-dynamodb"

  complete_stack_name      = "${var.stack_name}-${var.project_name}"
  table_name               = "command-store-table"
  hash_key                 = {
                                 name = "commandId"
                                 type = "S"
                             }
  attributes               = [
    {
      name = "commandId"
      type = "S"
    },
    {
      name = "partnerId"
      type = "S"
    },
    {
      name = "statusCreatedAt"
      type = "S"
    },
    {
      name = "statusCreatedAt"
      type = "S"
    }
  ]

  global_secondary_indexes = [
    {
      name               = "partner-index"
      hash_key           = "partnerId"
      range_key          = "statusCreatedAt"
      projection_type    = "ALL"
    },
    {
      name               = "partner-status-index"
      hash_key           = "partnerId"
      range_key          = "statusCreatedAt"
      projection_type    = "ALL"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

#----------------------------------------------------------------------------------------------------------------
# Partner DynamoDB Tables
#----------------------------------------------------------------------------------------------------------------

module "partner_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name      = "${var.stack_name}-${var.project_name}"
  table_name               = "partner-table"
  hash_key                 = {
                                 name = "partnerId"
                                 type = "S"
                             }
  attributes               = [
    {
      name = "partnerId"
      type = "S"
    },
    {
      name = "partnerStatus"
      type = "S"
    }
  ]

  global_secondary_indexes = [
    {
      name               = "status-index"
      hash_key           = "partnerStatus"
      projection_type    = "ALL"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

module "partner_configuration_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name      = "${var.stack_name}-${var.project_name}"
  table_name               = "partner-configuration-table"
  hash_key                 = {
                                 name = "partnerId"
                                 type = "S"
                             }
  range_key                = {
                                 name = "configurationType"
                                 type = "S"
                             }
  attributes               = [
    {
      name = "partnerId"
      type = "S"
    },
    {
      name = "configurationType"
      type = "S"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

module "api_keys_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name      = "${var.stack_name}-${var.project_name}"
  table_name               = "api-keys-table"
  hash_key                 = {
                                 name = "apiKeyHash"
                                 type = "S"
                             }
  attributes               = [
    {
      name = "apiKeyHash"
      type = "S"
    },
    {
      name = "partnerId"
      type = "S"
    }
  ]

  global_secondary_indexes = [
    {
      name               = "partner-index"
      hash_key           = "partnerId"
      projection_type    = "ALL"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

#----------------------------------------------------------------------------------------------------------------
# Billing & Usage Tracking DynamoDB Tables
#----------------------------------------------------------------------------------------------------------------

module "usage_records_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name      = "${var.stack_name}-${var.project_name}"
  table_name               = "usage-records-table"
  hash_key                 = {
                                 name = "partnerId"
                                 type = "S"
                             }
  range_key                = {
                                 name = "eventTimestamp"
                                 type = "S"
                             }
  attributes               = [
    {
      name = "partnerId"
      type = "S"
    },
    {
      name = "eventTimestamp"
      type = "S"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

module "usage_summaries_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name      = "${var.stack_name}-${var.project_name}"
  table_name               = "usage-summaries-table"
  hash_key                 = {
                                 name = "partnerId"
                                 type = "S"
                             }
  range_key                = {
                                 name = "periodKey"
                                 type = "S"
                             }
  attributes               = [
    {
      name = "partnerId"
      type = "S"
    },
    {
      name = "periodKey"
      type = "S"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

module "billing_plans_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name      = "${var.stack_name}-${var.project_name}"
  table_name               = "billing-plans-table"
  hash_key                 = {
                                 name = "partnerId"
                                 type = "S"
                             }
  attributes               = [
    {
      name = "partnerId"
      type = "S"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

#----------------------------------------------------------------------------------------------------------------
# Identity Vault DynamoDB Table (DHA cost optimization)
#----------------------------------------------------------------------------------------------------------------

module "verified_identities_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name      = "${var.stack_name}-${var.project_name}"
  table_name               = "verified-identities"
  hash_key                 = {
                                 name = "identityHash"
                                 type = "S"
                             }
  range_key                = {
                                 name = "partnerId"
                                 type = "S"
                             }
  attributes               = [
    {
      name = "identityHash"
      type = "S"
    },
    {
      name = "partnerId"
      type = "S"
    },
    {
      name = "verifiedAt"
      type = "S"
    }
  ]

  global_secondary_indexes = [
    {
      name               = "partner-verified-index"
      hash_key           = "partnerId"
      range_key          = "verifiedAt"
      projection_type    = "ALL"
    }
  ]

  ttl_attribute        = "expiresAt"
  fis_az_failure_ready = true
  default_tags = local.default_tags
}

resource "aws_ssm_parameter" "verified_identities_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/verified-identities/name"
  type  = "String"
  value = "${local.complete_stack_name}-verified-identities"
}

#----------------------------------------------------------------------------------------------------------------
# S3 Buckets
#----------------------------------------------------------------------------------------------------------------

module "documents_s3" {
  source = "./modules/tf-s3"

  complete_stack_name = "${var.stack_name}-${var.project_name}"
  bucket_name         = "documents"

  lifecycle_rules = [
    {
      id                         = "glacier-transition"
      enabled                    = true
      transition_days            = 90
      transition_storage_class   = "GLACIER"
      expiration_days            = 2555 # ~7 years
      noncurrent_transition_days = 30
      noncurrent_storage_class   = "GLACIER"
      noncurrent_expiration_days = 2555
    }
  ]

  cors_allowed_origins = ["*"]

  default_tags = local.default_tags
}

resource "aws_ssm_parameter" "documents_bucket_name" {
  name  = "/${local.ssm_prefix}/s3/documents/name"
  type  = "String"
  value = module.documents_s3.bucket_name
}

#----------------------------------------------------------------------------------------------------------------
# IAM Role and policy
#----------------------------------------------------------------------------------------------------------------

module "lambda_iam" {
  source = "./modules/tf-iam"

  role_name           = "${local.complete_stack_name}-lambda-role"
  policy_name         = "${local.complete_stack_name}-lambda-policy"
  policy_description  = "Policy for ${local.complete_stack_name} Lambda functions"
  complete_stack_name = local.complete_stack_name
  ssm_prefix          = "${local.ssm_prefix}-${var.environment_shortname}"

  assume_role_policy = file("./policies/lambda_assume_role_verification.json")

  policy_file     = "lambda_policy_verification.json"
  policies_path   = "./policies"
  template_policy = true
  template_vars = {
    region     = var.aws_region
    account_id = data.aws_caller_identity.current.account_id
  }

}

#----------------------------------------------------------------------------------------------------------------
# SQS Queues
#----------------------------------------------------------------------------------------------------------------

module "verify_party_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  ssm_prefix          = local.ssm_prefix
  queue_name = "verify-party"
  max_receive_count = 1
}
module "qlink_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-qlink"
  max_receive_count = 1
}

module "worldcheck_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-worldcheck"
  max_receive_count = 1
}


module "dha_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-dha"
  max_receive_count = 1
}

module "cipc_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-cipc"
  max_receive_count = 1
}

module "deedsweb_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-deedsweb"
  max_receive_count = 1
}

module "employment_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-employment"
  max_receive_count = 1
}

module "negativenews_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-negativenews"
  max_receive_count = 1
}

module "fraudwatchlist_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-fraudwatchlist"
  max_receive_count = 1
}

module "document_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-document"
  max_receive_count = 1
}

module "saqa_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-saqa"
  max_receive_count = 1
}

module "creditbureau_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-creditbureau"
  max_receive_count = 1
}

module "sars_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-sars"
  max_receive_count = 1
}

module "income_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-income"
  max_receive_count = 1
}

module "opensanctions_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-opensanctions"
  max_receive_count = 1
}

module "partner_create_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  ssm_prefix          = local.ssm_prefix
  queue_name = "partner-create"
  max_receive_count = 1
}

module "partner_config_update_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  ssm_prefix          = local.ssm_prefix
  queue_name = "partner-config-update"
  max_receive_count = 1
}

resource "aws_ssm_parameter" "api_keys_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/api-keys-table/name"
  type  = "String"
  value = "${local.complete_stack_name}-api-keys-table"
}

#----------------------------------------------------------------------------------------------------------------
# SSM Parameters - Partner & Billing Table Names
#----------------------------------------------------------------------------------------------------------------

resource "aws_ssm_parameter" "partner_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/partner-table/name"
  type  = "String"
  value = "${local.complete_stack_name}-partner-table"
}

resource "aws_ssm_parameter" "partner_configuration_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/partner-configuration-table/name"
  type  = "String"
  value = "${local.complete_stack_name}-partner-configuration-table"
}

resource "aws_ssm_parameter" "usage_records_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/usage-records-table/name"
  type  = "String"
  value = "${local.complete_stack_name}-usage-records-table"
}

resource "aws_ssm_parameter" "usage_summaries_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/usage-summaries-table/name"
  type  = "String"
  value = "${local.complete_stack_name}-usage-summaries-table"
}

resource "aws_ssm_parameter" "billing_plans_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/billing-plans-table/name"
  type  = "String"
  value = "${local.complete_stack_name}-billing-plans-table"
}

# Note: Partner queue ARN SSM parameters and DLQ/IMQ queues are already
# created by the tf-sqs module in partner_create_queue and
# partner_config_update_queue above (with overwrite = true).

#----------------------------------------------------------------------------------------------------------------
# Risk Engine DynamoDB Tables
#----------------------------------------------------------------------------------------------------------------

module "risk_scoring_config_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name      = "${var.stack_name}-${var.project_name}"
  table_name               = "risk-scoring-config"
  hash_key                 = {
                                 name = "partnerId"
                                 type = "S"
                             }
  attributes               = [
    {
      name = "partnerId"
      type = "S"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

module "risk_assessments_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name      = "${var.stack_name}-${var.project_name}"
  table_name               = "risk-assessments"
  hash_key                 = {
                                 name = "verificationId"
                                 type = "S"
                             }
  attributes               = [
    {
      name = "verificationId"
      type = "S"
    },
    {
      name = "partnerId"
      type = "S"
    },
    {
      name = "assessedAt"
      type = "S"
    }
  ]

  global_secondary_indexes = [
    {
      name               = "partner-assessed-index"
      hash_key           = "partnerId"
      range_key          = "assessedAt"
      projection_type    = "ALL"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

module "verification_workflows_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name      = "${var.stack_name}-${var.project_name}"
  table_name               = "verification-workflows"
  hash_key                 = {
                                 name = "workflowId"
                                 type = "S"
                             }
  attributes               = [
    {
      name = "workflowId"
      type = "S"
    },
    {
      name = "status"
      type = "S"
    },
    {
      name = "createdAt"
      type = "S"
    }
  ]

  global_secondary_indexes = [
    {
      name               = "status-created-index"
      hash_key           = "status"
      range_key          = "createdAt"
      projection_type    = "ALL"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

module "policies_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name      = "${var.stack_name}-${var.project_name}"
  table_name               = "policies"
  hash_key                 = {
                                 name = "partnerPolicyId"
                                 type = "S"
                             }
  attributes               = [
    {
      name = "partnerPolicyId"
      type = "S"
    },
    {
      name = "partnerId"
      type = "S"
    },
    {
      name = "status"
      type = "S"
    }
  ]

  global_secondary_indexes = [
    {
      name               = "partner-status-index"
      hash_key           = "partnerId"
      range_key          = "status"
      projection_type    = "ALL"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

#----------------------------------------------------------------------------------------------------------------
# SSM Parameters - Risk Engine Table Names
#----------------------------------------------------------------------------------------------------------------

resource "aws_ssm_parameter" "risk_scoring_config_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/risk-scoring-config/name"
  type  = "String"
  value = "${local.complete_stack_name}-risk-scoring-config"
}

resource "aws_ssm_parameter" "risk_assessments_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/risk-assessments/name"
  type  = "String"
  value = "${local.complete_stack_name}-risk-assessments"
}

resource "aws_ssm_parameter" "verification_workflows_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/verification-workflows/name"
  type  = "String"
  value = "${local.complete_stack_name}-verification-workflows"
}

resource "aws_ssm_parameter" "policies_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/policies/name"
  type  = "String"
  value = "${local.complete_stack_name}-policies"
}

#----------------------------------------------------------------------------------------------------------------
# Case Management DynamoDB Tables
#----------------------------------------------------------------------------------------------------------------

module "cases_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name      = "${var.stack_name}-${var.project_name}"
  table_name               = "cases"
  hash_key                 = {
                                 name = "caseId"
                                 type = "S"
                             }
  attributes               = [
    {
      name = "caseId"
      type = "S"
    },
    {
      name = "partnerId"
      type = "S"
    },
    {
      name = "statusCreatedAt"
      type = "S"
    }
  ]

  global_secondary_indexes = [
    {
      name               = "partner-status-index"
      hash_key           = "partnerId"
      range_key          = "statusCreatedAt"
      projection_type    = "ALL"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

resource "aws_ssm_parameter" "cases_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/cases/name"
  type  = "String"
  value = "${local.complete_stack_name}-cases"
}

#----------------------------------------------------------------------------------------------------------------
# Enhanced Due Diligence (Ongoing Monitoring) DynamoDB Tables
#----------------------------------------------------------------------------------------------------------------

module "monitored_subjects_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name = "${var.stack_name}-${var.project_name}"
  table_name          = "monitored-subjects"
  hash_key            = {
                             name = "subjectId"
                             type = "S"
                         }
  attributes          = [
    {
      name = "subjectId"
      type = "S"
    },
    {
      name = "partnerId"
      type = "S"
    },
    {
      name = "statusNextCheck"
      type = "S"
    }
  ]

  global_secondary_indexes = [
    {
      name            = "partner-status-index"
      hash_key        = "partnerId"
      range_key       = "statusNextCheck"
      projection_type = "ALL"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

module "monitoring_alerts_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name = "${var.stack_name}-${var.project_name}"
  table_name          = "monitoring-alerts"
  hash_key            = {
                             name = "alertId"
                             type = "S"
                         }
  attributes          = [
    {
      name = "alertId"
      type = "S"
    },
    {
      name = "partnerId"
      type = "S"
    },
    {
      name = "subjectIdCreatedAt"
      type = "S"
    }
  ]

  global_secondary_indexes = [
    {
      name            = "partner-subject-index"
      hash_key        = "partnerId"
      range_key       = "subjectIdCreatedAt"
      projection_type = "ALL"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

#----------------------------------------------------------------------------------------------------------------
# SSM Parameters - Monitoring Table Names
#----------------------------------------------------------------------------------------------------------------

resource "aws_ssm_parameter" "monitored_subjects_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/monitored-subjects/name"
  type  = "String"
  value = "${local.complete_stack_name}-monitored-subjects"
}

resource "aws_ssm_parameter" "monitoring_alerts_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/monitoring-alerts/name"
  type  = "String"
  value = "${local.complete_stack_name}-monitoring-alerts"
}

#----------------------------------------------------------------------------------------------------------------
# Kinesis
#----------------------------------------------------------------------------------------------------------------

module "verification_stream" {
  source = "./modules/tf-events"

  complete_stack_name = "${var.stack_name}-${var.project_name}"
  ssm_prefix          = local.ssm_prefix
  event_bus_name = "event-bus"
  event_stream_name = "event-stream"
}


#----------------------------------------------------------------------------------------------------------------
# QLINK
#----------------------------------------------------------------------------------------------------------------
module "qlink_secrets" {
  source = "./modules/tf-secrets-manager"
  
  prefix = "${var.secret_prefix}/qlink"

  default_recovery_window_in_days = var.recovery_window_in_days
  
  secrets = {

    "client_id" = {
      description = "Client ID for QLink"
      value       = var.qlink_client_id # Reference to a variable populated from GitHub Secrets
    },
     "username" = {
      description = "Username for QLink"
      value       = var.qlink_username # Reference to a variable populated from GitHub Secrets
    },
    "password" = {
      description = "Password for QLink"
      value       = var.qlink_password # Reference to a variable populated from GitHub Secrets
    }
  }
}

resource "aws_ssm_parameter" "qlink_api_url" {
  name  = "/${local.ssm_prefix}/qlink/api_url"
  type  = "String"
  value = var.qlink_api_url
} 

#----------------------------------------------------------------------------------------------------------------
# Worldcheck
#----------------------------------------------------------------------------------------------------------------

module "worldcheck_secrets" {
  source = "./modules/tf-secrets-manager"
  
  prefix = "${var.secret_prefix}/worldcheck"

  default_recovery_window_in_days = var.recovery_window_in_days
  secrets = {
    "client_id" = {
      description = "Client Id for Worldcheck"
      value       = var.worldcheck_client_id # Reference to a variable populated from GitHub Secrets
    },
    "client_secret" = {
      description = "Client Secret for Worldcheck"
      value       = var.worldcheck_client_secret # Reference to a variable populated from GitHub Secrets
    },
     "scope" = {
      description = "Scope for Worldcheck"
      value       = var.worldcheck_scope # Reference to a variable populated from GitHub Secrets
    },
     "subscription_key" = {
      description = "Subscription Key for Worldcheck"
      value       = var.worldcheck_subscription_key # Reference to a variable populated from GitHub Secrets
    },
     "basic_authorization" = {
      description = "Basic authorization for Worldcheck"
      value       = var.worldcheck_basic_authorization # Reference to a variable populated from GitHub Secrets
    },
  }
}
resource "aws_ssm_parameter" "worldcheck_authentication_url" {
  name  = "/${local.ssm_prefix}/worldcheck/authentication_url"
  type  = "String"
  value = var.worldcheck_authentication_url
} 

resource "aws_ssm_parameter" "worldcheck_qt_api_url" {
  name  = "/${local.ssm_prefix}/worldcheck/qt_api_url"
  type  = "String"
  value = var.worldcheck_qt_api_url
} 

#----------------------------------------------------------------------------------------------------------------
# World Check One
#----------------------------------------------------------------------------------------------------------------

module "worldcheck_one_secrets" {
  source = "./modules/tf-secrets-manager"

  prefix = "${var.secret_prefix}/worldcheck-one"

  default_recovery_window_in_days = var.recovery_window_in_days
  secrets = {
    "api_key" = {
      description = "API Key for World Check One"
      value       = var.worldcheck_api_key
    },
    "api_secret" = {
      description = "API Secret for World Check One"
      value       = var.worldcheck_api_secret
    },
    "user_id" = {
      description = "User ID for World Check One"
      value       = var.worldcheck_user_id
    },
    "default_group_id" = {
      description = "Default Group ID for World Check One"
      value       = var.worldcheck_default_group_id
    }
  }
}

resource "aws_ssm_parameter" "worldcheck_api_base_url" {
  name  = "/${local.ssm_prefix}/worldcheck/api_base_url"
  type  = "String"
  value = var.worldcheck_api_base_url
}

#----------------------------------------------------------------------------------------------------------------
# OpenSanctions
#----------------------------------------------------------------------------------------------------------------

module "opensanctions_secrets" {
  source = "./modules/tf-secrets-manager"

  prefix = "${var.secret_prefix}/opensanctions"

  default_recovery_window_in_days = var.recovery_window_in_days
  secrets = {
    "api_key" = {
      description = "API Key for OpenSanctions"
      value       = var.opensanctions_api_key
    }
  }
}

resource "aws_ssm_parameter" "opensanctions_api_url" {
  name  = "/${local.ssm_prefix}/opensanctions/api_url"
  type  = "String"
  value = var.opensanctions_api_url
}

#----------------------------------------------------------------------------------------------------------------
# DHA / HANIS
#----------------------------------------------------------------------------------------------------------------

resource "aws_ssm_parameter" "dha_api_url" {
  name  = "/${local.ssm_prefix}/dha/api_url"
  type  = "String"
  value = var.dha_api_url
}

resource "aws_ssm_parameter" "hanis_site_id" {
  name  = "/${local.ssm_prefix}/hanis/site_id"
  type  = "String"
  value = var.hanis_site_id
}

resource "aws_ssm_parameter" "hanis_workstation_id" {
  name  = "/${local.ssm_prefix}/hanis/workstation_id"
  type  = "String"
  value = var.hanis_workstation_id
}

resource "aws_ssm_parameter" "hanis_primary_url" {
  name  = "/${local.ssm_prefix}/hanis/primary_url"
  type  = "String"
  value = var.hanis_primary_url
}

resource "aws_ssm_parameter" "hanis_failover_url" {
  name  = "/${local.ssm_prefix}/hanis/failover_url"
  type  = "String"
  value = var.hanis_failover_url
}

#----------------------------------------------------------------------------------------------------------------
# CIPC
#----------------------------------------------------------------------------------------------------------------

resource "aws_ssm_parameter" "cipc_api_key" {
  name  = "/${local.ssm_prefix}/cipc/api_key"
  type  = "String"
  value = var.cipc_api_key
}

#----------------------------------------------------------------------------------------------------------------
# Adapter API URLs
#----------------------------------------------------------------------------------------------------------------

resource "aws_ssm_parameter" "deedsweb_api_url" {
  name  = "/${local.ssm_prefix}/deedsweb/api_url"
  type  = "String"
  value = var.deedsweb_api_url
}

resource "aws_ssm_parameter" "employment_api_url" {
  name  = "/${local.ssm_prefix}/employment/api_url"
  type  = "String"
  value = var.employment_api_url
}

resource "aws_ssm_parameter" "negativenews_api_url" {
  name  = "/${local.ssm_prefix}/negativenews/api_url"
  type  = "String"
  value = var.negativenews_api_url
}

resource "aws_ssm_parameter" "negativenews_api_key" {
  name  = "/${local.ssm_prefix}/negativenews/api_key"
  type  = "String"
  value = var.negativenews_api_key
}

resource "aws_ssm_parameter" "fraudwatchlist_api_url" {
  name  = "/${local.ssm_prefix}/fraudwatchlist/api_url"
  type  = "String"
  value = var.fraudwatchlist_api_url
}

resource "aws_ssm_parameter" "document_api_url" {
  name  = "/${local.ssm_prefix}/document/api_url"
  type  = "String"
  value = var.document_api_url
}

resource "aws_ssm_parameter" "saqa_api_url" {
  name  = "/${local.ssm_prefix}/saqa/api_url"
  type  = "String"
  value = var.saqa_api_url
}

resource "aws_ssm_parameter" "creditbureau_api_url" {
  name  = "/${local.ssm_prefix}/creditbureau/api_url"
  type  = "String"
  value = var.creditbureau_api_url
}

resource "aws_ssm_parameter" "sars_api_url" {
  name  = "/${local.ssm_prefix}/sars/api_url"
  type  = "String"
  value = var.sars_api_url
}

resource "aws_ssm_parameter" "income_api_url" {
  name  = "/${local.ssm_prefix}/income/api_url"
  type  = "String"
  value = var.income_api_url
}

#----------------------------------------------------------------------------------------------------------------
# Lambda Functions
#----------------------------------------------------------------------------------------------------------------

module "worldcheck_lambda" {
  source = "./modules/tf-lambda"
  
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  lambda_name = "worldcheck-verification"
  lambda_runtime = "java17"
  lambda_handler = "verigate.adapter.refinitiv.worldcheck.infrastructure.functions.lambda.handlers.WorldCheckVerificationLambdaHandler"
  lambda_timeout = 300
  lambda_memory_size = 1024
  lambda_role_arn = module.lambda_iam.role_arn
  
  environment_variables = {
    WORLDCHECK_API_BASE_URL  = var.worldcheck_api_base_url
    COMMAND_STORE_TABLE_NAME = module.verification_dynamodb_commandstore.table_name
    EVENT_BUS_NAME           = module.verification_stream.event_bus_name
  }
  
  # SQS Event Source
  enable_sqs_event_source = true
  event_source_arn = module.worldcheck_adapter_queue.queue_arn
  
  default_tags = local.default_tags
}
