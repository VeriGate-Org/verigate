/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.infrastructure.repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import verigate.partner.domain.models.*;
import verigate.partner.domain.repositories.PartnerRepository;

import java.time.Instant;
import java.util.*;

/**
 * DynamoDB implementation of PartnerRepository.
 */
public class DynamoDbPartnerRepository implements PartnerRepository {

    private static final Logger logger = LoggerFactory.getLogger(DynamoDbPartnerRepository.class);

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    public DynamoDbPartnerRepository(DynamoDbClient dynamoDbClient, String tableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
    }

    @Override
    public Optional<PartnerAggregateRoot> get(UUID partnerId) {
        try {
            GetItemResponse response = dynamoDbClient.getItem(GetItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(
                    "partnerId", AttributeValue.builder().s(partnerId.toString()).build(),
                    "entityType", AttributeValue.builder().s("METADATA").build()))
                .build());

            if (!response.hasItem() || response.item().isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(mapToAggregate(response.item()));
        } catch (Exception e) {
            logger.error("Failed to get partner: {}", partnerId, e);
            throw new RuntimeException("Failed to get partner", e);
        }
    }

    @Override
    public PartnerAggregateRoot addOrUpdate(PartnerAggregateRoot partner) {
        try {
            Map<String, AttributeValue> item = mapToItem(partner);
            dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build());
            logger.info("Saved partner: {}", partner.getPartnerId());
            return partner;
        } catch (Exception e) {
            logger.error("Failed to save partner: {}", partner.getPartnerId(), e);
            throw new RuntimeException("Failed to save partner", e);
        }
    }

    @Override
    public boolean exists(UUID partnerId) {
        return get(partnerId).isPresent();
    }

    @Override
    public boolean remove(UUID partnerId) {
        try {
            dynamoDbClient.deleteItem(DeleteItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(
                    "partnerId", AttributeValue.builder().s(partnerId.toString()).build(),
                    "entityType", AttributeValue.builder().s("METADATA").build()))
                .build());
            return true;
        } catch (Exception e) {
            logger.error("Failed to remove partner: {}", partnerId, e);
            return false;
        }
    }

    private Map<String, AttributeValue> mapToItem(PartnerAggregateRoot partner) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("partnerId", AttributeValue.builder().s(partner.getPartnerId().toString()).build());
        item.put("entityType", AttributeValue.builder().s("METADATA").build());
        item.put("partnerName", AttributeValue.builder().s(partner.getPartnerName()).build());
        item.put("contactEmail", AttributeValue.builder().s(partner.getContactEmail()).build());
        item.put("partnerType", AttributeValue.builder().s(partner.getPartnerType().name()).build());
        item.put("partnerStatus", AttributeValue.builder().s(partner.getStatus().name()).build());
        item.put("createdAt", AttributeValue.builder().s(partner.getCreatedAt().toString()).build());
        item.put("updatedAt", AttributeValue.builder().s(partner.getUpdatedAt().toString()).build());
        return item;
    }

    private PartnerAggregateRoot mapToAggregate(Map<String, AttributeValue> item) {
        return new PartnerAggregateRoot(
            UUID.fromString(item.get("partnerId").s()),
            item.get("partnerName").s(),
            item.get("contactEmail").s(),
            PartnerType.valueOf(item.get("partnerType").s()),
            PartnerStatus.valueOf(item.get("partnerStatus").s()),
            Instant.parse(item.get("createdAt").s()),
            Instant.parse(item.get("updatedAt").s()),
            null
        );
    }
}
