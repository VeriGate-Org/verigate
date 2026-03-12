package verigate.webbff.verification.repository.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class RiskScoringConfigDataModel {

  private String partnerId;
  private String strategy;
  private String weightsJson;
  private String tiersJson;
  private String overrideRulesJson;
  private String updatedAt;

  @DynamoDbPartitionKey
  @DynamoDbAttribute("partnerId")
  public String getPartnerId() {
    return partnerId;
  }

  public void setPartnerId(String partnerId) {
    this.partnerId = partnerId;
  }

  @DynamoDbAttribute("strategy")
  public String getStrategy() {
    return strategy;
  }

  public void setStrategy(String strategy) {
    this.strategy = strategy;
  }

  @DynamoDbAttribute("weightsJson")
  public String getWeightsJson() {
    return weightsJson;
  }

  public void setWeightsJson(String weightsJson) {
    this.weightsJson = weightsJson;
  }

  @DynamoDbAttribute("tiersJson")
  public String getTiersJson() {
    return tiersJson;
  }

  public void setTiersJson(String tiersJson) {
    this.tiersJson = tiersJson;
  }

  @DynamoDbAttribute("overrideRulesJson")
  public String getOverrideRulesJson() {
    return overrideRulesJson;
  }

  public void setOverrideRulesJson(String overrideRulesJson) {
    this.overrideRulesJson = overrideRulesJson;
  }

  @DynamoDbAttribute("updatedAt")
  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }
}
