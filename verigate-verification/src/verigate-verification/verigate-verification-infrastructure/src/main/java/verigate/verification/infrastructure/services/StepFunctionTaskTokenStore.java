package verigate.verification.infrastructure.services;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

/**
 * Persistence store for Step Functions task tokens keyed by stepId.
 * Table schema (provision separately):
 *  - PK: StepId (String)
 *  - Attributes: VerificationId (String), TaskToken (String), ExecutionArn (String), CreatedAt (Number epoch seconds), TTL (Number epoch seconds)
 */
@Singleton
public class StepFunctionTaskTokenStore {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;
    private final long ttlSeconds;

    @Inject
    public StepFunctionTaskTokenStore(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = System.getenv().getOrDefault("VERIFICATION_TOKENS_TABLE", "verification-step-tokens");
        this.ttlSeconds = Long.parseLong(System.getenv().getOrDefault("VERIFICATION_TOKEN_TTL_SECONDS", "86400"));
    }

    public void put(UUID verificationId, UUID stepId, String taskToken, String executionArn) {
        long now = Instant.now().getEpochSecond();
        long ttl = now + ttlSeconds;
        PutItemRequest req = PutItemRequest.builder()
            .tableName(tableName)
            .item(Map.of(
                "StepId", AttributeValue.builder().s(stepId.toString()).build(),
                "VerificationId", AttributeValue.builder().s(verificationId.toString()).build(),
                "TaskToken", AttributeValue.builder().s(taskToken).build(),
                "ExecutionArn", AttributeValue.builder().s(executionArn == null ? "" : executionArn).build(),
                "CreatedAt", AttributeValue.builder().n(Long.toString(now)).build(),
                "TTL", AttributeValue.builder().n(Long.toString(ttl)).build()
            ))
            .build();
        dynamoDbClient.putItem(req);
    }

    public Optional<String> getTaskToken(UUID stepId) {
        try {
            GetItemRequest req = GetItemRequest.builder()
                .tableName(tableName)
                .key(Map.of("StepId", AttributeValue.builder().s(stepId.toString()).build()))
                .consistentRead(true)
                .build();
            var resp = dynamoDbClient.getItem(req);
            if (resp.hasItem() && resp.item().containsKey("TaskToken")) {
                return Optional.ofNullable(resp.item().get("TaskToken").s());
            }
            return Optional.empty();
        } catch (ResourceNotFoundException e) {
            return Optional.empty();
        }
    }
}
