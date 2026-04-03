# Command Gateway Environment Variables

This document describes all environment variables used by the VeriGate Command Gateway SAM deployment.

## SAM Template Parameters

These parameters are passed to the SAM template during deployment:

| Parameter | Description | Default |
|-----------|-------------|---------|
| `ApplicationVersion` | The latest version tag for the application | (required) |
| `EnvironmentShortname` | Environment shortname (dev, prod) | (required) |
| `VerigateDomainName` | The Verigate system domain name | `{{resolve:ssm:/verigate/api-gateway/domain-name:1}}` |
| `KinesisStreamArn` | The kinesis stream ARN for events | `{{resolve:ssm:/verigate-verification-cg/events/kinesis/stream-arn:1}}` |

## Global Lambda Environment Variables

These environment variables are set for all Lambda functions via the SAM Globals section:

### Datadog Configuration

| Variable | Description | Value |
|----------|-------------|-------|
| `DD_ENV` | Datadog environment tag | `sds-${EnvironmentShortname}` |
| `DD_SERVICE` | Datadog service name | `verigate` |
| `DD_VERSION` | Application version | `${ApplicationVersion}` |
| `DD_SERVERLESS_APPSEC_ENABLED` | Enable Datadog AppSec | `false` (dev), `true` (prod) |
| `DD_SERVERLESS_LOGS_ENABLED` | Enable Datadog log forwarding | `false` (dev), `true` (prod) |
| `DD_TRACE_ENABLED` | Enable Datadog tracing | `false` (dev), `true` (prod) |
| `DD_TAGS` | Datadog custom tags | `stack_parent:${AWS::StackName}` |
| `DD_FLUSH_TO_LOG` | Flush Datadog metrics to logs | `false` (dev), `true` (prod) |
| `DD_ENHANCED_METRICS` | Enable enhanced metrics | `false` (dev), `true` (prod) |

### DynamoDB Configuration

| Variable | Description | Value |
|----------|-------------|-------|
| `VERIFICATION_COMMAND_STORE_DB` | Command store table name | `${AWS::StackName}-command-store-table` |

### Kinesis Configuration

| Variable | Description | Value |
|----------|-------------|-------|
| `EVENT_STREAM_NAME` | Kinesis event stream name | `{{resolve:ssm:/${AWS::StackName}/events/kinesis/stream-name}}` |

### SQS Queue Names - Verification Adapters

| Variable | Description | Value |
|----------|-------------|-------|
| `WORLDCHECK_ADAPTER_QUEUE_NAME` | WorldCheck adapter queue | `${AWS::StackName}-adapter-worldcheck` |
| `QLINK_ADAPTER_QUEUE_NAME` | QLink bank verification queue | `${AWS::StackName}-adapter-qlink` |
| `OPENSANCTIONS_ADAPTER_QUEUE_NAME` | OpenSanctions screening queue | `${AWS::StackName}-adapter-opensanctions` |
| `DHA_ADAPTER_QUEUE_NAME` | DHA identity verification queue | `${AWS::StackName}-adapter-dha` |
| `CIPC_ADAPTER_QUEUE_NAME` | CIPC company verification queue | `${AWS::StackName}-adapter-cipc` |
| `DEEDSWEB_ADAPTER_QUEUE_NAME` | DeedsWeb property verification queue | `${AWS::StackName}-adapter-deedsweb` |
| `EMPLOYMENT_ADAPTER_QUEUE_NAME` | Employment verification queue | `${AWS::StackName}-adapter-employment` |
| `NEGATIVENEWS_ADAPTER_QUEUE_NAME` | Negative news screening queue | `${AWS::StackName}-adapter-negativenews` |
| `FRAUDWATCHLIST_ADAPTER_QUEUE_NAME` | Fraud watchlist screening queue | `${AWS::StackName}-adapter-fraudwatchlist` |
| `DOCUMENT_ADAPTER_QUEUE_NAME` | Document verification queue | `${AWS::StackName}-adapter-document` |
| `SAQA_ADAPTER_QUEUE_NAME` | SAQA qualification verification queue | `${AWS::StackName}-adapter-saqa` |
| `CREDITBUREAU_ADAPTER_QUEUE_NAME` | Credit bureau check queue | `${AWS::StackName}-adapter-creditbureau` |
| `SARS_ADAPTER_QUEUE_NAME` | SARS tax compliance queue | `${AWS::StackName}-adapter-sars` |
| `INCOME_ADAPTER_QUEUE_NAME` | Income verification queue | `${AWS::StackName}-adapter-income` |

