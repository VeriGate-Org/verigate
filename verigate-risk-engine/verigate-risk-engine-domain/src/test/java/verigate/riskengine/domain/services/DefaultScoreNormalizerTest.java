/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.domain.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verigate.riskengine.domain.enums.VerificationOutcome;
import verigate.riskengine.domain.enums.VerificationType;

class DefaultScoreNormalizerTest {

    private DefaultScoreNormalizer normalizer;

    @BeforeEach
    void setUp() {
        normalizer = new DefaultScoreNormalizer();
    }

    // --- Company Verification ---
    @Test
    void companyVerification_activeWithDirectors_returns90() {
        var score = normalizer.normalize(
            VerificationType.COMPANY_VERIFICATION,
            VerificationOutcome.SUCCEEDED,
            Map.of("isActive", "true", "activeDirectorsCount", "3"));
        assertEquals(90, score.confidenceScore());
    }

    @Test
    void companyVerification_activeNoDirectors_returns60() {
        var score = normalizer.normalize(
            VerificationType.COMPANY_VERIFICATION,
            VerificationOutcome.SUCCEEDED,
            Map.of("isActive", "true", "activeDirectorsCount", "0"));
        assertEquals(60, score.confidenceScore());
    }

    @Test
    void companyVerification_inactive_returns20() {
        var score = normalizer.normalize(
            VerificationType.COMPANY_VERIFICATION,
            VerificationOutcome.SUCCEEDED,
            Map.of("isActive", "false"));
        assertEquals(20, score.confidenceScore());
    }

    // --- Identity Verification ---
    @Test
    void identityVerification_matchScore85_returns85() {
        var score = normalizer.normalize(
            VerificationType.IDENTITY_VERIFICATION,
            VerificationOutcome.SUCCEEDED,
            Map.of("matchScore", "85"));
        assertEquals(85, score.confidenceScore());
    }

    @Test
    void identityVerification_noMatchScore_fallback() {
        var score = normalizer.normalize(
            VerificationType.IDENTITY_VERIFICATION,
            VerificationOutcome.SUCCEEDED,
            Map.of());
        assertEquals(85, score.confidenceScore()); // fallback SUCCEEDED
    }

    // --- Credit Check ---
    @Test
    void creditCheck_score750_normalizes82() {
        var score = normalizer.normalize(
            VerificationType.CREDIT_CHECK,
            VerificationOutcome.SUCCEEDED,
            Map.of("creditScore", "750"));
        // (750 - 300) / (850 - 300) * 100 = 450/550*100 ≈ 82
        assertEquals(82, score.confidenceScore());
    }

    @Test
    void creditCheck_score300_normalizes0() {
        var score = normalizer.normalize(
            VerificationType.CREDIT_CHECK,
            VerificationOutcome.SUCCEEDED,
            Map.of("creditScore", "300"));
        assertEquals(0, score.confidenceScore());
    }

    @Test
    void creditCheck_score850_normalizes100() {
        var score = normalizer.normalize(
            VerificationType.CREDIT_CHECK,
            VerificationOutcome.SUCCEEDED,
            Map.of("creditScore", "850"));
        assertEquals(100, score.confidenceScore());
    }

    // --- Sanctions Screening ---
    @Test
    void sanctionsScreening_zeroHits_returns100() {
        var score = normalizer.normalize(
            VerificationType.SANCTIONS_SCREENING,
            VerificationOutcome.SUCCEEDED,
            Map.of("hitCount", "0"));
        assertEquals(100, score.confidenceScore());
    }

    @Test
    void sanctionsScreening_oneHit_returns30() {
        var score = normalizer.normalize(
            VerificationType.SANCTIONS_SCREENING,
            VerificationOutcome.SOFT_FAIL,
            Map.of("hitCount", "1"));
        assertEquals(30, score.confidenceScore());
    }

    @Test
    void sanctionsScreening_multipleHits_returns10() {
        var score = normalizer.normalize(
            VerificationType.SANCTIONS_SCREENING,
            VerificationOutcome.HARD_FAIL,
            Map.of("hitCount", "5"));
        assertEquals(10, score.confidenceScore());
    }

    // --- Negative News ---
    @Test
    void negativeNews_lowRisk_returns90() {
        var score = normalizer.normalize(
            VerificationType.NEGATIVE_NEWS_SCREENING,
            VerificationOutcome.SUCCEEDED,
            Map.of("riskLevel", "LOW"));
        assertEquals(90, score.confidenceScore());
    }

    @Test
    void negativeNews_mediumRisk_returns50() {
        var score = normalizer.normalize(
            VerificationType.NEGATIVE_NEWS_SCREENING,
            VerificationOutcome.SUCCEEDED,
            Map.of("riskLevel", "MEDIUM"));
        assertEquals(50, score.confidenceScore());
    }

