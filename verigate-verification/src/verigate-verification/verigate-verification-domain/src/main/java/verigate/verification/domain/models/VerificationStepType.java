/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.domain.models;

/**
 * Enumeration of verification step types, aligned with the unified VerificationType enum.
 */
public enum VerificationStepType {
  // Existing step types (backward compatible)
  BANK_ACCOUNT_VERIFICATION,
  IDENTITY_VERIFICATION,
  WATCHLIST_SCREENING,
  ADDRESS_VERIFICATION,
  PHONE_VERIFICATION,
  EMAIL_VERIFICATION,

  // Legacy step types (backward compatible with command-gateway)
  VERIFICATION_OF_PERSONAL_DETAILS,
  VERIFICATION_OF_BANK_DETAILS,
  SANCTIONS_SCREENING,

  // New core step types
  COMPANY_VERIFICATION,
  PROPERTY_OWNERSHIP_VERIFICATION,

  // Extended step types
  EMPLOYMENT_VERIFICATION,
  NEGATIVE_NEWS_SCREENING,
  FRAUD_WATCHLIST_SCREENING,
  DOCUMENT_VERIFICATION,
  QUALIFICATION_VERIFICATION,
  CREDIT_CHECK,
  TAX_COMPLIANCE_VERIFICATION,
  INCOME_VERIFICATION,

  // Composite
  FULL_VERIFICATION
}
