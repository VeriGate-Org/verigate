/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.application.handlers;

import com.google.inject.Inject;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.domain.models.DunningSchedule;
import verigate.billing.domain.services.DunningService;

/**
 * Daily handler that processes due dunning retries.
 */
public class DunningProcessorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DunningProcessorHandler.class);

    private final DunningService dunningService;

    @Inject
    public DunningProcessorHandler(DunningService dunningService) {
        this.dunningService = dunningService;
    }

    public void handle() {
        LOG.info("Starting dunning processor");

        try {
            List<DunningSchedule> dueRetries = dunningService.getDueRetries();

            if (dueRetries.isEmpty()) {
                LOG.info("No due dunning retries found");
                return;
            }

            LOG.info("Processing {} due dunning retries", dueRetries.size());

            int successful = 0;
            int failed = 0;

            for (DunningSchedule schedule : dueRetries) {
                try {
                    dunningService.processRetry(schedule.dunningId());
                    successful++;
                } catch (Exception e) {
                    failed++;
                    LOG.error("Failed to process dunning retry: {}", schedule.dunningId(), e);
                }
            }

            LOG.info("Dunning processor completed. Processed: {}, failed: {}", successful, failed);

        } catch (Exception e) {
            LOG.error("Dunning processor failed", e);
            throw new RuntimeException("Dunning processor failed", e);
        }
    }
}
