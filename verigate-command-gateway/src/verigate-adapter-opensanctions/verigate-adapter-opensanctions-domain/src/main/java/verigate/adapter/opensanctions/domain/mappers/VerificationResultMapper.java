/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.domain.mappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    VerificationOutcome outcome = determineOutcome(response);

    return new VerificationResult(outcome, createFailureReason(response, outcome));
  }

  /**
   * Creates detailed result information from the match response.
   *
   * @param response the OpenSanctions API response
   * @return map of result details
   */
  public static Map<String, String> createResultDetails(EntityMatchResponse response) {
    Map<String, String> details = new HashMap<>();

    details.put("provider", "OpenSanctions");
    details.put("algorithm", "entity-matching");

    if (response.getResponses() != null) {
      int totalMatches =
          response.getResponses().values().stream()
              .mapToInt(matches -> matches.getResults() != null ? matches.getResults().size() : 0)
              .sum();

      details.put("total_matches", String.valueOf(totalMatches));

      addHighScoreMatchDetails(response, details);
    }

    return details;
  }

  private static VerificationOutcome determineOutcome(EntityMatchResponse response) {
    if (response.getResponses() == null || response.getResponses().isEmpty()) {
      return VerificationOutcome.SUCCEEDED;
    }

    for (var entityMatches : response.getResponses().values()) {
      if (entityMatches.getResults() != null && !entityMatches.getResults().isEmpty()) {

        double highestScore =
            entityMatches.getResults().stream()
                .mapToDouble(entity -> entity.getScore() != null ? entity.getScore() : 0.0)
                .max()
                .orElse(0.0);

        if (highestScore >= DomainConstants.HIGH_MATCH_THRESHOLD) {
          return VerificationOutcome.HARD_FAIL;
        } else if (highestScore >= DomainConstants.MEDIUM_MATCH_THRESHOLD) {
          return VerificationOutcome.SOFT_FAIL;
        }
      }
    }

    return VerificationOutcome.SUCCEEDED;
  }

  private static String createFailureReason(
      EntityMatchResponse response, VerificationOutcome outcome) {
    if (outcome == VerificationOutcome.SUCCEEDED) {
      return null;
    }

    List<String> matchDescriptions = new ArrayList<>();

    if (response.getResponses() != null) {
      for (var entityMatches : response.getResponses().values()) {
        if (entityMatches.getResults() != null) {
          for (ScoredEntity entity : entityMatches.getResults()) {
            if (entity.getScore() != null
                && entity.getScore() >= DomainConstants.MEDIUM_MATCH_THRESHOLD) {
              String matchType = classifyMatchType(entity);
              matchDescriptions.add(
                  String.format(
                      "%s: '%s' (Score: %.2f, Datasets: %s)",
                      matchType,
                      entity.getCaption(),
                      entity.getScore(),
                      String.join(",", entity.getDatasets())));
            }
          }
        }
      }
    }

    if (matchDescriptions.isEmpty()) {
      return "OpenSanctions match found";
    }

    return "OpenSanctions match found: " + String.join("; ", matchDescriptions);
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
            details.put(prefix + "type", classifyMatchType(entity));

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

  /**
   * Classifies an entity match as PEP or Sanctions based on its datasets.
   */
  private static String classifyMatchType(ScoredEntity entity) {
    if (entity.getDatasets() != null) {
      for (String dataset : entity.getDatasets()) {
        if (dataset.toLowerCase().contains("pep")) {
          return "PEP";
        }
      }
    }
    return "SANCTIONS";
  }
}
