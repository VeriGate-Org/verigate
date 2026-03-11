/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.domain.services;

import java.time.Instant;
import java.util.Map;
import verigate.riskengine.domain.enums.VerificationOutcome;
import verigate.riskengine.domain.enums.VerificationType;
import verigate.riskengine.domain.models.AdapterScore;

/**
 * Default implementation that maps raw adapter auxiliaryData to a 0-100
 * confidence score based on the verification type.
 */
public class DefaultScoreNormalizer implements ScoreNormalizer {

    @Override
    public AdapterScore normalize(VerificationType type, VerificationOutcome outcome,
                                  Map<String, String> auxiliaryData) {
        Map<String, String> data = auxiliaryData != null ? auxiliaryData : Map.of();

        int score = switch (type) {
            case COMPANY_VERIFICATION -> normalizeCompanyVerification(outcome, data);
            case IDENTITY_VERIFICATION -> normalizeIdentityVerification(outcome, data);
            case CREDIT_CHECK -> normalizeCreditCheck(outcome, data);
            case SANCTIONS_SCREENING, WATCHLIST_SCREENING -> normalizeSanctionsScreening(outcome, data);
            case NEGATIVE_NEWS_SCREENING -> normalizeNegativeNews(outcome, data);
            case FRAUD_WATCHLIST_SCREENING -> normalizeFraudWatchlist(outcome, data);
            case VERIFICATION_OF_PERSONAL_DETAILS -> normalizePersonalDetails(outcome, data);
            case VERIFICATION_OF_BANK_DETAILS, BANK_ACCOUNT_VERIFICATION ->
                normalizeBankDetails(outcome, data);
            default -> normalizeFallback(outcome);
        };

        return new AdapterScore(type, outcome, score, data, Instant.now());
    }

    private int normalizeCompanyVerification(VerificationOutcome outcome,
                                             Map<String, String> data) {
        if (outcome == VerificationOutcome.SYSTEM_OUTAGE) return 0;
        if (outcome == VerificationOutcome.HARD_FAIL) return 10;

        String isActive = data.getOrDefault("isActive", "false");
        String directorsCount = data.getOrDefault("activeDirectorsCount", "0");

        boolean active = "true".equalsIgnoreCase(isActive);
        int directors = parseIntSafely(directorsCount, 0);

        if (active && directors > 0) return 90;
        if (active) return 60;
        return 20;
    }

    private int normalizeIdentityVerification(VerificationOutcome outcome,
                                              Map<String, String> data) {
        if (outcome == VerificationOutcome.SYSTEM_OUTAGE) return 0;
        if (outcome == VerificationOutcome.HARD_FAIL) return 10;

        String matchScore = data.get("matchScore");
        if (matchScore != null) {
            return clampScore(parseIntSafely(matchScore, 50));
        }
        return normalizeFallback(outcome);
    }

    private int normalizeCreditCheck(VerificationOutcome outcome,
                                     Map<String, String> data) {
        if (outcome == VerificationOutcome.SYSTEM_OUTAGE) return 0;
        if (outcome == VerificationOutcome.HARD_FAIL) return 10;

        String creditScore = data.get("creditScore");
        if (creditScore != null) {
            int raw = parseIntSafely(creditScore, 575);
            // Linear normalization: 300 → 0, 850 → 100
            int normalized = (int) Math.round(((double) (raw - 300) / (850 - 300)) * 100);
            return clampScore(normalized);
        }
        return normalizeFallback(outcome);
    }

    private int normalizeSanctionsScreening(VerificationOutcome outcome,
                                            Map<String, String> data) {
        if (outcome == VerificationOutcome.SYSTEM_OUTAGE) return 0;

        String hitCount = data.get("hitCount");
        if (hitCount != null) {
            int hits = parseIntSafely(hitCount, 0);
            if (hits == 0) return 100;
            if (hits == 1) return 30;
            return 10;
        }
        return outcome == VerificationOutcome.SUCCEEDED ? 100 : 30;
    }

    private int normalizeNegativeNews(VerificationOutcome outcome,
                                      Map<String, String> data) {
        if (outcome == VerificationOutcome.SYSTEM_OUTAGE) return 0;

        String riskLevel = data.get("riskLevel");
        if (riskLevel != null) {
            return switch (riskLevel.toUpperCase()) {
                case "LOW" -> 90;
                case "MEDIUM" -> 50;
                case "HIGH" -> 15;
                default -> normalizeFallback(outcome);
            };
        }
        return normalizeFallback(outcome);
    }

    private int normalizeFraudWatchlist(VerificationOutcome outcome,
                                        Map<String, String> data) {
        if (outcome == VerificationOutcome.SYSTEM_OUTAGE) return 0;

        String listed = data.get("listed");
        if (listed != null) {
            return "true".equalsIgnoreCase(listed) ? 5 : 100;
        }
        return normalizeFallback(outcome);
    }

    private int normalizePersonalDetails(VerificationOutcome outcome,
                                         Map<String, String> data) {
        if (outcome == VerificationOutcome.SYSTEM_OUTAGE) return 0;
        if (outcome == VerificationOutcome.HARD_FAIL) return 10;

        String confidence = data.get("nameMatchConfidence");
        if (confidence != null) {
            return clampScore(parseIntSafely(confidence, 50));
        }
        return normalizeFallback(outcome);
    }

    private int normalizeBankDetails(VerificationOutcome outcome,
                                     Map<String, String> data) {
        if (outcome == VerificationOutcome.SYSTEM_OUTAGE) return 0;
        if (outcome == VerificationOutcome.HARD_FAIL) return 10;

        boolean accountFound = "true".equalsIgnoreCase(data.getOrDefault("accountFound", "false"));
        boolean nameMatch = "true".equalsIgnoreCase(data.getOrDefault("nameMatch", "false"));

        if (accountFound && nameMatch) return 95;
        if (accountFound) return 60;
        return 10;
    }

    private int normalizeFallback(VerificationOutcome outcome) {
        return switch (outcome) {
            case SUCCEEDED -> 85;
            case SOFT_FAIL -> 40;
            case HARD_FAIL -> 10;
            case SYSTEM_OUTAGE -> 0;
        };
    }

    private static int parseIntSafely(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static int clampScore(int score) {
        return Math.max(0, Math.min(100, score));
    }
}
