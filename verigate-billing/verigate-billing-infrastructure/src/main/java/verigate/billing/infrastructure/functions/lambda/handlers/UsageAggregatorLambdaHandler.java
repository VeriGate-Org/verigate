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
import verigate.billing.application.handlers.UsageAggregationHandler;
import verigate.billing.infrastructure.functions.lambda.di.factories.UsageAggregatorDependencyFactory;

/**
 * AWS Lambda handler for scheduled usage aggregation.
 * Triggered by EventBridge on a daily schedule ({@code rate(1 day)}),
 * this handler aggregates the current month's usage records into
 * billing summaries for all active partners.
 *
 * <p>The aggregation is designed to be re-runnable: executing it multiple
 * times for the same period will overwrite existing summaries with the
 * latest counts, ensuring consistency without creating duplicates.</p>
 */
public class UsageAggregatorLambdaHandler implements RequestHandler<ScheduledEvent, Void> {

    private static final Logger LOG =
        LoggerFactory.getLogger(UsageAggregatorLambdaHandler.class);

    private final UsageAggregationHandler usageAggregationHandler;

    /**
     * Default no-arg constructor for AWS Lambda initialization.
     * Creates the dependency factory and resolves the aggregation handler.
     */
    public UsageAggregatorLambdaHandler() {
        this(new UsageAggregatorDependencyFactory());
    }

    /**
     * Constructor for testing with a custom dependency factory.
     *
     * @param factory the dependency factory to use
     */
    public UsageAggregatorLambdaHandler(UsageAggregatorDependencyFactory factory) {
        this.usageAggregationHandler = factory.getUsageAggregationHandler();
    }

    /**
     * Handles the scheduled event by triggering usage aggregation for the
     * current billing period.
     *
     * @param event   the EventBridge scheduled event
     * @param context the Lambda execution context
     * @return always returns {@code null}
     */
    @Override
    public Void handleRequest(ScheduledEvent event, Context context) {
        YearMonth currentPeriod = YearMonth.now();

        LOG.info("Usage aggregation triggered by EventBridge. "
                + "Event time: {}, aggregation period: {}",
            event != null ? event.getTime() : "N/A", currentPeriod);

        try {
            usageAggregationHandler.handle(currentPeriod);

            LOG.info("Usage aggregation completed successfully for period: {}", currentPeriod);

        } catch (Exception e) {
            LOG.error("Usage aggregation failed for period: {}", currentPeriod, e);
            throw new RuntimeException(
                "Usage aggregation failed for period: " + currentPeriod, e);
        }

        return null;
    }
}
