/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.infrastructure.repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import verigate.partner.domain.models.*;
import verigate.partner.domain.repositories.PartnerConfigurationRepository;

import java.time.Instant;
import java.util.*;

/**
 * DynamoDB implementation of PartnerConfigurationRepository.
 */
public class DynamoDbPartnerConfigurationRepository implements PartnerConfigurationRepository {

    private static final Logger logger = LoggerFactory.getLogger(
        DynamoDbPartnerConfigurationRepository.class);

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;
    private final ObjectMapper objectMapper;

    public DynamoDbPartnerConfigurationRepository(DynamoDbClient dynamoDbClient,
                                                  String tableName,
                                                  ObjectMapper objectMapper) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<PartnerConfiguration> findByPartnerId(String partnerId) {
        try {
            QueryResponse response = dynamoDbClient.query(QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("partnerId = :pid AND begins_with(entityType, :prefix)")
                .expressionAttributeValues(Map.of(
                    ":pid", AttributeValue.builder().s(partnerId).build(),
                    ":prefix", AttributeValue.builder().s("CONFIG#").build()))
                .build());

            if (!response.hasItems() || response.items().isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(assembleConfiguration(partnerId, response.items()));
        } catch (Exception e) {
            logger.error("Failed to find configuration for partner: {}", partnerId, e);
            throw new RuntimeException("Failed to find partner configuration", e);
        }
    }

    @Override
    public PartnerConfiguration save(PartnerConfiguration configuration) {
        try {
            saveConfigItem(configuration.getPartnerId(), "VERIFICATION_FLOW",
                objectMapper.writeValueAsString(configuration.getVerificationFlowConfiguration()));
            if (configuration.getApiConfiguration() != null) {
                saveConfigItem(configuration.getPartnerId(), "API",
                    objectMapper.writeValueAsString(configuration.getApiConfiguration()));
            }
            if (configuration.getBillingConfiguration() != null) {
                saveConfigItem(configuration.getPartnerId(), "BILLING",
                    objectMapper.writeValueAsString(configuration.getBillingConfiguration()));
            }
            logger.info("Saved configuration for partner: {}", configuration.getPartnerId());
            return configuration;
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize configuration for partner: {}",
                configuration.getPartnerId(), e);
            throw new RuntimeException("Failed to serialize partner configuration", e);
        }
    }

    @Override
    public boolean existsByPartnerId(String partnerId) {
        return findByPartnerId(partnerId).isPresent();
    }

    @Override
    public boolean deleteByPartnerId(String partnerId) {
        try {
            QueryResponse response = dynamoDbClient.query(QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("partnerId = :pid AND begins_with(entityType, :prefix)")
                .expressionAttributeValues(Map.of(
                    ":pid", AttributeValue.builder().s(partnerId).build(),
                    ":prefix", AttributeValue.builder().s("CONFIG#").build()))
                .build());

            for (Map<String, AttributeValue> item : response.items()) {
                dynamoDbClient.deleteItem(DeleteItemRequest.builder()
                    .tableName(tableName)
                    .key(Map.of(
                        "partnerId", item.get("partnerId"),
                        "entityType", item.get("entityType")))
                    .build());
            }
            return true;
        } catch (Exception e) {
            logger.error("Failed to delete configuration for partner: {}", partnerId, e);
            return false;
        }
    }

    private void saveConfigItem(String partnerId, String configurationType, String json) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("partnerId", AttributeValue.builder().s(partnerId).build());
        item.put("entityType", AttributeValue.builder().s("CONFIG#" + configurationType).build());
        item.put("configurationJson", AttributeValue.builder().s(json).build());
        item.put("updatedAt", AttributeValue.builder().s(Instant.now().toString()).build());

        dynamoDbClient.putItem(PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .build());
    }

    private PartnerConfiguration assembleConfiguration(String partnerId,
                                                       List<Map<String, AttributeValue>> items) {
        VerificationFlowConfiguration flowConfig = null;
        ApiConfiguration apiConfig = null;
        BillingConfiguration billingConfig = null;

        for (Map<String, AttributeValue> item : items) {
            String entityType = item.get("entityType").s();
            String type = entityType.substring("CONFIG#".length());
            String json = item.get("configurationJson").s();
            try {
                switch (type) {
                    case "VERIFICATION_FLOW" ->
                        flowConfig = objectMapper.readValue(json, VerificationFlowConfiguration.class);
                    case "API" ->
                        apiConfig = objectMapper.readValue(json, ApiConfiguration.class);
                    case "BILLING" ->
                        billingConfig = objectMapper.readValue(json, BillingConfiguration.class);
                    default -> logger.warn("Unknown configuration type: {}", type);
                }
            } catch (JsonProcessingException e) {
                logger.error("Failed to deserialize {} config for partner {}", type, partnerId, e);
            }
        }

        return new PartnerConfiguration(
            partnerId,
            flowConfig != null ? flowConfig : VerificationFlowConfiguration.defaultConfiguration(),
            apiConfig,
            billingConfig != null ? billingConfig : BillingConfiguration.defaultConfiguration()
        );
    }
}
