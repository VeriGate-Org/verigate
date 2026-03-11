/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.application.services;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.riskengine.domain.enums.RiskDecision;
import verigate.riskengine.domain.enums.VerificationOutcome;
import verigate.riskengine.domain.models.AdapterScore;
import verigate.riskengine.domain.models.OverrideRule;
import verigate.riskengine.domain.models.RiskAssessment;
import verigate.riskengine.domain.models.RiskScoringConfig;
import verigate.riskengine.domain.models.RiskTier;
import verigate.riskengine.domain.services.RiskAggregator;

/**
 * Default implementation of risk aggregation. Computes a composite score
 * using the configured strategy, maps to a risk tier, and evaluates
 * override rules.
 */
public class DefaultRiskAggregator implements RiskAggregator {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultRiskAggregator.class);

    @Override
    public RiskAssessment assess(UUID verificationId, String partnerId,
                                 List<AdapterScore> scores, RiskScoringConfig config) {
        // Filter out SYSTEM_OUTAGE scores
        List<AdapterScore> validScores = scores.stream()
            .filter(s -> s.outcome() != VerificationOutcome.SYSTEM_OUTAGE)
            .toList();

        int compositeScore;
        if (validScores.isEmpty()) {
            LOG.warn("No valid scores for verification {}, defaulting to 0", verificationId);
            compositeScore = 0;
        } else {
            compositeScore = computeComposite(validScores, config);
        }

        // Map to tier
        String tierName = "UNKNOWN";
        RiskDecision tierDecision = RiskDecision.MANUAL_REVIEW;
        for (RiskTier tier : config.tiers()) {
            if (tier.contains(compositeScore)) {
                tierName = tier.name();
                tierDecision = tier.decision();
                break;
            }
        }

        // Evaluate override rules
        boolean overrideApplied = false;
        String overrideRuleId = null;
        RiskDecision finalDecision = tierDecision;
        String decisionReason = "Score " + compositeScore + " maps to tier " + tierName;

        List<OverrideRule> sortedRules = config.overrideRules().stream()
            .sorted(Comparator.comparingInt(OverrideRule::priority))
            .toList();

        for (OverrideRule rule : sortedRules) {
            if (evaluateRule(rule, scores)) {
                overrideApplied = true;
                overrideRuleId = rule.id();
                finalDecision = rule.forcedDecision();
                decisionReason = "Override rule '" + rule.name() + "' triggered: "
                    + rule.condition().checkType() + "."
                    + rule.condition().signalKey() + " "
                    + rule.condition().operator() + " "
                    + rule.condition().value();
                LOG.info("Override rule '{}' triggered for verification {}",
                    rule.name(), verificationId);
                break;
            }
        }

        return new RiskAssessment(
            UUID.randomUUID(),
            verificationId,
            partnerId,
            compositeScore,
            tierName,
            finalDecision,
            decisionReason,
            scores,
            overrideApplied,
            overrideRuleId,
            Instant.now()
        );
    }

    private int computeComposite(List<AdapterScore> scores, RiskScoringConfig config) {
        return switch (config.strategy()) {
            case WEIGHTED_AVERAGE -> computeWeightedAverage(scores, config);
            case MINIMUM_SCORE -> scores.stream()
                .mapToInt(AdapterScore::confidenceScore)
                .min()
                .orElse(0);
            case MAXIMUM_SCORE -> scores.stream()
                .mapToInt(AdapterScore::confidenceScore)
                .max()
                .orElse(0);
        };
    }

    private int computeWeightedAverage(List<AdapterScore> scores, RiskScoringConfig config) {
        double weightedSum = 0;
        double totalWeight = 0;

        for (AdapterScore score : scores) {
            double weight = config.weights().getOrDefault(score.verificationType(), 1.0);
            weightedSum += score.confidenceScore() * weight;
            totalWeight += weight;
        }

        if (totalWeight == 0) return 0;
        return (int) Math.round(weightedSum / totalWeight);
    }

    private boolean evaluateRule(OverrideRule rule, List<AdapterScore> scores) {
        return scores.stream()
            .filter(s -> s.verificationType() == rule.condition().checkType())
            .anyMatch(s -> {
                String actualValue = s.signals().get(rule.condition().signalKey());
                return rule.condition().evaluate(actualValue);
            });
    }
}
