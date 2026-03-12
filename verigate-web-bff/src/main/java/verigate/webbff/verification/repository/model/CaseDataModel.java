package verigate.webbff.verification.repository.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

@DynamoDbBean
public class CaseDataModel {

  private String caseId;
  private String verificationId;
  private String workflowId;
  private String partnerId;
  private String status;
  private String assignee;
  private String priority;
  private String decision;
  private String decisionReason;
  private int compositeRiskScore;
  private String riskTier;
  private String subjectName;
  private String subjectId;
  private String commentsJson;
  private String timelineJson;
  private String statusCreatedAt;
  private String createdAt;
  private String updatedAt;
  private String resolvedAt;

  @DynamoDbPartitionKey
  @DynamoDbAttribute("caseId")
  public String getCaseId() {
    return caseId;
  }

  public void setCaseId(String caseId) {
    this.caseId = caseId;
  }

  @DynamoDbAttribute("verificationId")
  public String getVerificationId() {
    return verificationId;
  }

  public void setVerificationId(String verificationId) {
    this.verificationId = verificationId;
  }

  @DynamoDbAttribute("workflowId")
  public String getWorkflowId() {
    return workflowId;
  }

  public void setWorkflowId(String workflowId) {
    this.workflowId = workflowId;
  }

  @DynamoDbSecondaryPartitionKey(indexNames = "partner-status-index")
  @DynamoDbAttribute("partnerId")
  public String getPartnerId() {
    return partnerId;
  }

  public void setPartnerId(String partnerId) {
    this.partnerId = partnerId;
  }

  @DynamoDbAttribute("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @DynamoDbAttribute("assignee")
  public String getAssignee() {
    return assignee;
  }

  public void setAssignee(String assignee) {
    this.assignee = assignee;
  }

  @DynamoDbAttribute("priority")
  public String getPriority() {
    return priority;
  }

  public void setPriority(String priority) {
    this.priority = priority;
  }

  @DynamoDbAttribute("decision")
  public String getDecision() {
    return decision;
  }

  public void setDecision(String decision) {
    this.decision = decision;
  }

  @DynamoDbAttribute("decisionReason")
  public String getDecisionReason() {
    return decisionReason;
  }

  public void setDecisionReason(String decisionReason) {
    this.decisionReason = decisionReason;
  }

  @DynamoDbAttribute("compositeRiskScore")
  public int getCompositeRiskScore() {
    return compositeRiskScore;
  }

  public void setCompositeRiskScore(int compositeRiskScore) {
    this.compositeRiskScore = compositeRiskScore;
  }

  @DynamoDbAttribute("riskTier")
  public String getRiskTier() {
    return riskTier;
  }

  public void setRiskTier(String riskTier) {
    this.riskTier = riskTier;
  }

  @DynamoDbAttribute("subjectName")
  public String getSubjectName() {
    return subjectName;
  }

  public void setSubjectName(String subjectName) {
    this.subjectName = subjectName;
  }

  @DynamoDbAttribute("subjectId")
  public String getSubjectId() {
    return subjectId;
  }

  public void setSubjectId(String subjectId) {
    this.subjectId = subjectId;
  }

  @DynamoDbAttribute("commentsJson")
  public String getCommentsJson() {
    return commentsJson;
  }

  public void setCommentsJson(String commentsJson) {
    this.commentsJson = commentsJson;
  }

  @DynamoDbAttribute("timelineJson")
  public String getTimelineJson() {
    return timelineJson;
  }

  public void setTimelineJson(String timelineJson) {
    this.timelineJson = timelineJson;
  }

  @DynamoDbSecondarySortKey(indexNames = "partner-status-index")
  @DynamoDbAttribute("statusCreatedAt")
  public String getStatusCreatedAt() {
    return statusCreatedAt;
  }

  public void setStatusCreatedAt(String statusCreatedAt) {
    this.statusCreatedAt = statusCreatedAt;
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

  @DynamoDbAttribute("resolvedAt")
  public String getResolvedAt() {
    return resolvedAt;
  }

  public void setResolvedAt(String resolvedAt) {
    this.resolvedAt = resolvedAt;
  }
}
