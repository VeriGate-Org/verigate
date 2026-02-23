/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.infrastructure.functions.lambda.di.modules;

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
import crosscutting.resiliency.DefaultRetry;
import domain.events.DefaultEventIdFactory;
import domain.events.EventIdFactory;
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
import infrastructure.secrets.AwsSecretManager;
import infrastructure.secrets.SecretManager;
import java.time.Duration;
import java.util.Set;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import verigate.adapter.sars.application.handlers.DefaultVerifyTaxComplianceCommandHandler;
import verigate.adapter.sars.domain.services.SarsTaxComplianceService;
import verigate.adapter.sars.infrastructure.config.SarsApiConfiguration;
import verigate.adapter.sars.infrastructure.http.SarsHttpAdapter;
import verigate.adapter.sars.infrastructure.http.SarsTaxApiAdapter;
import verigate.adapter.sars.infrastructure.mappers.SarsDtoMapper;
import verigate.adapter.sars.infrastructure.services.DefaultSarsTaxComplianceService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.factories.EventFactory;
import verigate.verification.cg.domain.factories.VerificationEventFactory;

/**
 * This class is responsible for configuring the dependency injection bindings for the SARS
 * Tax Compliance adapter. It extends the AbstractModule class from the Google Guice library.
 */
public class ServiceModule extends AbstractModule {

  /**
   * Configures the bindings for the SARS Tax Compliance adapter. This method is called by the
   * Guice framework to set up the bindings.
   */
  @Override
  protected void configure() {
    // Infrastructure
    bind(InternalTransportJsonSerializer.class).to(DefaultInternalTransportJsonSerializer.class);
    bind(JsonSerializer.class).to(DefaultJsonSerializer.class);
    bind(InternalRequestBuilder.class).to(DefaultInternalRequestBuilder.class);

    // Builders
    bind(ParameterErrorMessageBuilder.class).to(DefaultParameterErrorMessageBuilder.class);
    bind(UrlBuilder.class).to(DefaultUrlBuilder.class);

    // Environment and Config
    bind(Environment.class).to(EnvironmentConfig.class);
    bind(Config.class).to(PropertiesFileConfig.class).asEagerSingleton();

    // Exceptions
    bind(LambdaExceptionHandler.class).to(LambdaApiGatewayRetryExceptionHandler.class);

    // Routing
    bind(Mapper.class).to(PassthroughMapper.class).in(Singleton.class);

    // SARS Services
    bind(SarsTaxComplianceService.class).to(DefaultSarsTaxComplianceService.class);

    // Factories
    bind(EventFactory.class).to(VerificationEventFactory.class);
    bind(EventIdFactory.class).to(DefaultEventIdFactory.class);
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

  /**
   * Provides the KinesisClient instance.
   * <p>
   * This client is used to access Kinesis for event publishing.
   * </p>
   *
   * @return A configured instance of KinesisClient
   */
  @Provides
  @Singleton
  private KinesisClient provideKinesisClient() {
    return KinesisClient.builder().build();
  }

  @Provides
  @Singleton
  public GlueClient provideAwsGlueClient() {
    return GlueClient.builder().build();
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
  private ObjectMapper provideObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return mapper;
  }

  @Provides
  @Singleton
  private SarsApiConfiguration provideSarsApiConfiguration(
      Environment environment, Config config) {
    return new SarsApiConfiguration(environment, config);
  }

  @Provides
  @Singleton
  private SarsHttpAdapter provideSarsHttpAdapter(
      Environment environment, ObjectMapper objectMapper) {
    return new SarsHttpAdapter(environment, objectMapper);
  }

  @Provides
  @Singleton
  private SarsTaxApiAdapter provideSarsTaxApiAdapter(SarsHttpAdapter httpAdapter) {
    return new SarsTaxApiAdapter(httpAdapter);
  }

  @Provides
  @Singleton
  private SarsDtoMapper provideSarsDtoMapper() {
    return new SarsDtoMapper();
  }

  @Provides
  @Singleton
  private DefaultSarsTaxComplianceService provideSarsTaxComplianceService(
      SarsTaxApiAdapter apiAdapter, SarsDtoMapper dtoMapper) {
    return new DefaultSarsTaxComplianceService(apiAdapter, dtoMapper);
  }

  @Provides
  @Singleton
  private DefaultVerifyTaxComplianceCommandHandler provideVerifyTaxComplianceCommandHandler(
      SarsTaxComplianceService sarsTaxComplianceService) {
    return new DefaultVerifyTaxComplianceCommandHandler(sarsTaxComplianceService);
  }

  protected DefaultRetry getDefaultRetry(Config config) {
    return new DefaultRetry(
        Integer.parseInt(config.get("verifications.retry.max-attempts")),
        Duration.ofMillis(Long.parseLong(config.get("verifications.retry.delay-ms"))),
        Set.of());
  }
}
