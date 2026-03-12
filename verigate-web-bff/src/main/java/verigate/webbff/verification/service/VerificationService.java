package verigate.webbff.verification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import verigate.webbff.observability.CorrelationIdFilter;
import verigate.webbff.config.properties.ResponsePollingProperties;
import verigate.webbff.verification.model.CommandStatus;
import verigate.webbff.verification.model.VerificationListItem;
import verigate.webbff.verification.model.VerificationListResponse;
import verigate.webbff.verification.model.VerificationRequest;
import verigate.webbff.verification.model.VerificationResponse;
import verigate.webbff.verification.model.VerificationType;
import verigate.webbff.verification.model.VerifyPartyCommandMessage;
import verigate.webbff.verification.model.VerifyPartyCommandMessage.Origination;
import verigate.webbff.verification.repository.CommandStatusRepository;
import verigate.webbff.verification.repository.DynamoDbPolicyRepository;
import verigate.webbff.verification.repository.RiskAssessmentRepository;
import verigate.webbff.verification.repository.model.PolicyDataModel;
import verigate.webbff.verification.repository.model.RiskAssessmentItem;
import verigate.webbff.verification.repository.model.VerificationCommandStoreItem;

@Service
public class VerificationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(VerificationService.class);

  /**
   * Maps verification types to their SQS queue names.
   * Used for multi-check workflow dispatch.
   */
  private static final Map<VerificationType, String> VERIFICATION_TYPE_QUEUE_MAP = Map.ofEntries(
      Map.entry(VerificationType.IDENTITY_VERIFICATION, "adapter-dha"),
      Map.entry(VerificationType.COMPANY_VERIFICATION, "adapter-cipc"),
      Map.entry(VerificationType.SANCTIONS_SCREENING, "adapter-worldcheck"),
      Map.entry(VerificationType.WATCHLIST_SCREENING, "adapter-worldcheck"),
      Map.entry(VerificationType.VERIFICATION_OF_PERSONAL_DETAILS, "adapter-qlink"),
      Map.entry(VerificationType.VERIFICATION_OF_BANK_DETAILS, "adapter-qlink"),
      Map.entry(VerificationType.BANK_ACCOUNT_VERIFICATION, "adapter-qlink"),
      Map.entry(VerificationType.PROPERTY_OWNERSHIP_VERIFICATION, "adapter-deedsweb"),
      Map.entry(VerificationType.EMPLOYMENT_VERIFICATION, "adapter-employment"),
      Map.entry(VerificationType.NEGATIVE_NEWS_SCREENING, "adapter-negativenews"),
      Map.entry(VerificationType.FRAUD_WATCHLIST_SCREENING, "adapter-fraudwatchlist"),
      Map.entry(VerificationType.DOCUMENT_VERIFICATION, "adapter-document"),
      Map.entry(VerificationType.QUALIFICATION_VERIFICATION, "adapter-saqa"),
      Map.entry(VerificationType.CREDIT_CHECK, "adapter-creditbureau"),
      Map.entry(VerificationType.TAX_COMPLIANCE_VERIFICATION, "adapter-sars"),
      Map.entry(VerificationType.INCOME_VERIFICATION, "adapter-income")
  );

  /**
   * Maps PolicyBuilder step types to VerificationType enum values.
   */
  private static final Map<String, VerificationType> STEP_TYPE_TO_VERIFICATION_TYPE = Map.of(
      "id_verification", VerificationType.IDENTITY_VERIFICATION,
      "address_verification", VerificationType.VERIFICATION_OF_PERSONAL_DETAILS,
      "sanctions_check", VerificationType.SANCTIONS_SCREENING,
      "company_check", VerificationType.COMPANY_VERIFICATION,
      "credit_check", VerificationType.CREDIT_CHECK,
      "tax_check", VerificationType.TAX_COMPLIANCE_VERIFICATION,
      "document_check", VerificationType.DOCUMENT_VERIFICATION
  );

  private final SqsClient sqsClient;
  private final ObjectMapper objectMapper;
  private final CommandStatusRepository statusRepository;
  private final RiskAssessmentRepository riskAssessmentRepository;
  private final DynamoDbPolicyRepository policyRepository;
  private final ResponsePollingProperties pollingProperties;
  private final String queueName;
  private final Counter verificationSubmittedCounter;

  public VerificationService(
      SqsClient sqsClient,
      ObjectMapper objectMapper,
      CommandStatusRepository statusRepository,
      RiskAssessmentRepository riskAssessmentRepository,
      DynamoDbPolicyRepository policyRepository,
      ResponsePollingProperties pollingProperties,
      MeterRegistry meterRegistry,
      @Value("${verigate.verification.queue-name:verify-party}") String queueName) {
    this.sqsClient = sqsClient;
    this.objectMapper = objectMapper;
    this.statusRepository = statusRepository;
    this.riskAssessmentRepository = riskAssessmentRepository;
    this.policyRepository = policyRepository;
    this.pollingProperties = pollingProperties;
    this.queueName = queueName;
    this.verificationSubmittedCounter = Counter.builder("verification.submitted")
        .description("Number of verification commands submitted")
        .register(meterRegistry);
  }

  public VerificationResponse submitVerification(VerificationRequest request) {
    if (request.isWorkflowRequest()) {
      return submitWorkflowVerification(request);
    }
    return submitSingleVerification(request);
  }

  /**
   * Single-check flow -- existing behavior, backwards compatible.
   */
  private VerificationResponse submitSingleVerification(VerificationRequest request) {
    var commandId = UUID.randomUUID();
    var command = buildCommand(commandId, request.verificationType(), request);

    dispatch(command, queueName);
    verificationSubmittedCounter.increment();

    var status = pollForStatus(commandId)
        .map(VerificationCommandStoreItem::getStatus)
        .orElse(CommandStatus.PENDING);

    return new VerificationResponse(commandId, status);
  }

  /**
   * Multi-check workflow flow -- fans out N commands to N adapter queues.
   */
  private VerificationResponse submitWorkflowVerification(VerificationRequest request) {
    UUID workflowId = UUID.randomUUID();
    List<VerificationType> steps = resolveWorkflowSteps(request);

    LOGGER.info("Starting multi-check workflow {}: policyId={}, steps={}",
        workflowId, request.policyId(), steps);

    Map<String, UUID> expectedChecks = new HashMap<>();
    for (VerificationType step : steps) {
      UUID commandId = UUID.randomUUID();
      var command = buildCommand(commandId, step, request);

      // Add workflowId to metadata so the risk engine can correlate
      var enrichedMetadata = new HashMap<>(
          Optional.ofNullable(request.metadata()).orElse(Map.of()));
      enrichedMetadata.put("workflowId", workflowId.toString());
      enrichedMetadata.put("policyId", request.policyId());

      var enrichedCommand = new VerifyPartyCommandMessage(
          commandId,
          command.getCreatedDate(),
          command.getCreatedBy(),
          step,
          command.getOrigination(),
          enrichedMetadata);

      String targetQueue = VERIFICATION_TYPE_QUEUE_MAP.getOrDefault(step, queueName);
      dispatch(enrichedCommand, targetQueue);
      expectedChecks.put(step.name(), commandId);
    }

    LOGGER.info("Workflow {} dispatched {} commands", workflowId, steps.size());
    return new VerificationResponse(
        expectedChecks.values().iterator().next(),
        CommandStatus.PENDING,
        workflowId);
  }

  /**
   * Resolves the verification steps for a workflow from the policy stored in DynamoDB.
   * Loads the policy by policyId, validates it is PUBLISHED, then extracts
   * VerificationType from each non-control step.
   */
  @SuppressWarnings("unchecked")
  private List<VerificationType> resolveWorkflowSteps(VerificationRequest request) {
    String policyId = request.policyId();
    PolicyDataModel policy = policyRepository.findById(policyId).orElse(null);

    if (policy == null || !"PUBLISHED".equals(policy.getStatus())) {
      LOGGER.warn("Policy {} not found or not published, using default steps", policyId);
      return List.of(
          VerificationType.IDENTITY_VERIFICATION,
          VerificationType.SANCTIONS_SCREENING,
          VerificationType.CREDIT_CHECK
      );
    }

    try {
      List<Map<String, Object>> steps = objectMapper.readValue(
          policy.getStepsJson(),
          objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));

      List<VerificationType> verificationTypes = steps.stream()
          .map(step -> (String) step.get("type"))
          .filter(type -> type != null && STEP_TYPE_TO_VERIFICATION_TYPE.containsKey(type))
          .map(STEP_TYPE_TO_VERIFICATION_TYPE::get)
          .toList();

      if (verificationTypes.isEmpty()) {
        LOGGER.warn("Policy {} has no verification steps, using default steps", policyId);
        return List.of(
            VerificationType.IDENTITY_VERIFICATION,
            VerificationType.SANCTIONS_SCREENING,
            VerificationType.CREDIT_CHECK
        );
      }

      return verificationTypes;
    } catch (JsonProcessingException e) {
      LOGGER.error("Failed to parse steps from policy {}", policyId, e);
      return List.of(
          VerificationType.IDENTITY_VERIFICATION,
          VerificationType.SANCTIONS_SCREENING,
          VerificationType.CREDIT_CHECK
      );
    }
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

  public Optional<RiskAssessmentItem> findRiskAssessment(UUID verificationId) {
    return riskAssessmentRepository.findByVerificationId(verificationId);
  }

  private VerifyPartyCommandMessage buildCommand(UUID commandId, VerificationType type,
                                                  VerificationRequest request) {
    Origination origination = new Origination(request.originationType(), request.originationId());
    Map<String, Object> metadata = new HashMap<>(
        Optional.ofNullable(request.metadata()).orElse(Map.of()));
    String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
    if (correlationId != null) {
      metadata.put("correlationId", correlationId);
    }
    return new VerifyPartyCommandMessage(
        commandId,
        Instant.now(),
        request.requestedBy(),
        type,
        origination,
        metadata);
  }

  private void dispatch(VerifyPartyCommandMessage command, String targetQueueName) {
    var queueUrl =
        sqsClient.getQueueUrl(
            GetQueueUrlRequest.builder().queueName(targetQueueName).build()).queueUrl();
    String payload;
    try {
      payload = objectMapper.writeValueAsString(command);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize command", e);
    }
    var requestBuilder = SendMessageRequest.builder()
        .queueUrl(queueUrl)
        .messageBody(payload);

    String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
    if (correlationId != null) {
      requestBuilder.messageAttributes(Map.of(
          "correlationId", MessageAttributeValue.builder()
              .dataType("String").stringValue(correlationId).build()));
    }

    sqsClient.sendMessage(requestBuilder.build());
    LOGGER.info("Dispatched verification command {} to queue {}", command.getId(), targetQueueName);
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
