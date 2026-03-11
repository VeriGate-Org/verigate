/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.functions.lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import verigate.billing.application.handlers.UsageEventHandler;
import verigate.billing.infrastructure.functions.lambda.di.factories.UsageEventDependencyFactory;

/**
 * AWS Lambda handler for consuming verification completed events from Kinesis.
 * This handler is triggered by Kinesis stream records and delegates each
 * record to the {@link UsageEventHandler} for processing.
 *
 * <p>Each Kinesis record contains a JSON-serialized {@code VerificationCompletedEvent}.
 * The handler processes records sequentially within each batch invocation.
 * Individual record failures are logged but do not prevent processing of
 * subsequent records in the batch.</p>
 */
public class UsageEventConsumerLambdaHandler implements RequestHandler<KinesisEvent, Void> {

    private static final Logger LOG =
        LoggerFactory.getLogger(UsageEventConsumerLambdaHandler.class);
    private static final ObjectMapper MDC_MAPPER = new ObjectMapper();

    private final UsageEventHandler usageEventHandler;

    /**
     * Default no-arg constructor for AWS Lambda initialization.
     * Creates the dependency factory and resolves the usage event handler.
     */
    public UsageEventConsumerLambdaHandler() {
        this(new UsageEventDependencyFactory());
    }

    /**
     * Constructor for testing with a custom dependency factory.
     *
     * @param factory the dependency factory to use
     */
    public UsageEventConsumerLambdaHandler(UsageEventDependencyFactory factory) {
        this.usageEventHandler = factory.getUsageEventHandler();
    }

    /**
     * Handles a batch of Kinesis event records.
     * Each record is deserialized and processed individually.
     *
     * @param event   the Kinesis event containing one or more records
     * @param context the Lambda execution context
     * @return always returns {@code null}
     */
    @Override
    public Void handleRequest(KinesisEvent event, Context context) {
        if (event == null || event.getRecords() == null || event.getRecords().isEmpty()) {
            LOG.warn("Received empty or null Kinesis event, nothing to process");
            return null;
        }

        LOG.info("Processing {} Kinesis records", event.getRecords().size());

        int successCount = 0;
        int failureCount = 0;

        for (KinesisEvent.KinesisEventRecord record : event.getRecords()) {
            try {
                String data = new String(
                    record.getKinesis().getData().array(), StandardCharsets.UTF_8);

                populateMdcFromEvent(data);

                LOG.debug("Processing Kinesis record: sequenceNumber={}, partitionKey={}",
                    record.getKinesis().getSequenceNumber(),
                    record.getKinesis().getPartitionKey());

                usageEventHandler.handle(data);
                successCount++;

            } catch (Exception e) {
                failureCount++;
                LOG.error("Failed to process Kinesis record: sequenceNumber={}, error={}",
                    record.getKinesis().getSequenceNumber(), e.getMessage(), e);
            } finally {
                MDC.clear();
            }
        }

        LOG.info("Kinesis batch processing complete: total={}, success={}, failed={}",
            event.getRecords().size(), successCount, failureCount);

        // If all records failed, throw to trigger Lambda retry
        if (failureCount > 0 && successCount == 0) {
            throw new RuntimeException(
                "All " + failureCount + " Kinesis records failed processing");
        }

        return null;
    }

    private void populateMdcFromEvent(String rawJson) {
        try {
            JsonNode node = MDC_MAPPER.readTree(rawJson);
            putMdcIfPresent(node, "correlationId");
            putMdcIfPresent(node, "partnerId");
            putMdcIfPresent(node, "verificationId");
        } catch (Exception e) {
            LOG.debug("Could not extract MDC fields from event payload", e);
        }
    }

    private void putMdcIfPresent(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value != null && !value.isNull() && value.isTextual()) {
            MDC.put(field, value.asText());
        }
    }
}
