/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.domain.models;

import java.util.Map;

/**
 * Result of cross-validating an extracted CIPC registration document against a live CIPC lookup.
 *
 * @param cipcLookupPerformed whether a CIPC lookup was attempted at all (false if, e.g., no
 *     registration number could be extracted from the document)
 * @param companyFound whether the looked-up registration number matched a real CIPC company
 * @param companyActive whether the CIPC record shows the company as active/in business
 * @param fieldStatuses per-field match status, keyed by the same field names used in
 *     {@code AiDocumentAnalyzer}'s extracted fields (e.g. "companyName", "companyStatus")
 * @param cipcValues the live CIPC value used for each compared field, keyed the same way, for
 *     display in the side-by-side report view
 * @param unavailableReason human-readable reason the lookup could not be completed (CIPC
 *     unreachable, no registration number extracted, etc.), null when not applicable
 */
public record CipcCrossValidationResult(
    boolean cipcLookupPerformed,
    boolean companyFound,
    boolean companyActive,
    Map<String, FieldMatchStatus> fieldStatuses,
    Map<String, String> cipcValues,
    String unavailableReason
) {

  /**
   * Compact constructor — defaults null maps to empty maps.
   */
  public CipcCrossValidationResult {
    if (fieldStatuses == null) {
      fieldStatuses = Map.of();
    }
    if (cipcValues == null) {
      cipcValues = Map.of();
    }
  }

  /**
   * Creates a result for when the CIPC lookup could not be performed at all (e.g. no
   * registration number extracted, or CIPC API unreachable). The document verification should
   * continue with AI-only results rather than hard-failing.
   */
  public static CipcCrossValidationResult unavailable(String reason) {
    return new CipcCrossValidationResult(false, false, false, Map.of(), Map.of(), reason);
  }

  /**
   * Returns true if any compared field came back as a definite mismatch.
   */
  public boolean hasMismatch() {
    return fieldStatuses.values().stream().anyMatch(status -> status == FieldMatchStatus.MISMATCH);
  }
}
