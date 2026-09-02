/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.domain.models;

import java.util.List;
import java.util.Map;

/**
 * Domain-level result of a full CIPC registration document verification: AI extraction with
 * authenticity/tampering scoring, plus cross-validation against a live CIPC lookup.
 *
 * <p>This is a deliberately domain-clean projection of the richer infrastructure-level
 * {@code AiDocumentAnalyzer.DocumentAnalysisResult} (which stays in the infrastructure layer,
 * since it's tied to the Bedrock prompt/response shape) — see {@code DocumentVerificationService}
 * javadoc.
 *
 * @param extractedFields flattened extracted document fields, keyed by the {@code CIPC_FIELD_*}
 *     constants in {@code DomainConstants}
 * @param authenticityScore AI-assessed document authenticity, 0.0-1.0 (higher = more likely
 *     genuine)
 * @param overallConfidence average per-field extraction confidence, 0.0-1.0
 * @param anomalies free-text list of anomalies flagged by the AI analysis
 * @param tamperingFlags free-text list of specific tampering indicators flagged by the AI
 *     analysis
 * @param overallTamperingScore AI-assessed tampering score, 0-100. NOTE: the prompt does not
 *     unambiguously define the direction of this score; this implementation assumes it follows
 *     the same convention as its sibling component scores (fontConsistency, layoutAlignment,
 *     etc.), where higher = more consistent/genuine. Confirm this against real model output
 *     before relying on the {@link #isTamperingSuspected()} threshold in production.
 * @param crossValidation the result of comparing extracted fields against a live CIPC lookup
 * @param errorMessage set when AI analysis itself could not be performed (Bedrock unavailable,
 *     response unparseable, etc.); null when analysis completed
 */
public record CipcDocumentAnalysisResult(
    Map<String, String> extractedFields,
    double authenticityScore,
    double overallConfidence,
    List<String> anomalies,
    List<String> tamperingFlags,
    int overallTamperingScore,
    CipcCrossValidationResult crossValidation,
    String errorMessage
) {

  /**
   * Compact constructor — defaults null collections to empty ones.
   */
  public CipcDocumentAnalysisResult {
    if (extractedFields == null) {
      extractedFields = Map.of();
    }
    if (anomalies == null) {
      anomalies = List.of();
    }
    if (tamperingFlags == null) {
      tamperingFlags = List.of();
    }
  }

  /**
   * Creates a result indicating AI analysis could not be performed at all.
   */
  public static CipcDocumentAnalysisResult aiUnavailable(String reason) {
    return new CipcDocumentAnalysisResult(
        Map.of(), 0.0, 0.0, List.of(), List.of(), 0,
        CipcCrossValidationResult.unavailable("AI analysis unavailable"), reason);
  }

  public boolean isAiAnalysisAvailable() {
    return errorMessage == null;
  }
}
