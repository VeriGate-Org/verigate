package verigate.webbff.verification.repository.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class AiRiskEnhancementItem {

  private String workflowId;
  private int confidenceAdjustment;
  private String reasoning;
  private String correlationsJson;
  private String anomaliesJson;
  private String analyzedAt;

  @DynamoDbPartitionKey
  @DynamoDbAttribute("workflowId")
  public String getWorkflowId() {
    return workflowId;
  }

  public void setWorkflowId(String workflowId) {
    this.workflowId = workflowId;
  }

  @DynamoDbAttribute("confidenceAdjustment")
  public int getConfidenceAdjustment() {
    return confidenceAdjustment;
  }

  public void setConfidenceAdjustment(int confidenceAdjustment) {
    this.confidenceAdjustment = confidenceAdjustment;
  }

  @DynamoDbAttribute("reasoning")
  public String getReasoning() {
    return reasoning;
  }

  public void setReasoning(String reasoning) {
    this.reasoning = reasoning;
  }

  @DynamoDbAttribute("correlationsJson")
  public String getCorrelationsJson() {
    return correlationsJson;
  }

  public void setCorrelationsJson(String correlationsJson) {
    this.correlationsJson = correlationsJson;
  }

  @DynamoDbAttribute("anomaliesJson")
  public String getAnomaliesJson() {
    return anomaliesJson;
  }

  public void setAnomaliesJson(String anomaliesJson) {
    this.anomaliesJson = anomaliesJson;
  }

  @DynamoDbAttribute("analyzedAt")
  public String getAnalyzedAt() {
    return analyzedAt;
  }

  public void setAnalyzedAt(String analyzedAt) {
    this.analyzedAt = analyzedAt;
  }
}
