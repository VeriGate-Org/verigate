/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.infrastructure.functions.lambda.di.modules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import crosscutting.builders.DefaultUrlBuilder;
import crosscutting.builders.UrlBuilder;
import crosscutting.config.Config;
import crosscutting.config.PropertiesFileConfig;
import crosscutting.environment.Environment;
import crosscutting.environment.EnvironmentConfig;
import crosscutting.metrics.DatadogMeter;
import crosscutting.metrics.Meter;
import crosscutting.resiliency.DefaultRetry;
import infrastructure.commands.commandstore.AggregateCommandStoreDao;
import infrastructure.featureflags.FeatureFlags;
import infrastructure.featureflags.GrowthBookProvider;
import infrastructure.functions.lambda.builders.DefaultParameterErrorMessageBuilder;
import infrastructure.functions.lambda.builders.ParameterErrorMessageBuilder;
import infrastructure.functions.lambda.exceptions.handlers.LambdaApiGatewayRetryExceptionHandler;
import infrastructure.functions.lambda.exceptions.handlers.LambdaExceptionHandler;
import infrastructure.functions.lambda.serializers.http.DefaultJsonSerializer;
import infrastructure.functions.lambda.serializers.http.JsonSerializer;
import infrastructure.functions.lambda.serializers.internal.DefaultInternalTransportJsonSerializer;
import infrastructure.functions.lambda.serializers.internal.InternalTransportJsonSerializer;
import infrastructure.http.AwsAuthHttpClient;
import infrastructure.http.DefaultInternalRequestBuilder;
import infrastructure.http.HttpClientFactory;
import infrastructure.http.InternalRequestBuilder;
import infrastructure.mapping.Mapper;
import infrastructure.mapping.PassthroughMapper;
import infrastructure.persistence.CommandStoreDatabaseRepository;
import infrastructure.secrets.AwsSecretManager;
import infrastructure.secrets.SecretManager;
import java.time.Duration;
import java.util.Set;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import verigate.verification.cg.application.factories.VerifyPartySpecificationFactory;
import verigate.verification.cg.application.handlers.DefaultVerifyPartyCommandHandler;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.handlers.VerifyPartyCommandHandler;
import verigate.verification.cg.domain.routing.DefaultCommandRouter;
import verigate.verification.cg.domain.routing.QueueDispatcherFactory;
import verigate.verification.cg.domain.routing.VerificationCommandRouter;
import verigate.verification.cg.domain.specifications.SpecificationFactory;
import verigate.verification.cg.infrastructure.commandstore.VerificationCommandStoreDao;
import verigate.verification.cg.infrastructure.persistence.commandstore.VerificationCommandStoreDynamoDbRepository;
import verigate.verification.cg.infrastructure.routing.DefaultQueueDispatcherFactory;

/**
 * This class is responsible for configuring the dependency injection bindings for the application.
 * It extends the AbstractModule class from the Google Guice library.
 */
public class ServiceModule extends AbstractModule {

  /**
   * Configures the bindings for the application. This method is called by the Guice framework to
   * set up the bindings.
   */
  @Override
  protected void configure() {
    // Infrastructure
    bind(InternalTransportJsonSerializer.class).to(DefaultInternalTransportJsonSerializer.class);
    bind(JsonSerializer.class).to(DefaultJsonSerializer.class);
    bind(InternalRequestBuilder.class).to(DefaultInternalRequestBuilder.class);
    bind(AggregateCommandStoreDao.class).to(VerificationCommandStoreDao.class);
    bind(CommandStoreDatabaseRepository.class).to(VerificationCommandStoreDynamoDbRepository.class);

    // Builders
    bind(ParameterErrorMessageBuilder.class).to(DefaultParameterErrorMessageBuilder.class);
    bind(UrlBuilder.class).to(DefaultUrlBuilder.class);

    // Environment and Config
    bind(Environment.class).to(EnvironmentConfig.class);
    bind(Config.class).to(PropertiesFileConfig.class).asEagerSingleton();
    bind(FeatureFlags.class).to(GrowthBookProvider.class);

    // Exceptions
    bind(LambdaExceptionHandler.class).to(LambdaApiGatewayRetryExceptionHandler.class);

    // Routing
    bind(VerificationCommandRouter.class).to(DefaultCommandRouter.class).in(Singleton.class);
    bind(QueueDispatcherFactory.class).to(DefaultQueueDispatcherFactory.class).in(Singleton.class);
    bind(Mapper.class).to(PassthroughMapper.class).in(Singleton.class);

    // Metrics
    bind(Meter.class).to(DatadogMeter.class).in(Singleton.class);

    // Command handlers
    bind(VerifyPartyCommandHandler.class).to(DefaultVerifyPartyCommandHandler.class);

    // Mappers
    // bind(VerificationCommandMapper.class).to(VerifyPartyCommandMapper.class);
  }

  @Provides
  @Singleton
  @Named("artifactSerializer")
  private InternalTransportJsonSerializer provideArtifactSerializer() {
    var serializer = new DefaultInternalTransportJsonSerializer();

    // Register Class Types for Commands
    serializer.registerClassType(
        VerifyPartyCommand.class.getSimpleName(), VerifyPartyCommand.class);

    return serializer;
  }

  @Provides
  @Singleton
  private SpecificationFactory<VerifyPartyCommand> provideVerifyPartySpecificationFactory() {
    return new VerifyPartySpecificationFactory();
  }

  @Provides
  @Singleton
  private HttpClientFactory provideHttpClientFactory() {
    return AwsAuthHttpClient::new;
  }

  @Provides
  @Singleton
  private SecretManager provideSecretManager(SecretsManagerClient secretsManagerClient) {
    return new AwsSecretManager(secretsManagerClient);
  }

  @Provides
  @Singleton
  private SecretsManagerClient provideSecretsManagerClient() {
    return SecretsManagerClient.builder().build();
  }

  @Provides
  @Singleton
  private SqsClient provideSqsClient() {
    return SqsClient.builder().build();
  }

  @Provides
  @Singleton
  public GlueClient provideAwsGlueClient() {
    return GlueClient.builder().build();
  }

  @Provides
  @Singleton
  private ObjectMapper provideObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return mapper;
  }

  protected DefaultRetry getDefaultRetry(Config config) {
    return new DefaultRetry(
        Integer.parseInt(config.get("verifications.retry.max-attempts")),
        Duration.ofMillis(Long.parseLong(config.get("verifications.retry.delay-ms"))),
        Set.of());
  }
}
