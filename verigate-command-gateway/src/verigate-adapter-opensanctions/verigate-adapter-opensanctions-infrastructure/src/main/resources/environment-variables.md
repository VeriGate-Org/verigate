# OpenSanctions Adapter Environment Variables

This document describes the environment variables used by the OpenSanctions adapter for configuring API access and operational parameters.

## Required Environment Variables

### OPENSANCTIONS_API_KEY
- **Description**: API key for accessing the OpenSanctions API
- **Required**: Yes
- **Example**: `your-opensanctions-api-key-here`
- **Notes**: Obtain from [OpenSanctions](https://www.opensanctions.org/api/)

## Optional Environment Variables

### OPENSANCTIONS_BASE_URL
- **Description**: Base URL for the OpenSanctions API
- **Required**: No
- **Default**: `https://api.opensanctions.org`
- **Example**: `https://api.opensanctions.org`

### OPENSANCTIONS_CONNECTION_TIMEOUT_MS
- **Description**: HTTP connection timeout in milliseconds
- **Required**: No
- **Default**: `30000` (30 seconds)
- **Example**: `45000`

### OPENSANCTIONS_READ_TIMEOUT_MS
- **Description**: HTTP read timeout in milliseconds
- **Required**: No
- **Default**: `60000` (60 seconds)
- **Example**: `90000`

### OPENSANCTIONS_RETRY_ATTEMPTS
- **Description**: Number of retry attempts for failed requests
- **Required**: No
- **Default**: `3`
- **Example**: `5`

### OPENSANCTIONS_RETRY_DELAY_MS
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
export OPENSANCTIONS_API_KEY="your-api-key-here"

# Optional customizations
export OPENSANCTIONS_CONNECTION_TIMEOUT_MS="45000"
export OPENSANCTIONS_READ_TIMEOUT_MS="90000"
export OPENSANCTIONS_RETRY_ATTEMPTS="5"
```

## Production Recommendations

- **API Key**: Store securely using AWS Secrets Manager or similar
- **Timeouts**: Increase for production workloads (45-90 seconds)
- **Retries**: Set to 3-5 attempts with exponential backoff
- **Monitoring**: Enable detailed logging in non-production environments