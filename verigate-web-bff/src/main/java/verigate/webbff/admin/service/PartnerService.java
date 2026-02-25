package verigate.webbff.admin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import verigate.webbff.admin.model.CreatePartnerCommandMessage;
import verigate.webbff.admin.model.CreatePartnerRequest;

@Service
public class PartnerService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PartnerService.class);

  private final SqsClient sqsClient;
  private final ObjectMapper objectMapper;
  private final String createQueueName;

  public PartnerService(
      SqsClient sqsClient,
      ObjectMapper objectMapper,
      @Value("${verigate.partner.create-queue-name:partner-create}") String createQueueName) {
    this.sqsClient = sqsClient;
    this.objectMapper = objectMapper;
    this.createQueueName = createQueueName;
  }

  public UUID submitCreatePartner(CreatePartnerRequest request) {
    var commandId = UUID.randomUUID();
    var command = new CreatePartnerCommandMessage(
        commandId,
        Instant.now(),
        "admin",
        request.name(),
        request.contactEmail(),
        "FINANCIAL_INSTITUTION");

    var queueUrl = sqsClient.getQueueUrl(
        GetQueueUrlRequest.builder().queueName(createQueueName).build()).queueUrl();
    String payload;
    try {
      payload = objectMapper.writeValueAsString(command);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize command", e);
    }
    sqsClient.sendMessage(
        SendMessageRequest.builder().queueUrl(queueUrl).messageBody(payload).build());

    LOGGER.info("Dispatched CreatePartnerCommand {} to queue {}", commandId, createQueueName);
    return commandId;
  }
}
