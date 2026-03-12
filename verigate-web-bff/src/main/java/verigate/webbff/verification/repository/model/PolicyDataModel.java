package verigate.webbff.verification.repository.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

@DynamoDbBean
public class PolicyDataModel {

  private String partnerPolicyId;
  private String partnerId;
  private String name;
  private String description;
  private String stepsJson;
  private String scoringConfigJson;
  private String status;
  private int version;
  private String createdAt;
  private String updatedAt;

  @DynamoDbPartitionKey
  @DynamoDbAttribute("partnerPolicyId")
  public String getPartnerPolicyId() {
    return partnerPolicyId;
  }

  public void setPartnerPolicyId(String partnerPolicyId) {
    this.partnerPolicyId = partnerPolicyId;
  }

  @DynamoDbSecondaryPartitionKey(indexNames = "partner-status-index")
  @DynamoDbAttribute("partnerId")
  public String getPartnerId() {
    return partnerId;
  }

  public void setPartnerId(String partnerId) {
    this.partnerId = partnerId;
  }

  @DynamoDbAttribute("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @DynamoDbAttribute("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @DynamoDbAttribute("stepsJson")
  public String getStepsJson() {
    return stepsJson;
  }

  public void setStepsJson(String stepsJson) {
    this.stepsJson = stepsJson;
  }

  @DynamoDbAttribute("scoringConfigJson")
  public String getScoringConfigJson() {
    return scoringConfigJson;
  }

  public void setScoringConfigJson(String scoringConfigJson) {
    this.scoringConfigJson = scoringConfigJson;
  }

  @DynamoDbSecondarySortKey(indexNames = "partner-status-index")
  @DynamoDbAttribute("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @DynamoDbAttribute("version")
  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  @DynamoDbAttribute("createdAt")
  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  @DynamoDbAttribute("updatedAt")
  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }
}
