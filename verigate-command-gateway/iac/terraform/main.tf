
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

  complete_stack_name      = var.stack_name
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
# Partner Hub — consolidated single-table design
#----------------------------------------------------------------------------------------------------------------

module "partner_hub_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name      = var.stack_name
  table_name               = "partner-hub"
  hash_key                 = {
                                 name = "partnerId"
                                 type = "S"
                             }
  range_key                = {
                                 name = "entityType"
                                 type = "S"
                             }
  attributes               = [
    {
      name = "partnerId"
      type = "S"
    },
    {
      name = "entityType"
      type = "S"
    },
    {
      name = "partnerStatus"
      type = "S"
    },
    {
      name = "partnerPolicyId"
      type = "S"
    },
    {
      name = "slug"
      type = "S"
    }
  ]

  global_secondary_indexes = [
    {
      name               = "status-index"
      hash_key           = "partnerStatus"
      projection_type    = "ALL"
    },
    {
      name               = "policy-id-index"
      hash_key           = "partnerPolicyId"
      projection_type    = "ALL"
    },
    {
      name               = "slug-index"
      hash_key           = "slug"
      projection_type    = "ALL"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

resource "aws_ssm_parameter" "partner_hub_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/partner-hub/name"
  type  = "String"
  value = "${local.complete_stack_name}-partner-hub"
}

module "api_keys_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name      = var.stack_name
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

  complete_stack_name      = var.stack_name
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

  complete_stack_name      = var.stack_name
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

  complete_stack_name      = var.stack_name
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
# Billing Extended DynamoDB Tables (Invoicing, Payments, Subscriptions, Dunning, Reporting)
#----------------------------------------------------------------------------------------------------------------

module "invoices_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name = var.stack_name
  table_name          = "invoices"
  hash_key            = {
                             name = "partnerId"
                             type = "S"
                         }
  range_key           = {
                             name = "invoiceId"
                             type = "S"
                         }
  attributes          = [
    {
      name = "partnerId"
      type = "S"
    },
    {
      name = "invoiceId"
      type = "S"
    },
    {
      name = "invoiceNumber"
      type = "S"
    }
  ]

  global_secondary_indexes = [
    {
      name            = "invoiceNumber-index"
      hash_key        = "invoiceNumber"
      projection_type = "ALL"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

module "invoice_sequences_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name = var.stack_name
  table_name          = "invoice-sequences"
  hash_key            = {
                             name = "sequenceKey"
                             type = "S"
                         }
  attributes          = [
    {
      name = "sequenceKey"
      type = "S"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

module "payments_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name = var.stack_name
  table_name          = "payments"
  hash_key            = {
                             name = "partnerId"
                             type = "S"
                         }
  range_key           = {
                             name = "paymentId"
                             type = "S"
                         }
  attributes          = [
    {
      name = "partnerId"
      type = "S"
    },
    {
      name = "paymentId"
      type = "S"
    },
    {
      name = "invoiceId"
      type = "S"
    },
    {
      name = "payFastReference"
      type = "S"
    }
  ]

  global_secondary_indexes = [
    {
      name            = "invoiceId-index"
      hash_key        = "invoiceId"
      projection_type = "ALL"
    },
    {
      name            = "payFastReference-index"
      hash_key        = "payFastReference"
      projection_type = "ALL"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

module "plan_changes_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name = var.stack_name
  table_name          = "plan-changes"
  hash_key            = {
                             name = "partnerId"
                             type = "S"
                         }
  range_key           = {
                             name = "sortKey"
                             type = "S"
                         }
  attributes          = [
    {
      name = "partnerId"
      type = "S"
    },
    {
      name = "sortKey"
      type = "S"
    },
    {
      name = "status"
      type = "S"
    },
    {
      name = "effectiveDate"
      type = "S"
    }
  ]

  global_secondary_indexes = [
    {
      name            = "status-index"
      hash_key        = "status"
      range_key       = "effectiveDate"
      projection_type = "ALL"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

module "trials_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name = var.stack_name
  table_name          = "trials"
  hash_key            = {
                             name = "partnerId"
                             type = "S"
                         }
  range_key           = {
                             name = "trialId"
                             type = "S"
                         }
  attributes          = [
    {
      name = "partnerId"
      type = "S"
    },
    {
      name = "trialId"
      type = "S"
    },
    {
      name = "status"
      type = "S"
    },
    {
      name = "endDate"
      type = "S"
    }
  ]

  global_secondary_indexes = [
    {
      name            = "status-endDate-index"
      hash_key        = "status"
      range_key       = "endDate"
      projection_type = "ALL"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

module "subscriptions_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name = var.stack_name
  table_name          = "subscriptions"
  hash_key            = {
                             name = "partnerId"
                             type = "S"
                         }
  range_key           = {
                             name = "subscriptionId"
                             type = "S"
                         }
  attributes          = [
    {
      name = "partnerId"
      type = "S"
    },
    {
      name = "subscriptionId"
      type = "S"
    },
    {
      name = "status"
      type = "S"
    },
    {
      name = "currentPeriodEnd"
      type = "S"
    }
  ]

  global_secondary_indexes = [
    {
      name            = "status-index"
      hash_key        = "status"
      range_key       = "currentPeriodEnd"
      projection_type = "ALL"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

module "dunning_schedules_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name = var.stack_name
  table_name          = "dunning-schedules"
  hash_key            = {
                             name = "partnerId"
                             type = "S"
                         }
  range_key           = {
                             name = "sortKey"
                             type = "S"
                         }
  attributes          = [
    {
      name = "partnerId"
      type = "S"
    },
    {
      name = "sortKey"
      type = "S"
    },
    {
      name = "status"
      type = "S"
    },
    {
      name = "nextRetryDate"
      type = "S"
    }
  ]

  global_secondary_indexes = [
    {
      name            = "nextRetry-index"
      hash_key        = "status"
      range_key       = "nextRetryDate"
      projection_type = "ALL"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

module "credit_notes_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name = var.stack_name
  table_name          = "credit-notes"
  hash_key            = {
                             name = "partnerId"
                             type = "S"
                         }
  range_key           = {
                             name = "sortKey"
                             type = "S"
                         }
  attributes          = [
    {
      name = "partnerId"
      type = "S"
    },
    {
      name = "sortKey"
      type = "S"
    },
    {
      name = "status"
      type = "S"
    },
    {
      name = "gsiPartnerId"
      type = "S"
    }
  ]

  global_secondary_indexes = [
    {
      name            = "status-index"
      hash_key        = "status"
      range_key       = "gsiPartnerId"
      projection_type = "ALL"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

module "ledger_entries_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name = var.stack_name
  table_name          = "ledger-entries"
  hash_key            = {
                             name = "partnerId"
                             type = "S"
                         }
  range_key           = {
                             name = "sortKey"
                             type = "S"
                         }
  attributes          = [
    {
      name = "partnerId"
      type = "S"
    },
    {
      name = "sortKey"
      type = "S"
    },
    {
      name = "account"
      type = "S"
    },
    {
      name = "gsiSortKey"
      type = "S"
    }
  ]

  global_secondary_indexes = [
    {
      name            = "account-period-index"
      hash_key        = "account"
      range_key       = "gsiSortKey"
      projection_type = "ALL"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

module "reconciliation_reports_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name = var.stack_name
  table_name          = "reconciliation-reports"
  hash_key            = {
                             name = "period"
                             type = "S"
                         }
  range_key           = {
                             name = "reconciliationId"
                             type = "S"
                         }
  attributes          = [
    {
      name = "period"
      type = "S"
    },
    {
      name = "reconciliationId"
      type = "S"
    }
  ]

  fis_az_failure_ready = true
  default_tags = local.default_tags
}

#----------------------------------------------------------------------------------------------------------------
# Billing S3 Bucket (Invoice PDFs)
#----------------------------------------------------------------------------------------------------------------

module "invoices_s3" {
  source = "./modules/tf-s3"

  complete_stack_name = var.stack_name
  bucket_name         = "invoices"

  lifecycle_rules = [
    {
      id                         = "glacier-transition"
      enabled                    = true
      transition_days            = 365
      transition_storage_class   = "GLACIER"
      expiration_days            = 2555 # ~7 years
      noncurrent_transition_days = 30
      noncurrent_storage_class   = "GLACIER"
      noncurrent_expiration_days = 2555
    }
  ]

  default_tags = local.default_tags
}

resource "aws_ssm_parameter" "invoices_bucket_name" {
  name  = "/${local.ssm_prefix}/s3/invoices/name"
  type  = "String"
  value = module.invoices_s3.bucket_name
}

#----------------------------------------------------------------------------------------------------------------
# SSM Parameters - Billing Table Names
#----------------------------------------------------------------------------------------------------------------

resource "aws_ssm_parameter" "invoices_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/invoices/name"
  type  = "String"
  value = "${local.complete_stack_name}-invoices"
}

resource "aws_ssm_parameter" "invoice_sequences_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/invoice-sequences/name"
  type  = "String"
  value = "${local.complete_stack_name}-invoice-sequences"
}

resource "aws_ssm_parameter" "payments_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/payments/name"
  type  = "String"
  value = "${local.complete_stack_name}-payments"
}

resource "aws_ssm_parameter" "plan_changes_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/plan-changes/name"
  type  = "String"
  value = "${local.complete_stack_name}-plan-changes"
}

resource "aws_ssm_parameter" "trials_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/trials/name"
  type  = "String"
  value = "${local.complete_stack_name}-trials"
}

resource "aws_ssm_parameter" "subscriptions_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/subscriptions/name"
  type  = "String"
  value = "${local.complete_stack_name}-subscriptions"
}

resource "aws_ssm_parameter" "dunning_schedules_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/dunning-schedules/name"
  type  = "String"
  value = "${local.complete_stack_name}-dunning-schedules"
}

resource "aws_ssm_parameter" "credit_notes_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/credit-notes/name"
  type  = "String"
  value = "${local.complete_stack_name}-credit-notes"
}

resource "aws_ssm_parameter" "ledger_entries_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/ledger-entries/name"
  type  = "String"
  value = "${local.complete_stack_name}-ledger-entries"
}

resource "aws_ssm_parameter" "reconciliation_reports_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/reconciliation-reports/name"
  type  = "String"
  value = "${local.complete_stack_name}-reconciliation-reports"
}

#----------------------------------------------------------------------------------------------------------------
# PayFast Secrets
#----------------------------------------------------------------------------------------------------------------

module "payfast_secrets" {
  source = "./modules/tf-secrets-manager"

  prefix = "${var.secret_prefix}/payfast"

  default_recovery_window_in_days = var.recovery_window_in_days

  secrets = {
    "merchant_id" = {
      description = "PayFast Merchant ID"
      value       = var.payfast_merchant_id
    },
    "merchant_key" = {
      description = "PayFast Merchant Key"
      value       = var.payfast_merchant_key
    },
    "passphrase" = {
      description = "PayFast Passphrase"
      value       = var.payfast_passphrase
    }
  }
}

#----------------------------------------------------------------------------------------------------------------
# Identity Vault DynamoDB Table (DHA cost optimization)
#----------------------------------------------------------------------------------------------------------------

module "verified_identities_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name      = var.stack_name
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

  complete_stack_name = var.stack_name
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
# Bedrock AI IAM Policy
#----------------------------------------------------------------------------------------------------------------

resource "aws_iam_policy" "bedrock_invoke" {
  name = "${local.complete_stack_name}-bedrock-invoke"
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect   = "Allow"
      Action   = ["bedrock:InvokeModel", "bedrock:InvokeModelWithResponseStream"]
      Resource = "arn:aws:bedrock:${var.bedrock_region}::foundation-model/us.anthropic.claude-sonnet-4-5-*"
    }]
  })
}

resource "aws_iam_role_policy_attachment" "lambda_bedrock" {
  role       = module.lambda_iam.role_name
  policy_arn = aws_iam_policy.bedrock_invoke.arn
}

#----------------------------------------------------------------------------------------------------------------
# AI DynamoDB Tables
#----------------------------------------------------------------------------------------------------------------

module "ai_risk_enhancements_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name = var.stack_name
  table_name          = "ai-risk-enhancements"
  hash_key            = {
                             name = "workflowId"
                             type = "S"
                         }
  attributes          = [
    {
      name = "workflowId"
      type = "S"
    }
  ]

  ttl_attribute        = "ttl"
  fis_az_failure_ready = true
  default_tags = local.default_tags
}

resource "aws_ssm_parameter" "ai_risk_enhancements_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/ai-risk-enhancements/name"
  type  = "String"
  value = "${local.complete_stack_name}-ai-risk-enhancements"
}

module "fraud_velocity_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name = var.stack_name
  table_name          = "fraud-velocity"
  hash_key            = {
                             name = "identifierHash"
                             type = "S"
                         }
  range_key           = {
                             name = "windowKey"
                             type = "S"
                         }
  attributes          = [
    {
      name = "identifierHash"
      type = "S"
    },
    {
      name = "windowKey"
      type = "S"
    }
  ]

  ttl_attribute        = "ttl"
  fis_az_failure_ready = true
  default_tags = local.default_tags
}

resource "aws_ssm_parameter" "fraud_velocity_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/fraud-velocity/name"
  type  = "String"
  value = "${local.complete_stack_name}-fraud-velocity"
}

