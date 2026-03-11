/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.infrastructure.functions.lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.riskengine.application.handlers.WorkflowTimeoutHandler;
import verigate.riskengine.infrastructure.functions.lambda.di.factories.DependencyFactory;

/**
 * Scheduled Lambda handler that scans for timed-out workflows
 * and triggers partial aggregation.
 */
public class WorkflowTimeoutLambdaHandler implements RequestHandler<ScheduledEvent, Void> {

    private static final Logger LOG =
        LoggerFactory.getLogger(WorkflowTimeoutLambdaHandler.class);

    private final WorkflowTimeoutHandler timeoutHandler;

    public WorkflowTimeoutLambdaHandler() {
        this(new DependencyFactory().getWorkflowTimeoutHandler());
    }

    public WorkflowTimeoutLambdaHandler(WorkflowTimeoutHandler timeoutHandler) {
        this.timeoutHandler = timeoutHandler;
    }

    @Override
    public Void handleRequest(ScheduledEvent event, Context context) {
        LOG.info("Workflow timeout scan triggered");
        try {
            timeoutHandler.handleTimeouts();
            LOG.info("Workflow timeout scan completed successfully");
        } catch (Exception e) {
            LOG.error("Workflow timeout scan failed", e);
            throw e;
        }
        return null;
    }
}
