package verigate.webbff.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "verigate.dha-verification")
public class DhaVerificationProperties {

  private String notificationEmail = "asmverifications@dha.gov.za";
  private String senderEmail = "noreply@verigate.co.za";

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
