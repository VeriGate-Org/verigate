/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.functions.lambda.di.modules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import verigate.billing.application.handlers.UsageEventHandler;
import verigate.billing.domain.services.UsageTrackingService;

/**
 * Guice module specific to the Usage Event Consumer Lambda.
 * Extends the base {@link ServiceModule} and provides the
 * {@link UsageEventHandler} binding.
 */
public final class UsageEventServiceModule extends ServiceModule {

    /**
     * Provides the usage event handler with its dependencies injected.
     *
     * @param usageTrackingService the usage tracking service
     * @param objectMapper         the JSON object mapper
     * @return a configured usage event handler
     */
    @Provides
    @Singleton
    UsageEventHandler provideUsageEventHandler(
        UsageTrackingService usageTrackingService,
        ObjectMapper objectMapper) {
        return new UsageEventHandler(usageTrackingService, objectMapper);
    }
}
