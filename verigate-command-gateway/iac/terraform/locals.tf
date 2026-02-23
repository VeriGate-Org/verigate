locals {
  complete_stack_name = join("-", compact([
    var.stack_name,
    var.project_name,
    var.environment_shortname == "prd" ? "" : var.environment_shortname,
    var.stack_version,
    var.environment_shortname == "sbx" ? var.developer_stack_name : ""
  ]))

  complete_stack_name_slash = join("/", compact([
    var.stack_name,
    var.project_name,
    var.environment_shortname == "prd" ? "" : var.environment_shortname,
    var.stack_version,
    var.environment_shortname == "sbx" ? var.developer_stack_name : ""
  ]))

  # Marked for future use
  # full_stack_name_no_space = join("", compact([
  #   var.stack_name,
  #   var.project_name,
  #   var.environment_shortname == "prd" ? "" : var.environment_shortname,
  #   var.stack_version,
  #   var.environment_shortname == "sbx" ? var.developer_stack_name : ""
  # ]))

  environment_specific_tags = {
    accountName = "aws-verigate-${var.environment_shortname}"
    environment = "${var.environment_shortname}"
  }

  default_tags = merge(var.default_tags, local.environment_specific_tags)
}

