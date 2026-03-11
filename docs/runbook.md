# VeriGate Operational Runbook

## Architecture Overview

VeriGate is a serverless verification platform deployed on AWS, consisting of:
- **Web BFF**: Spring Boot application (ECS/Fargate) handling API requests
- **Command Gateway**: 15 Lambda functions processing verification commands via SQS
- **DynamoDB**: Command store, partner configuration, API keys, billing tables
- **Kinesis**: Event stream for domain events
- **Datadog**: APM, logging, and monitoring (production only)

## Environments

| Environment | Stack Name | Account | Deploy Trigger |
|-------------|-----------|---------|----------------|
| Sandbox | verigate-verification-cg-sbx | aws-verigate-sbx | Push to main |
| Development | verigate-verification-cg-dev | aws-verigate-dev | After sandbox |
| Preproduction | verigate-verification-cg-ppe | aws-verigate-ppe | Push to verigate-release |
| Production | verigate-verification-cg | aws-verigate-prd | After preproduction |

## Common Operational Tasks

### Checking Lambda Health

```bash
# List all Lambda functions in a stack
aws lambda list-functions --query "Functions[?starts_with(FunctionName, 'verigate-verification-cg')].{Name:FunctionName,State:State,LastModified:LastModified}" --output table

# Check specific function configuration
aws lambda get-function --function-name verigate-verification-cg-verify-party-router
```

### Checking SQS Queue Depth

```bash
# Get queue attributes (messages available, in-flight, delayed)
aws sqs get-queue-attributes \
  --queue-url $(aws sqs get-queue-url --queue-name verigate-verification-cg-verify-party --query QueueUrl --output text) \
  --attribute-names ApproximateNumberOfMessages ApproximateNumberOfMessagesNotVisible ApproximateNumberOfMessagesDelayed
```

### Checking DLQ Messages

```bash
# Count messages in DLQ
aws sqs get-queue-attributes \
  --queue-url $(aws sqs get-queue-url --queue-name verigate-verification-cg-verify-party-dlq --query QueueUrl --output text) \
  --attribute-names ApproximateNumberOfMessages

# Peek at DLQ messages (does not delete them)
aws sqs receive-message \
  --queue-url $(aws sqs get-queue-url --queue-name verigate-verification-cg-verify-party-dlq --query QueueUrl --output text) \
  --max-number-of-messages 5 \
  --visibility-timeout 0
```

### Viewing CloudWatch Logs

```bash
# Tail logs for a specific function
aws logs tail /aws/lambda/verigate-verification-cg-verify-party-router --follow

# Search logs for a specific verificationId
aws logs filter-log-events \
  --log-group-name /aws/lambda/verigate-verification-cg-verify-party-router \
  --filter-pattern "verificationId=abc-123"

# Search logs for errors
aws logs filter-log-events \
  --log-group-name /aws/lambda/verigate-verification-cg-verify-party-router \
  --filter-pattern "ERROR"
```

### Querying DynamoDB

```bash
# Look up a command by ID
aws dynamodb get-item \
  --table-name verigate-verification-cg-command-store-table \
  --key '{"commandId": {"S": "your-command-id"}}'

# Query commands by partner (sorted by status#createdAt)
aws dynamodb query \
  --table-name verigate-verification-cg-command-store-table \
  --index-name partner-index \
  --key-condition-expression "partnerId = :pid" \
  --expression-attribute-values '{":pid": {"S": "partner-001"}}' \
  --limit 10

# Query commands by partner filtered by status
aws dynamodb query \
  --table-name verigate-verification-cg-command-store-table \
  --index-name partner-index \
  --key-condition-expression "partnerId = :pid AND begins_with(statusCreatedAt, :status)" \
  --expression-attribute-values '{":pid": {"S": "partner-001"}, ":status": {"S": "COMPLETED#"}}' \
  --limit 10
```

## Incident Response

### High Error Rate Alert

**Symptoms**: CloudWatch alarm `VerificationErrorAlarm` fires, Datadog alert.

