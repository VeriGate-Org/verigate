/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.functions.lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import java.time.YearMonth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.application.handlers.ReconciliationHandler;
import verigate.billing.infrastructure.functions.lambda.di.factories.ReconciliationDependencyFactory;

/**
 * AWS Lambda handler for monthly reconciliation.
 * Triggered by EventBridge on the 1st of each month.
 */
public class ReconciliationLambdaHandler implements RequestHandler<ScheduledEvent, Void> {

    private static final Logger LOG =
        LoggerFactory.getLogger(ReconciliationLambdaHandler.class);

    private final ReconciliationHandler reconciliationHandler;

    public ReconciliationLambdaHandler() {
        this(new ReconciliationDependencyFactory());
    }

    public ReconciliationLambdaHandler(ReconciliationDependencyFactory factory) {
        this.reconciliationHandler = factory.getReconciliationHandler();
    }

    @Override
    public Void handleRequest(ScheduledEvent event, Context context) {
        YearMonth previousMonth = YearMonth.now().minusMonths(1);

        LOG.info("Reconciliation triggered by EventBridge for period: {}", previousMonth);

        try {
            reconciliationHandler.handle(previousMonth);
            LOG.info("Reconciliation completed for period: {}", previousMonth);
        } catch (Exception e) {
            LOG.error("Reconciliation failed for period: {}", previousMonth, e);
            throw new RuntimeException(
                "Reconciliation failed for period: " + previousMonth, e);
        }

        return null;
    }
}
