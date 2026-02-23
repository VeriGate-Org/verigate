resource "aws_secretsmanager_secret" "this" {
  for_each = var.secrets

  name                    = "${var.prefix}/${each.key}"
  recovery_window_in_days = var.default_recovery_window_in_days
  description             = each.value.description
}

resource "aws_secretsmanager_secret_version" "this" {
  for_each = var.secrets

  secret_id     = aws_secretsmanager_secret.this[each.key].id
  secret_string = each.value.value
}