module "fraud_patterns_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name = var.stack_name
  table_name          = "fraud-patterns"
  hash_key            = {
                             name = "patternId"
                             type = "S"
                         }
  attributes          = [
    {
      name = "patternId"
      type = "S"
    }
  ]

  ttl_attribute        = "ttl"
  fis_az_failure_ready = true
  default_tags = local.default_tags
}

resource "aws_ssm_parameter" "fraud_patterns_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/fraud-patterns/name"
  type  = "String"
  value = "${local.complete_stack_name}-fraud-patterns"
}

module "ai_chat_history_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name = var.stack_name
  table_name          = "ai-chat-history"
  hash_key            = {
                             name = "conversationId"
                             type = "S"
                         }
  range_key           = {
                             name = "messageTimestamp"
                             type = "S"
                         }
  attributes          = [
    {
      name = "conversationId"
      type = "S"
    },
    {
      name = "messageTimestamp"
      type = "S"
    }
  ]

  ttl_attribute        = "ttl"
  fis_az_failure_ready = true
  default_tags = local.default_tags
}

resource "aws_ssm_parameter" "ai_chat_history_table_name" {
  name  = "/${local.ssm_prefix}/dynamodb/ai-chat-history/name"
  type  = "String"
  value = "${local.complete_stack_name}-ai-chat-history"
}

