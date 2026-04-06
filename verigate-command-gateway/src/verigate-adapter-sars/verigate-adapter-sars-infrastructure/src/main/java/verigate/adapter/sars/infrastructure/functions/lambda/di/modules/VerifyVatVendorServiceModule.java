/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.infrastructure.functions.lambda.di.modules;

import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import crosscutting.config.Config;
import crosscutting.environment.Environment;
import domain.commands.CommandHandler;
import domain.commands.RetryCommandHandler;
import domain.messages.DeadLetterQueue;
import domain.messages.InvalidMessageQueue;
import infrastructure.functions.lambda.serializers.internal.InternalTransportJsonSerializer;
import infrastructure.secrets.SecretManager;
import infrastructure.sqs.SqsLambdaEventRawMessageQueue;
import java.time.Duration;
import java.util.Map;
import software.amazon.awssdk.services.sqs.SqsClient;
import verigate.adapter.sars.application.handlers.DefaultVerifyVatVendorCommandHandler;
import verigate.adapter.sars.domain.services.SarsVatVendorService;
import verigate.adapter.sars.infrastructure.config.EnvironmentConstants;
import verigate.adapter.sars.infrastructure.config.SarsEfilingCredentials;
import verigate.adapter.sars.infrastructure.services.DefaultSarsVatVendorService;
import verigate.adapter.sars.infrastructure.soap.SarsVatSoapClient;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Guice module for SARS VAT Vendor Search adapter dependencies.
 */
public final class VerifyVatVendorServiceModule extends ServiceModule {

  public VerifyVatVendorServiceModule() {
    super();
  }

  @Override
  protected void configure() {
    super.configure();
    // Override the SarsTaxComplianceService binding with the VAT vendor service
    bind(SarsVatVendorService.class).to(DefaultSarsVatVendorService.class);
  }

  @Provides
  @Singleton
  @Named("VerifyVatVendorInvalidMessageQueue")
  private InvalidMessageQueue<SQSMessage> provideVerifyVatVendorInvalidMessageQueue(
      Environment environment, SqsClient sqsClient) {
    return new SqsLambdaEventRawMessageQueue(
        sqsClient, environment.get(EnvironmentConstants.VERIFY_VAT_VENDOR_IMQ_NAME));
  }

  @Provides
  @Singleton
  @Named("VerifyVatVendorDeadLetterMessageQueue")
  private DeadLetterQueue<SQSMessage> provideVerifyVatVendorDeadLetterMessageQueue(
      Environment environment, SqsClient sqsClient) {
    return new SqsLambdaEventRawMessageQueue(
        sqsClient, environment.get(EnvironmentConstants.VERIFY_VAT_VENDOR_DLQ_NAME));
  }

  @Provides
  @Singleton
  private SarsEfilingCredentials provideSarsEfilingCredentials(
      SecretManager secretManager, Environment environment) {
    String secretName = environment.get(EnvironmentConstants.SARS_EFILING_SECRET_NAME);
    return new SarsEfilingCredentials(secretManager, secretName);
  }

  @Provides
  @Singleton
  private SarsVatSoapClient provideSarsVatSoapClient(
      Environment environment, SarsEfilingCredentials credentials) {
    String endpointUrl = environment.get(EnvironmentConstants.SARS_VAT_ENDPOINT_URL);
    if (endpointUrl == null || endpointUrl.isBlank()) {
      endpointUrl = EnvironmentConstants.DEFAULT_SARS_VAT_ENDPOINT_URL;
    }
    return new SarsVatSoapClient(endpointUrl, credentials, Duration.ofSeconds(30));
  }

  @Provides
  @Singleton
  private DefaultSarsVatVendorService provideDefaultSarsVatVendorService(
      SarsVatSoapClient soapClient) {
    return new DefaultSarsVatVendorService(soapClient);
  }

  @Provides
  @Singleton
  private DefaultVerifyVatVendorCommandHandler provideVerifyVatVendorCommandHandler(
      SarsVatVendorService sarsVatVendorService) {
    return new DefaultVerifyVatVendorCommandHandler(sarsVatVendorService);
  }

  @Provides
  @Singleton
  public CommandHandler<VerifyPartyCommand, Map<String, String>>
      provideVerifyVatVendorRetryCommandHandler(
          DefaultVerifyVatVendorCommandHandler commandHandler,
          Config config,
          InternalTransportJsonSerializer jsonSerializer) {

    return new RetryCommandHandler<VerifyPartyCommand, Map<String, String>>(
        commandHandler, getDefaultRetry(config), "handler-verify-vat-vendor");
  }
}
