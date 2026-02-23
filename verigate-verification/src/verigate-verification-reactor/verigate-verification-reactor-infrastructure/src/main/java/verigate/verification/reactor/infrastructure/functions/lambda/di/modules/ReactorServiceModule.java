/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.reactor.infrastructure.functions.lambda.di.modules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import verigate.verification.reactor.application.handlers.*;
import verigate.verification.reactor.domain.handlers.*;

public class ReactorServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(VerificationStepCompletedEventHandler.class)
            .to(DefaultVerificationStepCompletedEventHandler.class);
        bind(VerificationStepFailedEventHandler.class)
            .to(DefaultVerificationStepFailedEventHandler.class);
        bind(VerificationEventRouter.class)
            .to(DefaultVerificationEventRouter.class);
    }

    @Provides
    @Singleton
    private ObjectMapper provideObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
