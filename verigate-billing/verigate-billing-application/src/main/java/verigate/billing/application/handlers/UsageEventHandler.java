/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.application.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.events.VerificationCompletedEvent;
import verigate.billing.domain.models.UsageRecord;
import verigate.billing.domain.services.UsageTrackingService;

/**
 * Processes verification completed events from the Kinesis stream.
 * Converts each event into a usage record and delegates to the
 * {@link UsageTrackingService} for persistence. The handler is
 * idempotent: duplicate events (same verificationId) are silently ignored.
 */
public class UsageEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(UsageEventHandler.class);

    private final UsageTrackingService usageTrackingService;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a new {@link UsageEventHandler}.
     *
     * @param usageTrackingService the service used to record usage
     * @param objectMapper         the JSON object mapper
     */
    @Inject
    public UsageEventHandler(UsageTrackingService usageTrackingService, ObjectMapper objectMapper) {
        this.usageTrackingService = usageTrackingService;
        this.objectMapper = objectMapper;
    }

    /**
     * Handles a raw JSON string from the Kinesis stream by deserializing it
     * into a {@link VerificationCompletedEvent} and recording the usage.
     *
     * @param rawEventJson the raw JSON payload from the Kinesis record
     */
    public void handle(String rawEventJson) {
        LOG.info("Processing usage event from Kinesis stream");

        try {
            VerificationCompletedEvent event = objectMapper.readValue(
                rawEventJson, VerificationCompletedEvent.class);

            LOG.debug("Deserialized verification completed event: verificationId={}, partnerId={}, "
                    + "verificationType={}, outcome={}",
                event.verificationId(), event.partnerId(),
                event.verificationType(), event.outcome());

            UsageRecord usageRecord = toUsageRecord(event);

            boolean recorded = usageTrackingService.recordUsage(usageRecord);

            if (recorded) {
                LOG.info("Usage record created: usageId={}, partnerId={}, verificationType={}, "
                        + "verificationId={}",
                    usageRecord.usageId(), usageRecord.partnerId(),
                    usageRecord.verificationType(), usageRecord.verificationId());
            } else {
                LOG.info("Duplicate usage event ignored: verificationId={}",
                    event.verificationId());
            }

        } catch (IllegalArgumentException e) {
            LOG.warn("Invalid verification event received, skipping: {}", e.getMessage());
        } catch (Exception e) {
            LOG.error("Failed to process usage event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process usage event", e);
        }
    }

    /**
     * Converts a {@link VerificationCompletedEvent} into a {@link UsageRecord}.
     * Generates a unique usage ID and uses the event timestamp if available,
     * falling back to the current time.
     *
     * @param event the verification completed event
     * @return the corresponding usage record
     */
    private UsageRecord toUsageRecord(VerificationCompletedEvent event) {
        String usageId = DomainConstants.USAGE_ID_PREFIX + UUID.randomUUID();
        LocalDateTime timestamp = event.eventTimestamp() != null
            ? event.eventTimestamp()
            : LocalDateTime.now();

        return new UsageRecord(
            usageId,
            event.partnerId(),
            event.verificationType(),
            event.verificationId(),
            event.outcome(),
            timestamp
        );
    }
}
