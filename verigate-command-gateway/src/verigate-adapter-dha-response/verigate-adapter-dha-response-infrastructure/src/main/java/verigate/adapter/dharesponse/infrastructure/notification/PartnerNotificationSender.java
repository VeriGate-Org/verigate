package verigate.adapter.dharesponse.infrastructure.notification;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import verigate.adapter.dharesponse.application.handlers.DefaultProcessDhaResponseCommandHandler;
import verigate.adapter.dharesponse.domain.models.DhaResponseVerificationResult;

/** Sends email notifications to partners about DHA verification results. */
public class PartnerNotificationSender
    implements DefaultProcessDhaResponseCommandHandler.PartnerNotifier {

  private static final Logger logger =
      LoggerFactory.getLogger(PartnerNotificationSender.class);

  private final SesClient sesClient;
  private final DynamoDbClient dynamoDbClient;
  private final String partnerHubTableName;
  private final String senderEmail;
  private final String portalBaseUrl;

  /** Creates a new PartnerNotificationSender. */
  public PartnerNotificationSender(
      SesClient sesClient, DynamoDbClient dynamoDbClient,
      String partnerHubTableName, String senderEmail,
      String portalBaseUrl) {
    this.sesClient = sesClient;
    this.dynamoDbClient = dynamoDbClient;
    this.partnerHubTableName = partnerHubTableName;
    this.senderEmail = senderEmail;
    this.portalBaseUrl = portalBaseUrl;
  }

  @Override
  public void notifyPartner(
      String partnerId, String commandId,
      DhaResponseVerificationResult result) {
    String partnerEmail = lookupPartnerEmail(partnerId);
    if (partnerEmail == null || partnerEmail.isBlank()) {
      logger.warn(
          "No email found for partner {}, skipping notification",
          partnerId);
      return;
    }

    String subject =
        "DHA Permit Verification Complete - " + result.outcome().name();
    String body = buildNotificationBody(commandId, result);

    sesClient.sendEmail(SendEmailRequest.builder()
        .source(senderEmail)
        .destination(
            Destination.builder().toAddresses(partnerEmail).build())
        .message(Message.builder()
            .subject(Content.builder().data(subject).build())
            .body(Body.builder()
                .text(Content.builder().data(body).build())
                .build())
            .build())
        .build());

    logger.info(
        "Partner notification sent: partnerId={}, commandId={}, email={}",
        partnerId, commandId, partnerEmail);
  }

  private String lookupPartnerEmail(String partnerId) {
    try {
      GetItemResponse response = dynamoDbClient.getItem(
          GetItemRequest.builder()
              .tableName(partnerHubTableName)
              .key(Map.of(
                  "partnerId",
                  AttributeValue.builder().s(partnerId).build(),
                  "entityType",
                  AttributeValue.builder().s("PARTNER").build()))
              .build());

      if (response.hasItem()
          && response.item().containsKey("contactEmail")) {
        return response.item().get("contactEmail").s();
      }
    } catch (Exception e) {
      logger.error(
          "Failed to look up partner email for {}", partnerId, e);
    }
    return null;
  }

  private String buildNotificationBody(
      String commandId, DhaResponseVerificationResult result) {
    String portalLink = portalBaseUrl
        + "/services/document-verification?commandId=" + commandId;

    return "Dear Partner,\n\n"
        + "The Department of Home Affairs has responded to your "
        + "permit verification request.\n\n"
        + "Verification Outcome: " + result.outcome().name() + "\n"
        + "Authentic: " + (result.isAuthentic() ? "Yes" : "No") + "\n"
        + "Currently Valid: "
        + (result.isCurrentlyValid() ? "Yes" : "No") + "\n"
        + "Confidence: "
        + String.format("%.0f%%", result.confidenceScore() * 100)
        + "\n\n"
        + "View full details and download the report on the "
        + "VeriGate portal:\n"
        + portalLink + "\n\n"
        + "Kind regards,\n"
        + "VeriGate Verification Services\n";
  }
}
