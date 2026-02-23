# VeriGate Partner Onboarding Guide

## Overview

This guide walks through the steps to onboard a new partner to the VeriGate
verification platform.

## Prerequisites

- VeriGate admin access (ROLE_ADMIN in Cognito)
- AWS console access (for initial setup if needed)

## Step 1: Create Partner Record

```bash
# Via Admin API
curl -X POST https://api.verigate.co.za/api/admin/partners \
  -H "Authorization: Bearer <admin-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Acme Financial Services",
    "contactEmail": "tech@acmefinancial.co.za",
    "billingPlan": "STANDARD"
  }'
```

Response:
```json
{
  "partnerId": "partner-abc123",
  "name": "Acme Financial Services",
  "status": "PENDING",
  "contactEmail": "tech@acmefinancial.co.za",
  "createdAt": "2025-01-15T10:30:00Z"
}
```

## Step 2: Configure Verification Types

Configure which verification types the partner is allowed to use and set
per-type thresholds.

```bash
curl -X PUT https://api.verigate.co.za/api/admin/partners/partner-abc123/configuration \
  -H "Authorization: Bearer <admin-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "allowedVerificationTypes": [
      "IDENTITY_VERIFICATION",
      "COMPANY_VERIFICATION",
      "BANK_ACCOUNT_VERIFICATION",
      "CREDIT_CHECK"
    ],
    "rateLimit": {
      "requestsPerMinute": 200
    }
  }'
```

## Step 3: Generate API Key

```bash
curl -X POST https://api.verigate.co.za/api/admin/partners/partner-abc123/api-keys \
  -H "Authorization: Bearer <admin-jwt-token>"
```

Response:
```json
{
  "apiKey": "vg_a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0",
  "partnerId": "partner-abc123",
  "keyPrefix": "vg_a1b2",
  "createdAt": "2025-01-15T10:30:00Z"
}
```

**IMPORTANT**: The raw API key is only shown once. Share it securely with the
partner (e.g., via encrypted channel, not email).

## Step 4: Activate Partner

```bash
curl -X POST https://api.verigate.co.za/api/admin/partners/partner-abc123/activate \
  -H "Authorization: Bearer <admin-jwt-token>"
```

## Step 5: Share Integration Documentation

Provide the partner with:
1. **API Base URL**: `https://api.verigate.co.za`
2. **OpenAPI Spec**: Available at `/api/docs` or share the YAML file
3. **API Key**: Generated in Step 3
4. **Allowed verification types**: Configured in Step 2
5. **Rate limits**: Configured in Step 2

## Partner Integration Example

Share this example with the partner:

```bash
# Submit a company verification
curl -X POST https://api.verigate.co.za/api/verifications \
  -H "X-API-Key: vg_a1b2c3d4e5f6..." \
  -H "Content-Type: application/json" \
  -d '{
    "verificationType": "COMPANY_VERIFICATION",
    "subject": {
      "enterpriseNumber": "2020/939681/07"
    }
  }'

# Check verification status
curl https://api.verigate.co.za/api/verifications/{verificationId} \
  -H "X-API-Key: vg_a1b2c3d4e5f6..."
```

## Verification Types Reference

| Type | Description | Required Fields |
|------|-------------|----------------|
| IDENTITY_VERIFICATION | DHA identity verification | idNumber, firstName, lastName, dateOfBirth |
| COMPANY_VERIFICATION | CIPC company verification | enterpriseNumber |
| BANK_ACCOUNT_VERIFICATION | QLink bank verification | idNumber, accountNumber, bankCode |
| PROPERTY_OWNERSHIP_VERIFICATION | Deeds Office property check | idNumber OR enterpriseNumber |
| WATCHLIST_SCREENING | Sanctions/PEP screening | firstName, lastName, dateOfBirth |
| EMPLOYMENT_VERIFICATION | Employment status verification | idNumber, employerName |
| CREDIT_CHECK | Credit bureau check | idNumber, firstName, lastName |
| TAX_COMPLIANCE_VERIFICATION | SARS tax compliance | idNumber OR enterpriseNumber |
| INCOME_VERIFICATION | Income verification | idNumber |
| DOCUMENT_VERIFICATION | Document OCR verification | documentType, documentUrl |
| QUALIFICATION_VERIFICATION | SAQA qualification check | idNumber, qualificationId |
| NEGATIVE_NEWS_SCREENING | Adverse media screening | firstName, lastName |
| FRAUD_WATCHLIST_SCREENING | Fraud watchlist check | idNumber |
| SANCTIONS_SCREENING | ORMS sanctions screening | firstName, lastName, dateOfBirth |

## Billing

Partners are billed monthly based on:
- Number of verification requests per type
- Billing plan tier (BASIC, STANDARD, ENTERPRISE)
- Monthly minimum applies per plan

Usage reports are available via the partner portal.

## Support

For technical support: engineering@verigate.co.za
For billing inquiries: billing@verigate.co.za
Partner portal: https://portal.verigate.co.za
