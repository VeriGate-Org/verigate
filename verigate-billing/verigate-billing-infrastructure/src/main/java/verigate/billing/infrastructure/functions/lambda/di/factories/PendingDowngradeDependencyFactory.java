/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.functions.lambda.di.factories;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import verigate.billing.application.handlers.PendingDowngradeHandler;
import verigate.billing.infrastructure.functions.lambda.di.modules.PendingDowngradeServiceModule;

public class PendingDowngradeDependencyFactory extends DependencyFactory {

    public PendingDowngradeDependencyFactory() {
        super(Guice.createInjector(Stage.PRODUCTION, new PendingDowngradeServiceModule()));
    }

    public PendingDowngradeDependencyFactory(Injector injector) {
        super(injector);
    }

    public PendingDowngradeHandler getPendingDowngradeHandler() {
        return injector.getInstance(PendingDowngradeHandler.class);
    }
}
