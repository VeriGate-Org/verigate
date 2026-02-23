/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.webbff.auth;

/**
 * ThreadLocal holder for the current partner context.
 * Set by the ApiKeyAuthenticationFilter after resolving the API key.
 */
public final class PartnerContextHolder {

  private static final ThreadLocal<String> PARTNER_ID = new ThreadLocal<>();

  private PartnerContextHolder() {}

  public static void setPartnerId(String partnerId) {
    PARTNER_ID.set(partnerId);
  }

  public static String getPartnerId() {
    return PARTNER_ID.get();
  }

  public static void clear() {
    PARTNER_ID.remove();
  }

  public static String requirePartnerId() {
    String partnerId = PARTNER_ID.get();
    if (partnerId == null || partnerId.isBlank()) {
      throw new IllegalStateException("No partner context available. API key required.");
    }
    return partnerId;
  }
}
