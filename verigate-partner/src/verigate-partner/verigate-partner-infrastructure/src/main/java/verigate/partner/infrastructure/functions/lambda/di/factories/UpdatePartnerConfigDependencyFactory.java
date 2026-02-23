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
import verigate.partner.application.handlers.DefaultUpdatePartnerConfigurationCommandHandler;
import verigate.partner.infrastructure.functions.lambda.di.modules.UpdatePartnerConfigServiceModule;

public class UpdatePartnerConfigDependencyFactory extends DependencyFactory {

    public UpdatePartnerConfigDependencyFactory() {
        super(Guice.createInjector(Stage.PRODUCTION, new UpdatePartnerConfigServiceModule()));
    }

    public UpdatePartnerConfigDependencyFactory(Injector injector) {
        super(injector);
    }

    public DefaultUpdatePartnerConfigurationCommandHandler getUpdatePartnerConfigCommandHandler() {
        return injector.getInstance(DefaultUpdatePartnerConfigurationCommandHandler.class);
    }

    public InvalidMessageQueue<SQSMessage> getUpdatePartnerConfigInvalidMessageQueue() {
        return injector.getInstance(
            Key.get(new TypeLiteral<InvalidMessageQueue<SQSMessage>>() {},
                Names.named("UpdatePartnerConfigInvalidMessageQueue")));
    }

    public DeadLetterQueue<SQSMessage> getUpdatePartnerConfigDeadLetterQueue() {
        return injector.getInstance(
            Key.get(new TypeLiteral<DeadLetterQueue<SQSMessage>>() {},
                Names.named("UpdatePartnerConfigDeadLetterMessageQueue")));
    }
}
