/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.functions.lambda.di.factories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import verigate.billing.infrastructure.functions.lambda.di.modules.ServiceModule;

/**
 * Base dependency factory for the VeriGate Billing service.
 * Wraps the Guice injector and provides access to core dependencies.
 * Subclasses extend this for Lambda-specific dependency resolution.
 */
public class DependencyFactory {

    protected final Injector injector;

    /**
     * Creates a factory with the default {@link ServiceModule}.
     */
    public DependencyFactory() {
        this.injector = Guice.createInjector(Stage.PRODUCTION, new ServiceModule());
    }

    /**
     * Creates a factory with a custom Guice injector.
     * Useful for testing with mock bindings.
     *
     * @param injector the custom Guice injector
     */
    public DependencyFactory(Injector injector) {
        this.injector = injector;
    }

    /**
     * Returns the Jackson ObjectMapper configured for the billing service.
     *
     * @return the ObjectMapper instance
     */
    public ObjectMapper getObjectMapper() {
        return injector.getInstance(ObjectMapper.class);
    }
}
