/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.domain.mappers;

import java.util.HashMap;
import java.util.Map;
import verigate.adapter.opensanctions.domain.constants.DomainConstants;
import verigate.adapter.opensanctions.domain.models.EntityMatchResponse;
import verigate.adapter.opensanctions.domain.models.ScoredEntity;
import verigate.verification.cg.domain.models.VerificationOutcome;
import verigate.verification.cg.domain.models.VerificationResult;

/**
 * Maps OpenSanctions API responses to VeriGate VerificationResult.
 */
public class VerificationResultMapper {

  /**
   * Maps an OpenSanctions EntityMatchResponse to a VeriGate VerificationResult.
   *
   * @param response the OpenSanctions API response
   * @param requestId the original request identifier
   * @return the VeriGate verification result
   */
  public static VerificationResult mapToVerificationResult(
      EntityMatchResponse response, String requestId) {

    // Analyze the response to determine verification outcome
    VerificationOutcome outcome = determineOutcome(response);

    return new VerificationResult(outcome, createFailureReason(response, outcome));
  }

  private static VerificationOutcome determineOutcome(EntityMatchResponse response) {
    if (response.getResponses() == null || response.getResponses().isEmpty()) {
      return VerificationOutcome.SUCCEEDED; // No matches found - clean result
    }

    // Check each query result
    for (var entityMatches : response.getResponses().values()) {
      if (entityMatches.getResults() != null && !entityMatches.getResults().isEmpty()) {

        // Find highest scoring match
        double highestScore =
            entityMatches.getResults().stream()
                .mapToDouble(entity -> entity.getScore() != null ? entity.getScore() : 0.0)
                .max()
                .orElse(0.0);

        // Determine outcome based on match score thresholds
        if (highestScore >= DomainConstants.HIGH_MATCH_THRESHOLD) {
          return VerificationOutcome.HARD_FAIL; // High confidence match - likely sanctioned
        } else if (highestScore >= DomainConstants.MEDIUM_MATCH_THRESHOLD) {
          return VerificationOutcome.SOFT_FAIL; // Medium confidence - requires review
        }
      }
    }

    return VerificationOutcome.SUCCEEDED; // No significant matches found
  }

  private static String createFailureReason(
      EntityMatchResponse response, VerificationOutcome outcome) {
    if (outcome == VerificationOutcome.SUCCEEDED) {
      return null; // No failure reason for successful outcomes
    }

    // Create detailed failure reason for failed outcomes
    StringBuilder reason = new StringBuilder();
    reason.append("OpenSanctions match found: ");

    if (response.getResponses() != null) {
      for (var entityMatches : response.getResponses().values()) {
        if (entityMatches.getResults() != null && !entityMatches.getResults().isEmpty()) {
          var highestMatch =
              entityMatches.getResults().stream()
                  .max(
                      (a, b) ->
                          Double.compare(
                              a.getScore() != null ? a.getScore() : 0.0,
                              b.getScore() != null ? b.getScore() : 0.0));

          if (highestMatch.isPresent()) {
            var match = highestMatch.get();
            reason.append(
                String.format(
                    "'%s' (Score: %.2f, Datasets: %s)",
                    match.getCaption(), match.getScore(), String.join(",", match.getDatasets())));
            break; // Only show the highest match in the reason
          }
        }
      }
    }

    return reason.toString();
  }

  private static Map<String, String> createResultDetails(EntityMatchResponse response) {
    Map<String, String> details = new HashMap<>();

    details.put("provider", "OpenSanctions");
    details.put("algorithm", "entity-matching");

    if (response.getResponses() != null) {
      int totalMatches =
          response.getResponses().values().stream()
              .mapToInt(matches -> matches.getResults() != null ? matches.getResults().size() : 0)
              .sum();

      details.put("total_matches", String.valueOf(totalMatches));

      // Include details of high-scoring matches
      addHighScoreMatchDetails(response, details);
    }

    return details;
  }

  private static void addHighScoreMatchDetails(
      EntityMatchResponse response, Map<String, String> details) {
    int matchIndex = 0;

    for (var entityMatches : response.getResponses().values()) {
      if (entityMatches.getResults() != null) {
        for (ScoredEntity entity : entityMatches.getResults()) {
          if (entity.getScore() != null
              && entity.getScore() >= DomainConstants.MEDIUM_MATCH_THRESHOLD) {
            String prefix = "match_" + matchIndex + "_";
            details.put(prefix + "id", entity.getId());
            details.put(prefix + "caption", entity.getCaption());
            details.put(prefix + "score", String.valueOf(entity.getScore()));
            details.put(prefix + "datasets", String.join(",", entity.getDatasets()));

            if (entity.getTarget() != null) {
              details.put(prefix + "target", String.valueOf(entity.getTarget()));
            }

            matchIndex++;
          }
        }
      }
    }

    details.put("significant_matches_count", String.valueOf(matchIndex));
  }
}
