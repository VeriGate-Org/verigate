# ── API Gateway Custom Domain ────────────────────────────────────

resource "aws_apigatewayv2_domain_name" "api" {
  count       = var.enable_custom_domain ? 1 : 0
  domain_name = var.api_domain

  domain_name_configuration {
    certificate_arn = var.acm_certificate_arn
    endpoint_type   = "REGIONAL"
    security_policy = "TLS_1_2"
  }
}

# SSM parameter so SAM can look up the domain name at deploy time
resource "aws_ssm_parameter" "api_domain_name" {
  count = var.enable_custom_domain ? 1 : 0
  name  = "/verigate/api-gateway/domain-name"
  type  = "String"
  value = aws_apigatewayv2_domain_name.api[0].domain_name
}
