# ── Partner Hub table name (owned by command-gateway) ────────────
# The partner-hub DynamoDB table is created by command-gateway Terraform,
# which publishes its name to /${stack_name}/dynamodb/partner-hub/name.
# The web-bff SAM template resolves SSM params under /${AWS::StackName}/
# (i.e. /verigate-web-bff-{env}/), so we bridge the two prefixes here.

data "aws_ssm_parameter" "partner_hub_table_name" {
  name = "/${var.stack_name}/dynamodb/partner-hub/name"
}

resource "aws_ssm_parameter" "partner_hub_table_name" {
  name  = "/${local.name_prefix}/dynamodb/partner-hub/name"
  type  = "String"
  value = data.aws_ssm_parameter.partner_hub_table_name.value
}
