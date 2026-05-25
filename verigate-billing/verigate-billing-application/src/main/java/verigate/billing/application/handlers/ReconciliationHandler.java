/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.application.handlers;

import com.google.inject.Inject;
import java.time.YearMonth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.domain.models.ReconciliationReport;
import verigate.billing.domain.services.ReportingService;

/**
 * Monthly handler for reconciliation of the previous month's financial data.
 */
public class ReconciliationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ReconciliationHandler.class);

    private final ReportingService reportingService;

    @Inject
    public ReconciliationHandler(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    public void handle(YearMonth period) {
        LOG.info("Starting reconciliation for period: {}", period);

        try {
            ReconciliationReport report = reportingService.generateReconciliation(period);
            LOG.info("Reconciliation completed for period: {}, status: {}",
                period, report.status());
        } catch (Exception e) {
            LOG.error("Reconciliation failed for period: {}", period, e);
            throw new RuntimeException("Reconciliation failed for period: " + period, e);
        }
    }
}
