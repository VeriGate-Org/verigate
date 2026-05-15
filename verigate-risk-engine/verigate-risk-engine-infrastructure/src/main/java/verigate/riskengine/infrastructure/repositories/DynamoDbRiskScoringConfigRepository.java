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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import verigate.riskengine.domain.enums.AggregationStrategy;
import verigate.riskengine.domain.enums.VerificationType;
import verigate.riskengine.domain.models.OverrideRule;
import verigate.riskengine.domain.models.RiskScoringConfig;
import verigate.riskengine.domain.models.RiskTier;
import verigate.riskengine.domain.services.RiskScoringConfigRepository;

public class DynamoDbRiskScoringConfigRepository implements RiskScoringConfigRepository {

    private static final Logger LOG = LoggerFactory.getLogger(DynamoDbRiskScoringConfigRepository.class);

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;
    private final ObjectMapper objectMapper;

    @Inject
    public DynamoDbRiskScoringConfigRepository(
            DynamoDbClient dynamoDbClient,
            ObjectMapper objectMapper,
            @Named("partnerHubTableName") String tableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<RiskScoringConfig> findByPartnerId(String partnerId) {
        try {
            GetItemResponse response = dynamoDbClient.getItem(GetItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(
                    "partnerId", AttributeValue.builder().s(partnerId).build(),
                    "entityType", AttributeValue.builder().s("RISK_SCORING").build()))
                .build());

            if (!response.hasItem() || response.item().isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(toDomain(response.item()));
        } catch (Exception e) {
            LOG.error("Failed to fetch risk scoring config for partnerId={}", partnerId, e);
            return Optional.empty();
        }
    }

    @Override
    public void save(RiskScoringConfig config) {
        try {
            dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(toItem(config))
                .build());
        } catch (Exception e) {
            LOG.error("Failed to save risk scoring config for partnerId={}", config.partnerId(), e);
            throw new RuntimeException("Failed to save risk scoring config", e);
        }
    }

    private RiskScoringConfig toDomain(Map<String, AttributeValue> item) {
        try {
            Map<VerificationType, Double> weights = objectMapper.readValue(
                getStr(item, "weightsJson"), new TypeReference<>() {});
            List<RiskTier> tiers = objectMapper.readValue(
                getStr(item, "tiersJson"), new TypeReference<>() {});
            List<OverrideRule> rules = objectMapper.readValue(
                getStr(item, "overrideRulesJson"), new TypeReference<>() {});

            return new RiskScoringConfig(
                getStr(item, "partnerId"),
                weights,
                AggregationStrategy.valueOf(getStr(item, "strategy")),
                tiers,
                rules,
                getStr(item, "version"),
                Instant.parse(getStr(item, "updatedAt"))
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize risk scoring config", e);
        }
    }

    private Map<String, AttributeValue> toItem(RiskScoringConfig config) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("partnerId", AttributeValue.builder().s(config.partnerId()).build());
            item.put("entityType", AttributeValue.builder().s("RISK_SCORING").build());
            item.put("weightsJson",
                AttributeValue.builder().s(objectMapper.writeValueAsString(config.weights())).build());
            item.put("strategy",
                AttributeValue.builder().s(config.strategy().name()).build());
            item.put("tiersJson",
                AttributeValue.builder().s(objectMapper.writeValueAsString(config.tiers())).build());
            item.put("overrideRulesJson",
                AttributeValue.builder().s(objectMapper.writeValueAsString(config.overrideRules())).build());
            if (config.version() != null) {
                item.put("version", AttributeValue.builder().s(config.version()).build());
            }
            item.put("updatedAt",
                AttributeValue.builder().s(config.updatedAt().toString()).build());
            return item;
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize risk scoring config", e);
        }
    }

    private static String getStr(Map<String, AttributeValue> item, String key) {
        AttributeValue val = item.get(key);
        return (val != null && val.s() != null) ? val.s() : null;
    }
}
