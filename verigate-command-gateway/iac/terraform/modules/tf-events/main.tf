resource "aws_cloudwatch_event_bus" "this" {
  name = "${var.complete_stack_name}-${var.event_bus_name}"
}

resource "aws_kinesis_stream" "this" {
  name             = "${var.complete_stack_name}-${var.event_stream_name}"
  shard_count      = 1
  retention_period = 24

  stream_mode_details {
    stream_mode = "PROVISIONED"
  }
}

resource "aws_ssm_parameter" "stream_arn" {
  name  = "/${var.complete_stack_name}/events/kinesis/stream-arn"
  type      = "String"
  value     = aws_kinesis_stream.this.arn
  overwrite = true
}

resource "aws_ssm_parameter" "stream_name" {
  name      = "/${var.complete_stack_name}/events/kinesis/stream-name"
  type      = "String"
  value     = aws_kinesis_stream.this.name
  overwrite = true
}
