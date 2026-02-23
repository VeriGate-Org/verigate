
# Datadog monitoring
#----------------------------------------------------------------------------------------------------------------

module "datadog_monitoring" {
  count  = var.environment_shortname == "prd" ? 1 : 0
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
      name = "createdAt"
      type = "S"
    }
  ]

  global_secondary_indexes = [
    {
      name               = "partner-index"
      hash_key           = "partnerId"
      range_key          = "createdAt"
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
# IAM Role and policy
#----------------------------------------------------------------------------------------------------------------

module "lambda_iam" {
  source = "./modules/tf-iam"

  role_name           = "${local.complete_stack_name}-lambda-role"
  policy_name         = "${local.complete_stack_name}-lambda-policy"
  policy_description  = "Policy for ${local.complete_stack_name} Lambda functions"
  complete_stack_name = local.complete_stack_name

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
  queue_name = "verify-party"
  max_receive_count = 1
}
module "qlink_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  queue_name = "adapter-qlink"
  max_receive_count = 1
}

module "worldcheck_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  queue_name = "adapter-worldcheck"
  max_receive_count = 1
}


module "dha_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  queue_name = "adapter-dha"
  max_receive_count = 1
}

module "cipc_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  queue_name = "adapter-cipc"
  max_receive_count = 1
}

module "deedsweb_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  queue_name = "adapter-deedsweb"
  max_receive_count = 1
}

module "employment_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  queue_name = "adapter-employment"
  max_receive_count = 1
}

module "negativenews_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  queue_name = "adapter-negativenews"
  max_receive_count = 1
}

module "fraudwatchlist_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  queue_name = "adapter-fraudwatchlist"
  max_receive_count = 1
}

module "document_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  queue_name = "adapter-document"
  max_receive_count = 1
}

module "saqa_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  queue_name = "adapter-saqa"
  max_receive_count = 1
}

module "creditbureau_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  queue_name = "adapter-creditbureau"
  max_receive_count = 1
}

module "sars_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  queue_name = "adapter-sars"
  max_receive_count = 1
}

module "income_adapter_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  queue_name = "adapter-income"
  max_receive_count = 1
}

module "partner_create_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  queue_name = "partner-create"
  max_receive_count = 1
}

module "partner_config_update_queue" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  queue_name = "partner-config-update"
  max_receive_count = 1
}

resource "aws_ssm_parameter" "api_keys_table_name" {
  name  = "/${var.stack_name}-${var.project_name}/dynamodb/api-keys-table/name"
  type  = "String"
  value = "${local.complete_stack_name}-api-keys-table"
}

#----------------------------------------------------------------------------------------------------------------
# SSM Parameters - Partner & Billing Table Names
#----------------------------------------------------------------------------------------------------------------

resource "aws_ssm_parameter" "partner_table_name" {
  name  = "/${var.stack_name}-${var.project_name}/dynamodb/partner-table/name"
  type  = "String"
  value = "${local.complete_stack_name}-partner-table"
}

resource "aws_ssm_parameter" "partner_configuration_table_name" {
  name  = "/${var.stack_name}-${var.project_name}/dynamodb/partner-configuration-table/name"
  type  = "String"
  value = "${local.complete_stack_name}-partner-configuration-table"
}

resource "aws_ssm_parameter" "usage_records_table_name" {
  name  = "/${var.stack_name}-${var.project_name}/dynamodb/usage-records-table/name"
  type  = "String"
  value = "${local.complete_stack_name}-usage-records-table"
}

resource "aws_ssm_parameter" "usage_summaries_table_name" {
  name  = "/${var.stack_name}-${var.project_name}/dynamodb/usage-summaries-table/name"
  type  = "String"
  value = "${local.complete_stack_name}-usage-summaries-table"
}

resource "aws_ssm_parameter" "billing_plans_table_name" {
  name  = "/${var.stack_name}-${var.project_name}/dynamodb/billing-plans-table/name"
  type  = "String"
  value = "${local.complete_stack_name}-billing-plans-table"
}

#----------------------------------------------------------------------------------------------------------------
# SSM Parameters - Partner Queue ARNs (for SAM SQS event sources)
#----------------------------------------------------------------------------------------------------------------

resource "aws_ssm_parameter" "partner_create_queue_arn" {
  name  = "/${var.stack_name}-${var.project_name}/queues/partner-create/arn"
  type  = "String"
  value = module.partner_create_queue.queue_arn
}

resource "aws_ssm_parameter" "partner_config_update_queue_arn" {
  name  = "/${var.stack_name}-${var.project_name}/queues/partner-config-update/arn"
  type  = "String"
  value = module.partner_config_update_queue.queue_arn
}

#----------------------------------------------------------------------------------------------------------------
# Partner IMQ/DLQ Queues
#----------------------------------------------------------------------------------------------------------------

module "partner_create_imq" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  queue_name = "partner-create-imq"
  max_receive_count = 1
}

module "partner_create_dlq" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  queue_name = "partner-create-dlq"
  max_receive_count = 1
}

module "partner_config_update_imq" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  queue_name = "partner-config-update-imq"
  max_receive_count = 1
}

module "partner_config_update_dlq" {
  source = "./modules/tf-sqs"
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  queue_name = "partner-config-update-dlq"
  max_receive_count = 1
}

#----------------------------------------------------------------------------------------------------------------
# Kinesis
#----------------------------------------------------------------------------------------------------------------

module "verification_stream" {
  source = "./modules/tf-events"
  
  complete_stack_name = "${var.stack_name}-${var.project_name}"
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
  name  = "/${var.stack_name}-${var.project_name}/qlink/api_url"
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
  name  = "/${var.stack_name}-${var.project_name}/worldcheck/authentication_url"
  type  = "String"
  value = var.worldcheck_authentication_url
} 

resource "aws_ssm_parameter" "worldcheck_qt_api_url" {
  name  = "/${var.stack_name}-${var.project_name}/worldcheck/qt_api_url"
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
  name  = "/${var.stack_name}-${var.project_name}/worldcheck/api_base_url"
  type  = "String"
  value = var.worldcheck_api_base_url
} 

#----------------------------------------------------------------------------------------------------------------
# Lambda Functions
#----------------------------------------------------------------------------------------------------------------

module "worldcheck_lambda" {
  source = "./modules/tf-lambda"
  
  complete_stack_name = "${var.stack_name}-${var.project_name}"
  lambda_name = "worldcheck-verification"
  lambda_runtime = "java17"
  lambda_handler = "verigate.adapter.refinitiv.worldcheck.infrastructure.functions.lambda.handlers.WorldCheckVerificationLambdaHandler::handleRequest"
  lambda_timeout = 300
  lambda_memory_size = 1024
  lambda_role_arn = module.lambda_iam.role_arn
  
  environment_variables = {
    WORLDCHECK_API_BASE_URL = var.worldcheck_api_base_url
    COMMAND_STORE_TABLE_NAME = module.verification_dynamodb_commandstore.table_name
    EVENT_BUS_NAME = module.verification_stream.event_bus_name
    AWS_REGION = var.aws_region
  }
  
  # SQS Event Source
  event_source_arn = module.worldcheck_adapter_queue.queue_arn
  
  default_tags = local.default_tags
}
