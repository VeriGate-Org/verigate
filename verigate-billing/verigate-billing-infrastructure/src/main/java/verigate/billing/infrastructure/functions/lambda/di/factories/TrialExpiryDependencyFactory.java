/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.functions.lambda.di.factories;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import verigate.billing.application.handlers.TrialExpiryHandler;
import verigate.billing.infrastructure.functions.lambda.di.modules.TrialExpiryServiceModule;

public class TrialExpiryDependencyFactory extends DependencyFactory {

    public TrialExpiryDependencyFactory() {
        super(Guice.createInjector(Stage.PRODUCTION, new TrialExpiryServiceModule()));
    }

    public TrialExpiryDependencyFactory(Injector injector) {
        super(injector);
    }

    public TrialExpiryHandler getTrialExpiryHandler() {
        return injector.getInstance(TrialExpiryHandler.class);
    }
}