**Investigation**:
1. Check CloudWatch Logs for the affected function
2. Look for patterns: specific partner, verification type, or time window
3. Check external API health (CIPC, DHA, QLink etc.)
4. Check SQS DLQ for failed messages

**Remediation**:
- If external API is down: wait for recovery (messages will retry from SQS)
- If serialization error: check for schema changes, redeploy if needed
- If DynamoDB throttling: check consumed capacity, increase if needed

### SQS DLQ Accumulating Messages

**Symptoms**: `SqsDeadLetterQueueAlarm` fires.

**Investigation**:
1. Read DLQ messages to identify the failure pattern
2. Check Lambda CloudWatch logs for the corresponding errors
3. Determine if messages are malformed or if processing failed

**Remediation**:
- Malformed messages: identify the source, fix upstream
- Processing failures: fix the bug, then redrive DLQ messages:
  ```bash
  # Start DLQ redrive (moves messages back to source queue)
  aws sqs start-message-move-task \
    --source-arn arn:aws:sqs:eu-west-1:ACCOUNT:verigate-verification-cg-verify-party-dlq \
    --destination-arn arn:aws:sqs:eu-west-1:ACCOUNT:verigate-verification-cg-verify-party
  ```

### Lambda Cold Start Issues

**Symptoms**: High p99 latency, timeouts on first invocations.

**Investigation**:
1. Check if SnapStart is enabled (`aws lambda get-function-configuration`)
2. Review INIT_REPORT in CloudWatch Logs
3. Check memory allocation vs actual usage

**Remediation**:
- Ensure SnapStart is enabled (should be by default via SAM template)
- Consider increasing memory allocation for heavy initializers
- The scheduled EventBridge "keep warm" events (every 5 min) should prevent most cold starts

### Partner Authentication Failures

**Symptoms**: 401/403 errors from BFF.

**Investigation**:
1. Check if the API key exists in DynamoDB api-keys-table
2. Verify key status is ACTIVE
3. If JWT auth: verify Cognito User Pool configuration
4. Check rate limiting (429 vs 401)

**Remediation**:
- Regenerate API key if compromised
- Check partner status in partner-table
- Adjust rate limits in BFF application.yaml if needed

## Secrets Management

### API Key Rotation

API keys should be rotated periodically or immediately if compromised. The web-bff supports zero-downtime key rotation.

**Rotation Process**:

1. Generate a new API key for the partner:
   ```bash
   # Via BFF API (requires admin credentials)
   curl -X POST https://api.verigate.com/api/admin/partners/PARTNER_ID/api-keys \
     -H "Authorization: Bearer $ADMIN_TOKEN"
   ```

2. The response contains the new raw API key (shown only once):
   ```json
   {
     "rawApiKey": "vg_abc123...",
     "partnerId": "partner-001",
     "keyPrefix": "vg_abc12",
     "status": "ACTIVE",
     "createdAt": "2025-01-15T10:30:00"
   }
   ```

3. Provide the new key to the partner via secure channel (encrypted email, vault, etc.)

4. Verify the new key works before revoking the old one:
   ```bash
   curl -X POST https://api.verigate.com/api/verifications \
     -H "X-API-Key: vg_abc123..." \
     -H "Content-Type: application/json" \
     -d '{"verificationType": "ID_VERIFICATION", ...}'
   ```

5. Once confirmed, revoke the old key:
   ```bash
   curl -X DELETE https://api.verigate.com/api/admin/partners/PARTNER_ID/api-keys/OLD_PREFIX \
     -H "Authorization: Bearer $ADMIN_TOKEN"
   ```

6. Verify the old key no longer works (should return 401).

**Emergency Revocation** (if key is compromised):

