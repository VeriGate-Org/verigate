/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.functions.lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.application.handlers.PendingDowngradeHandler;
import verigate.billing.infrastructure.functions.lambda.di.factories.PendingDowngradeDependencyFactory;

/**
 * AWS Lambda handler for daily pending downgrade processing.
 */
public class PendingDowngradeLambdaHandler implements RequestHandler<ScheduledEvent, Void> {

    private static final Logger LOG =
        LoggerFactory.getLogger(PendingDowngradeLambdaHandler.class);

    private final PendingDowngradeHandler pendingDowngradeHandler;

    public PendingDowngradeLambdaHandler() {
        this(new PendingDowngradeDependencyFactory());
    }

    public PendingDowngradeLambdaHandler(PendingDowngradeDependencyFactory factory) {
        this.pendingDowngradeHandler = factory.getPendingDowngradeHandler();
    }

    @Override
    public Void handleRequest(ScheduledEvent event, Context context) {
        LOG.info("Pending downgrade check triggered by EventBridge");
        try {
            pendingDowngradeHandler.handle();
            LOG.info("Pending downgrade check completed");
        } catch (Exception e) {
            LOG.error("Pending downgrade check failed", e);
            throw new RuntimeException("Pending downgrade check failed", e);
        }
        return null;
    }
}
