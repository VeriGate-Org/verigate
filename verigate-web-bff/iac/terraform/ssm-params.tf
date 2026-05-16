# ── SSM Parameters for Web BFF SAM Template ──────────────────────────
# The SAM template resolves params under /${AWS::StackName}/ which expands
# to /verigate-web-bff-{env}/. This file creates all required params in
# that namespace, bridging from command-gateway where possible and using
# terraform variables for configuration values.

# ── Cognito ──────────────────────────────────────────────────────────

data "aws_ssm_parameter" "cognito_user_pool_id" {
  name = "/${var.stack_name}/cognito/user-pool-id"
}

data "aws_ssm_parameter" "cognito_client_id" {
  name = "/${var.stack_name}/cognito/client-id"
}

resource "aws_ssm_parameter" "cognito_enabled" {
  name      = "/${local.name_prefix}/cognito/enabled"
  type      = "String"
  value     = var.cognito_enabled
  overwrite = true
}

resource "aws_ssm_parameter" "cognito_region" {
  name      = "/${local.name_prefix}/cognito/region"
  type      = "String"
  value     = var.aws_region
  overwrite = true
}

resource "aws_ssm_parameter" "cognito_user_pool_id" {
  name      = "/${local.name_prefix}/cognito/user-pool-id"
  type      = "String"
  value     = data.aws_ssm_parameter.cognito_user_pool_id.value
  overwrite = true
}

resource "aws_ssm_parameter" "cognito_app_client_id" {
  name      = "/${local.name_prefix}/cognito/app-client-id"
  type      = "String"
  value     = data.aws_ssm_parameter.cognito_client_id.value
  overwrite = true
}

# ── Queues ───────────────────────────────────────────────────────────

data "aws_ssm_parameter" "verify_party_queue_arn" {
  name = "/${var.stack_name}/queues/verify-party/arn"
}

data "aws_ssm_parameter" "partner_create_queue_arn" {
  name = "/${var.stack_name}/queues/partner-create/arn"
}

resource "aws_ssm_parameter" "verify_party_queue_name" {
  name      = "/${local.name_prefix}/queues/verify-party/name"
  type      = "String"
  value     = element(split(":", data.aws_ssm_parameter.verify_party_queue_arn.value), 5)
  overwrite = true
}

resource "aws_ssm_parameter" "partner_create_queue_name" {
  name      = "/${local.name_prefix}/queues/partner-create/name"
  type      = "String"
  value     = element(split(":", data.aws_ssm_parameter.partner_create_queue_arn.value), 5)
  overwrite = true
}

# ── DynamoDB Tables ──────────────────────────────────────────────────

data "aws_ssm_parameter" "api_keys_table_name" {
  name = "/${var.stack_name}/dynamodb/api-keys-table/name"
}

resource "aws_ssm_parameter" "api_keys_table_name" {
  name      = "/${local.name_prefix}/tables/api-keys/name"
  type      = "String"
  value     = data.aws_ssm_parameter.api_keys_table_name.value
  overwrite = true
}

resource "aws_ssm_parameter" "command_store_table_name" {
  name      = "/${local.name_prefix}/tables/command-store/name"
  type      = "String"
  value     = "${var.stack_name}-command-store-table"
  overwrite = true
}

# ── Kinesis ──────────────────────────────────────────────────────────

data "aws_ssm_parameter" "kinesis_stream_name" {
  name = "/${var.stack_name}/events/kinesis/stream-name"
}

resource "aws_ssm_parameter" "kinesis_stream_name" {
  name      = "/${local.name_prefix}/kinesis/stream-name"
  type      = "String"
  value     = data.aws_ssm_parameter.kinesis_stream_name.value
  overwrite = true
}

# ── Partner Hub (bridged from command-gateway) ───────────────────────

data "aws_ssm_parameter" "partner_hub_table_name" {
  name = "/${var.stack_name}/dynamodb/partner-hub/name"
}

resource "aws_ssm_parameter" "partner_hub_table_name" {
  name      = "/${local.name_prefix}/dynamodb/partner-hub/name"
  type      = "String"
  value     = data.aws_ssm_parameter.partner_hub_table_name.value
  overwrite = true
}

# ── Health Check URLs ────────────────────────────────────────────────

resource "aws_ssm_parameter" "health_deedsweb_url" {
  name      = "/${local.name_prefix}/health/deedsweb-url"
  type      = "String"
  value     = var.health_deedsweb_url
  overwrite = true
}

resource "aws_ssm_parameter" "health_dha_url" {
  name      = "/${local.name_prefix}/health/dha-url"
  type      = "String"
  value     = var.health_dha_url
  overwrite = true
}

resource "aws_ssm_parameter" "health_worldcheck_url" {
  name      = "/${local.name_prefix}/health/worldcheck-url"
  type      = "String"
  value     = var.health_worldcheck_url
  overwrite = true
}

resource "aws_ssm_parameter" "health_experian_url" {
  name      = "/${local.name_prefix}/health/experian-url"
  type      = "String"
  value     = var.health_experian_url
  overwrite = true
}

resource "aws_ssm_parameter" "health_transunion_url" {
  name      = "/${local.name_prefix}/health/transunion-url"
  type      = "String"
  value     = var.health_transunion_url
  overwrite = true
}

resource "aws_ssm_parameter" "health_xds_url" {
  name      = "/${local.name_prefix}/health/xds-url"
  type      = "String"
  value     = var.health_xds_url
  overwrite = true
}

resource "aws_ssm_parameter" "health_cipc_url" {
  name      = "/${local.name_prefix}/health/cipc-url"
  type      = "String"
  value     = var.health_cipc_url
  overwrite = true
}
