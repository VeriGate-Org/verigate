locals {
  complete_stack_name = join("-", compact([
    var.stack_name,
    var.project_name,
    var.environment_shortname == "prod" ? "" : var.environment_shortname,
  ]))

  complete_stack_name_slash = join("/", compact([
    var.stack_name,
    var.project_name,
    var.environment_shortname == "prod" ? "" : var.environment_shortname,
  ]))

  environment_specific_tags = {
    accountName = "aws-verigate-${var.environment_shortname}"
    environment = "${var.environment_shortname}"
  }

  default_tags = merge(var.default_tags, local.environment_specific_tags)

  ssm_prefix = var.stack_name
}
