package verigate.webbff.admin.model.health;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class HealthSnapshotItem {

  private String serviceId;
  private String checkedAt;
  private String status;
  private long latencyMs;
  private String detail;
  private long ttl;

  @DynamoDbPartitionKey
  @DynamoDbAttribute("serviceId")
  public String getServiceId() {
    return serviceId;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  @DynamoDbSortKey
  @DynamoDbSecondarySortKey(indexNames = "status-checkedAt-index")
  @DynamoDbAttribute("checkedAt")
  public String getCheckedAt() {
    return checkedAt;
  }

  public void setCheckedAt(String checkedAt) {
    this.checkedAt = checkedAt;
  }

  @DynamoDbSecondaryPartitionKey(indexNames = "status-checkedAt-index")
  @DynamoDbAttribute("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @DynamoDbAttribute("latencyMs")
  public long getLatencyMs() {
    return latencyMs;
  }

  public void setLatencyMs(long latencyMs) {
    this.latencyMs = latencyMs;
  }

  @DynamoDbAttribute("detail")
  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  @DynamoDbAttribute("ttl")
  public long getTtl() {
    return ttl;
  }

  public void setTtl(long ttl) {
    this.ttl = ttl;
  }
}
