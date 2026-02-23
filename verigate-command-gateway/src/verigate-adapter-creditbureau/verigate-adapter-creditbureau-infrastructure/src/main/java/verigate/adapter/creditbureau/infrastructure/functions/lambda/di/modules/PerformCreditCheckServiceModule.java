/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.creditbureau.infrastructure.functions.lambda.di.modules;

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
import infrastructure.sqs.SqsLambdaEventRawMessageQueue;
import java.util.Map;
import software.amazon.awssdk.services.sqs.SqsClient;
import verigate.adapter.creditbureau.application.handlers.DefaultPerformCreditCheckCommandHandler;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * This class is responsible for configuring the dependency injection bindings for the Credit Bureau
 * adapter. It extends the ServiceModule class and provides specific bindings for credit check
 * verification.
 */
public final class PerformCreditCheckServiceModule extends ServiceModule {

  /**
   * Configures the bindings for the Credit Bureau adapter. This method is called by the Guice
   * framework to set up the bindings.
   */
  public PerformCreditCheckServiceModule() {
    super();
  }

  @Provides
  @Singleton
  @Named("PerformCreditCheckInvalidMessageQueue")
  private InvalidMessageQueue<SQSMessage> providePerformCreditCheckInvalidMessageQueue(
      Environment environment, SqsClient sqsClient) {
    return new SqsLambdaEventRawMessageQueue(
        sqsClient, environment.get("PERFORM_CREDIT_CHECK_IMQ_NAME"));
  }

  @Provides
  @Singleton
  @Named("PerformCreditCheckDeadLetterMessageQueue")
  private DeadLetterQueue<SQSMessage> providePerformCreditCheckDeadLetterMessageQueue(
      Environment environment, SqsClient sqsClient) {
    return new SqsLambdaEventRawMessageQueue(
        sqsClient, environment.get("PERFORM_CREDIT_CHECK_DLQ_NAME"));
  }

  /**
   * This method provides the command handler with retry capabilities.
   */
  @Provides
  @Singleton
  public CommandHandler<VerifyPartyCommand, Map<String, String>>
      providePerformCreditCheckCommandHandler(
          DefaultPerformCreditCheckCommandHandler commandHandler,
          Config config,
          InternalTransportJsonSerializer jsonSerializer) {

    return new RetryCommandHandler<VerifyPartyCommand, Map<String, String>>(
        commandHandler, getDefaultRetry(config), "handler-perform-credit-check");
  }
}
