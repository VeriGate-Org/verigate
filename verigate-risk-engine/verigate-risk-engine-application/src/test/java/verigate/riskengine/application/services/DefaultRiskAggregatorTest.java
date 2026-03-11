/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.application.services;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verigate.riskengine.domain.enums.*;
import verigate.riskengine.domain.models.*;

class DefaultRiskAggregatorTest {

    private DefaultRiskAggregator aggregator;
    private RiskScoringConfig defaultConfig;

    @BeforeEach
    void setUp() {
        aggregator = new DefaultRiskAggregator();
        defaultConfig = RiskScoringConfig.systemDefault("partner-1");
    }

    private AdapterScore score(VerificationType type, int confidence) {
        return new AdapterScore(type, VerificationOutcome.SUCCEEDED, confidence,
            Map.of(), Instant.now());
    }

    private AdapterScore score(VerificationType type, int confidence,
                                Map<String, String> signals) {
        return new AdapterScore(type, VerificationOutcome.SUCCEEDED, confidence,
            signals, Instant.now());
    }

    // --- Weighted Average ---
    @Test
    void weightedAverage_equalWeights_computesSimpleAverage() {
        var scores = List.of(
            score(VerificationType.IDENTITY_VERIFICATION, 85),
            score(VerificationType.SANCTIONS_SCREENING, 100),
            score(VerificationType.CREDIT_CHECK, 72)
        );

        var result = aggregator.assess(UUID.randomUUID(), "partner-1", scores, defaultConfig);

        // (85 + 100 + 72) / 3 = 85.67 ≈ 86
        assertEquals(86, result.compositeScore());
        assertEquals(RiskDecision.APPROVE, result.decision());
        assertEquals("LOW_RISK", result.riskTier());
    }

    @Test
    void weightedAverage_customWeights_appliesWeights() {
        var config = new RiskScoringConfig(
            "partner-1",
            Map.of(
                VerificationType.IDENTITY_VERIFICATION, 0.3,
                VerificationType.SANCTIONS_SCREENING, 0.4,
                VerificationType.CREDIT_CHECK, 0.3
            ),
            AggregationStrategy.WEIGHTED_AVERAGE,
            defaultConfig.tiers(),
            List.of(),
            "1", Instant.now()
        );

        var scores = List.of(
            score(VerificationType.IDENTITY_VERIFICATION, 85),
            score(VerificationType.SANCTIONS_SCREENING, 100),
            score(VerificationType.CREDIT_CHECK, 72)
        );

        var result = aggregator.assess(UUID.randomUUID(), "partner-1", scores, config);

        // (85*0.3 + 100*0.4 + 72*0.3) / (0.3+0.4+0.3) = (25.5+40+21.6)/1.0 = 87.1 ≈ 87
        assertEquals(87, result.compositeScore());
        assertEquals(RiskDecision.APPROVE, result.decision());
    }

    // --- Minimum Score Strategy ---
    @Test
    void minimumScore_returnsLowestScore() {
        var config = new RiskScoringConfig(
            "partner-1", Map.of(),
            AggregationStrategy.MINIMUM_SCORE,
            defaultConfig.tiers(), List.of(),
            "1", Instant.now()
        );

        var scores = List.of(
            score(VerificationType.IDENTITY_VERIFICATION, 90),
            score(VerificationType.CREDIT_CHECK, 45),
            score(VerificationType.SANCTIONS_SCREENING, 100)
        );

        var result = aggregator.assess(UUID.randomUUID(), "partner-1", scores, config);

        assertEquals(45, result.compositeScore());
        assertEquals(RiskDecision.REJECT, result.decision());
    }

    // --- Maximum Score Strategy ---
    @Test
    void maximumScore_returnsHighestScore() {
        var config = new RiskScoringConfig(
            "partner-1", Map.of(),
            AggregationStrategy.MAXIMUM_SCORE,
            defaultConfig.tiers(), List.of(),
            "1", Instant.now()
        );

        var scores = List.of(
            score(VerificationType.IDENTITY_VERIFICATION, 30),
            score(VerificationType.CREDIT_CHECK, 95)
        );

        var result = aggregator.assess(UUID.randomUUID(), "partner-1", scores, config);

        assertEquals(95, result.compositeScore());
        assertEquals(RiskDecision.APPROVE, result.decision());
    }

