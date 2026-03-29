# DeedsWeb Adapter Environment Variables

This document describes the environment variables used by the DeedsWeb adapter for configuring API access and operational parameters.

## Required Environment Variables

### DEEDSWEB_API_KEY
- **Description**: API key, token, or credential secret for accessing the DeedsWeb provider
- **Required**: Yes
- **Example**: `your-deedsweb-api-key-here`
- **Notes**: Until the real provider contract is wired, this is treated as a generic provider credential.

## Optional Environment Variables

### DEEDSWEB_BASE_URL
- **Description**: Base URL for the DeedsWeb endpoint
- **Required**: No
- **Default**: `https://deedssoap.deeds.gov.za`
- **Example**: `https://deedssoap.deeds.gov.za`

### DEEDSWEB_CONNECTION_TIMEOUT_MS
- **Description**: HTTP connection timeout in milliseconds
- **Required**: No
- **Default**: `30000` (30 seconds)
- **Example**: `45000`

### DEEDSWEB_READ_TIMEOUT_MS
- **Description**: HTTP read timeout in milliseconds
- **Required**: No
- **Default**: `60000` (60 seconds)
- **Example**: `90000`

### DEEDSWEB_RETRY_ATTEMPTS
- **Description**: Number of retry attempts for failed requests
- **Required**: No
- **Default**: `3`
- **Example**: `5`

### DEEDSWEB_RETRY_DELAY_MS
- **Description**: Delay between retry attempts in milliseconds
- **Required**: No
- **Default**: `1000` (1 second)
- **Example**: `2000`

## Configuration Priority

The adapter uses the following configuration priority (highest to lowest):

1. Environment variables
2. Java system properties  
3. application.properties file values
4. Built-in defaults

## Example Configuration

```bash
# Required
export DEEDSWEB_API_KEY="your-api-key-here"

# Optional customizations
export DEEDSWEB_CONNECTION_TIMEOUT_MS="45000"
export DEEDSWEB_READ_TIMEOUT_MS="90000"
export DEEDSWEB_RETRY_ATTEMPTS="5"
```

## Production Recommendations

- **API Key**: Store securely using AWS Secrets Manager or similar
- **Timeouts**: Increase for production workloads (45-90 seconds)
- **Retries**: Set to 3-5 attempts with exponential backoff
- **Monitoring**: Enable detailed logging in non-production environments
