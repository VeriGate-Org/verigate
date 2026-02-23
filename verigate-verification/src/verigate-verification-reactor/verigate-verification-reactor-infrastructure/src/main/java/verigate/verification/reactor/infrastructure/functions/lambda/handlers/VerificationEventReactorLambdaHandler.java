/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.reactor.infrastructure.functions.lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.verification.reactor.domain.handlers.VerificationEventRouter;
import verigate.verification.reactor.infrastructure.functions.lambda.di.factories.ReactorDependencyFactory;

import java.nio.charset.StandardCharsets;

/**
 * Lambda handler that consumes Kinesis events from the verification event stream
 * and routes them to appropriate event handlers to advance the verification flow.
 */
public class VerificationEventReactorLambdaHandler
        implements RequestHandler<KinesisEvent, Void> {

    private static final Logger logger = LoggerFactory.getLogger(
        VerificationEventReactorLambdaHandler.class);

    private final VerificationEventRouter eventRouter;
    private final ObjectMapper objectMapper;

    public VerificationEventReactorLambdaHandler() {
        ReactorDependencyFactory factory = new ReactorDependencyFactory();
        this.eventRouter = factory.getEventRouter();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public VerificationEventReactorLambdaHandler(VerificationEventRouter eventRouter) {
        this.eventRouter = eventRouter;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Void handleRequest(KinesisEvent event, Context context) {
        logger.info("Processing {} Kinesis records", event.getRecords().size());

        for (KinesisEvent.KinesisEventRecord record : event.getRecords()) {
            try {
                String payload = new String(
                    record.getKinesis().getData().array(), StandardCharsets.UTF_8);

                logger.debug("Processing Kinesis record: {}", record.getEventID());

                JsonNode eventNode = objectMapper.readTree(payload);
                String eventType = eventNode.has("eventType")
                    ? eventNode.get("eventType").asText() : "unknown";

                eventRouter.routeEvent(eventType, payload);

            } catch (Exception e) {
                logger.error("Failed to process Kinesis record: {}",
                    record.getEventID(), e);
            }
        }

        logger.info("Finished processing Kinesis records");
        return null;
    }
}
