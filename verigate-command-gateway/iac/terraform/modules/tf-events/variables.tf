variable "complete_stack_name" {
  description = "Complete stack name prefix for resource naming"
  type        = string
}

variable "event_bus_name" {
  description = "Name of the EventBridge event bus"
  type        = string
}

variable "event_stream_name" {
  description = "Name of the Kinesis event stream"
  type        = string
}
