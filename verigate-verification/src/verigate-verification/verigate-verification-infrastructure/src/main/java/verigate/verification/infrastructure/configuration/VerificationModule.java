/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import domain.commands.CommandDispatcher;
import domain.events.EventPublisher;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.sfn.SfnClient;
import verigate.verification.application.handlers.InitiateVerificationCommandHandler;
import verigate.verification.application.handlers.RetryVerificationCommandHandler;
import verigate.verification.application.services.DefaultVerificationOrchestrationService;
import verigate.verification.domain.repositories.VerificationRepository;
import verigate.verification.domain.services.VerificationOrchestrationService;
import verigate.verification.infrastructure.repositories.DynamoDbVerificationRepository;
import verigate.verification.infrastructure.services.StepFunctionsVerificationOrchestrationService;

/**
 * Guice module for dependency injection configuration.
 */
public class VerificationModule extends AbstractModule {

    @Override
    protected void configure() {
        // Bind repositories
        bind(VerificationRepository.class).to(DynamoDbVerificationRepository.class).in(Singleton.class);

        // Feature flag to switch orchestration implementation
        if (Boolean.parseBoolean(System.getenv().getOrDefault("USE_STEP_FUNCTIONS", "false"))) {
            bind(VerificationOrchestrationService.class).to(StepFunctionsVerificationOrchestrationService.class)
                    .in(Singleton.class);
        } else {
            bind(VerificationOrchestrationService.class).to(DefaultVerificationOrchestrationService.class)
                    .in(Singleton.class);
        }

        // Bind command handlers
        bind(InitiateVerificationCommandHandler.class).in(Singleton.class);
        bind(RetryVerificationCommandHandler.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public DynamoDbClient provideDynamoDbClient() {
        return DynamoDbClient.builder().build();
    }

    @Provides
    @Singleton
    public ObjectMapper provideObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Provides
    @Singleton
    public CommandDispatcher<Object> provideCommandDispatcher() {
        // This would be implemented to dispatch commands via SQS/SNS
        return new CommandDispatcher<Object>() {
            @Override
            public void dispatch(Object command) {
                // Implementation would send command to appropriate queue
                // For now, this is a placeholder
                System.out.println("Dispatching command: " + command.getClass().getSimpleName());
            }
        };
    }

    @Provides
    @Singleton
    public EventPublisher<Object> provideEventPublisher() {
        // This would be implemented to publish events via SNS/EventBridge
        return new EventPublisher<Object>() {
            @Override
            public void publish(Object event) {
                // Implementation would publish event to appropriate topic
                // For now, this is a placeholder
                System.out.println("Publishing event: " + event.getClass().getSimpleName());
            }
        };
    }

    @Provides
    @Singleton
    public SfnClient provideSfnClient() {
        return SfnClient.builder().build();
    }
}