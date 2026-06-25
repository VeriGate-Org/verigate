/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.domain.services;

import java.util.Map;

/**
 * Generates and stores a sanctions screening report for a completed verification.
 * Returns the storage key so callers can persist it in the command store.
 */
public interface SanctionsReportService {

  /**
   * Generates a screening report and stores it durably.
   *
   * @param commandId     the verification command identifier
   * @param partnerId     the partner who initiated the screening
   * @param resultDetails flat map of screening result details (match scores, datasets, outcome, etc.)
   * @return the storage key (e.g. S3 object key) where the report was saved
   */
  String generateReport(String commandId, String partnerId, Map<String, String> resultDetails);
}
