/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.functions.lambda.di.factories;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import verigate.billing.application.handlers.UsageAggregationHandler;
import verigate.billing.infrastructure.functions.lambda.di.modules.UsageAggregatorServiceModule;

/**
 * Dependency factory for the Usage Aggregator Lambda.
 * Resolves the {@link UsageAggregationHandler} and its transitive dependencies
 * using the {@link UsageAggregatorServiceModule}.
 */
public class UsageAggregatorDependencyFactory extends DependencyFactory {

    /**
     * Creates a factory with the default {@link UsageAggregatorServiceModule}.
     */
    public UsageAggregatorDependencyFactory() {
        super(Guice.createInjector(Stage.PRODUCTION, new UsageAggregatorServiceModule()));
    }

    /**
     * Creates a factory with a custom Guice injector.
     * Useful for testing with mock bindings.
     *
     * @param injector the custom Guice injector
     */
    public UsageAggregatorDependencyFactory(Injector injector) {
        super(injector);
    }

    /**
     * Returns the usage aggregation handler for scheduled aggregation.
     *
     * @return the usage aggregation handler instance
     */
    public UsageAggregationHandler getUsageAggregationHandler() {
        return injector.getInstance(UsageAggregationHandler.class);
    }
}
