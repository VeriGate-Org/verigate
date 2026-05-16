locals {
  name_prefix = "${var.stack_name}-web-bff-${var.environment_shortname}"
}

data "aws_caller_identity" "current" {}

data "aws_ssm_parameter" "vpc_id" {
  name = "/platform/vpcid"
}

data "aws_ssm_parameter" "private_subnet_1" {
  name = "/platform/privateSubnet-1"
}

data "aws_ssm_parameter" "private_subnet_2" {
  name = "/platform/privateSubnet-2"
}

# IAM Role for Lambda execution
data "aws_iam_policy_document" "lambda_assume_role" {
  statement {
    effect  = "Allow"
    actions = ["sts:AssumeRole"]
    principals {
      type        = "Service"
      identifiers = ["lambda.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "lambda" {
  name               = "${local.name_prefix}-lambda"
  assume_role_policy = data.aws_iam_policy_document.lambda_assume_role.json
}

resource "aws_iam_role_policy_attachment" "lambda_basic" {
  role       = aws_iam_role.lambda.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_iam_role_policy_attachment" "lambda_vpc" {
  role       = aws_iam_role.lambda.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
}

resource "aws_iam_role_policy" "lambda" {
  name = "${local.name_prefix}-lambda-policy"
  role = aws_iam_role.lambda.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "sqs:SendMessage",
          "sqs:GetQueueUrl",
          "dynamodb:GetItem",
          "dynamodb:PutItem",
          "dynamodb:Query",
          "dynamodb:Scan",
          "ssm:GetParameter",
          "ssm:GetParameters",
          "ssm:GetParametersByPath",
          "s3:GetObject",
          "s3:PutObject",
          "s3:ListBucket",
          "ses:SendRawEmail",
          "ses:SendEmail",
          "cognito-idp:*"
        ]
        Resource = "*"
      },
      {
        Effect = "Allow"
        Action = [
          "bedrock:InvokeModel",
          "bedrock:InvokeModelWithResponseStream"
        ]
        Resource = "arn:aws:bedrock:us-east-1::foundation-model/us.anthropic.claude-sonnet-4-5-*"
      }
    ]
  })
}

# SSM Parameter to store the IAM role ARN for SAM template resolution
resource "aws_ssm_parameter" "lambda_role_arn" {
  name  = "/application/iam-role/${local.name_prefix}/arn"
  type  = "String"
  value = aws_iam_role.lambda.arn
}

# CloudWatch Log Group for Lambda
resource "aws_cloudwatch_log_group" "this" {
  name              = "/aws/lambda/${local.name_prefix}-api"
  retention_in_days = 14
}
