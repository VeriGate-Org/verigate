package verigate.webbff.config.properties;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "verigate.dha-verification")
public class DhaVerificationProperties {

  private String senderEmail = "dhaverifications@verigate.co.za";

  /**
   * Fallback recipient when no refugeeOffice is provided.
   */
  private String notificationEmail = "ASMverifications@dha.gov.za";

  private static final Map<String, String> RRO_EMAIL_MAP = Map.of(
      "Desmond Tutu Refugee Reception Centre", "ASMverifications@dha.gov.za",
      "Gqeberha Refugee Reception Office", "verification.perro@dha.gov.za",
      "Durban Refugee Reception Office", "verification.durban@dha.gov.za",
      "Musina Refugee Reception Office", "verification.musina@dha.gov.za",
      "Cape Town Refugee Reception Office", "verification.ctrro@dha.gov.za"
  );

  /**
   * Resolves the DHA recipient email for the given refugee reception office.
   * Falls back to the default notificationEmail if the office is unknown or null.
   */
  public String resolveRecipientEmail(String refugeeOffice) {
    if (refugeeOffice != null && !refugeeOffice.isBlank()) {
      return RRO_EMAIL_MAP.getOrDefault(refugeeOffice, notificationEmail);
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
