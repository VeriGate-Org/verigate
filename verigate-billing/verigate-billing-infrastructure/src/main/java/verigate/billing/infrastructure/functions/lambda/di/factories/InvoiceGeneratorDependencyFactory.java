/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.functions.lambda.di.factories;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import verigate.billing.application.handlers.InvoiceGenerationHandler;
import verigate.billing.infrastructure.functions.lambda.di.modules.InvoiceGeneratorServiceModule;

public class InvoiceGeneratorDependencyFactory extends DependencyFactory {

    public InvoiceGeneratorDependencyFactory() {
        super(Guice.createInjector(Stage.PRODUCTION, new InvoiceGeneratorServiceModule()));
    }

    public InvoiceGeneratorDependencyFactory(Injector injector) {
        super(injector);
    }

    public InvoiceGenerationHandler getInvoiceGenerationHandler() {
        return injector.getInstance(InvoiceGenerationHandler.class);
    }
}
