/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.infrastructure.repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import verigate.verification.domain.models.VerificationAggregateRoot;
import verigate.verification.domain.repositories.VerificationRepository;

/**
 * DynamoDB implementation of the verification repository.
 */
public class DynamoDbVerificationRepository implements VerificationRepository {

    private static final String TABLE_NAME = "verigate-verification";
    private static final String VERIFICATION_REQUEST_INDEX = "verification-request-index";
    private static final String PARTNER_INDEX = "partner-index";

    private final DynamoDbClient dynamoDbClient;
    private final ObjectMapper objectMapper;

    @Inject
    public DynamoDbVerificationRepository(DynamoDbClient dynamoDbClient, ObjectMapper objectMapper) {
        this.dynamoDbClient = dynamoDbClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public void addOrUpdate(VerificationAggregateRoot verification) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("verificationId", AttributeValue.builder().s(verification.getVerificationId().toString()).build());
            item.put("partnerId", AttributeValue.builder().s(verification.getPartnerId()).build());
            item.put("verificationRequestId",
                    AttributeValue.builder().s(verification.getVerificationRequestId()).build());
            item.put("verificationType",
                    AttributeValue.builder().s(verification.getVerificationType().toString()).build());
            item.put("status", AttributeValue.builder().s(verification.getStatus().toString()).build());
            item.put("createdAt", AttributeValue.builder().s(verification.getCreatedAt().toString()).build());
            item.put("updatedAt", AttributeValue.builder().s(verification.getUpdatedAt().toString()).build());
            item.put("retryCount", AttributeValue.builder().n(String.valueOf(verification.getRetryCount())).build());

            if (verification.getOverallResult() != null) {
                item.put("overallResult",
                        AttributeValue.builder().s(verification.getOverallResult().toString()).build());
            }

            if (verification.getFailureReason() != null) {
                item.put("failureReason", AttributeValue.builder().s(verification.getFailureReason()).build());
            }

            // Serialize the entire aggregate as JSON for complex data
            String aggregateJson = objectMapper.writeValueAsString(verification);
            item.put("aggregateData", AttributeValue.builder().s(aggregateJson).build());

            PutItemRequest request = PutItemRequest.builder()
                    .tableName(TABLE_NAME)
                    .item(item)
                    .build();

            dynamoDbClient.putItem(request);

        } catch (Exception e) {
            throw new RuntimeException("Failed to save verification: " + verification.getVerificationId(), e);
        }
    }

    @Override
    public VerificationAggregateRoot get(UUID verificationId) {
        try {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("verificationId", AttributeValue.builder().s(verificationId.toString()).build());

            GetItemRequest request = GetItemRequest.builder()
                    .tableName(TABLE_NAME)
                    .key(key)
                    .build();

            GetItemResponse response = dynamoDbClient.getItem(request);

            if (!response.hasItem() || response.item().isEmpty()) {
                return null;
            }

            // Deserialize from JSON
            String aggregateJson = response.item().get("aggregateData").s();
            return objectMapper.readValue(aggregateJson, VerificationAggregateRoot.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find verification: " + verificationId, e);
        }
    }

    // @Override
    // public VerificationAggregateRoot findByVerificationRequestId(String
    // verificationRequestId) {
    // try {
    // Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
    // expressionAttributeValues.put(":verificationRequestId",
    // AttributeValue.builder().s(verificationRequestId).build());

    // QueryRequest request = QueryRequest.builder()
    // .tableName(TABLE_NAME)
    // .indexName(VERIFICATION_REQUEST_INDEX)
    // .keyConditionExpression("verificationRequestId = :verificationRequestId")
    // .expressionAttributeValues(expressionAttributeValues)
    // .build();

    // QueryResponse response = dynamoDbClient.query(request);

    // if (response.items().isEmpty()) {
    // return null;
    // }

    // // Get the first item (should be unique)
    // Map<String, AttributeValue> item = response.items().get(0);
    // String aggregateJson = item.get("aggregateData").s();
    // return objectMapper.readValue(aggregateJson,
    // VerificationAggregateRoot.class);

    // } catch (Exception e) {
    // throw new RuntimeException("Failed to find verification by request ID: " +
    // verificationRequestId, e);
    // }
    // }

    /**
     * Finds all verifications for a specific partner using the partner-index GSI.
     *
     * @param partnerId the partner identifier
     * @return list of verification aggregates for the partner
     */
    public List<VerificationAggregateRoot> findByPartnerId(String partnerId) {
        try {
            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
            expressionAttributeValues.put(":partnerId",
                    AttributeValue.builder().s(partnerId).build());

            QueryRequest request = QueryRequest.builder()
                    .tableName(TABLE_NAME)
                    .indexName(PARTNER_INDEX)
                    .keyConditionExpression("partnerId = :partnerId")
                    .expressionAttributeValues(expressionAttributeValues)
                    .scanIndexForward(false)
                    .build();

            QueryResponse response = dynamoDbClient.query(request);

            List<VerificationAggregateRoot> results = new ArrayList<>();
            for (Map<String, AttributeValue> item : response.items()) {
                String aggregateJson = item.get("aggregateData").s();
                results.add(objectMapper.readValue(aggregateJson, VerificationAggregateRoot.class));
            }
            return results;

        } catch (Exception e) {
            throw new RuntimeException("Failed to find verifications for partner: " + partnerId, e);
        }
    }

    /**
     * Checks if a verification exists.
     */
    public boolean exists(UUID verificationId) {
        try {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("verificationId",
                    AttributeValue.builder().s(verificationId.toString()).build());

            GetItemRequest request = GetItemRequest.builder()
                    .tableName(TABLE_NAME)
                    .key(key)
                    .projectionExpression("verificationId")
                    .build();

            GetItemResponse response = dynamoDbClient.getItem(request);
            return response.hasItem() && !response.item().isEmpty();

        } catch (Exception e) {
            throw new RuntimeException("Failed to check verification existence: " +
                    verificationId, e);
        }
    }
}