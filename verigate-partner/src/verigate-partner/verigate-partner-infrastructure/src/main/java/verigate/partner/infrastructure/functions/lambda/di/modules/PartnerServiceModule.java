/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.infrastructure.functions.lambda.di.modules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import crosscutting.environment.Environment;
import crosscutting.environment.EnvironmentConfig;
import infrastructure.functions.lambda.serializers.internal.DefaultInternalTransportJsonSerializer;
import infrastructure.functions.lambda.serializers.internal.InternalTransportJsonSerializer;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import verigate.partner.application.handlers.*;
import verigate.partner.application.services.DefaultPartnerConfigurationService;
import verigate.partner.domain.commands.CreatePartnerCommand;
import verigate.partner.domain.repositories.PartnerConfigurationRepository;
import verigate.partner.domain.repositories.PartnerRepository;
import verigate.partner.domain.services.PartnerConfigurationService;
import verigate.partner.infrastructure.repositories.DynamoDbPartnerConfigurationRepository;
import verigate.partner.infrastructure.repositories.DynamoDbPartnerRepository;

public class PartnerServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Environment.class).to(EnvironmentConfig.class);
        bind(PartnerConfigurationService.class).to(DefaultPartnerConfigurationService.class);
    }

    @Provides
    @Singleton
    @Named("artifactSerializer")
    private InternalTransportJsonSerializer provideArtifactSerializer() {
        var serializer = new DefaultInternalTransportJsonSerializer();
        serializer.registerClassType(
            CreatePartnerCommand.class.getSimpleName(), CreatePartnerCommand.class);
        return serializer;
    }

    @Provides
    @Singleton
    private DynamoDbClient provideDynamoDbClient() {
        return DynamoDbClient.builder().build();
    }

    @Provides
    @Singleton
    private SqsClient provideSqsClient() {
        return SqsClient.builder().build();
    }

    @Provides
    @Singleton
    private ObjectMapper provideObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Provides
    @Singleton
    private PartnerRepository providePartnerRepository(DynamoDbClient dynamoDbClient,
                                                       Environment environment) {
        String tableName = environment.get("PARTNER_TABLE_NAME");
        if (tableName == null || tableName.isBlank()) {
            tableName = "verigate-partner";
        }
        return new DynamoDbPartnerRepository(dynamoDbClient, tableName);
    }

    @Provides
    @Singleton
    private PartnerConfigurationRepository providePartnerConfigurationRepository(
            DynamoDbClient dynamoDbClient, ObjectMapper objectMapper, Environment environment) {
        String tableName = environment.get("PARTNER_CONFIGURATION_TABLE_NAME");
        if (tableName == null || tableName.isBlank()) {
            tableName = "verigate-partner-configuration";
        }
        return new DynamoDbPartnerConfigurationRepository(dynamoDbClient, tableName, objectMapper);
    }

    @Provides
    @Singleton
    private DefaultPartnerConfigurationService providePartnerConfigurationService(
            PartnerConfigurationRepository configurationRepository) {
        return new DefaultPartnerConfigurationService(configurationRepository);
    }

    @Provides
    @Singleton
    private DefaultActivatePartnerCommandHandler provideActivatePartnerCommandHandler(
            PartnerRepository partnerRepository) {
        return new DefaultActivatePartnerCommandHandler(partnerRepository);
    }

    @Provides
    @Singleton
    private DefaultUpdatePartnerConfigurationCommandHandler provideUpdatePartnerConfigCommandHandler(
            PartnerRepository partnerRepository,
            PartnerConfigurationRepository configurationRepository) {
        return new DefaultUpdatePartnerConfigurationCommandHandler(
            partnerRepository, configurationRepository);
    }
}
