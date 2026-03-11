/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.domain.models;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import verigate.riskengine.domain.enums.AggregationStrategy;
import verigate.riskengine.domain.enums.RiskDecision;
import verigate.riskengine.domain.enums.VerificationType;

class RiskScoringConfigTest {

    // --- System Default ---

    @Test
    void systemDefault_hasExpectedTiers() {
        var config = RiskScoringConfig.systemDefault("partner-1");

        assertEquals("partner-1", config.partnerId());
        assertEquals(AggregationStrategy.WEIGHTED_AVERAGE, config.strategy());
        assertEquals(3, config.tiers().size());
        assertTrue(config.overrideRules().isEmpty());
        assertTrue(config.weights().isEmpty());
    }

    @Test
    void systemDefault_tiersMapCorrectDecisions() {
        var config = RiskScoringConfig.systemDefault("partner-1");
        var tiers = config.tiers();

        // HIGH_RISK: 0-50 → REJECT
        var highRisk = tiers.stream().filter(t -> t.name().equals("HIGH_RISK")).findFirst().orElseThrow();
        assertEquals(RiskDecision.REJECT, highRisk.decision());
        assertEquals(0, highRisk.lowerBound());
        assertEquals(50, highRisk.upperBound());

        // MEDIUM_RISK: 50-80 → MANUAL_REVIEW
        var medRisk = tiers.stream().filter(t -> t.name().equals("MEDIUM_RISK")).findFirst().orElseThrow();
        assertEquals(RiskDecision.MANUAL_REVIEW, medRisk.decision());

        // LOW_RISK: 80-101 → APPROVE
        var lowRisk = tiers.stream().filter(t -> t.name().equals("LOW_RISK")).findFirst().orElseThrow();
        assertEquals(RiskDecision.APPROVE, lowRisk.decision());
    }

    // --- Partner Isolation ---

    @Test
    void differentPartners_haveIndependentConfigs() {
        var configA = new RiskScoringConfig(
            "partner-A",
            Map.of(VerificationType.CREDIT_CHECK, 0.8),
            AggregationStrategy.WEIGHTED_AVERAGE,
            List.of(new RiskTier("LOW", 70, 101, RiskDecision.APPROVE),
                    new RiskTier("HIGH", 0, 70, RiskDecision.REJECT)),
            List.of(),
            "1", Instant.now()
        );

        var configB = new RiskScoringConfig(
            "partner-B",
            Map.of(VerificationType.SANCTIONS_SCREENING, 0.9),
            AggregationStrategy.MINIMUM_SCORE,
            List.of(new RiskTier("LOW", 60, 101, RiskDecision.APPROVE),
                    new RiskTier("HIGH", 0, 60, RiskDecision.REJECT)),
            List.of(),
            "1", Instant.now()
        );

        // Configs are completely independent
        assertNotEquals(configA.partnerId(), configB.partnerId());
        assertNotEquals(configA.strategy(), configB.strategy());
        assertNotEquals(configA.weights(), configB.weights());
        assertNotEquals(configA.tiers().get(0).lowerBound(), configB.tiers().get(0).lowerBound());
    }

    @Test
    void customConfig_doesNotAffectSystemDefault() {
        var customConfig = new RiskScoringConfig(
            "partner-custom",
            Map.of(VerificationType.CREDIT_CHECK, 0.8),
            AggregationStrategy.MINIMUM_SCORE,
            List.of(new RiskTier("PASS", 60, 101, RiskDecision.APPROVE),
                    new RiskTier("FAIL", 0, 60, RiskDecision.REJECT)),
            List.of(),
            "2", Instant.now()
        );

        var defaultConfig = RiskScoringConfig.systemDefault("partner-other");

        // System default is unaffected by custom configs
        assertEquals(AggregationStrategy.WEIGHTED_AVERAGE, defaultConfig.strategy());
        assertEquals(3, defaultConfig.tiers().size());
        assertTrue(defaultConfig.weights().isEmpty());

        // Custom config has its own settings
        assertEquals(AggregationStrategy.MINIMUM_SCORE, customConfig.strategy());
        assertEquals(2, customConfig.tiers().size());
    }

    // --- Validation ---

    @Test
    void constructor_blankPartnerId_throws() {
        assertThrows(IllegalArgumentException.class, () ->
            new RiskScoringConfig(
                "", Map.of(), AggregationStrategy.WEIGHTED_AVERAGE,
                List.of(new RiskTier("T", 0, 100, RiskDecision.APPROVE)),
                List.of(), "1", Instant.now()
            )
        );
    }

    @Test
    void constructor_emptyTiers_throws() {
        assertThrows(IllegalArgumentException.class, () ->
            new RiskScoringConfig(
                "partner-1", Map.of(), AggregationStrategy.WEIGHTED_AVERAGE,
                List.of(), List.of(), "1", Instant.now()
            )
        );
    }

    @Test
    void constructor_nullWeights_defaultsToEmpty() {
        var config = new RiskScoringConfig(
            "partner-1", null, AggregationStrategy.WEIGHTED_AVERAGE,
            List.of(new RiskTier("T", 0, 100, RiskDecision.APPROVE)),
            List.of(), "1", Instant.now()
        );

        assertNotNull(config.weights());
        assertTrue(config.weights().isEmpty());
    }

    @Test
    void constructor_nullStrategy_defaultsToWeightedAverage() {
        var config = new RiskScoringConfig(
            "partner-1", Map.of(), null,
            List.of(new RiskTier("T", 0, 100, RiskDecision.APPROVE)),
            List.of(), "1", Instant.now()
        );

        assertEquals(AggregationStrategy.WEIGHTED_AVERAGE, config.strategy());
    }

    @Test
    void constructor_nullOverrideRules_defaultsToEmpty() {
        var config = new RiskScoringConfig(
            "partner-1", Map.of(), AggregationStrategy.WEIGHTED_AVERAGE,
            List.of(new RiskTier("T", 0, 100, RiskDecision.APPROVE)),
            null, "1", Instant.now()
        );

        assertNotNull(config.overrideRules());
        assertTrue(config.overrideRules().isEmpty());
    }
}
