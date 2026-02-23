# CIPC Adapter Environment Variables

This document describes the environment variables used by the CIPC adapter.

## Required Environment Variables

### API Authentication
- `CIPC_API_KEY` - API key for CIPC Public Data API access (required)
- `CIPC_BASE_URL` - Base URL for CIPC API (optional, defaults to dev environment)

## Optional Environment Variables

### HTTP Configuration
- `CIPC_HTTP_TIMEOUT_SECONDS` - HTTP request timeout in seconds (default: 30)
- `CIPC_HTTP_RETRY_ATTEMPTS` - Number of retry attempts for failed requests (default: 3)
- `CIPC_HTTP_RETRY_DELAY_MS` - Delay between retry attempts in milliseconds (default: 1000)

### Rate Limiting
- `CIPC_RATE_LIMIT_RPS` - Requests per second limit (default: 5)
- `CIPC_RATE_LIMIT_BURST` - Burst capacity for rate limiting (default: 10)

### Monitoring and Logging
- `CIPC_ENABLE_REQUEST_LOGGING` - Enable detailed request logging (default: false)
- `CIPC_ENABLE_RESPONSE_LOGGING` - Enable detailed response logging (default: false)
- `CIPC_LOG_LEVEL` - Log level for CIPC operations (default: INFO)

### AWS Lambda Environment Variables
- `VERIFY_COMPANY_DETAILS_IMQ_NAME` - SQS queue name for invalid messages
- `VERIFY_COMPANY_DETAILS_DLQ_NAME` - SQS queue name for dead letter messages

## Environment-Specific Configuration

### Development
```bash
CIPC_BASE_URL=https://cipc-apm-rs-dev.azure-api.net/enterprise/v1
CIPC_ENABLE_REQUEST_LOGGING=true
CIPC_ENABLE_RESPONSE_LOGGING=true
CIPC_LOG_LEVEL=DEBUG
```

### Production
```bash
CIPC_BASE_URL=https://cipc-apm-rs-prod.azure-api.net/enterprise/v1
CIPC_ENABLE_REQUEST_LOGGING=false
CIPC_ENABLE_RESPONSE_LOGGING=false
CIPC_LOG_LEVEL=INFO
CIPC_RATE_LIMIT_RPS=3
```

## Security Considerations

- **Never commit API keys** to source control
- Use AWS Secrets Manager or similar for production API keys
- Rotate API keys regularly
- Monitor API usage and implement alerting for unusual patterns
- Use least-privilege access principles for CIPC API permissions

## API Key Management

The CIPC API key should be obtained from CIPC and configured according to your environment:

1. **Development**: Set environment variable directly or in IDE configuration
2. **Testing**: Use test environment API key with limited permissions
3. **Production**: Use AWS Secrets Manager or equivalent secure storage
4. **CI/CD**: Use encrypted secrets in your CI/CD pipeline

## Validation

The adapter validates required environment variables on startup and will fail fast if:
- `CIPC_API_KEY` is missing or empty
- `CIPC_BASE_URL` is malformed (if provided)

## Troubleshooting

Common issues and solutions:

1. **Authentication Errors (401)**
   - Verify `CIPC_API_KEY` is correct and not expired
   - Check API key permissions with CIPC

2. **Network Timeouts**
   - Increase `CIPC_HTTP_TIMEOUT_SECONDS`
   - Check network connectivity to CIPC endpoints

3. **Rate Limiting (429)**
   - Reduce `CIPC_RATE_LIMIT_RPS`
   - Implement exponential backoff in retry logic

4. **Invalid Enterprise Numbers**
   - Ensure enterprise numbers follow format: YYYY/NNNNNN/NN
   - Example: 2020/939681/07