    // --- Tier Boundaries ---
    @Test
    void tierBoundary_exactly50_mapsToManualReview() {
        var scores = List.of(score(VerificationType.IDENTITY_VERIFICATION, 50));

        var result = aggregator.assess(UUID.randomUUID(), "partner-1", scores, defaultConfig);

        assertEquals(50, result.compositeScore());
        assertEquals("MEDIUM_RISK", result.riskTier());
        assertEquals(RiskDecision.MANUAL_REVIEW, result.decision());
    }

    @Test
    void tierBoundary_exactly80_mapsToApprove() {
        var scores = List.of(score(VerificationType.IDENTITY_VERIFICATION, 80));

        var result = aggregator.assess(UUID.randomUUID(), "partner-1", scores, defaultConfig);

        assertEquals(80, result.compositeScore());
        assertEquals("LOW_RISK", result.riskTier());
        assertEquals(RiskDecision.APPROVE, result.decision());
    }

    @Test
    void tierBoundary_49_mapsToReject() {
        var scores = List.of(score(VerificationType.IDENTITY_VERIFICATION, 49));

        var result = aggregator.assess(UUID.randomUUID(), "partner-1", scores, defaultConfig);

        assertEquals(49, result.compositeScore());
        assertEquals("HIGH_RISK", result.riskTier());
        assertEquals(RiskDecision.REJECT, result.decision());
    }

    // --- Override Rules ---
    @Test
    void overrideRule_sanctionsHitGreaterThanZero_forcesReject() {
        var config = new RiskScoringConfig(
            "partner-1", Map.of(),
            AggregationStrategy.WEIGHTED_AVERAGE,
            defaultConfig.tiers(),
            List.of(new OverrideRule(
                "rule-sanctions", "Sanctions hit override",
                new RuleCondition(VerificationType.SANCTIONS_SCREENING, "hitCount",
                    ComparisonOperator.GT, "0"),
                RiskDecision.REJECT, 1
            )),
            "1", Instant.now()
        );

        var scores = List.of(
            score(VerificationType.IDENTITY_VERIFICATION, 95),
            score(VerificationType.SANCTIONS_SCREENING, 30,
                Map.of("hitCount", "2"))
        );

        var result = aggregator.assess(UUID.randomUUID(), "partner-1", scores, config);

        assertTrue(result.overrideApplied());
        assertEquals("rule-sanctions", result.overrideRuleId());
        assertEquals(RiskDecision.REJECT, result.decision());
    }

    @Test
    void overrideRule_fraudListed_forcesReject() {
        var config = new RiskScoringConfig(
            "partner-1", Map.of(),
            AggregationStrategy.WEIGHTED_AVERAGE,
            defaultConfig.tiers(),
            List.of(new OverrideRule(
                "rule-fraud", "Fraud watchlist override",
                new RuleCondition(VerificationType.FRAUD_WATCHLIST_SCREENING, "listed",
                    ComparisonOperator.EQ, "true"),
                RiskDecision.REJECT, 1
            )),
            "1", Instant.now()
        );

        var scores = List.of(
            score(VerificationType.IDENTITY_VERIFICATION, 95),
            score(VerificationType.FRAUD_WATCHLIST_SCREENING, 5,
                Map.of("listed", "true"))
        );

        var result = aggregator.assess(UUID.randomUUID(), "partner-1", scores, config);

        assertTrue(result.overrideApplied());
        assertEquals(RiskDecision.REJECT, result.decision());
    }

