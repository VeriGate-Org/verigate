package verigate.webbff.admin.service;

import infrastructure.functions.lambda.serializers.internal.DefaultInternalTransportJsonSerializer;
import infrastructure.mapping.Mapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import verigate.partner.domain.commands.CreatePartnerCommand;
import verigate.partner.domain.models.PartnerType;
import verigate.webbff.admin.model.CreatePartnerRequest;

@Service
public class PartnerService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PartnerService.class);

  private final SqsClient sqsClient;
  private final DefaultInternalTransportJsonSerializer serializer;
  private final Mapper mapper;
  private final String createQueueName;

  public PartnerService(
      SqsClient sqsClient,
      DefaultInternalTransportJsonSerializer serializer,
      Mapper mapper,
      @Value("${verigate.partner.create-queue-name:partner-create}") String createQueueName) {
    this.sqsClient = sqsClient;
    this.serializer = serializer;
    this.mapper = mapper;
    this.createQueueName = createQueueName;
  }

  public UUID submitCreatePartner(CreatePartnerRequest request) {
    CreatePartnerCommand command = new CreatePartnerCommand(
        request.name(),
        request.contactEmail(),
        PartnerType.FINANCIAL_INSTITUTION);

    var queueUrl = sqsClient.getQueueUrl(
        GetQueueUrlRequest.builder().queueName(createQueueName).build()).queueUrl();
    var payload = serializer.serialize(mapper.toDto(command));
    sqsClient.sendMessage(
        SendMessageRequest.builder().queueUrl(queueUrl).messageBody(payload).build());

    LOGGER.info("Dispatched CreatePartnerCommand {} to queue {}", command.getId(), createQueueName);
    return command.getId();
  }
}
