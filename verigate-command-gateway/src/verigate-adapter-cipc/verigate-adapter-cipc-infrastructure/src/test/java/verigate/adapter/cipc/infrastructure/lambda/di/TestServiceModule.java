/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.infrastructure.lambda.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import crosscutting.config.Config;
import crosscutting.resiliency.DefaultRetry;
import infrastructure.functions.lambda.serializers.internal.DefaultInternalTransportJsonSerializer;
import infrastructure.functions.lambda.serializers.internal.InternalTransportJsonSerializer;
import java.time.Duration;
import java.util.Set;
import verigate.adapter.cipc.domain.services.CipcCompanyService;
import verigate.adapter.cipc.infrastructure.mocks.MockCipcCompanyService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Test module that provides mock implementations for testing.
 */
public class TestServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        // Bind mock implementations for testing
        bind(CipcCompanyService.class).to(MockCipcCompanyService.class).in(Singleton.class);
        bind(InternalTransportJsonSerializer.class)
            .to(DefaultInternalTransportJsonSerializer.class);
    }

    /**
     * Provides default retry configuration for test scenarios.
     */
    protected DefaultRetry getDefaultRetry(Config config) {
        return new DefaultRetry(
            Integer.parseInt(config.get("verifications.retry.max-attempts")),
            Duration.ofMillis(Long.parseLong(config.get("verifications.retry.delay-ms"))),
            Set.of());
    }

    @Provides
    @Singleton
    @Named("artifactSerializer")
    private InternalTransportJsonSerializer provideArtifactSerializer() {
        var serializer = new DefaultInternalTransportJsonSerializer();
        serializer.registerClassType(
            VerifyPartyCommand.class.getSimpleName(), VerifyPartyCommand.class);
        return serializer;
    }
}
