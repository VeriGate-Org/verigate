/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.functions.lambda.di.factories;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import verigate.billing.application.handlers.ReconciliationHandler;
import verigate.billing.infrastructure.functions.lambda.di.modules.ReconciliationServiceModule;

public class ReconciliationDependencyFactory extends DependencyFactory {

    public ReconciliationDependencyFactory() {
        super(Guice.createInjector(Stage.PRODUCTION, new ReconciliationServiceModule()));
    }

    public ReconciliationDependencyFactory(Injector injector) {
        super(injector);
    }

    public ReconciliationHandler getReconciliationHandler() {
        return injector.getInstance(ReconciliationHandler.class);
    }
}
