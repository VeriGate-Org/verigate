/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.functions.lambda.di.factories;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import verigate.billing.application.handlers.DunningProcessorHandler;
import verigate.billing.infrastructure.functions.lambda.di.modules.DunningProcessorServiceModule;

public class DunningProcessorDependencyFactory extends DependencyFactory {

    public DunningProcessorDependencyFactory() {
        super(Guice.createInjector(Stage.PRODUCTION, new DunningProcessorServiceModule()));
    }

    public DunningProcessorDependencyFactory(Injector injector) {
        super(injector);
    }

    public DunningProcessorHandler getDunningProcessorHandler() {
        return injector.getInstance(DunningProcessorHandler.class);
    }
}
