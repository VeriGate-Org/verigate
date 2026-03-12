#!/bin/bash
set -euo pipefail

ENDPOINT="http://localhost:4566"
REGION="eu-west-1"
AWS="awslocal"

echo "=== Creating DynamoDB tables ==="

# 1. verigate-api-keys (PK: lookupHash, GSI: partnerId-index)
$AWS dynamodb create-table \
  --table-name verigate-api-keys \
  --attribute-definitions \
    AttributeDefinition={AttributeName=lookupHash,AttributeType=S} \
    AttributeDefinition={AttributeName=partnerId,AttributeType=S} \
  --key-schema \
    KeySchemaElement={AttributeName=lookupHash,KeyType=HASH} \
  --global-secondary-indexes \
    '[{"IndexName":"partnerId-index","KeySchema":[{"AttributeName":"partnerId","KeyType":"HASH"}],"Projection":{"ProjectionType":"ALL"}}]' \
  --billing-mode PAY_PER_REQUEST \
  --region "$REGION"

# 2. verigate-partner-table (PK: partnerId, GSI: status-index)
$AWS dynamodb create-table \
  --table-name verigate-partner-table \
  --attribute-definitions \
    AttributeDefinition={AttributeName=partnerId,AttributeType=S} \
    AttributeDefinition={AttributeName=partnerStatus,AttributeType=S} \
  --key-schema \
    KeySchemaElement={AttributeName=partnerId,KeyType=HASH} \
  --global-secondary-indexes \
    '[{"IndexName":"status-index","KeySchema":[{"AttributeName":"partnerStatus","KeyType":"HASH"}],"Projection":{"ProjectionType":"ALL"}}]' \
  --billing-mode PAY_PER_REQUEST \
  --region "$REGION"

# 3. verigate-partner-data (PK: PK, SK: SK — single-table design)
$AWS dynamodb create-table \
  --table-name verigate-partner-data \
  --attribute-definitions \
    AttributeDefinition={AttributeName=PK,AttributeType=S} \
    AttributeDefinition={AttributeName=SK,AttributeType=S} \
  --key-schema \
    KeySchemaElement={AttributeName=PK,KeyType=HASH} \
    KeySchemaElement={AttributeName=SK,KeyType=RANGE} \
  --billing-mode PAY_PER_REQUEST \
  --region "$REGION"

# 4. verification-command-store (PK: commandId, GSI: partner-index on partnerId + statusCreatedAt)
$AWS dynamodb create-table \
  --table-name verification-command-store \
  --attribute-definitions \
    AttributeDefinition={AttributeName=commandId,AttributeType=S} \
    AttributeDefinition={AttributeName=partnerId,AttributeType=S} \
    AttributeDefinition={AttributeName=statusCreatedAt,AttributeType=S} \
  --key-schema \
    KeySchemaElement={AttributeName=commandId,KeyType=HASH} \
  --global-secondary-indexes \
    '[{"IndexName":"partner-index","KeySchema":[{"AttributeName":"partnerId","KeyType":"HASH"},{"AttributeName":"statusCreatedAt","KeyType":"RANGE"}],"Projection":{"ProjectionType":"ALL"}}]' \
  --billing-mode PAY_PER_REQUEST \
  --region "$REGION"

# 5. cases (PK: caseId, GSI: partner-status-index on partnerId + statusCreatedAt)
$AWS dynamodb create-table \
  --table-name cases \
  --attribute-definitions \
    AttributeDefinition={AttributeName=caseId,AttributeType=S} \
    AttributeDefinition={AttributeName=partnerId,AttributeType=S} \
    AttributeDefinition={AttributeName=statusCreatedAt,AttributeType=S} \
  --key-schema \
    KeySchemaElement={AttributeName=caseId,KeyType=HASH} \
  --global-secondary-indexes \
    '[{"IndexName":"partner-status-index","KeySchema":[{"AttributeName":"partnerId","KeyType":"HASH"},{"AttributeName":"statusCreatedAt","KeyType":"RANGE"}],"Projection":{"ProjectionType":"ALL"}}]' \
  --billing-mode PAY_PER_REQUEST \
  --region "$REGION"

