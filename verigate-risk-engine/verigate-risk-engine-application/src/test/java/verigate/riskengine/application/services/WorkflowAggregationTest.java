/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.application.services;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verigate.riskengine.domain.enums.*;
import verigate.riskengine.domain.models.*;

/**
 * Tests that simulate the full workflow: checks arriving at different times,
 * aggregation triggering only when all expected checks are complete,
 * and timeout scenarios with partial completion.
 */
class WorkflowAggregationTest {

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

    // --- Full Workflow: 3 Checks Arrive Sequentially ---

    @Test
    void threeChecks_aggregationTriggersOnLastArrival() {
        UUID workflowId = UUID.randomUUID();

        // Create workflow with 3 expected checks
        Map<VerificationType, UUID> expected = Map.of(
            VerificationType.IDENTITY_VERIFICATION, UUID.randomUUID(),
            VerificationType.SANCTIONS_SCREENING, UUID.randomUUID(),
            VerificationType.CREDIT_CHECK, UUID.randomUUID()
        );

        // Step 1: First check arrives
        var completed1 = new HashMap<VerificationType, AdapterScore>();
        completed1.put(VerificationType.IDENTITY_VERIFICATION,
            score(VerificationType.IDENTITY_VERIFICATION, 85));

        var workflow1 = new VerificationWorkflow(
            workflowId, "partner-1", "policy-1",
            expected, completed1, WorkflowStatus.PENDING, Instant.now(), null
        );

        assertFalse(workflow1.isComplete());

        // Step 2: Second check arrives
        var completed2 = new HashMap<>(completed1);
        completed2.put(VerificationType.SANCTIONS_SCREENING,
            score(VerificationType.SANCTIONS_SCREENING, 100));

        var workflow2 = new VerificationWorkflow(
            workflowId, "partner-1", "policy-1",
            expected, completed2, WorkflowStatus.PENDING, Instant.now(), null
        );

        assertFalse(workflow2.isComplete());

        // Step 3: Third check arrives — now complete
        var completed3 = new HashMap<>(completed2);
        completed3.put(VerificationType.CREDIT_CHECK,
            score(VerificationType.CREDIT_CHECK, 72));

        var workflow3 = new VerificationWorkflow(
            workflowId, "partner-1", "policy-1",
            expected, completed3, WorkflowStatus.PENDING, Instant.now(), null
        );

        assertTrue(workflow3.isComplete());

        // Trigger aggregation
        List<AdapterScore> scores = List.copyOf(completed3.values());
        var result = aggregator.assess(workflowId, "partner-1", scores, defaultConfig);

        assertNotNull(result);
        assertEquals(workflowId, result.verificationId());
        assertEquals(3, result.individualScores().size());
        // (85 + 100 + 72) / 3 = 85.67 ≈ 86
        assertEquals(86, result.compositeScore());
        assertEquals(RiskDecision.APPROVE, result.decision());
    }

    // --- Timeout: 2 of 3 Complete, Assess with Partial Data ---

    @Test
    void timeout_assessWithPartialResults() {
        UUID workflowId = UUID.randomUUID();

        // 3 expected checks, only 2 completed
        var completed = new HashMap<VerificationType, AdapterScore>();
        completed.put(VerificationType.IDENTITY_VERIFICATION,
            score(VerificationType.IDENTITY_VERIFICATION, 90));
        completed.put(VerificationType.SANCTIONS_SCREENING,
            score(VerificationType.SANCTIONS_SCREENING, 100));

        var workflow = new VerificationWorkflow(
            workflowId, "partner-1", "policy-1",
            Map.of(
                VerificationType.IDENTITY_VERIFICATION, UUID.randomUUID(),
                VerificationType.SANCTIONS_SCREENING, UUID.randomUUID(),
                VerificationType.CREDIT_CHECK, UUID.randomUUID()
            ),
            completed,
            WorkflowStatus.PENDING,
            Instant.now(), null
        );

        assertFalse(workflow.isComplete());

        // On timeout, assess with available scores only
        List<AdapterScore> scores = List.copyOf(completed.values());
        var result = aggregator.assess(workflowId, "partner-1", scores, defaultConfig);

        // (90 + 100) / 2 = 95
        assertEquals(95, result.compositeScore());
        assertEquals(RiskDecision.APPROVE, result.decision());
        assertEquals(2, result.individualScores().size());
    }

    @Test
    void timeout_singleCheckComplete_assessesWithOne() {
        UUID workflowId = UUID.randomUUID();

        var completed = new HashMap<VerificationType, AdapterScore>();
        completed.put(VerificationType.CREDIT_CHECK,
            score(VerificationType.CREDIT_CHECK, 45));

        List<AdapterScore> scores = List.copyOf(completed.values());
        var result = aggregator.assess(workflowId, "partner-1", scores, defaultConfig);

        assertEquals(45, result.compositeScore());
        assertEquals(RiskDecision.REJECT, result.decision());
    }

