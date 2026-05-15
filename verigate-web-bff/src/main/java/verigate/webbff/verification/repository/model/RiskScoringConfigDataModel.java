package verigate.webbff.verification.repository.model;

public class RiskScoringConfigDataModel {

  private String partnerId;
  private String strategy;
  private String weightsJson;
  private String tiersJson;
  private String overrideRulesJson;
  private String updatedAt;

  public String getPartnerId() {
    return partnerId;
  }

  public void setPartnerId(String partnerId) {
    this.partnerId = partnerId;
  }

  public String getStrategy() {
    return strategy;
  }

  public void setStrategy(String strategy) {
    this.strategy = strategy;
  }

  public String getWeightsJson() {
    return weightsJson;
  }

  public void setWeightsJson(String weightsJson) {
    this.weightsJson = weightsJson;
  }

  public String getTiersJson() {
    return tiersJson;
  }

  public void setTiersJson(String tiersJson) {
    this.tiersJson = tiersJson;
  }

  public String getOverrideRulesJson() {
    return overrideRulesJson;
  }

  public void setOverrideRulesJson(String overrideRulesJson) {
    this.overrideRulesJson = overrideRulesJson;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }
}
