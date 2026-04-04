#----------------------------------------------------------------------------------------------------------------
# Cognito User Pool for partner authentication
#----------------------------------------------------------------------------------------------------------------

resource "aws_cognito_user_pool" "partner_pool" {
  name = "${local.complete_stack_name}-partner-pool"

  username_attributes      = ["email"]
  auto_verified_attributes = ["email"]

  password_policy {
    minimum_length    = 12
    require_lowercase = true
    require_numbers   = true
    require_symbols   = true
    require_uppercase = true
  }

  schema {
    name                = "partnerId"
    attribute_data_type = "String"
    mutable             = true

    string_attribute_constraints {
      min_length = 1
      max_length = 256
    }
  }

  schema {
    name                = "partnerName"
    attribute_data_type = "String"
    mutable             = true

    string_attribute_constraints {
      min_length = 1
      max_length = 256
    }
  }

  account_recovery_setting {
    recovery_mechanism {
      name     = "verified_email"
      priority = 1
    }
  }

  admin_create_user_config {
    allow_admin_create_user_only = true
  }

  tags = local.default_tags
}

resource "aws_cognito_user_pool_client" "partner_client" {
  name         = "${local.complete_stack_name}-partner-client"
  user_pool_id = aws_cognito_user_pool.partner_pool.id

  generate_secret                      = false
  explicit_auth_flows                  = [
    "ALLOW_USER_PASSWORD_AUTH",
    "ALLOW_REFRESH_TOKEN_AUTH",
    "ALLOW_USER_SRP_AUTH"
  ]
  supported_identity_providers         = ["COGNITO"]

  access_token_validity  = 1
  id_token_validity      = 1
  refresh_token_validity = 30

  token_validity_units {
    access_token  = "hours"
    id_token      = "hours"
    refresh_token = "days"
  }

  read_attributes  = ["custom:partnerId", "custom:partnerName", "email"]
  write_attributes = ["custom:partnerId", "custom:partnerName", "email"]
}

resource "aws_cognito_user_pool_domain" "partner_domain" {
  domain       = "${local.complete_stack_name}-${data.aws_caller_identity.current.account_id}-auth"
  user_pool_id = aws_cognito_user_pool.partner_pool.id
}

resource "aws_cognito_user_group" "admin_group" {
  name         = "admin"
  user_pool_id = aws_cognito_user_pool.partner_pool.id
  description  = "VeriGate platform administrators"
}

resource "aws_cognito_user_group" "partner_group" {
  name         = "partner"
  user_pool_id = aws_cognito_user_pool.partner_pool.id
  description  = "VeriGate partner users"
}

#----------------------------------------------------------------------------------------------------------------
# SSM Parameters for Cognito configuration
#----------------------------------------------------------------------------------------------------------------

resource "aws_ssm_parameter" "cognito_user_pool_id" {
  name  = "/${local.ssm_prefix}/cognito/user-pool-id"
  type  = "String"
  value = aws_cognito_user_pool.partner_pool.id
}

resource "aws_ssm_parameter" "cognito_client_id" {
  name  = "/${local.ssm_prefix}/cognito/client-id"
  type  = "String"
  value = aws_cognito_user_pool_client.partner_client.id
}
