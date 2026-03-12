package verigate.webbff.verification.repository.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

@DynamoDbBean
public class MonitoringAlertDataModel {

  private String alertId;
  private String subjectId;
  private String partnerId;
  private String severity;
  private String alertType;
  private String title;
  private String description;
  private int previousRiskScore;
  private int currentRiskScore;
  private String previousDecision;
  private String currentDecision;
  private boolean acknowledged;
  private String acknowledgedBy;
  private String acknowledgedAt;
  private String subjectIdCreatedAt;
  private String createdAt;

  @DynamoDbPartitionKey
  @DynamoDbAttribute("alertId")
  public String getAlertId() {
    return alertId;
  }

  public void setAlertId(String alertId) {
    this.alertId = alertId;
  }

  @DynamoDbAttribute("subjectId")
  public String getSubjectId() {
    return subjectId;
  }

  public void setSubjectId(String subjectId) {
    this.subjectId = subjectId;
  }

  @DynamoDbSecondaryPartitionKey(indexNames = "partner-subject-index")
  @DynamoDbAttribute("partnerId")
  public String getPartnerId() {
    return partnerId;
  }

  public void setPartnerId(String partnerId) {
    this.partnerId = partnerId;
  }

  @DynamoDbAttribute("severity")
  public String getSeverity() {
    return severity;
  }

  public void setSeverity(String severity) {
    this.severity = severity;
  }

  @DynamoDbAttribute("alertType")
  public String getAlertType() {
    return alertType;
  }

  public void setAlertType(String alertType) {
    this.alertType = alertType;
  }

  @DynamoDbAttribute("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @DynamoDbAttribute("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @DynamoDbAttribute("previousRiskScore")
  public int getPreviousRiskScore() {
    return previousRiskScore;
  }

  public void setPreviousRiskScore(int previousRiskScore) {
    this.previousRiskScore = previousRiskScore;
  }

  @DynamoDbAttribute("currentRiskScore")
  public int getCurrentRiskScore() {
    return currentRiskScore;
  }

  public void setCurrentRiskScore(int currentRiskScore) {
    this.currentRiskScore = currentRiskScore;
  }

  @DynamoDbAttribute("previousDecision")
  public String getPreviousDecision() {
    return previousDecision;
  }

  public void setPreviousDecision(String previousDecision) {
    this.previousDecision = previousDecision;
  }

  @DynamoDbAttribute("currentDecision")
  public String getCurrentDecision() {
    return currentDecision;
  }

  public void setCurrentDecision(String currentDecision) {
    this.currentDecision = currentDecision;
  }

  @DynamoDbAttribute("acknowledged")
  public boolean isAcknowledged() {
    return acknowledged;
  }

  public void setAcknowledged(boolean acknowledged) {
    this.acknowledged = acknowledged;
  }

  @DynamoDbAttribute("acknowledgedBy")
  public String getAcknowledgedBy() {
    return acknowledgedBy;
  }

  public void setAcknowledgedBy(String acknowledgedBy) {
    this.acknowledgedBy = acknowledgedBy;
  }

  @DynamoDbAttribute("acknowledgedAt")
  public String getAcknowledgedAt() {
    return acknowledgedAt;
  }

  public void setAcknowledgedAt(String acknowledgedAt) {
    this.acknowledgedAt = acknowledgedAt;
  }

  @DynamoDbSecondarySortKey(indexNames = "partner-subject-index")
  @DynamoDbAttribute("subjectIdCreatedAt")
  public String getSubjectIdCreatedAt() {
    return subjectIdCreatedAt;
  }

  public void setSubjectIdCreatedAt(String subjectIdCreatedAt) {
    this.subjectIdCreatedAt = subjectIdCreatedAt;
  }

  @DynamoDbAttribute("createdAt")
  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }
}
