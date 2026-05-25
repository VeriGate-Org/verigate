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
import verigate.billing.application.handlers.TrialExpiryHandler;
import verigate.billing.infrastructure.functions.lambda.di.factories.TrialExpiryDependencyFactory;

/**
 * AWS Lambda handler for daily trial expiry processing.
 */
public class TrialExpiryLambdaHandler implements RequestHandler<ScheduledEvent, Void> {

    private static final Logger LOG =
        LoggerFactory.getLogger(TrialExpiryLambdaHandler.class);

    private final TrialExpiryHandler trialExpiryHandler;

    public TrialExpiryLambdaHandler() {
        this(new TrialExpiryDependencyFactory());
    }

    public TrialExpiryLambdaHandler(TrialExpiryDependencyFactory factory) {
        this.trialExpiryHandler = factory.getTrialExpiryHandler();
    }

    @Override
    public Void handleRequest(ScheduledEvent event, Context context) {
        LOG.info("Trial expiry check triggered by EventBridge");
        try {
            trialExpiryHandler.handle();
            LOG.info("Trial expiry check completed");
        } catch (Exception e) {
            LOG.error("Trial expiry check failed", e);
            throw new RuntimeException("Trial expiry check failed", e);
        }
        return null;
    }
}
