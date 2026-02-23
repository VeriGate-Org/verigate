#----------------------------------------------------------------------------------------------------------------
# CloudWatch Dashboard
#----------------------------------------------------------------------------------------------------------------

resource "aws_cloudwatch_dashboard" "verification_dashboard" {
  dashboard_name = "${local.complete_stack_name}-overview"

  dashboard_body = jsonencode({
    widgets = [
      {
        type   = "metric"
        x      = 0
        y      = 0
        width  = 12
        height = 6
        properties = {
          title   = "Lambda Invocations"
          view    = "timeSeries"
          stacked = false
          region  = var.aws_region
          metrics = [
            for fn in [
              "verify-party-router",
              "verify-bank-account-details",
              "verify-personal-details",
              "verify-identity",
              "verify-company-details",
              "verify-property-ownership",
              "verify-employment",
              "screen-negative-news",
              "screen-fraud-watchlist",
              "verify-document",
              "verify-qualification",
              "perform-credit-check",
              "verify-tax-compliance",
              "verify-income"
            ] : ["AWS/Lambda", "Invocations", "FunctionName", "${local.complete_stack_name}-${fn}", { stat = "Sum" }]
          ]
          period = 300
        }
      },
      {
        type   = "metric"
        x      = 12
        y      = 0
        width  = 12
        height = 6
        properties = {
          title   = "Lambda Errors"
          view    = "timeSeries"
          stacked = true
          region  = var.aws_region
          metrics = [
            for fn in [
              "verify-party-router",
              "verify-bank-account-details",
              "verify-personal-details",
              "verify-identity",
              "verify-company-details",
              "verify-property-ownership",
              "verify-employment",
              "screen-negative-news",
              "screen-fraud-watchlist",
              "verify-document",
              "verify-qualification",
              "perform-credit-check",
              "verify-tax-compliance",
              "verify-income"
            ] : ["AWS/Lambda", "Errors", "FunctionName", "${local.complete_stack_name}-${fn}", { stat = "Sum", color = "#d62728" }]
          ]
          period = 300
        }
      },
      {
        type   = "metric"
        x      = 0
        y      = 6
        width  = 12
        height = 6
        properties = {
          title   = "Lambda Duration (p99)"
          view    = "timeSeries"
          stacked = false
          region  = var.aws_region
          metrics = [
            for fn in [
              "verify-party-router",
              "verify-bank-account-details",
              "verify-personal-details",
              "verify-identity",
              "verify-company-details",
              "verify-property-ownership",
              "verify-employment",
              "screen-negative-news",
              "screen-fraud-watchlist",
              "verify-document",
              "verify-qualification",
              "perform-credit-check",
              "verify-tax-compliance",
              "verify-income"
            ] : ["AWS/Lambda", "Duration", "FunctionName", "${local.complete_stack_name}-${fn}", { stat = "p99" }]
          ]
          period = 300
        }
      },
      {
        type   = "metric"
        x      = 12
        y      = 6
        width  = 12
        height = 6
        properties = {
          title   = "SQS Queue Depth"
          view    = "timeSeries"
          stacked = false
          region  = var.aws_region
          metrics = [
            for q in [
              "verify-party",
              "adapter-qlink",
              "adapter-worldcheck",
              "adapter-dha",
              "adapter-cipc",
              "adapter-deedsweb",
              "adapter-employment",
              "adapter-negativenews",
              "adapter-fraudwatchlist",
              "adapter-document",
              "adapter-saqa",
              "adapter-creditbureau",
              "adapter-sars",
              "adapter-income"
            ] : ["AWS/SQS", "ApproximateNumberOfMessagesVisible", "QueueName", "${local.complete_stack_name}-${q}", { stat = "Average" }]
          ]
          period = 60
        }
      },
      {
        type   = "metric"
        x      = 0
        y      = 12
        width  = 12
        height = 6
        properties = {
          title   = "DynamoDB Read/Write Capacity"
          view    = "timeSeries"
          stacked = false
          region  = var.aws_region
          metrics = [
            ["AWS/DynamoDB", "ConsumedReadCapacityUnits", "TableName", "${local.complete_stack_name}-command-store-table", { stat = "Sum" }],
            ["AWS/DynamoDB", "ConsumedWriteCapacityUnits", "TableName", "${local.complete_stack_name}-command-store-table", { stat = "Sum" }],
            ["AWS/DynamoDB", "ConsumedReadCapacityUnits", "TableName", "${local.complete_stack_name}-partner-table", { stat = "Sum" }],
            ["AWS/DynamoDB", "ConsumedWriteCapacityUnits", "TableName", "${local.complete_stack_name}-partner-table", { stat = "Sum" }]
          ]
          period = 300
        }
      },
      {
        type   = "metric"
        x      = 12
        y      = 12
        width  = 12
        height = 6
        properties = {
          title   = "Lambda Throttles & Concurrent Executions"
          view    = "timeSeries"
          stacked = false
          region  = var.aws_region
          metrics = [
            for fn in [
              "verify-party-router",
              "verify-company-details",
              "verify-bank-account-details",
              "verify-identity"
            ] : ["AWS/Lambda", "ConcurrentExecutions", "FunctionName", "${local.complete_stack_name}-${fn}", { stat = "Maximum" }]
          ]
          period = 60
        }
      }
    ]
  })
}
