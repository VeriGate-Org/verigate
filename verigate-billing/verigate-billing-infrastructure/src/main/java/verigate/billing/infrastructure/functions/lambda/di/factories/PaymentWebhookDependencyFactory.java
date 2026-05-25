/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.functions.lambda.di.factories;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import verigate.billing.domain.services.PaymentService;
import verigate.billing.infrastructure.functions.lambda.di.modules.PaymentWebhookServiceModule;

public class PaymentWebhookDependencyFactory extends DependencyFactory {

    public PaymentWebhookDependencyFactory() {
        super(Guice.createInjector(Stage.PRODUCTION, new PaymentWebhookServiceModule()));
    }

    public PaymentWebhookDependencyFactory(Injector injector) {
        super(injector);
    }

    public PaymentService getPaymentService() {
        return injector.getInstance(PaymentService.class);
    }
}
