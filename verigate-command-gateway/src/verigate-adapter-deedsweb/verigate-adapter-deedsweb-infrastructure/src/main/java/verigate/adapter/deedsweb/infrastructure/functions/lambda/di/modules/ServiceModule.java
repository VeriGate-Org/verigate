/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.functions.lambda.di.modules;

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
import infrastructure.functions.lambda.builders.DefaultParameterErrorMessageBuilder;
import infrastructure.functions.lambda.builders.ParameterErrorMessageBuilder;
import infrastructure.functions.lambda.exceptions.handlers.LambdaApiGatewayRetryExceptionHandler;
import infrastructure.functions.lambda.exceptions.handlers.LambdaExceptionHandler;
import infrastructure.functions.lambda.serializers.http.DefaultJsonSerializer;
import infrastructure.functions.lambda.serializers.http.JsonSerializer;
import infrastructure.functions.lambda.serializers.internal.DefaultInternalTransportJsonSerializer;
import infrastructure.functions.lambda.serializers.internal.InternalTransportJsonSerializer;
import infrastructure.mapping.Mapper;
import infrastructure.mapping.PassthroughMapper;
import infrastructure.secrets.AwsSecretManager;
import infrastructure.secrets.SecretManager;
import java.time.Duration;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import verigate.adapter.deedsweb.application.handlers.DefaultPropertyVerificationCommandHandler;
import verigate.adapter.deedsweb.application.services.DefaultPropertyOwnershipVerificationService;
import verigate.adapter.deedsweb.domain.services.DeedsRegistryClient;
import verigate.adapter.deedsweb.domain.services.DeedsWebCredentialsProvider;
import verigate.adapter.deedsweb.domain.services.PropertyOwnershipVerificationService;
import verigate.adapter.deedsweb.infrastructure.config.DeedsWebApiConfiguration;
import verigate.adapter.deedsweb.infrastructure.secrets.SecretsManagerDeedsWebCredentialsProvider;
import verigate.adapter.deedsweb.infrastructure.soap.CachingOfficeRegistry;
import verigate.adapter.deedsweb.infrastructure.soap.CxfDeedsRegistryClient;
import verigate.adapter.deedsweb.infrastructure.soap.CxfPortFactory;
import verigate.adapter.deedsweb.infrastructure.soap.generated.DeedsRegistrationEnquiryService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/** Configures dependency injection bindings for the DeedsWeb adapter. */
public class ServiceModule extends AbstractModule {

  /**
   * Fan-out parallelism. Sized to balance concurrency with the Lambda's vCPU tier — DeedsWeb
   * has at most ~12 deeds offices, so 8 worker threads keeps tail latency low without
   * overwhelming the upstream registry.
   */
  private static final int FANOUT_THREADS = 8;

  @Override
  protected void configure() {
    bind(InternalTransportJsonSerializer.class).to(DefaultInternalTransportJsonSerializer.class);
    bind(JsonSerializer.class).to(DefaultJsonSerializer.class);
    bind(ParameterErrorMessageBuilder.class).to(DefaultParameterErrorMessageBuilder.class);
    bind(UrlBuilder.class).to(DefaultUrlBuilder.class);
    bind(Environment.class).to(EnvironmentConfig.class);
    bind(Config.class).to(PropertiesFileConfig.class).asEagerSingleton();
    bind(LambdaExceptionHandler.class).to(LambdaApiGatewayRetryExceptionHandler.class);
    bind(Mapper.class).to(PassthroughMapper.class).in(Singleton.class);
    bind(DeedsRegistryClient.class).to(CxfDeedsRegistryClient.class);
    bind(PropertyOwnershipVerificationService.class)
        .to(DefaultPropertyOwnershipVerificationService.class);
  }

  @Provides
  @Singleton
  @Named("artifactSerializer")
  private InternalTransportJsonSerializer provideArtifactSerializer() {
    var serializer = new DefaultInternalTransportJsonSerializer();
    serializer.registerClassType(VerifyPartyCommand.class.getSimpleName(), VerifyPartyCommand.class);
    return serializer;
  }

  @Provides
  @Singleton
  public GlueClient provideAwsGlueClient() {
    return GlueClient.builder().build();
  }

  @Provides
  @Singleton
  private SecretsManagerClient provideSecretsManagerClient() {
    return SecretsManagerClient.builder().build();
  }

  @Provides
  @Singleton
  private SecretManager provideSecretManager(SecretsManagerClient secretsManagerClient) {
    return new AwsSecretManager(secretsManagerClient);
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
  private DeedsWebApiConfiguration provideDeedsWebApiConfiguration() {
    return new DeedsWebApiConfiguration(new Properties());
  }

  @Provides
  @Singleton
  private DeedsRegistrationEnquiryService provideDeedsRegistrationEnquiryService(
      DeedsWebApiConfiguration config) {
    return CxfPortFactory.create(config);
  }

  @Provides
  @Singleton
  private DeedsWebCredentialsProvider provideDeedsWebCredentialsProvider(
      SecretManager secretManager,
      ObjectMapper objectMapper,
      DeedsWebApiConfiguration config) {
    return new SecretsManagerDeedsWebCredentialsProvider(secretManager, objectMapper, config);
  }

  @Provides
  @Singleton
  private CachingOfficeRegistry provideCachingOfficeRegistry(
      DeedsRegistrationEnquiryService port) {
    return new CachingOfficeRegistry(port);
  }

  @Provides
  @Singleton
  @Named("deedsWebFanoutExecutor")
  private ExecutorService provideFanoutExecutor() {
    return Executors.newFixedThreadPool(
        FANOUT_THREADS,
        r -> {
          Thread t = new Thread(r, "deedsweb-fanout");
          t.setDaemon(true);
          return t;
        });
  }

  @Provides
  @Singleton
  private CxfDeedsRegistryClient provideDeedsRegistryClient(
      DeedsRegistrationEnquiryService port,
      DeedsWebCredentialsProvider credentialsProvider,
      CachingOfficeRegistry officeCache,
      @Named("deedsWebFanoutExecutor") ExecutorService fanoutExecutor) {
    return new CxfDeedsRegistryClient(port, credentialsProvider, officeCache, fanoutExecutor);
  }

  @Provides
  @Singleton
  private DefaultPropertyOwnershipVerificationService providePropertyOwnershipVerificationService(
      DeedsRegistryClient deedsRegistryClient) {
    return new DefaultPropertyOwnershipVerificationService(deedsRegistryClient);
  }

  @Provides
  @Singleton
  private DefaultPropertyVerificationCommandHandler providePropertyVerificationCommandHandler(
      PropertyOwnershipVerificationService propertyOwnershipVerificationService) {
    return new DefaultPropertyVerificationCommandHandler(propertyOwnershipVerificationService);
  }

  protected DefaultRetry getDefaultRetry(Config config) {
    return new DefaultRetry(
        Integer.parseInt(config.get("verifications.retry.max-attempts")),
        Duration.ofMillis(Long.parseLong(config.get("verifications.retry.delay-ms"))),
        Set.of());
  }
}