#----------------------------------------------------------------------------------------------------------------
# SQS Queues
#----------------------------------------------------------------------------------------------------------------

module "verify_party_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = var.stack_name
  ssm_prefix          = local.ssm_prefix
  queue_name = "verify-party"
  max_receive_count = 1
}
module "qlink_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = var.stack_name
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-qlink"
  max_receive_count = 1
}

module "worldcheck_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = var.stack_name
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-worldcheck"
  max_receive_count = 1
}


module "dha_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = var.stack_name
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-dha"
  max_receive_count = 1
}

module "cipc_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = var.stack_name
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-cipc"
  max_receive_count = 1
}

module "deedsweb_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = var.stack_name
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-deedsweb"
  max_receive_count = 1
}

module "employment_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = var.stack_name
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-employment"
  max_receive_count = 1
}

module "negativenews_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = var.stack_name
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-negativenews"
  max_receive_count = 1
}

module "fraudwatchlist_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = var.stack_name
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-fraudwatchlist"
  max_receive_count = 1
}

module "document_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = var.stack_name
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-document"
  max_receive_count = 1
}

module "saqa_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = var.stack_name
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-saqa"
  max_receive_count = 1
}

module "creditbureau_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = var.stack_name
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-creditbureau"
  max_receive_count = 1
}

