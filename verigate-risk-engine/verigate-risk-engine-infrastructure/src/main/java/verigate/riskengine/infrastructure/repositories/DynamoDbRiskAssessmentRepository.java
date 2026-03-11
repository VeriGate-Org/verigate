/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.infrastructure.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import verigate.riskengine.domain.enums.RiskDecision;
import verigate.riskengine.domain.models.AdapterScore;
import verigate.riskengine.domain.models.RiskAssessment;
import verigate.riskengine.domain.services.RiskAssessmentRepository;
import verigate.riskengine.infrastructure.repositories.datamodels.RiskAssessmentDataModel;

public class DynamoDbRiskAssessmentRepository implements RiskAssessmentRepository {

    private static final Logger LOG = LoggerFactory.getLogger(DynamoDbRiskAssessmentRepository.class);

    private final DynamoDbTable<RiskAssessmentDataModel> table;
    private final DynamoDbIndex<RiskAssessmentDataModel> partnerIndex;
    private final ObjectMapper objectMapper;

    @Inject
    public DynamoDbRiskAssessmentRepository(
            DynamoDbEnhancedClient enhancedClient,
            ObjectMapper objectMapper,
            @Named("riskAssessmentsTableName") String tableName) {
        this.table = enhancedClient.table(tableName,
            TableSchema.fromBean(RiskAssessmentDataModel.class));
        this.partnerIndex = table.index("partner-assessed-index");
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(RiskAssessment assessment) {
        try {
            table.putItem(toDataModel(assessment));
        } catch (Exception e) {
            LOG.error("Failed to save risk assessment for verificationId={}",
                assessment.verificationId(), e);
            throw new RuntimeException("Failed to save risk assessment", e);
        }
    }

    @Override
    public Optional<RiskAssessment> findByVerificationId(UUID verificationId) {
        try {
            var item = table.getItem(
                Key.builder().partitionValue(verificationId.toString()).build());
            if (item == null) return Optional.empty();
            return Optional.of(toDomain(item));
        } catch (Exception e) {
            LOG.error("Failed to fetch risk assessment for verificationId={}", verificationId, e);
            return Optional.empty();
        }
    }

    @Override
    public List<RiskAssessment> findByPartnerId(String partnerId, int limit) {
        try {
            var request = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(
                    Key.builder().partitionValue(partnerId).build()))
                .scanIndexForward(false)
                .limit(limit)
                .build();

            return partnerIndex.query(request).stream()
                .flatMap(page -> page.items().stream())
                .map(this::toDomain)
                .limit(limit)
                .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.error("Failed to fetch risk assessments for partnerId={}", partnerId, e);
            return List.of();
        }
    }

    private RiskAssessment toDomain(RiskAssessmentDataModel model) {
        try {
            List<AdapterScore> scores = objectMapper.readValue(
                model.getIndividualScoresJson(), new TypeReference<>() {});

            return new RiskAssessment(
                UUID.fromString(model.getAssessmentId()),
                UUID.fromString(model.getVerificationId()),
                model.getPartnerId(),
                model.getCompositeScore(),
                model.getRiskTier(),
                RiskDecision.valueOf(model.getDecision()),
                model.getDecisionReason(),
                scores,
                model.isOverrideApplied(),
                model.getOverrideRuleId(),
                Instant.parse(model.getAssessedAt())
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize risk assessment", e);
        }
    }

    private RiskAssessmentDataModel toDataModel(RiskAssessment assessment) {
        try {
            return RiskAssessmentDataModel.builder()
                .verificationId(assessment.verificationId().toString())
                .assessmentId(assessment.assessmentId().toString())
                .partnerId(assessment.partnerId())
                .compositeScore(assessment.compositeScore())
                .riskTier(assessment.riskTier())
                .decision(assessment.decision().name())
                .decisionReason(assessment.decisionReason())
                .individualScoresJson(objectMapper.writeValueAsString(assessment.individualScores()))
                .overrideApplied(assessment.overrideApplied())
                .overrideRuleId(assessment.overrideRuleId())
                .assessedAt(assessment.assessedAt().toString())
                .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize risk assessment", e);
        }
    }
}