    @Test
    void overrideRule_priorityOrder_firstMatchWins() {
        var config = new RiskScoringConfig(
            "partner-1", Map.of(),
            AggregationStrategy.WEIGHTED_AVERAGE,
            defaultConfig.tiers(),
            List.of(
                new OverrideRule(
                    "rule-2", "Lower priority rule",
                    new RuleCondition(VerificationType.SANCTIONS_SCREENING, "hitCount",
                        ComparisonOperator.GT, "0"),
                    RiskDecision.MANUAL_REVIEW, 10
                ),
                new OverrideRule(
                    "rule-1", "Higher priority rule",
                    new RuleCondition(VerificationType.SANCTIONS_SCREENING, "hitCount",
                        ComparisonOperator.GT, "0"),
                    RiskDecision.REJECT, 1
                )
            ),
            "1", Instant.now()
        );

        var scores = List.of(
            score(VerificationType.SANCTIONS_SCREENING, 30,
                Map.of("hitCount", "1"))
        );

        var result = aggregator.assess(UUID.randomUUID(), "partner-1", scores, config);

        assertTrue(result.overrideApplied());
        assertEquals("rule-1", result.overrideRuleId());
        assertEquals(RiskDecision.REJECT, result.decision());
    }

    @Test
    void overrideRule_noMatch_useTierDecision() {
        var config = new RiskScoringConfig(
            "partner-1", Map.of(),
            AggregationStrategy.WEIGHTED_AVERAGE,
            defaultConfig.tiers(),
            List.of(new OverrideRule(
                "rule-sanctions", "Sanctions hit override",
                new RuleCondition(VerificationType.SANCTIONS_SCREENING, "hitCount",
                    ComparisonOperator.GT, "0"),
                RiskDecision.REJECT, 1
            )),
            "1", Instant.now()
        );

        var scores = List.of(
            score(VerificationType.SANCTIONS_SCREENING, 100,
                Map.of("hitCount", "0")),
            score(VerificationType.IDENTITY_VERIFICATION, 90)
        );

        var result = aggregator.assess(UUID.randomUUID(), "partner-1", scores, config);

        assertFalse(result.overrideApplied());
        assertEquals(RiskDecision.APPROVE, result.decision());
    }

    // --- Edge Cases ---
    @Test
    void emptyScores_returns0WithManualReview() {
        var result = aggregator.assess(
            UUID.randomUUID(), "partner-1", List.of(), defaultConfig);

        assertEquals(0, result.compositeScore());
        assertEquals(RiskDecision.REJECT, result.decision());
    }

    @Test
    void systemOutageScores_excluded_fromAggregation() {
        var scores = List.of(
            score(VerificationType.IDENTITY_VERIFICATION, 90),
            new AdapterScore(VerificationType.CREDIT_CHECK,
                VerificationOutcome.SYSTEM_OUTAGE, 0, Map.of(), Instant.now())
        );

        var result = aggregator.assess(UUID.randomUUID(), "partner-1", scores, defaultConfig);

        // Only the IDENTITY score (90) should be considered
        assertEquals(90, result.compositeScore());
        assertEquals(RiskDecision.APPROVE, result.decision());
    }

    @Test
    void allSystemOutage_returns0() {
        var scores = List.of(
            new AdapterScore(VerificationType.IDENTITY_VERIFICATION,
                VerificationOutcome.SYSTEM_OUTAGE, 0, Map.of(), Instant.now()),
            new AdapterScore(VerificationType.CREDIT_CHECK,
                VerificationOutcome.SYSTEM_OUTAGE, 0, Map.of(), Instant.now())
        );

        var result = aggregator.assess(UUID.randomUUID(), "partner-1", scores, defaultConfig);

        assertEquals(0, result.compositeScore());
    }

    @Test
    void assessmentFields_populated() {
        var verificationId = UUID.randomUUID();
        var scores = List.of(score(VerificationType.IDENTITY_VERIFICATION, 85));

        var result = aggregator.assess(verificationId, "partner-1", scores, defaultConfig);

        assertNotNull(result.assessmentId());
        assertEquals(verificationId, result.verificationId());
        assertEquals("partner-1", result.partnerId());
        assertNotNull(result.assessedAt());
        assertEquals(1, result.individualScores().size());
    }
}