## Per-Adapter Lambda Environment Variables

Each adapter Lambda function has additional environment variables specific to its operation:

### Verify Party Router (Command Gateway)

| Variable | Description |
|----------|-------------|
| `VERIFY_PARTY_IMQ_NAME` | In-flight message queue name |
| `VERIFY_PARTY_DLQ_NAME` | Dead letter queue name |

### QLink Adapter (Bank Account Verification)

| Variable | Description | Source |
|----------|-------------|--------|
| `VERIFY_BANK_ACCOUNT_DETAILS_IMQ_NAME` | In-flight message queue | `${AWS::StackName}-adapter-qlink-imq` |
| `VERIFY_BANK_ACCOUNT_DETAILS_DLQ_NAME` | Dead letter queue | `${AWS::StackName}-adapter-qlink-dlq` |
| `QLINK_API_URL` | QLink API endpoint | `{{resolve:ssm:/verigate-verification-cg/qlink/api_url}}` |

### DHA Adapter (Identity Verification)

| Variable | Description | Source |
|----------|-------------|--------|
| `VERIFY_IDENTITY_IMQ_NAME` | In-flight message queue | `${AWS::StackName}-adapter-dha-imq` |
| `VERIFY_IDENTITY_DLQ_NAME` | Dead letter queue | `${AWS::StackName}-adapter-dha-dlq` |
| `DHA_API_URL` | DHA API endpoint | `{{resolve:ssm:/verigate-verification-cg/dha/api_url}}` |

### CIPC Adapter (Company Verification)

| Variable | Description | Source |
|----------|-------------|--------|
| `VERIFY_COMPANY_DETAILS_IMQ_NAME` | In-flight message queue | `${AWS::StackName}-adapter-cipc-imq` |
| `VERIFY_COMPANY_DETAILS_DLQ_NAME` | Dead letter queue | `${AWS::StackName}-adapter-cipc-dlq` |
| `CIPC_API_KEY` | CIPC API key | `{{resolve:ssm:/verigate-verification-cg/cipc/api_key}}` |

### DeedsWeb Adapter (Property Ownership)

| Variable | Description | Source |
|----------|-------------|--------|
| `VERIFY_PROPERTY_OWNERSHIP_IMQ_NAME` | In-flight message queue | `${AWS::StackName}-adapter-deedsweb-imq` |
| `VERIFY_PROPERTY_OWNERSHIP_DLQ_NAME` | Dead letter queue | `${AWS::StackName}-adapter-deedsweb-dlq` |
| `DEEDSWEB_API_URL` | DeedsWeb API endpoint | `{{resolve:ssm:/verigate-verification-cg/deedsweb/api_url}}` |

### Employment Adapter

| Variable | Description | Source |
|----------|-------------|--------|
| `VERIFY_EMPLOYMENT_IMQ_NAME` | In-flight message queue | `${AWS::StackName}-adapter-employment-imq` |
| `VERIFY_EMPLOYMENT_DLQ_NAME` | Dead letter queue | `${AWS::StackName}-adapter-employment-dlq` |
| `EMPLOYMENT_API_URL` | Employment verification API endpoint | `{{resolve:ssm:/verigate-verification-cg/employment/api_url}}` |

### Negative News Adapter

| Variable | Description | Source |
|----------|-------------|--------|
| `SCREEN_NEGATIVE_NEWS_IMQ_NAME` | In-flight message queue | `${AWS::StackName}-adapter-negativenews-imq` |
| `SCREEN_NEGATIVE_NEWS_DLQ_NAME` | Dead letter queue | `${AWS::StackName}-adapter-negativenews-dlq` |
| `NEGATIVENEWS_API_URL` | Negative news API endpoint | `{{resolve:ssm:/verigate-verification-cg/negativenews/api_url}}` |

### Fraud Watchlist Adapter

| Variable | Description | Source |
|----------|-------------|--------|
| `SCREEN_FRAUD_WATCHLIST_IMQ_NAME` | In-flight message queue | `${AWS::StackName}-adapter-fraudwatchlist-imq` |
| `SCREEN_FRAUD_WATCHLIST_DLQ_NAME` | Dead letter queue | `${AWS::StackName}-adapter-fraudwatchlist-dlq` |
| `FRAUDWATCHLIST_API_URL` | Fraud watchlist API endpoint | `{{resolve:ssm:/verigate-verification-cg/fraudwatchlist/api_url}}` |

