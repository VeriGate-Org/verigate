/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.infrastructure.functions.lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.riskengine.application.handlers.VerificationEventHandler;
import verigate.riskengine.application.handlers.VerificationEventHandler.VerificationCompletedPayload;
import verigate.riskengine.domain.enums.VerificationOutcome;
import verigate.riskengine.domain.enums.VerificationType;
import verigate.riskengine.infrastructure.functions.lambda.di.factories.DependencyFactory;

/**
 * AWS Lambda handler that consumes verification events from Kinesis
 * and delegates to the VerificationEventHandler for workflow tracking
 * and risk aggregation.
 */
public class RiskEngineEventConsumerLambdaHandler implements RequestHandler<KinesisEvent, Void> {

    private static final Logger LOG =
        LoggerFactory.getLogger(RiskEngineEventConsumerLambdaHandler.class);

    private final VerificationEventHandler eventHandler;
    private final ObjectMapper objectMapper;

    public RiskEngineEventConsumerLambdaHandler() {
        var factory = new DependencyFactory();
        this.eventHandler = factory.getVerificationEventHandler();
        this.objectMapper = factory.getObjectMapper();
    }

    public RiskEngineEventConsumerLambdaHandler(VerificationEventHandler eventHandler,
                                                 ObjectMapper objectMapper) {
        this.eventHandler = eventHandler;
        this.objectMapper = objectMapper;
    }

    @Override
    public Void handleRequest(KinesisEvent event, Context context) {
        if (event == null || event.getRecords() == null || event.getRecords().isEmpty()) {
            LOG.warn("Received empty or null Kinesis event");
            return null;
        }

        LOG.info("Processing {} Kinesis records for risk engine", event.getRecords().size());

        int successCount = 0;
        int failureCount = 0;

        for (KinesisEvent.KinesisEventRecord record : event.getRecords()) {
            try {
                String data = new String(
                    record.getKinesis().getData().array(), StandardCharsets.UTF_8);

                var payload = parsePayload(data);
                if (payload != null) {
                    eventHandler.handle(payload);
                    successCount++;
                }
            } catch (Exception e) {
                failureCount++;
                LOG.error("Failed to process Kinesis record: sequenceNumber={}, error={}",
                    record.getKinesis().getSequenceNumber(), e.getMessage(), e);
            }
        }

        LOG.info("Risk engine batch processing complete: total={}, success={}, failed={}",
            event.getRecords().size(), successCount, failureCount);

        if (failureCount > 0 && successCount == 0) {
            throw new RuntimeException(
                "All " + failureCount + " Kinesis records failed processing");
        }

        return null;
    }

    private VerificationCompletedPayload parsePayload(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);

            String detailType = root.has("detailType") ? root.get("detailType").asText() : "";
            if (!detailType.contains("Verification")) {
                LOG.debug("Skipping non-verification event: {}", detailType);
                return null;
            }

            // Extract fields from the event structure
            UUID workflowId = root.has("workflowId")
                ? UUID.fromString(root.get("workflowId").asText())
                : UUID.fromString(root.get("id").asText());

            UUID commandId = UUID.fromString(root.get("id").asText());

            VerificationType verificationType = VerificationType.valueOf(
                root.get("verificationType").asText());

            VerificationOutcome outcome = mapDetailTypeToOutcome(detailType);

            Map<String, String> auxiliaryData = Map.of();
            if (root.has("details") && root.get("details").has("auxiliaryData")) {
                JsonNode auxNode = root.get("details").get("auxiliaryData");
                auxiliaryData = objectMapper.convertValue(auxNode,
                    objectMapper.getTypeFactory().constructMapType(
                        java.util.HashMap.class, String.class, String.class));
            }

            String partnerId = root.has("partnerId") ? root.get("partnerId").asText() : "unknown";

            return new VerificationCompletedPayload(
                workflowId, commandId, verificationType, outcome, auxiliaryData, partnerId);
        } catch (Exception e) {
            LOG.error("Failed to parse verification event payload", e);
            return null;
        }
    }

    private VerificationOutcome mapDetailTypeToOutcome(String detailType) {
        if (detailType.contains("Succeeded")) return VerificationOutcome.SUCCEEDED;
        if (detailType.contains("HardFail")) return VerificationOutcome.HARD_FAIL;
        if (detailType.contains("SoftFail")) return VerificationOutcome.SOFT_FAIL;
        if (detailType.contains("SystemOutage")) return VerificationOutcome.SYSTEM_OUTAGE;
        return VerificationOutcome.SOFT_FAIL;
    }
}
