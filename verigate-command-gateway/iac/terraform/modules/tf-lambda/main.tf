resource "aws_lambda_function" "this" {
  function_name = "${var.complete_stack_name}-${var.lambda_name}"
  role          = var.lambda_role_arn
  runtime       = var.lambda_runtime
  handler       = var.lambda_handler
  timeout       = var.lambda_timeout
  memory_size   = var.lambda_memory_size

  filename = "${path.module}/placeholder.zip"

  environment {
    variables = var.environment_variables
  }

  tags = var.default_tags

  lifecycle {
    ignore_changes = [filename]
  }
}

resource "aws_lambda_event_source_mapping" "sqs" {
  count            = var.enable_sqs_event_source ? 1 : 0
  event_source_arn = var.event_source_arn
  function_name    = aws_lambda_function.this.arn
  batch_size       = 10
}