module "sars_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = var.stack_name
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-sars"
  max_receive_count = 1
}

module "sars_vat_adapter_queue" {
  source              = "./modules/tf-sqs"
  complete_stack_name = var.stack_name
  ssm_prefix          = local.ssm_prefix
  queue_name          = "adapter-sars-vat"
  max_receive_count   = 1
}

module "income_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = var.stack_name
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-income"
  max_receive_count = 1
}

module "opensanctions_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = var.stack_name
  ssm_prefix          = local.ssm_prefix
  queue_name = "adapter-opensanctions"
  max_receive_count = 1
}

module "partner_create_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = var.stack_name
  ssm_prefix          = local.ssm_prefix
  queue_name = "partner-create"
  max_receive_count = 1
}

module "partner_config_update_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = var.stack_name
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

module "risk_assessments_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name      = var.stack_name
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

  complete_stack_name      = var.stack_name
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

#----------------------------------------------------------------------------------------------------------------
# SSM Parameters - Risk Engine Table Names
#----------------------------------------------------------------------------------------------------------------

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

#----------------------------------------------------------------------------------------------------------------
# Case Management DynamoDB Tables
#----------------------------------------------------------------------------------------------------------------

module "cases_dynamodb" {
  source = "./modules/tf-dynamodb"

  complete_stack_name      = var.stack_name
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

  complete_stack_name = var.stack_name
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

  complete_stack_name = var.stack_name
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