    // --- Partner Isolation in Aggregation ---

    @Test
    void partnerA_customConfig_doesNotAffectPartnerB() {
        // Partner A: strict config — 70+ to approve
        var configA = new RiskScoringConfig(
            "partner-A",
            Map.of(VerificationType.IDENTITY_VERIFICATION, 1.0),
            AggregationStrategy.WEIGHTED_AVERAGE,
            List.of(
                new RiskTier("PASS", 70, 101, RiskDecision.APPROVE),
                new RiskTier("FAIL", 0, 70, RiskDecision.REJECT)
            ),
            List.of(),
            "1", Instant.now()
        );

        // Partner B: lenient config — 50+ to approve
        var configB = new RiskScoringConfig(
            "partner-B",
            Map.of(VerificationType.IDENTITY_VERIFICATION, 1.0),
            AggregationStrategy.WEIGHTED_AVERAGE,
            List.of(
                new RiskTier("PASS", 50, 101, RiskDecision.APPROVE),
                new RiskTier("FAIL", 0, 50, RiskDecision.REJECT)
            ),
            List.of(),
            "1", Instant.now()
        );

        // Same score: 65
        var scores = List.of(score(VerificationType.IDENTITY_VERIFICATION, 65));

        var resultA = aggregator.assess(UUID.randomUUID(), "partner-A", scores, configA);
        var resultB = aggregator.assess(UUID.randomUUID(), "partner-B", scores, configB);

        // Partner A rejects, Partner B approves
        assertEquals(RiskDecision.REJECT, resultA.decision());
        assertEquals(RiskDecision.APPROVE, resultB.decision());
        assertEquals("partner-A", resultA.partnerId());
        assertEquals("partner-B", resultB.partnerId());
    }

    @Test
    void partnerWithOverrides_doesNotAffectPartnerWithout() {
        // Partner A has a sanctions override rule
        var configA = new RiskScoringConfig(
            "partner-A", Map.of(),
            AggregationStrategy.WEIGHTED_AVERAGE,
            defaultConfig.tiers(),
            List.of(new OverrideRule(
                "sanctions-override", "Auto-reject on sanctions",
                new RuleCondition(VerificationType.SANCTIONS_SCREENING, "hitCount",
                    ComparisonOperator.GT, "0"),
                RiskDecision.REJECT, 1
            )),
            "1", Instant.now()
        );

        // Partner B has no override rules
        var configB = RiskScoringConfig.systemDefault("partner-B");

        var scores = List.of(
            score(VerificationType.IDENTITY_VERIFICATION, 90),
            new AdapterScore(VerificationType.SANCTIONS_SCREENING,
                VerificationOutcome.SUCCEEDED, 30,
                Map.of("hitCount", "1"), Instant.now())
        );

        var resultA = aggregator.assess(UUID.randomUUID(), "partner-A", scores, configA);
        var resultB = aggregator.assess(UUID.randomUUID(), "partner-B", scores, configB);

        // Partner A overridden to REJECT
        assertTrue(resultA.overrideApplied());
        assertEquals(RiskDecision.REJECT, resultA.decision());

        // Partner B uses tier-based decision (avg of 90 + 30 = 60 → MANUAL_REVIEW)
        assertFalse(resultB.overrideApplied());
        assertEquals(RiskDecision.MANUAL_REVIEW, resultB.decision());
    }

    // --- Default Config Fallback ---

    @Test
    void systemDefault_usedWhenNoCustomConfig() {
        var config = RiskScoringConfig.systemDefault("new-partner");

        var scores = List.of(
            score(VerificationType.IDENTITY_VERIFICATION, 85),
            score(VerificationType.CREDIT_CHECK, 75)
        );

        var result = aggregator.assess(UUID.randomUUID(), "new-partner", scores, config);

        // (85 + 75) / 2 = 80 → LOW_RISK → APPROVE
        assertEquals(80, result.compositeScore());
        assertEquals("LOW_RISK", result.riskTier());
        assertEquals(RiskDecision.APPROVE, result.decision());
        assertFalse(result.overrideApplied());
    }

    @Test
    void systemDefault_equalWeightsForAllTypes() {
        var config = RiskScoringConfig.systemDefault("partner-1");

        // Different verification types should all have equal weight (empty map = 1.0 each)
        assertTrue(config.weights().isEmpty());

        var scores = List.of(
            score(VerificationType.IDENTITY_VERIFICATION, 100),
            score(VerificationType.CREDIT_CHECK, 0)
        );

        var result = aggregator.assess(UUID.randomUUID(), "partner-1", scores, config);

        // Equal weight: (100 + 0) / 2 = 50
        assertEquals(50, result.compositeScore());
    }
}
