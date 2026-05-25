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
import verigate.billing.application.handlers.DunningProcessorHandler;
import verigate.billing.infrastructure.functions.lambda.di.factories.DunningProcessorDependencyFactory;

/**
 * AWS Lambda handler for daily dunning processor.
 */
public class DunningProcessorLambdaHandler implements RequestHandler<ScheduledEvent, Void> {

    private static final Logger LOG =
        LoggerFactory.getLogger(DunningProcessorLambdaHandler.class);

    private final DunningProcessorHandler dunningProcessorHandler;

    public DunningProcessorLambdaHandler() {
        this(new DunningProcessorDependencyFactory());
    }

    public DunningProcessorLambdaHandler(DunningProcessorDependencyFactory factory) {
        this.dunningProcessorHandler = factory.getDunningProcessorHandler();
    }

    @Override
    public Void handleRequest(ScheduledEvent event, Context context) {
        LOG.info("Dunning processor triggered by EventBridge");
        try {
            dunningProcessorHandler.handle();
            LOG.info("Dunning processor completed");
        } catch (Exception e) {
            LOG.error("Dunning processor failed", e);
            throw new RuntimeException("Dunning processor failed", e);
        }
        return null;
    }
}