    @Test
    void negativeNews_highRisk_returns15() {
        var score = normalizer.normalize(
            VerificationType.NEGATIVE_NEWS_SCREENING,
            VerificationOutcome.SUCCEEDED,
            Map.of("riskLevel", "HIGH"));
        assertEquals(15, score.confidenceScore());
    }

    // --- Fraud Watchlist ---
    @Test
    void fraudWatchlist_notListed_returns100() {
        var score = normalizer.normalize(
            VerificationType.FRAUD_WATCHLIST_SCREENING,
            VerificationOutcome.SUCCEEDED,
            Map.of("listed", "false"));
        assertEquals(100, score.confidenceScore());
    }

    @Test
    void fraudWatchlist_listed_returns5() {
        var score = normalizer.normalize(
            VerificationType.FRAUD_WATCHLIST_SCREENING,
            VerificationOutcome.HARD_FAIL,
            Map.of("listed", "true"));
        assertEquals(5, score.confidenceScore());
    }

    // --- Bank Details ---
    @Test
    void bankDetails_accountFoundAndNameMatch_returns95() {
        var score = normalizer.normalize(
            VerificationType.VERIFICATION_OF_BANK_DETAILS,
            VerificationOutcome.SUCCEEDED,
            Map.of("accountFound", "true", "nameMatch", "true"));
        assertEquals(95, score.confidenceScore());
    }

    @Test
    void bankDetails_accountFoundNoNameMatch_returns60() {
        var score = normalizer.normalize(
            VerificationType.VERIFICATION_OF_BANK_DETAILS,
            VerificationOutcome.SUCCEEDED,
            Map.of("accountFound", "true", "nameMatch", "false"));
        assertEquals(60, score.confidenceScore());
    }

    @Test
    void bankDetails_noAccount_returns10() {
        var score = normalizer.normalize(
            VerificationType.VERIFICATION_OF_BANK_DETAILS,
            VerificationOutcome.HARD_FAIL,
            Map.of("accountFound", "false"));
        assertEquals(10, score.confidenceScore());
    }

    // --- Personal Details ---
    @Test
    void personalDetails_confidence92_returns92() {
        var score = normalizer.normalize(
            VerificationType.VERIFICATION_OF_PERSONAL_DETAILS,
            VerificationOutcome.SUCCEEDED,
            Map.of("nameMatchConfidence", "92"));
        assertEquals(92, score.confidenceScore());
    }

    // --- Fallback ---
    @Test
    void fallback_succeeded_returns85() {
        var score = normalizer.normalize(
            VerificationType.EMPLOYMENT_VERIFICATION,
            VerificationOutcome.SUCCEEDED,
            Map.of());
        assertEquals(85, score.confidenceScore());
    }

    @Test
    void fallback_softFail_returns40() {
        var score = normalizer.normalize(
            VerificationType.EMPLOYMENT_VERIFICATION,
            VerificationOutcome.SOFT_FAIL,
            Map.of());
        assertEquals(40, score.confidenceScore());
    }

    @Test
    void fallback_hardFail_returns10() {
        var score = normalizer.normalize(
            VerificationType.DOCUMENT_VERIFICATION,
            VerificationOutcome.HARD_FAIL,
            Map.of());
        assertEquals(10, score.confidenceScore());
    }

    @Test
    void fallback_systemOutage_returns0() {
        var score = normalizer.normalize(
            VerificationType.QUALIFICATION_VERIFICATION,
            VerificationOutcome.SYSTEM_OUTAGE,
            Map.of());
        assertEquals(0, score.confidenceScore());
    }

    // --- Edge Cases ---
    @Test
    void nullAuxiliaryData_usesFallback() {
        var score = normalizer.normalize(
            VerificationType.CREDIT_CHECK,
            VerificationOutcome.SUCCEEDED,
            null);
        assertEquals(85, score.confidenceScore());
    }

    @Test
    void systemOutage_alwaysZero_companyVerification() {
        var score = normalizer.normalize(
            VerificationType.COMPANY_VERIFICATION,
            VerificationOutcome.SYSTEM_OUTAGE,
            Map.of("isActive", "true", "activeDirectorsCount", "5"));
        assertEquals(0, score.confidenceScore());
    }

    @Test
    void creditCheck_belowMinScore_clampedTo0() {
        var score = normalizer.normalize(
            VerificationType.CREDIT_CHECK,
            VerificationOutcome.SUCCEEDED,
            Map.of("creditScore", "100"));
        assertEquals(0, score.confidenceScore());
    }
}
