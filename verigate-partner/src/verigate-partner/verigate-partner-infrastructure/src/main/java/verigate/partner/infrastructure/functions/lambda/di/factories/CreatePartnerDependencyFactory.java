/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.infrastructure.functions.lambda.di.factories;

import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import domain.messages.DeadLetterQueue;
import domain.messages.InvalidMessageQueue;
import verigate.partner.application.handlers.DefaultCreatePartnerCommandHandler;
import verigate.partner.infrastructure.functions.lambda.di.modules.CreatePartnerServiceModule;

public class CreatePartnerDependencyFactory extends DependencyFactory {

    public CreatePartnerDependencyFactory() {
        super(Guice.createInjector(Stage.PRODUCTION, new CreatePartnerServiceModule()));
    }

    public CreatePartnerDependencyFactory(Injector injector) {
        super(injector);
    }

    public DefaultCreatePartnerCommandHandler getCreatePartnerCommandHandler() {
        return injector.getInstance(DefaultCreatePartnerCommandHandler.class);
    }

    public InvalidMessageQueue<SQSMessage> getCreatePartnerInvalidMessageQueue() {
        return injector.getInstance(
            Key.get(new TypeLiteral<InvalidMessageQueue<SQSMessage>>() {},
                Names.named("CreatePartnerInvalidMessageQueue")));
    }

    public DeadLetterQueue<SQSMessage> getCreatePartnerDeadLetterQueue() {
        return injector.getInstance(
            Key.get(new TypeLiteral<DeadLetterQueue<SQSMessage>>() {},
                Names.named("CreatePartnerDeadLetterMessageQueue")));
    }
}