### Document Adapter

| Variable | Description | Source |
|----------|-------------|--------|
| `VERIFY_DOCUMENT_IMQ_NAME` | In-flight message queue | `${AWS::StackName}-adapter-document-imq` |
| `VERIFY_DOCUMENT_DLQ_NAME` | Dead letter queue | `${AWS::StackName}-adapter-document-dlq` |
| `DOCUMENT_API_URL` | Document verification API endpoint | `{{resolve:ssm:/verigate-verification-cg/document/api_url}}` |
| `DOCUMENT_S3_BUCKET` | S3 bucket for document storage | `${AWS::StackName}-documents` |

### SAQA Adapter (Qualification Verification)

| Variable | Description | Source |
|----------|-------------|--------|
| `VERIFY_QUALIFICATION_IMQ_NAME` | In-flight message queue | `${AWS::StackName}-adapter-saqa-imq` |
| `VERIFY_QUALIFICATION_DLQ_NAME` | Dead letter queue | `${AWS::StackName}-adapter-saqa-dlq` |
| `SAQA_API_URL` | SAQA API endpoint | `{{resolve:ssm:/verigate-verification-cg/saqa/api_url}}` |

### Credit Bureau Adapter

| Variable | Description | Source |
|----------|-------------|--------|
| `PERFORM_CREDIT_CHECK_IMQ_NAME` | In-flight message queue | `${AWS::StackName}-adapter-creditbureau-imq` |
| `PERFORM_CREDIT_CHECK_DLQ_NAME` | Dead letter queue | `${AWS::StackName}-adapter-creditbureau-dlq` |
| `CREDITBUREAU_API_URL` | Credit bureau API endpoint | `{{resolve:ssm:/verigate-verification-cg/creditbureau/api_url}}` |

### SARS Adapter (Tax Compliance)

| Variable | Description | Source |
|----------|-------------|--------|
| `VERIFY_TAX_COMPLIANCE_IMQ_NAME` | In-flight message queue | `${AWS::StackName}-adapter-sars-imq` |
| `VERIFY_TAX_COMPLIANCE_DLQ_NAME` | Dead letter queue | `${AWS::StackName}-adapter-sars-dlq` |
| `SARS_API_URL` | SARS API endpoint | `{{resolve:ssm:/verigate-verification-cg/sars/api_url}}` |

### Income Adapter

| Variable | Description | Source |
|----------|-------------|--------|
| `VERIFY_INCOME_IMQ_NAME` | In-flight message queue | `${AWS::StackName}-adapter-income-imq` |
| `VERIFY_INCOME_DLQ_NAME` | Dead letter queue | `${AWS::StackName}-adapter-income-dlq` |
| `INCOME_API_URL` | Income verification API endpoint | `{{resolve:ssm:/verigate-verification-cg/income/api_url}}` |

## Partner Service Lambda Environment Variables

### Create Partner Handler

| Variable | Description | Source |
|----------|-------------|--------|
| `CREATE_PARTNER_IMQ_NAME` | In-flight message queue | `${AWS::StackName}-partner-create-imq` |
| `CREATE_PARTNER_DLQ_NAME` | Dead letter queue | `${AWS::StackName}-partner-create-dlq` |
| `PARTNER_TABLE_NAME` | Partner table | `${AWS::StackName}-partner-table` |
| `PARTNER_CONFIGURATION_TABLE_NAME` | Partner configuration table | `${AWS::StackName}-partner-configuration-table` |

### Update Partner Config Handler

| Variable | Description | Source |
|----------|-------------|--------|
| `UPDATE_PARTNER_CONFIG_IMQ_NAME` | In-flight message queue | `${AWS::StackName}-partner-config-update-imq` |
| `UPDATE_PARTNER_CONFIG_DLQ_NAME` | Dead letter queue | `${AWS::StackName}-partner-config-update-dlq` |
| `PARTNER_TABLE_NAME` | Partner table | `${AWS::StackName}-partner-table` |
| `PARTNER_CONFIGURATION_TABLE_NAME` | Partner configuration table | `${AWS::StackName}-partner-configuration-table` |

## Billing Service Lambda Environment Variables

### Usage Event Consumer Handler

