package verigate.verification.infrastructure.services;

import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import domain.events.EventPublisher;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.SendTaskFailureRequest;
import software.amazon.awssdk.services.sfn.model.SendTaskSuccessRequest;
import software.amazon.awssdk.services.sfn.model.StartExecutionRequest;
import software.amazon.awssdk.services.sfn.model.StartExecutionResponse;
import verigate.verification.domain.events.VerificationStepStartedEvent;
import verigate.verification.domain.models.VerificationAggregateRoot;
import verigate.verification.domain.repositories.VerificationRepository;
import verigate.verification.domain.services.VerificationOrchestrationService;

/**
 * Step Functions based implementation of verification orchestration.
 * Feature flag controlled via env var USE_STEP_FUNCTIONS=true
 */
public class StepFunctionsVerificationOrchestrationService implements VerificationOrchestrationService {

    private final SfnClient sfnClient;
    private final VerificationRepository verificationRepository;
    private final ObjectMapper objectMapper;
    private final EventPublisher<Object> eventPublisher;
    private final String stateMachineArn;
    private final StepFunctionTaskTokenStore tokenStore;

    @Inject
    public StepFunctionsVerificationOrchestrationService(SfnClient sfnClient,
            VerificationRepository verificationRepository,
            ObjectMapper objectMapper,
            EventPublisher<Object> eventPublisher,
            StepFunctionTaskTokenStore tokenStore) {
        this.sfnClient = sfnClient;
        this.verificationRepository = verificationRepository;
        this.objectMapper = objectMapper;
        this.eventPublisher = eventPublisher;
        this.tokenStore = tokenStore;
        this.stateMachineArn = System.getenv().getOrDefault("VERIFICATION_STATE_MACHINE_ARN", "");
    }

    @Override
    public void startVerification(VerificationAggregateRoot verification, Map<String, Object> metadata) {
        // Persist current aggregate state first
        verificationRepository.addOrUpdate(verification);
        // Compose input
        StartExecutionRequest.Builder builder = StartExecutionRequest.builder()
                .stateMachineArn(stateMachineArn)
                .name(verification.getVerificationId().toString());
        try {
            builder.input(objectMapper.writeValueAsString(Map.of(
                    "verificationId", verification.getVerificationId().toString(),
                    "partnerId", verification.getPartnerId(),
                    "steps", verification.getSteps(),
                    "metadata", metadata)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize Step Functions input", e);
        }
        StartExecutionResponse response = sfnClient.startExecution(builder.build());
        // Publish an event indicating orchestration started (reusing existing event type minimally)
        eventPublisher.publish(new VerificationStepStartedEvent(
            verification.getVerificationId(),
            verification.getVerificationId(), // placeholder step id for start marker
            verification.getPartnerId(),
            null // step type unknown at orchestration start
        ));
        // Optionally log executionArn for debugging
        System.out.println("Started Step Functions execution: " + response.executionArn());
    }

    @Override
    public void retryVerification(VerificationAggregateRoot verification) {
        // For now start a new execution (idempotency to be handled with unique name or
        // execution id)
        startVerification(verification, Map.of());
    }

    @Override
    public void processStepCompletion(UUID verificationId, UUID stepId, boolean success, String details) {
        // In SFN we expect an external callback with task token; a mapping store would
        // be needed (not implemented yet)
        // Placeholder: look up token (future DynamoDB table). For now no-op.
        String taskToken = lookupTaskToken(stepId);
        if (taskToken == null) {
            throw new IllegalStateException("Task token not found for step " + stepId);
        }
        if (success) {
            sfnClient.sendTaskSuccess(SendTaskSuccessRequest.builder()
                    .taskToken(taskToken)
                    .output("{\"result\":\"PASSED\"}")
                    .build());
        } else {
            sfnClient.sendTaskFailure(SendTaskFailureRequest.builder()
                    .taskToken(taskToken)
                    .error("STEP_FAILED")
                    .cause(details)
                    .build());
        }
    }

    @Override
    public void cancelVerification(UUID verificationId, String reason) {
        // TODO optionally call StopExecution (requires storing executionArn by
        // verificationId)
    }

    private String lookupTaskToken(UUID stepId) {
        return tokenStore.getTaskToken(stepId).orElse(null);
    }
}
