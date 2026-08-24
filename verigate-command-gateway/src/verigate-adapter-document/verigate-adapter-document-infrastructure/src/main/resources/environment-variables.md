# Document Adapter Environment Variables

This document describes the environment variables used by the Document Verification adapter.

## Document Verification API (placeholder, non-CIPC document types)

- `DOCUMENT_API_URL` - Base URL for the document verification API (optional, defaults to a dev
  placeholder — not yet pointed at a real vendor)
- `DOCUMENT_S3_BUCKET` - S3 bucket used for uploaded document storage
- `DOCUMENT_HTTP_TIMEOUT_SECONDS`, `DOCUMENT_HTTP_RETRY_ATTEMPTS`, `DOCUMENT_HTTP_RETRY_DELAY_MS`
- `DOCUMENT_RATE_LIMIT_RPS`, `DOCUMENT_RATE_LIMIT_BURST`
- `DOCUMENT_ENABLE_REQUEST_LOGGING`, `DOCUMENT_ENABLE_RESPONSE_LOGGING`, `DOCUMENT_LOG_LEVEL`

## CIPC Cross-Validation Client (story 2.1)

Used only for `CIPC_REGISTRATION` document type uploads, to cross-validate AI-extracted fields
against a live CIPC company lookup.

**This is a separate client and configuration surface from `verigate-adapter-cipc`** — the
document adapter does not depend on the CIPC adapter's modules; it calls the CIPC public API
directly with its own HTTP client. This keeps the two adapters independently deployable and
avoids sharing config/secrets across an adapter boundary. See `DocumentCipcApiConfiguration` and
`DocumentCipcHttpAdapter` javadoc for the full rationale.

- `DOCUMENT_CIPC_API_KEY` - API key for CIPC Public Data API access (required for the CIPC
  cross-validation call to work; if unset, cross-validation degrades to "unavailable" rather than
  failing the whole document verification — see `CipcCrossValidationResult.unavailable`)
- `DOCUMENT_CIPC_BASE_URL` - Base URL for the CIPC API (optional, defaults to the dev environment
  URL — must be set to the production CIPC URL for a live cross-check)

### Environment-specific values

Mirror the values used by `verigate-adapter-cipc` (same external API, same API key can be reused
if CIPC issues one key per consumer, or a separate key can be requested — confirm with whoever
owns the CIPC account relationship):

```bash
# Development
DOCUMENT_CIPC_BASE_URL=https://cipc-apm-rs-dev.azure-api.net/enterprise/v1

# Production
DOCUMENT_CIPC_BASE_URL=https://cipc-apm-rs-prod.azure-api.net/enterprise/v1
```

## AWS Lambda Environment Variables

- `VERIFY_DOCUMENT_IMQ_NAME` - SQS queue name for invalid messages
- `VERIFY_DOCUMENT_DLQ_NAME` - SQS queue name for dead letter messages

## Security Considerations

- Never commit API keys to source control
- Use AWS Secrets Manager or similar for production API keys
- `DOCUMENT_CIPC_API_KEY` should be rotated and monitored the same way as the CIPC adapter's own
  `CIPC_API_KEY`
