/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.domain.models;

/**
 * Unified VerificationType enumeration covering all supported verification types.
 */
public enum VerificationType {
  // Existing types (backward compatible)
  VERIFICATION_OF_PERSONAL_DETAILS,
  VERIFICATION_OF_BANK_DETAILS,
  SANCTIONS_SCREENING,

  // New core types
  BANK_ACCOUNT_VERIFICATION,
  IDENTITY_VERIFICATION,
  COMPANY_VERIFICATION,
  PROPERTY_OWNERSHIP_VERIFICATION,

  // Extended verification types
  EMPLOYMENT_VERIFICATION,
  NEGATIVE_NEWS_SCREENING,
  FRAUD_WATCHLIST_SCREENING,
  DOCUMENT_VERIFICATION,
  QUALIFICATION_VERIFICATION,
  CREDIT_CHECK,
  TAX_COMPLIANCE_VERIFICATION,
  INCOME_VERIFICATION,

  // Composite
  FULL_VERIFICATION,

  // Watchlist (alias for backward compat with verification module)
  WATCHLIST_SCREENING
}
