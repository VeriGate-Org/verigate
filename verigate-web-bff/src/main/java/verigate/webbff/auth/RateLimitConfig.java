/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.webbff.auth;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for per-partner rate limiting.
 * <p>
 * Partners not listed in the {@code partner-limits} map receive the
 * {@code defaultRequestsPerMinute} rate. Set {@code enabled} to
 * {@code false} to disable rate limiting entirely.
 */
@Configuration
@ConfigurationProperties(prefix = "verigate.auth.rate-limit")
public class RateLimitConfig {

  private boolean enabled = true;
  private int defaultRequestsPerMinute = 100;
  private Map<String, Integer> partnerLimits = new HashMap<>();

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public int getDefaultRequestsPerMinute() {
    return defaultRequestsPerMinute;
  }

  public void setDefaultRequestsPerMinute(int defaultRequestsPerMinute) {
    this.defaultRequestsPerMinute = defaultRequestsPerMinute;
  }

  public Map<String, Integer> getPartnerLimits() {
    return partnerLimits;
  }

  public void setPartnerLimits(Map<String, Integer> partnerLimits) {
    this.partnerLimits = partnerLimits;
  }

  /**
   * Returns the rate limit for a specific partner.
   * Falls back to the default if no per-partner override is configured.
   *
   * @param partnerId the partner identifier
   * @return maximum requests per minute allowed for the partner
   */
  public int getLimitForPartner(String partnerId) {
    return partnerLimits.getOrDefault(partnerId, defaultRequestsPerMinute);
  }
}
