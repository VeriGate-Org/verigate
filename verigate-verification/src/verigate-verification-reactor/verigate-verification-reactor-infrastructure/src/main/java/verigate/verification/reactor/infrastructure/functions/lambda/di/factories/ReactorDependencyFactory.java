/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.reactor.infrastructure.functions.lambda.di.factories;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import verigate.verification.reactor.domain.handlers.VerificationEventRouter;
import verigate.verification.reactor.infrastructure.functions.lambda.di.modules.ReactorServiceModule;

public class ReactorDependencyFactory {

    protected final Injector injector;

    public ReactorDependencyFactory() {
        this.injector = Guice.createInjector(Stage.PRODUCTION, new ReactorServiceModule());
    }

    public ReactorDependencyFactory(Injector injector) {
        this.injector = injector;
    }

    public VerificationEventRouter getEventRouter() {
        return injector.getInstance(VerificationEventRouter.class);
    }
}
