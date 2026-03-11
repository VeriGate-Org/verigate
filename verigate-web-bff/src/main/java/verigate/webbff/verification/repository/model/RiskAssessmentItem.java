package verigate.webbff.verification.repository.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class RiskAssessmentItem {

  private String verificationId;
  private String assessmentId;
  private String partnerId;
  private int compositeScore;
  private String riskTier;
  private String decision;
  private String decisionReason;
  private String individualScoresJson;
  private boolean overrideApplied;
  private String overrideRuleId;
  private String assessedAt;

  @DynamoDbPartitionKey
  @DynamoDbAttribute("verificationId")
  public String getVerificationId() {
    return verificationId;
  }

  public void setVerificationId(String verificationId) {
    this.verificationId = verificationId;
  }

  @DynamoDbAttribute("assessmentId")
  public String getAssessmentId() {
    return assessmentId;
  }

  public void setAssessmentId(String assessmentId) {
    this.assessmentId = assessmentId;
  }

  @DynamoDbAttribute("partnerId")
  public String getPartnerId() {
    return partnerId;
  }

  public void setPartnerId(String partnerId) {
    this.partnerId = partnerId;
  }

  @DynamoDbAttribute("compositeScore")
  public int getCompositeScore() {
    return compositeScore;
  }

  public void setCompositeScore(int compositeScore) {
    this.compositeScore = compositeScore;
  }

  @DynamoDbAttribute("riskTier")
  public String getRiskTier() {
    return riskTier;
  }

  public void setRiskTier(String riskTier) {
    this.riskTier = riskTier;
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

  @DynamoDbAttribute("individualScoresJson")
  public String getIndividualScoresJson() {
    return individualScoresJson;
  }

  public void setIndividualScoresJson(String individualScoresJson) {
    this.individualScoresJson = individualScoresJson;
  }

  @DynamoDbAttribute("overrideApplied")
  public boolean isOverrideApplied() {
    return overrideApplied;
  }

  public void setOverrideApplied(boolean overrideApplied) {
    this.overrideApplied = overrideApplied;
  }

  @DynamoDbAttribute("overrideRuleId")
  public String getOverrideRuleId() {
    return overrideRuleId;
  }

  public void setOverrideRuleId(String overrideRuleId) {
    this.overrideRuleId = overrideRuleId;
  }

  @DynamoDbAttribute("assessedAt")
  public String getAssessedAt() {
    return assessedAt;
  }

  public void setAssessedAt(String assessedAt) {
    this.assessedAt = assessedAt;
  }
}