```bash
# Immediately revoke without rotation
curl -X DELETE https://api.verigate.com/api/admin/partners/PARTNER_ID/api-keys/KEY_PREFIX \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

**Security Notes**:
- API keys use salted SHA-256 hashing with constant-time comparison to prevent rainbow table and timing attacks
- Raw keys are never stored; only lookup hash (unsalted) and verification hash (salted) are persisted
- Keys should be rotated every 90 days as a security best practice
- Monitor CloudWatch Logs for failed authentication attempts (potential key compromise indicators)

### Adapter Secrets Rotation (AWS Secrets Manager)

External adapter credentials (CIPC, DHA, QLink, etc.) are stored in AWS Secrets Manager and should be rotated when provider credentials change or every 180 days.

**List all adapter secrets**:
```bash
aws secretsmanager list-secrets \
  --filters Key=name,Values=verigate-adapter \
  --query 'SecretList[].{Name:Name,LastChanged:LastChangedDate}' \
  --output table
```

**Rotate a secret** (e.g., CIPC adapter credentials):

1. Obtain new credentials from the external provider

2. Update the secret value:
   ```bash
   aws secretsmanager update-secret \
     --secret-id verigate-adapter-cipc-credentials \
     --secret-string '{
       "username": "new_username",
       "password": "new_password",
       "apiKey": "new_api_key"
     }'
   ```

3. Verify the Lambda functions pick up the new secret (they cache for up to 5 minutes):
   ```bash
   # Trigger a test verification to force secret refresh
   # Monitor CloudWatch Logs for the adapter Lambda
   aws logs tail /aws/lambda/verigate-verification-cg-verify-company-details --follow
   ```

4. If immediate refresh is required, restart the Lambda by updating environment variable:
   ```bash
   aws lambda update-function-configuration \
     --function-name verigate-verification-cg-verify-company-details \
     --environment Variables={FORCE_REFRESH=$(date +%s)}
   ```

**Enable automatic rotation** (requires rotation Lambda):
```bash
aws secretsmanager rotate-secret \
  --secret-id verigate-adapter-cipc-credentials \
  --rotation-lambda-arn arn:aws:lambda:REGION:ACCOUNT:function:SecretsManagerRotation \
  --rotation-rules AutomaticallyAfterDays=180
```

**Rollback if new credentials fail**:
```bash
# List secret versions
aws secretsmanager list-secret-version-ids \
  --secret-id verigate-adapter-cipc-credentials

# Restore previous version
aws secretsmanager update-secret-version-stage \
  --secret-id verigate-adapter-cipc-credentials \
  --version-stage AWSCURRENT \
  --move-to-version-id PREVIOUS_VERSION_ID \
  --remove-from-version-id CURRENT_VERSION_ID
```

**Audit secret access**:
```bash
# Check CloudTrail for secret access events
aws cloudtrail lookup-events \
  --lookup-attributes AttributeKey=ResourceName,AttributeValue=verigate-adapter-cipc-credentials \
  --max-results 50 \
  --query 'Events[].{Time:EventTime,User:Username,Action:EventName}' \
  --output table
```

## Deployment

### Rolling Back a Deployment

SAM uses Lambda aliases with versioning. To rollback:

```bash
# Find the previous version
aws lambda list-versions-by-function \
  --function-name verigate-verification-cg-verify-party-router \
  --query 'Versions[-2].Version'

# Update alias to previous version
aws lambda update-alias \
  --function-name verigate-verification-cg-verify-party-router \
  --name live \
  --function-version PREVIOUS_VERSION
```

### Emergency: Disable a Verification Type

Set reserved concurrency to 0 to stop processing:

```bash
aws lambda put-function-concurrency \
  --function-name verigate-verification-cg-verify-company-details \
  --reserved-concurrent-executions 0
```

To re-enable:
```bash
aws lambda delete-function-concurrency \
  --function-name verigate-verification-cg-verify-company-details
```

## Key Contacts

| Role | Contact |
|------|---------|
| Tech Owner | Giulio Di Giannatale |
| Business Owner | Edwin Theron |
| On-Call | Check PagerDuty/OpsGenie rotation |

## Monitoring Dashboards

- **CloudWatch**: `verigate-verification-cg-overview` dashboard
- **Datadog**: VeriGate service dashboard (production only)
- **Datadog APM**: Distributed traces under `verigate` service
