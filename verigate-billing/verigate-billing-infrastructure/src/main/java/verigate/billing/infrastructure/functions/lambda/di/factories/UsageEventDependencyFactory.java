/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.functions.lambda.di.factories;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import verigate.billing.application.handlers.UsageEventHandler;
import verigate.billing.infrastructure.functions.lambda.di.modules.UsageEventServiceModule;

/**
 * Dependency factory for the Usage Event Consumer Lambda.
 * Resolves the {@link UsageEventHandler} and its transitive dependencies
 * using the {@link UsageEventServiceModule}.
 */
public class UsageEventDependencyFactory extends DependencyFactory {

    /**
     * Creates a factory with the default {@link UsageEventServiceModule}.
     */
    public UsageEventDependencyFactory() {
        super(Guice.createInjector(Stage.PRODUCTION, new UsageEventServiceModule()));
    }

    /**
     * Creates a factory with a custom Guice injector.
     * Useful for testing with mock bindings.
     *
     * @param injector the custom Guice injector
     */
    public UsageEventDependencyFactory(Injector injector) {
        super(injector);
    }

    /**
     * Returns the usage event handler for processing Kinesis records.
     *
     * @return the usage event handler instance
     */
    public UsageEventHandler getUsageEventHandler() {
        return injector.getInstance(UsageEventHandler.class);
    }
}
