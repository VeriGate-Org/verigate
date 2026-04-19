# DeedsWeb Adapter Environment Variables

This document describes the environment variables used by the DeedsWeb adapter, which integrates
with the South African DeedsWeb SOAP registry.

## Required Environment Variables

### DEEDSWEB_CREDENTIALS_SECRET_NAME
- **Description**: Name (or ARN) of the AWS Secrets Manager secret holding the SOAP credentials
- **Required**: Yes
- **Default**: `verigate/deedsweb/credentials`
- **Secret payload**: JSON of the form `{"username":"...","password":"..."}`
- **Notes**: The Lambda execution role must have `secretsmanager:GetSecretValue` on this secret.
  Credentials are fetched per verification request (no in-process caching).

## Optional Environment Variables

### DEEDSWEB_BASE_URL
- **Description**: SOAP endpoint base URL for the DeedsWeb registry
- **Required**: No
- **Default**: `http://deedssoap.deeds.gov.za:80/deeds-registration-soap/`
- **Notes**: DeedsWeb requires the source IP to be whitelisted. The verification Lambda egresses
  via NAT EIP `13.246.247.144`.

### DEEDSWEB_CONNECTION_TIMEOUT_MS
- **Description**: HTTP connection timeout in milliseconds
- **Required**: No
- **Default**: `30000` (30 seconds)

### DEEDSWEB_READ_TIMEOUT_MS
- **Description**: HTTP read timeout in milliseconds
- **Required**: No
- **Default**: `60000` (60 seconds)

### DEEDSWEB_RETRY_ATTEMPTS
- **Description**: Number of retry attempts for transient failures
- **Required**: No
- **Default**: `3`

### DEEDSWEB_RETRY_DELAY_MS
- **Description**: Delay between retry attempts in milliseconds
- **Required**: No
- **Default**: `1000` (1 second)

## Configuration Priority

The adapter resolves each setting in the following order (highest to lowest):

1. Environment variables
2. `application.properties` values
3. Built-in defaults

## Example Configuration

```bash
# Required
export DEEDSWEB_CREDENTIALS_SECRET_NAME="verigate/deedsweb/credentials"

# Optional overrides
export DEEDSWEB_CONNECTION_TIMEOUT_MS="45000"
export DEEDSWEB_READ_TIMEOUT_MS="90000"
export DEEDSWEB_RETRY_ATTEMPTS="5"
```
