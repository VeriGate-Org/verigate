package verigate.webbff.verification.repository.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

@DynamoDbBean
public class MonitoredSubjectDataModel {

  private String subjectId;
  private String partnerId;
  private String policyId;
  private String subjectType;
  private String subjectName;
  private String subjectIdentifier;
  private String metadataJson;
  private String monitoringFrequency;
  private String status;
  private String lastCheckedAt;
  private String nextCheckAt;
  private int lastRiskScore;
  private String lastRiskDecision;
  private String statusNextCheck;
  private String createdAt;
  private String updatedAt;

  @DynamoDbPartitionKey
  @DynamoDbAttribute("subjectId")
  public String getSubjectId() {
    return subjectId;
  }

  public void setSubjectId(String subjectId) {
    this.subjectId = subjectId;
  }

  @DynamoDbSecondaryPartitionKey(indexNames = "partner-status-index")
  @DynamoDbAttribute("partnerId")
  public String getPartnerId() {
    return partnerId;
  }

  public void setPartnerId(String partnerId) {
    this.partnerId = partnerId;
  }

  @DynamoDbAttribute("policyId")
  public String getPolicyId() {
    return policyId;
  }

  public void setPolicyId(String policyId) {
    this.policyId = policyId;
  }

  @DynamoDbAttribute("subjectType")
  public String getSubjectType() {
    return subjectType;
  }

  public void setSubjectType(String subjectType) {
    this.subjectType = subjectType;
  }

  @DynamoDbAttribute("subjectName")
  public String getSubjectName() {
    return subjectName;
  }

  public void setSubjectName(String subjectName) {
    this.subjectName = subjectName;
  }

  @DynamoDbAttribute("subjectIdentifier")
  public String getSubjectIdentifier() {
    return subjectIdentifier;
  }

  public void setSubjectIdentifier(String subjectIdentifier) {
    this.subjectIdentifier = subjectIdentifier;
  }

  @DynamoDbAttribute("metadataJson")
  public String getMetadataJson() {
    return metadataJson;
  }

  public void setMetadataJson(String metadataJson) {
    this.metadataJson = metadataJson;
  }

  @DynamoDbAttribute("monitoringFrequency")
  public String getMonitoringFrequency() {
    return monitoringFrequency;
  }

  public void setMonitoringFrequency(String monitoringFrequency) {
    this.monitoringFrequency = monitoringFrequency;
  }

  @DynamoDbAttribute("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @DynamoDbAttribute("lastCheckedAt")
  public String getLastCheckedAt() {
    return lastCheckedAt;
  }

  public void setLastCheckedAt(String lastCheckedAt) {
    this.lastCheckedAt = lastCheckedAt;
  }

  @DynamoDbAttribute("nextCheckAt")
  public String getNextCheckAt() {
    return nextCheckAt;
  }

  public void setNextCheckAt(String nextCheckAt) {
    this.nextCheckAt = nextCheckAt;
  }

  @DynamoDbAttribute("lastRiskScore")
  public int getLastRiskScore() {
    return lastRiskScore;
  }

  public void setLastRiskScore(int lastRiskScore) {
    this.lastRiskScore = lastRiskScore;
  }

  @DynamoDbAttribute("lastRiskDecision")
  public String getLastRiskDecision() {
    return lastRiskDecision;
  }

  public void setLastRiskDecision(String lastRiskDecision) {
    this.lastRiskDecision = lastRiskDecision;
  }

  @DynamoDbSecondarySortKey(indexNames = "partner-status-index")
  @DynamoDbAttribute("statusNextCheck")
  public String getStatusNextCheck() {
    return statusNextCheck;
  }

  public void setStatusNextCheck(String statusNextCheck) {
    this.statusNextCheck = statusNextCheck;
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
