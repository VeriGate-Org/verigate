package verigate.webbff.verification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import verigate.webbff.config.properties.ResponsePollingProperties;
import verigate.webbff.verification.model.CommandStatus;
import verigate.webbff.verification.model.VerificationListItem;
import verigate.webbff.verification.model.VerificationListResponse;
import verigate.webbff.verification.model.VerificationRequest;
import verigate.webbff.verification.model.VerificationResponse;
import verigate.webbff.verification.model.VerifyPartyCommandMessage;
import verigate.webbff.verification.model.VerifyPartyCommandMessage.Origination;
import verigate.webbff.verification.repository.CommandStatusRepository;
import verigate.webbff.verification.repository.model.VerificationCommandStoreItem;

@Service
public class VerificationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(VerificationService.class);

  private final SqsClient sqsClient;
  private final ObjectMapper objectMapper;
  private final CommandStatusRepository statusRepository;
  private final ResponsePollingProperties pollingProperties;
  private final String queueName;

  public VerificationService(
      SqsClient sqsClient,
      ObjectMapper objectMapper,
      CommandStatusRepository statusRepository,
      ResponsePollingProperties pollingProperties,
      @Value("${verigate.verification.queue-name:verify-party}") String queueName) {
    this.sqsClient = sqsClient;
    this.objectMapper = objectMapper;
    this.statusRepository = statusRepository;
    this.pollingProperties = pollingProperties;
    this.queueName = queueName;
  }

  public VerificationResponse submitVerification(VerificationRequest request) {
    var commandId = UUID.randomUUID();
    var command = buildCommand(commandId, request);

    dispatch(command);

    var status = pollForStatus(commandId)
        .map(VerificationCommandStoreItem::getStatus)
        .orElse(CommandStatus.PENDING);

    return new VerificationResponse(commandId, status);
  }

  public Optional<VerificationCommandStoreItem> findVerification(UUID commandId) {
    return statusRepository.findById(commandId);
  }

  public VerificationListResponse listVerifications(
      String partnerId, String status, int limit, Map<String, AttributeValue> cursor) {
    var page = statusRepository.findByPartnerId(partnerId, status, limit, cursor);
    var items = page.items().stream()
        .map(item -> new VerificationListItem(
            UUID.fromString(item.getCommandId()),
            item.getStatus(),
            item.getCreatedAt(),
            item.getCommandName()))
        .toList();

    String nextCursor = null;
    if (page.hasMore()) {
      var lastKey = page.lastEvaluatedKey();
      var cursorCommandId = lastKey.get("commandId");
      nextCursor = cursorCommandId != null ? cursorCommandId.s() : null;
    }

    return new VerificationListResponse(items, nextCursor, page.hasMore());
  }

  private VerifyPartyCommandMessage buildCommand(UUID commandId, VerificationRequest request) {
    Origination origination = new Origination(request.originationType(), request.originationId());
    Map<String, Object> metadata = Optional.ofNullable(request.metadata()).orElse(Map.of());
    return new VerifyPartyCommandMessage(
        commandId,
        Instant.now(),
        request.requestedBy(),
        request.verificationType(),
        origination,
        metadata);
  }

  private void dispatch(VerifyPartyCommandMessage command) {
    var queueUrl =
        sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).queueUrl();
    String payload;
    try {
      payload = objectMapper.writeValueAsString(command);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize command", e);
    }
    sqsClient.sendMessage(
        SendMessageRequest.builder().queueUrl(queueUrl).messageBody(payload).build());
    LOGGER.info("Dispatched verification command {} to queue {}", command.getId(), queueName);
  }

  private Optional<VerificationCommandStoreItem> pollForStatus(UUID commandId) {
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