# 6. policies (PK: partnerPolicyId, GSI: partner-status-index on partnerId + status)
$AWS dynamodb create-table \
  --table-name policies \
  --attribute-definitions \
    AttributeDefinition={AttributeName=partnerPolicyId,AttributeType=S} \
    AttributeDefinition={AttributeName=partnerId,AttributeType=S} \
    AttributeDefinition={AttributeName=status,AttributeType=S} \
  --key-schema \
    KeySchemaElement={AttributeName=partnerPolicyId,KeyType=HASH} \
  --global-secondary-indexes \
    '[{"IndexName":"partner-status-index","KeySchema":[{"AttributeName":"partnerId","KeyType":"HASH"},{"AttributeName":"status","KeyType":"RANGE"}],"Projection":{"ProjectionType":"ALL"}}]' \
  --billing-mode PAY_PER_REQUEST \
  --region "$REGION"

# 7. monitored-subjects (PK: subjectId, GSI: partner-status-index on partnerId + statusNextCheck)
$AWS dynamodb create-table \
  --table-name monitored-subjects \
  --attribute-definitions \
    AttributeDefinition={AttributeName=subjectId,AttributeType=S} \
    AttributeDefinition={AttributeName=partnerId,AttributeType=S} \
    AttributeDefinition={AttributeName=statusNextCheck,AttributeType=S} \
  --key-schema \
    KeySchemaElement={AttributeName=subjectId,KeyType=HASH} \
  --global-secondary-indexes \
    '[{"IndexName":"partner-status-index","KeySchema":[{"AttributeName":"partnerId","KeyType":"HASH"},{"AttributeName":"statusNextCheck","KeyType":"RANGE"}],"Projection":{"ProjectionType":"ALL"}}]' \
  --billing-mode PAY_PER_REQUEST \
  --region "$REGION"

# 8. monitoring-alerts (PK: alertId, GSI: partner-subject-index on partnerId + subjectIdCreatedAt)
$AWS dynamodb create-table \
  --table-name monitoring-alerts \
  --attribute-definitions \
    AttributeDefinition={AttributeName=alertId,AttributeType=S} \
    AttributeDefinition={AttributeName=partnerId,AttributeType=S} \
    AttributeDefinition={AttributeName=subjectIdCreatedAt,AttributeType=S} \
  --key-schema \
    KeySchemaElement={AttributeName=alertId,KeyType=HASH} \
  --global-secondary-indexes \
    '[{"IndexName":"partner-subject-index","KeySchema":[{"AttributeName":"partnerId","KeyType":"HASH"},{"AttributeName":"subjectIdCreatedAt","KeyType":"RANGE"}],"Projection":{"ProjectionType":"ALL"}}]' \
  --billing-mode PAY_PER_REQUEST \
  --region "$REGION"

# 9. risk-assessments (PK: verificationId — no GSI needed by BFF code)
$AWS dynamodb create-table \
  --table-name risk-assessments \
  --attribute-definitions \
    AttributeDefinition={AttributeName=verificationId,AttributeType=S} \
  --key-schema \
    KeySchemaElement={AttributeName=verificationId,KeyType=HASH} \
  --billing-mode PAY_PER_REQUEST \
  --region "$REGION"

# 10. risk-scoring-config (PK: partnerId — no GSI needed)
$AWS dynamodb create-table \
  --table-name risk-scoring-config \
  --attribute-definitions \
    AttributeDefinition={AttributeName=partnerId,AttributeType=S} \
  --key-schema \
    KeySchemaElement={AttributeName=partnerId,KeyType=HASH} \
  --billing-mode PAY_PER_REQUEST \
  --region "$REGION"

echo "=== Creating SQS queues ==="

$AWS sqs create-queue --queue-name verify-party --region "$REGION"
$AWS sqs create-queue --queue-name partner-create --region "$REGION"

echo "=== Creating S3 bucket ==="

$AWS s3 mb "s3://verigate-documents" --region "$REGION"

$AWS s3api put-bucket-cors \
  --bucket verigate-documents \
  --cors-configuration '{
    "CORSRules": [{
      "AllowedOrigins": ["http://localhost:3000"],
      "AllowedMethods": ["GET", "PUT", "POST"],
      "AllowedHeaders": ["*"],
      "MaxAgeSeconds": 3600
    }]
  }' \
  --region "$REGION"

echo "=== LocalStack init complete ==="
$AWS dynamodb list-tables --region "$REGION"
