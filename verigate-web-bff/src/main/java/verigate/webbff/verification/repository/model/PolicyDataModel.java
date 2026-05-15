package verigate.webbff.verification.repository.model;

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

  public String getPartnerPolicyId() {
    return partnerPolicyId;
  }

  public void setPartnerPolicyId(String partnerPolicyId) {
    this.partnerPolicyId = partnerPolicyId;
  }

  public String getPartnerId() {
    return partnerId;
  }

  public void setPartnerId(String partnerId) {
    this.partnerId = partnerId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getStepsJson() {
    return stepsJson;
  }

  public void setStepsJson(String stepsJson) {
    this.stepsJson = stepsJson;
  }

  public String getScoringConfigJson() {
    return scoringConfigJson;
  }

  public void setScoringConfigJson(String scoringConfigJson) {
    this.scoringConfigJson = scoringConfigJson;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }
}
