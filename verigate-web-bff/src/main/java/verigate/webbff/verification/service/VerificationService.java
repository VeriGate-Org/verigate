package verigate.webbff.verification.service;

import infrastructure.functions.lambda.serializers.internal.DefaultInternalTransportJsonSerializer;
import infrastructure.mapping.Mapper;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import verigate.verification.cg.application.factories.VerifyPartySpecificationFactory;
import verigate.verification.cg.domain.commands.commandstore.VerificationCommandStatusEnum;
import verigate.verification.cg.domain.commands.commandstore.VerificationCommandStoreRecord;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.Origination;
import verigate.webbff.config.properties.ResponsePollingProperties;
import verigate.webbff.verification.model.VerificationRequest;
import verigate.webbff.verification.model.VerificationResponse;
import verigate.webbff.verification.repository.CommandStatusRepository;
import verigate.webbff.verification.support.VerificationQueueResolver;

@Service
public class VerificationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(VerificationService.class);

  private final VerificationQueueResolver queueResolver;
  private final SqsClient sqsClient;
  private final DefaultInternalTransportJsonSerializer serializer;
  private final Mapper mapper;
  private final VerifyPartySpecificationFactory specificationFactory;
  private final CommandStatusRepository statusRepository;
  private final ResponsePollingProperties pollingProperties;

  public VerificationService(
      VerificationQueueResolver queueResolver,
      SqsClient sqsClient,
      DefaultInternalTransportJsonSerializer serializer,
      Mapper mapper,
      VerifyPartySpecificationFactory specificationFactory,
      CommandStatusRepository statusRepository,
      ResponsePollingProperties pollingProperties) {
    this.queueResolver = queueResolver;
    this.sqsClient = sqsClient;
    this.serializer = serializer;
    this.mapper = mapper;
    this.specificationFactory = specificationFactory;
    this.statusRepository = statusRepository;
    this.pollingProperties = pollingProperties;
  }

  public VerificationResponse submitVerification(VerificationRequest request) {
    var commandId = UUID.randomUUID();
    var command = buildCommand(commandId, request);

    validate(command);

    dispatch(command, queueResolver.resolve(request.verificationType()));

    var status = pollForStatus(commandId).map(VerificationCommandStoreRecord::getStatus)
        .orElse(VerificationCommandStatusEnum.PENDING);

    return new VerificationResponse(commandId, status);
  }

  public Optional<VerificationCommandStoreRecord> findVerification(UUID commandId) {
    return statusRepository.findById(commandId);
  }

  private VerifyPartyCommand buildCommand(UUID commandId, VerificationRequest request) {
    Origination origination = new Origination(request.originationType(), request.originationId());
    Map<String, Object> metadata = Optional.ofNullable(request.metadata()).orElse(Map.of());
    return new VerifyPartyCommand(
        commandId,
        Instant.now(),
        request.requestedBy(),
        request.verificationType(),
        origination,
        metadata);
  }

  private void validate(VerifyPartyCommand command) {
    var specification = specificationFactory.createSpecification(command);
    command.validate(specification);
  }

  private void dispatch(VerifyPartyCommand command, String queueName) {
    var queueUrl =
        sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).queueUrl();
    var payload = serializer.serialize(mapper.toDto(command));
    sqsClient.sendMessage(
        SendMessageRequest.builder().queueUrl(queueUrl).messageBody(payload).build());
    LOGGER.info("Dispatched verification command {} to queue {}", command.getId(), queueName);
  }

  private Optional<VerificationCommandStoreRecord> pollForStatus(UUID commandId) {
    int attempts = Math.max(1, pollingProperties.getPollMaxAttempts());
    long pauseMs = Math.max(0L, pollingProperties.getPollTimeoutMs());

    for (int i = 0; i < attempts; i++) {
      var record = statusRepository.findById(commandId);
      if (record.isPresent()) {
        return record;
      }

      if (pauseMs > 0 && i < attempts - 1) {
        try {
          TimeUnit.MILLISECONDS.sleep(pauseMs);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          LOGGER.warn("Interrupted while polling for verification status", e);
          break;
        }
      }
    }

    return Optional.empty();
  }
}
