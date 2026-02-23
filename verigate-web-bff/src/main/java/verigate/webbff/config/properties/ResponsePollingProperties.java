package verigate.webbff.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "verigate.verification.response")
public class ResponsePollingProperties {

  private long pollTimeoutMs = 50L;
  private int pollMaxAttempts = 1;

  public long getPollTimeoutMs() {
    return pollTimeoutMs;
  }

  public void setPollTimeoutMs(long pollTimeoutMs) {
    this.pollTimeoutMs = pollTimeoutMs;
  }

  public int getPollMaxAttempts() {
    return pollMaxAttempts;
  }

  public void setPollMaxAttempts(int pollMaxAttempts) {
    this.pollMaxAttempts = pollMaxAttempts;
  }
}
