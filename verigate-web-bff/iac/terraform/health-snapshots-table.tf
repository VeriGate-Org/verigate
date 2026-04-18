# ── Health Snapshots DynamoDB Table ──────────────────────────────

module "health_snapshots_table" {
  source = "../../../verigate-command-gateway/iac/terraform/modules/tf-dynamodb"

  complete_stack_name = local.name_prefix
  table_name          = "health-snapshots"
  hash_key = {
    name = "serviceId"
    type = "S"
  }
  range_key = {
    name = "checkedAt"
    type = "S"
  }
  attributes = [
    {
      name = "serviceId"
      type = "S"
    },
    {
      name = "checkedAt"
      type = "S"
    },
    {
      name = "status"
      type = "S"
    }
  ]
  global_secondary_indexes = [
    {
      name            = "status-checkedAt-index"
      hash_key        = "status"
      range_key       = "checkedAt"
      projection_type = "ALL"
    }
  ]
  ttl_attribute = "ttl"
}

resource "aws_ssm_parameter" "health_snapshots_table_name" {
  name  = "/${local.name_prefix}/tables/health-snapshots/name"
  type  = "String"
  value = module.health_snapshots_table.table_name
}
