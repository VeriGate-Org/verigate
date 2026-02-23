/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.functions.lambda.di.modules;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import verigate.billing.application.handlers.UsageAggregationHandler;
import verigate.billing.domain.services.BillingService;
import verigate.billing.domain.services.UsageTrackingService;

/**
 * Guice module specific to the Usage Aggregator Lambda.
 * Extends the base {@link ServiceModule} and provides the
 * {@link UsageAggregationHandler} binding.
 */
public final class UsageAggregatorServiceModule extends ServiceModule {

    /**
     * Provides the usage aggregation handler with its dependencies injected.
     *
     * @param usageTrackingService the usage tracking service
     * @param billingService       the billing service
     * @return a configured usage aggregation handler
     */
    @Provides
    @Singleton
    UsageAggregationHandler provideUsageAggregationHandler(
        UsageTrackingService usageTrackingService,
        BillingService billingService) {
        return new UsageAggregationHandler(usageTrackingService, billingService);
    }
}