| Variable | Description | Source |
|----------|-------------|--------|
| `USAGE_RECORDS_TABLE_NAME` | Usage records table | `${AWS::StackName}-usage-records-table` |
| `USAGE_SUMMARIES_TABLE_NAME` | Usage summaries table | `${AWS::StackName}-usage-summaries-table` |
| `BILLING_PLANS_TABLE_NAME` | Billing plans table | `${AWS::StackName}-billing-plans-table` |

### Usage Aggregator Handler

| Variable | Description | Source |
|----------|-------------|--------|
| `USAGE_RECORDS_TABLE_NAME` | Usage records table | `${AWS::StackName}-usage-records-table` |
| `USAGE_SUMMARIES_TABLE_NAME` | Usage summaries table | `${AWS::StackName}-usage-summaries-table` |
| `BILLING_PLANS_TABLE_NAME` | Billing plans table | `${AWS::StackName}-billing-plans-table` |

## IAM Roles

Lambda functions use environment-specific IAM roles:

| Environment | Role ARN Source |
|-------------|-----------------|
| All | `{{resolve:ssm:/application/iam-role/verigate-verification-cg-${EnvironmentShortname}/arn}}` |

## VPC Configuration

All Lambda functions are deployed in VPC with the following configuration:

| Configuration | Value |
|---------------|-------|
| Security Group | `{{resolve:ssm:/platform/security_groups/verigate_verigate_verification}}` |
| Subnets | `{{resolve:ssm:/platform/privateSubnet-1}}`, `{{resolve:ssm:/platform/privateSubnet-2}}`, `{{resolve:ssm:/platform/privateSubnet-3}}` |

## Secrets and SSM Parameters

The following secrets and parameters must be configured in AWS Systems Manager Parameter Store per environment:

### API Endpoints (SSM Parameters)

| Parameter | Description |
|-----------|-------------|
| `/verigate-verification-cg/qlink/api_url` | QLink API endpoint |
| `/verigate-verification-cg/dha/api_url` | DHA API endpoint |
| `/verigate-verification-cg/deedsweb/api_url` | DeedsWeb API endpoint |
| `/verigate-verification-cg/employment/api_url` | Employment verification API endpoint |
| `/verigate-verification-cg/negativenews/api_url` | Negative news API endpoint |
| `/verigate-verification-cg/fraudwatchlist/api_url` | Fraud watchlist API endpoint |
| `/verigate-verification-cg/document/api_url` | Document verification API endpoint |
| `/verigate-verification-cg/saqa/api_url` | SAQA API endpoint |
| `/verigate-verification-cg/creditbureau/api_url` | Credit bureau API endpoint |
| `/verigate-verification-cg/sars/api_url` | SARS API endpoint |
| `/verigate-verification-cg/income/api_url` | Income verification API endpoint |

### API Keys (SSM Secure String Parameters)

| Parameter | Description |
|-----------|-------------|
| `/verigate-verification-cg/cipc/api_key` | CIPC API key |

### Infrastructure Parameters

| Parameter | Description |
|-----------|-------------|
| `/verigate/api-gateway/domain-name` | API Gateway domain name |
| `/verigate-verification-cg/events/kinesis/stream-arn` | Kinesis event stream ARN |
| `/verigate-verification-cg/events/kinesis/stream-name` | Kinesis event stream name |
| `/${AWS::StackName}/queues/verify-party/arn` | Verify party queue ARN |
| `/${AWS::StackName}/queues/adapter-*/arn` | Adapter queue ARNs (per adapter) |
| `/${AWS::StackName}/queues/partner-create/arn` | Partner creation queue ARN |
| `/${AWS::StackName}/queues/partner-config-update/arn` | Partner config update queue ARN |

## Local Development

For local development using LocalStack, override the following environment variables:

```bash
# Override AWS endpoints for local development
export AWS_SQS_ENDPOINT=http://localhost:4566
export AWS_DYNAMODB_ENDPOINT=http://localhost:4566
export AWS_KINESIS_ENDPOINT=http://localhost:4566

# Use local AWS region
export AWS_REGION=eu-west-1

# Disable Datadog integration
export DD_TRACE_ENABLED=false
export DD_SERVERLESS_LOGS_ENABLED=false
```

## Deployment Configuration

During SAM deployment, these environment-specific values are typically configured:

| Environment | EnvironmentShortname | StackName |
|-------------|---------------------|-----------|
| Dev | `dev` | `verigate-verification-cg-dev` |
| Prod | `prod` | `verigate-verification-cg` |
