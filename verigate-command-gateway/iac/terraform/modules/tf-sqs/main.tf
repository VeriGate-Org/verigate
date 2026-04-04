resource "aws_sqs_queue" "dlq" {
  name                      = "${var.complete_stack_name}-${var.queue_name}-dlq"
  message_retention_seconds = 1209600 # 14 days
}

resource "aws_sqs_queue" "this" {
  name                       = "${var.complete_stack_name}-${var.queue_name}"
  visibility_timeout_seconds = 300

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.dlq.arn
    maxReceiveCount     = var.max_receive_count
  })
}

resource "aws_sqs_queue" "imq" {
  name                       = "${var.complete_stack_name}-${var.queue_name}-imq"
  visibility_timeout_seconds = 300
}

resource "aws_ssm_parameter" "queue_arn" {
  name      = "/${var.ssm_prefix}/queues/${var.queue_name}/arn"
  type      = "String"
  value     = aws_sqs_queue.this.arn
  overwrite = true
}
