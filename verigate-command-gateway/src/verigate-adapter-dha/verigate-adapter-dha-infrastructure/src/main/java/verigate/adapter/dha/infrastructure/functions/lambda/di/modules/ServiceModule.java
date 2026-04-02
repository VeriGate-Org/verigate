/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.infrastructure.functions.lambda.di.modules;

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
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import verigate.adapter.dha.application.handlers.DefaultVerifyIdentityCommandHandler;
import verigate.adapter.dha.domain.services.DhaIdentityVerificationService;
import verigate.adapter.dha.domain.services.IdentityVaultService;
import verigate.adapter.dha.infrastructure.config.DhaApiConfiguration;
import verigate.adapter.dha.infrastructure.http.DhaHttpAdapter;
import verigate.adapter.dha.infrastructure.vault.DynamoDbIdentityVaultAdapter;
import verigate.adapter.dha.infrastructure.http.DhaIdentityApiAdapter;
import verigate.adapter.dha.infrastructure.mappers.DhaDtoMapper;
import verigate.adapter.dha.infrastructure.mappers.HanisResponseMapper;
import verigate.adapter.dha.infrastructure.config.HanisConfiguration;
import verigate.adapter.dha.infrastructure.services.DefaultDhaIdentityVerificationService;
import verigate.adapter.dha.infrastructure.services.DefaultHanisVerificationService;
import verigate.adapter.dha.infrastructure.services.HanisDhaIdentityVerificationService;
import verigate.adapter.dha.infrastructure.services.MockHanisVerificationService;
import verigate.adapter.dha.infrastructure.soap.HanisSoapClient;
import verigate.adapter.dha.domain.services.HanisVerificationService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.factories.EventFactory;
import verigate.verification.cg.domain.factories.VerificationEventFactory;

/**
 * This class is responsible for configuring the dependency injection bindings for the DHA adapter.
 * It extends the AbstractModule class from the Google Guice library.
 */
public class ServiceModule extends AbstractModule {

  /**
   * Configures the bindings for the DHA adapter. This method is called by the Guice framework to
   * set up the bindings.
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

    // DHA Services - binding is handled by provider method (HANIS or REST based on config)

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
  private DhaApiConfiguration provideDhaApiConfiguration(
      Environment environment, Config config) {
    return new DhaApiConfiguration(environment, config);
  }

  @Provides
  @Singleton
  private DhaHttpAdapter provideDhaHttpAdapter(
      Environment environment, ObjectMapper objectMapper) {
    return new DhaHttpAdapter(environment, objectMapper);
  }

  @Provides
  @Singleton
  private DhaIdentityApiAdapter provideDhaIdentityApiAdapter(DhaHttpAdapter httpAdapter) {
    return new DhaIdentityApiAdapter(httpAdapter);
  }

  @Provides
  @Singleton
  private DhaDtoMapper provideDhaDtoMapper() {
    return new DhaDtoMapper();
  }

  @Provides
  @Singleton
  private DefaultDhaIdentityVerificationService provideDhaIdentityVerificationService(
      DhaIdentityApiAdapter apiAdapter, DhaDtoMapper dtoMapper) {
    return new DefaultDhaIdentityVerificationService(apiAdapter, dtoMapper);
  }

  @Provides
  @Singleton
  private HanisConfiguration provideHanisConfiguration(Environment environment) {
    return new HanisConfiguration(environment);
  }

  @Provides
  @Singleton
  private HanisResponseMapper provideHanisResponseMapper() {
    return new HanisResponseMapper();
  }

  @Provides
  @Singleton
  private HanisVerificationService provideHanisVerificationService(
      HanisConfiguration hanisConfig) {
    if (hanisConfig.isEnabled()) {
      hanisConfig.validate();
      HanisSoapClient soapClient = new HanisSoapClient(
          hanisConfig.getPrimaryUrl(),
          hanisConfig.getFailoverUrl(),
          Duration.ofSeconds(hanisConfig.getTimeoutSeconds()));
      return new DefaultHanisVerificationService(soapClient);
    }
    return new MockHanisVerificationService();
  }

  @Provides
  @Singleton
  private DhaIdentityVerificationService provideDhaIdentityVerificationServiceBinding(
      HanisConfiguration hanisConfig,
      HanisVerificationService hanisVerificationService,
      HanisResponseMapper hanisResponseMapper,
      DefaultDhaIdentityVerificationService restService) {
    if (hanisConfig.isEnabled()) {
      return new HanisDhaIdentityVerificationService(
          hanisVerificationService, hanisResponseMapper, hanisConfig);
    }
    return restService;
  }

  @Provides
  @Singleton
  private DynamoDbClient provideDynamoDbClient() {
    return DynamoDbClient.builder().build();
  }

  @Provides
  @Singleton
  private IdentityVaultService provideIdentityVaultService(
      DynamoDbClient dynamoDbClient, Environment environment) {
    String tableName = environment.get("IDENTITY_VAULT_TABLE_NAME");
    String freshnessDaysStr = environment.get("IDENTITY_VAULT_FRESHNESS_DAYS");
    int freshnessDays = (freshnessDaysStr != null && !freshnessDaysStr.isBlank())
        ? Integer.parseInt(freshnessDaysStr) : 90;
    return new DynamoDbIdentityVaultAdapter(dynamoDbClient, tableName, freshnessDays);
  }

  @Provides
  @Singleton
  private DefaultVerifyIdentityCommandHandler provideVerifyIdentityCommandHandler(
      DhaIdentityVerificationService identityVerificationService,
      IdentityVaultService identityVaultService,
      Environment environment) {
    String vaultEnabledStr = environment.get("IDENTITY_VAULT_ENABLED");
    boolean vaultEnabled = Boolean.parseBoolean(
        vaultEnabledStr != null ? vaultEnabledStr : "false");
    return new DefaultVerifyIdentityCommandHandler(
        identityVerificationService, identityVaultService, vaultEnabled);
  }

  protected DefaultRetry getDefaultRetry(Config config) {
    return new DefaultRetry(
        Integer.parseInt(config.get("verifications.retry.max-attempts")),
        Duration.ofMillis(Long.parseLong(config.get("verifications.retry.delay-ms"))),
        Set.of());
  }
}
