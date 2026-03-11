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
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import verigate.riskengine.domain.enums.AggregationStrategy;
import verigate.riskengine.domain.enums.VerificationType;
import verigate.riskengine.domain.models.OverrideRule;
import verigate.riskengine.domain.models.RiskScoringConfig;
import verigate.riskengine.domain.models.RiskTier;
import verigate.riskengine.domain.services.RiskScoringConfigRepository;
import verigate.riskengine.infrastructure.repositories.datamodels.RiskScoringConfigDataModel;

public class DynamoDbRiskScoringConfigRepository implements RiskScoringConfigRepository {

    private static final Logger LOG = LoggerFactory.getLogger(DynamoDbRiskScoringConfigRepository.class);

    private final DynamoDbTable<RiskScoringConfigDataModel> table;
    private final ObjectMapper objectMapper;

    @Inject
    public DynamoDbRiskScoringConfigRepository(
            DynamoDbEnhancedClient enhancedClient,
            ObjectMapper objectMapper,
            @Named("riskScoringConfigTableName") String tableName) {
        this.table = enhancedClient.table(tableName,
            TableSchema.fromBean(RiskScoringConfigDataModel.class));
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<RiskScoringConfig> findByPartnerId(String partnerId) {
        try {
            var item = table.getItem(Key.builder().partitionValue(partnerId).build());
            if (item == null) return Optional.empty();
            return Optional.of(toDomain(item));
        } catch (Exception e) {
            LOG.error("Failed to fetch risk scoring config for partnerId={}", partnerId, e);
            return Optional.empty();
        }
    }

    @Override
    public void save(RiskScoringConfig config) {
        try {
            table.putItem(toDataModel(config));
        } catch (Exception e) {
            LOG.error("Failed to save risk scoring config for partnerId={}", config.partnerId(), e);
            throw new RuntimeException("Failed to save risk scoring config", e);
        }
    }

    private RiskScoringConfig toDomain(RiskScoringConfigDataModel model) {
        try {
            Map<VerificationType, Double> weights = objectMapper.readValue(
                model.getWeightsJson(), new TypeReference<>() {});
            List<RiskTier> tiers = objectMapper.readValue(
                model.getTiersJson(), new TypeReference<>() {});
            List<OverrideRule> rules = objectMapper.readValue(
                model.getOverrideRulesJson(), new TypeReference<>() {});

            return new RiskScoringConfig(
                model.getPartnerId(),
                weights,
                AggregationStrategy.valueOf(model.getStrategy()),
                tiers,
                rules,
                model.getVersion(),
                Instant.parse(model.getUpdatedAt())
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize risk scoring config", e);
        }
    }

    private RiskScoringConfigDataModel toDataModel(RiskScoringConfig config) {
        try {
            return RiskScoringConfigDataModel.builder()
                .partnerId(config.partnerId())
                .weightsJson(objectMapper.writeValueAsString(config.weights()))
                .strategy(config.strategy().name())
                .tiersJson(objectMapper.writeValueAsString(config.tiers()))
                .overrideRulesJson(objectMapper.writeValueAsString(config.overrideRules()))
                .version(config.version())
                .updatedAt(config.updatedAt().toString())
                .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize risk scoring config", e);
        }
    }
}