  complete_stack_name = var.stack_name
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
  value = length(var.negativenews_api_key) > 0 ? var.negativenews_api_key : "NOT_CONFIGURED"
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

resource "aws_ssm_parameter" "sars_vat_endpoint_url" {
  name  = "/${local.ssm_prefix}/sars/vat_endpoint_url"
  type  = "String"
  value = var.sars_vat_endpoint_url
}

resource "aws_ssm_parameter" "sars_efiling_secret_name" {
  name  = "/${local.ssm_prefix}/sars/efiling_secret_name"
  type  = "String"
  value = "${var.secret_prefix}/sars-efiling"
}

module "sars_efiling_secrets" {
  source = "./modules/tf-secrets-manager"

  prefix = "${var.secret_prefix}/sars-efiling"

  default_recovery_window_in_days = var.recovery_window_in_days

  secrets = {
    "login_name" = {
      description = "SARS eFiling login name"
      value       = var.sars_efiling_login_name
    },
    "password" = {
      description = "SARS eFiling password"
      value       = var.sars_efiling_password
    }
  }
}

resource "aws_ssm_parameter" "income_api_url" {
  name  = "/${local.ssm_prefix}/income/api_url"
  type  = "String"
  value = var.income_api_url
}

#----------------------------------------------------------------------------------------------------------------
# Lambda Functions
#----------------------------------------------------------------------------------------------------------------

#----------------------------------------------------------------------------------------------------------------
# DHA Inbound Email Response Pipeline
#----------------------------------------------------------------------------------------------------------------

# SQS queue for DHA email responses (S3 event notifications → Lambda)
module "dha_response_adapter_queue" {
  source              = "./modules/tf-sqs"
  complete_stack_name = var.stack_name
  ssm_prefix          = local.ssm_prefix
  queue_name          = "adapter-dha-response"
  max_receive_count   = 3
}

# SES Receipt Rule Set — receives replies to dhaverifications@verigate.co.za
resource "aws_ses_receipt_rule_set" "dha_inbound" {
  rule_set_name = "${local.complete_stack_name}-dha-inbound"
}

resource "aws_ses_active_receipt_rule_set" "dha_inbound" {
  rule_set_name = aws_ses_receipt_rule_set.dha_inbound.rule_set_name
}

resource "aws_ses_receipt_rule" "dha_response" {
  name          = "${local.complete_stack_name}-dha-response"
  rule_set_name = aws_ses_receipt_rule_set.dha_inbound.rule_set_name
  recipients    = ["dhaverifications@verigate.co.za"]
  enabled       = true
  scan_enabled  = true

  s3_action {
    bucket_name       = module.documents_s3.bucket_name
    object_key_prefix = "inbound-emails/dha/"
    position          = 1
  }
}

# S3 bucket policy — allow SES to write inbound emails
resource "aws_s3_bucket_policy" "ses_inbound_write" {
  bucket = module.documents_s3.bucket_name
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid       = "AllowSESPuts"
        Effect    = "Allow"
        Principal = { Service = "ses.amazonaws.com" }
        Action    = "s3:PutObject"
        Resource  = "${module.documents_s3.bucket_arn}/inbound-emails/*"
        Condition = {
          StringEquals = {
            "AWS:SourceAccount" = data.aws_caller_identity.current.account_id
          }
        }
      }
    ]
  })
}

# SQS policy — allow S3 to send event notifications to the queue
resource "aws_sqs_queue_policy" "dha_response_s3_notify" {
  queue_url = module.dha_response_adapter_queue.queue_url
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid       = "AllowS3Notification"
        Effect    = "Allow"
        Principal = { Service = "s3.amazonaws.com" }
        Action    = "sqs:SendMessage"
        Resource  = module.dha_response_adapter_queue.queue_arn
        Condition = {
          ArnEquals = {
            "aws:SourceArn" = module.documents_s3.bucket_arn
          }
        }
      }
    ]
  })
}

# S3 event notification — trigger on new objects in inbound-emails/dha/ → SQS
resource "aws_s3_bucket_notification" "dha_inbound_email" {
  bucket = module.documents_s3.bucket_name

  queue {
    queue_arn     = module.dha_response_adapter_queue.queue_arn
    events        = ["s3:ObjectCreated:*"]
    filter_prefix = "inbound-emails/dha/"
  }

  depends_on = [aws_sqs_queue_policy.dha_response_s3_notify]
}

#----------------------------------------------------------------------------------------------------------------
# Lambda Functions
#----------------------------------------------------------------------------------------------------------------

module "worldcheck_lambda" {
  source = "./modules/tf-lambda"
  
  complete_stack_name = var.stack_name
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
