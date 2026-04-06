/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.domain.constants;

import java.util.Map;
import verigate.verification.cg.domain.models.VerificationType;

/**
 * This class contains constants used throughout the domain layer of the application.
 */
public class DomainConstants {

  public static final Map<VerificationType, String> VERIFICATION_TYPE_TO_QUEUE_NAME_MAP =
      Map.ofEntries(
          Map.entry(VerificationType.VERIFICATION_OF_BANK_DETAILS, "QLINK_ADAPTER_QUEUE_NAME"),
          Map.entry(VerificationType.BANK_ACCOUNT_VERIFICATION, "QLINK_ADAPTER_QUEUE_NAME"),
          Map.entry(
              VerificationType.VERIFICATION_OF_PERSONAL_DETAILS, "WORLDCHECK_ADAPTER_QUEUE_NAME"),
          Map.entry(VerificationType.SANCTIONS_SCREENING, "OPENSANCTIONS_ADAPTER_QUEUE_NAME"),
          Map.entry(VerificationType.WATCHLIST_SCREENING, "OPENSANCTIONS_ADAPTER_QUEUE_NAME"),
          Map.entry(VerificationType.IDENTITY_VERIFICATION, "DHA_ADAPTER_QUEUE_NAME"),
          Map.entry(VerificationType.COMPANY_VERIFICATION, "CIPC_ADAPTER_QUEUE_NAME"),
          Map.entry(
              VerificationType.PROPERTY_OWNERSHIP_VERIFICATION, "DEEDSWEB_ADAPTER_QUEUE_NAME"),
          Map.entry(VerificationType.EMPLOYMENT_VERIFICATION, "EMPLOYMENT_ADAPTER_QUEUE_NAME"),
          Map.entry(VerificationType.NEGATIVE_NEWS_SCREENING, "NEGATIVENEWS_ADAPTER_QUEUE_NAME"),
          Map.entry(
              VerificationType.FRAUD_WATCHLIST_SCREENING, "FRAUDWATCHLIST_ADAPTER_QUEUE_NAME"),
          Map.entry(VerificationType.DOCUMENT_VERIFICATION, "DOCUMENT_ADAPTER_QUEUE_NAME"),
          Map.entry(VerificationType.QUALIFICATION_VERIFICATION, "SAQA_ADAPTER_QUEUE_NAME"),
          Map.entry(VerificationType.CREDIT_CHECK, "CREDITBUREAU_ADAPTER_QUEUE_NAME"),
          Map.entry(VerificationType.TAX_COMPLIANCE_VERIFICATION, "SARS_ADAPTER_QUEUE_NAME"),
          Map.entry(VerificationType.INCOME_VERIFICATION, "INCOME_ADAPTER_QUEUE_NAME"),
          Map.entry(
              VerificationType.VAT_VENDOR_VERIFICATION, "SARS_VAT_ADAPTER_QUEUE_NAME"));

  public static final String VERIFICATION_SUCCEEDED_EVENT = "verificationSucceededEvent";
  public static final String VERIFICATION_HARD_FAIL_EVENT = "verificationHardFailEvent";
  public static final String VERIFICATION_SOFT_FAIL_EVENT = "verificationSoftFailEvent";
  public static final int TRANSACTION_REASON_ID = 1;
}
