package verigate.webbff.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "verigate.cache")
public class CacheProperties {

  private boolean enabled = true;
  private CacheSpec policies = new CacheSpec(500, 60);
  private CacheSpec riskAssessments = new CacheSpec(1000, 30);
  private CacheSpec commandStatus = new CacheSpec(2000, 5);

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public CacheSpec getPolicies() {
    return policies;
  }

  public void setPolicies(CacheSpec policies) {
    this.policies = policies;
  }

  public CacheSpec getRiskAssessments() {
    return riskAssessments;
  }

  public void setRiskAssessments(CacheSpec riskAssessments) {
    this.riskAssessments = riskAssessments;
  }

  public CacheSpec getCommandStatus() {
    return commandStatus;
  }

  public void setCommandStatus(CacheSpec commandStatus) {
    this.commandStatus = commandStatus;
  }

  public static class CacheSpec {
    private int maxSize;
    private int ttlMinutes;

    public CacheSpec() {}

    public CacheSpec(int maxSize, int ttlMinutes) {
      this.maxSize = maxSize;
      this.ttlMinutes = ttlMinutes;
    }

    public int getMaxSize() {
      return maxSize;
    }

    public void setMaxSize(int maxSize) {
      this.maxSize = maxSize;
    }

    public int getTtlMinutes() {
      return ttlMinutes;
    }

    public void setTtlMinutes(int ttlMinutes) {
      this.ttlMinutes = ttlMinutes;
    }
  }
}
