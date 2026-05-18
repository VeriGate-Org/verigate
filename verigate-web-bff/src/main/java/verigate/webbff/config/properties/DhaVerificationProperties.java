package verigate.webbff.config.properties;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "verigate.dha-verification")
public class DhaVerificationProperties {

  private String senderEmail = "dhaverifications@verigate.co.za";

  /**
   * Fallback recipient when no refugeeOffice is provided.
   * Defaults to test address; production sets VERIGATE_DHA_NOTIFICATION_EMAIL.
   */
  private String notificationEmail = "arthur@verigate.co.za";

  private static final Map<String, String> PROD_RRO_EMAIL_MAP = Map.of(
      "Desmond Tutu Refugee Reception Centre", "ASMverifications@dha.gov.za",
      "Gqeberha Refugee Reception Office", "verification.perro@dha.gov.za",
      "Durban Refugee Reception Office", "verification.durban@dha.gov.za",
      "Musina Refugee Reception Office", "verification.musina@dha.gov.za",
      "Cape Town Refugee Reception Office", "verification.ctrro@dha.gov.za"
  );

  /**
   * Resolves the DHA recipient email for the given refugee reception office.
   * In non-production (default notificationEmail), all emails route to the test
   * address. In production the notificationEmail env var is overridden, so the
   * real RRO map is used.
   */
  public String resolveRecipientEmail(String refugeeOffice) {
    boolean isTestMode = "arthur@verigate.co.za".equals(notificationEmail);
    if (isTestMode) {
      return notificationEmail;
    }
    if (refugeeOffice != null && !refugeeOffice.isBlank()) {
      return PROD_RRO_EMAIL_MAP.getOrDefault(refugeeOffice, notificationEmail);
    }
    return notificationEmail;
  }

  public String getNotificationEmail() {
    return notificationEmail;
  }

  public void setNotificationEmail(String notificationEmail) {
    this.notificationEmail = notificationEmail;
  }

  public String getSenderEmail() {
    return senderEmail;
  }

  public void setSenderEmail(String senderEmail) {
    this.senderEmail = senderEmail;
  }
}
