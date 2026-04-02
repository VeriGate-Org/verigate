/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.infrastructure.bulk;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import verigate.adapter.dha.domain.models.BillingGroupSelection;
import verigate.adapter.dha.domain.models.BulkVerificationJob;
import verigate.adapter.dha.domain.models.BulkVerificationJob.BulkJobStatus;

/**
 * DynamoDB repository for bulk verification jobs.
 */
public class BulkJobRepository {

  private static final Logger logger = LoggerFactory.getLogger(BulkJobRepository.class);

  private final DynamoDbClient dynamoDbClient;
  private final String tableName;

  public BulkJobRepository(DynamoDbClient dynamoDbClient, String tableName) {
    this.dynamoDbClient = dynamoDbClient;
    this.tableName = tableName;
  }

  /**
   * Stores a bulk verification job.
   */
  public void save(BulkVerificationJob job) {
    Map<String, AttributeValue> item = new HashMap<>();
    item.put("jobId", AttributeValue.builder().s(job.jobId()).build());
    item.put("partnerId", AttributeValue.builder().s(job.partnerId()).build());
    item.put("status", AttributeValue.builder().s(job.status().name()).build());
    item.put("idCount", AttributeValue.builder().n(String.valueOf(job.idCount())).build());
    item.put("createdAt", AttributeValue.builder().s(job.createdAt().toString()).build());

    if (job.requestId() != null) {
      item.put("requestId", AttributeValue.builder().s(job.requestId()).build());
    }
    if (job.billingGroups() != null) {
      item.put("billingGroups",
          AttributeValue.builder().s(job.billingGroups().toHeaderString()).build());
    }
    if (job.uploadedAt() != null) {
      item.put("uploadedAt", AttributeValue.builder().s(job.uploadedAt().toString()).build());
    }
    if (job.completedAt() != null) {
      item.put("completedAt", AttributeValue.builder().s(job.completedAt().toString()).build());
    }
    if (job.errorCode() != 0) {
      item.put("errorCode", AttributeValue.builder().n(String.valueOf(job.errorCode())).build());
    }
    if (job.errorDescription() != null) {
      item.put("errorDescription",
          AttributeValue.builder().s(job.errorDescription()).build());
    }

    dynamoDbClient.putItem(PutItemRequest.builder()
        .tableName(tableName)
        .item(item)
        .build());

    logger.info("Saved bulk job: jobId={}, status={}", job.jobId(), job.status());
  }

  /**
   * Finds a bulk verification job by its ID.
   */
  public Optional<BulkVerificationJob> findById(String jobId) {
    var response = dynamoDbClient.getItem(GetItemRequest.builder()
        .tableName(tableName)
        .key(Map.of("jobId", AttributeValue.builder().s(jobId).build()))
        .build());

    if (!response.hasItem() || response.item().isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(mapFromItem(response.item()));
  }

  /**
   * Lists bulk verification jobs for a partner, ordered by creation date descending.
   */
  public List<BulkVerificationJob> findByPartnerId(String partnerId, int limit) {
    QueryResponse response = dynamoDbClient.query(QueryRequest.builder()
        .tableName(tableName)
        .indexName("partner-created-index")
        .keyConditionExpression("partnerId = :pid")
        .expressionAttributeValues(Map.of(
            ":pid", AttributeValue.builder().s(partnerId).build()))
        .scanIndexForward(false)
        .limit(limit)
        .build());

    List<BulkVerificationJob> jobs = new ArrayList<>();
    for (Map<String, AttributeValue> item : response.items()) {
      jobs.add(mapFromItem(item));
    }
    return jobs;
  }

  private BulkVerificationJob mapFromItem(Map<String, AttributeValue> item) {
    return new BulkVerificationJob(
        getStr(item, "jobId"),
        getStr(item, "partnerId"),
        BulkJobStatus.valueOf(getStr(item, "status")),
        getStr(item, "requestId"),
        item.containsKey("billingGroups")
            ? BillingGroupSelection.fromHeaderString(getStr(item, "billingGroups"))
            : null,
        getInt(item, "idCount"),
        parseInstant(getStr(item, "createdAt")),
        parseInstant(getStr(item, "uploadedAt")),
        parseInstant(getStr(item, "completedAt")),
        getInt(item, "errorCode"),
        getStr(item, "errorDescription"));
  }

  private String getStr(Map<String, AttributeValue> item, String key) {
    AttributeValue val = item.get(key);
    return val != null && val.s() != null ? val.s() : null;
  }

  private int getInt(Map<String, AttributeValue> item, String key) {
    AttributeValue val = item.get(key);
    if (val != null && val.n() != null) {
      try { return Integer.parseInt(val.n()); } catch (NumberFormatException e) { return 0; }
    }
    return 0;
  }

  private Instant parseInstant(String value) {
    if (value == null || value.isBlank()) return null;
    try { return Instant.parse(value); } catch (Exception e) { return null; }
  }
}
