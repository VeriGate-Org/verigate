/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.income.infrastructure.functions.lambda.di.modules;

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
import verigate.adapter.income.application.handlers.DefaultVerifyIncomeCommandHandler;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * This class is responsible for configuring the dependency injection bindings for the Income
 * Verification adapter. It extends the ServiceModule class and provides specific bindings for
 * income verification.
 */
public final class VerifyIncomeServiceModule extends ServiceModule {

  /**
   * Configures the bindings for the Income Verification adapter. This method is called by the
   * Guice framework to set up the bindings.
   */
  public VerifyIncomeServiceModule() {
    super();
  }

  @Provides
  @Singleton
  @Named("VerifyIncomeInvalidMessageQueue")
  private InvalidMessageQueue<SQSMessage> provideVerifyIncomeInvalidMessageQueue(
      Environment environment, SqsClient sqsClient) {
    return new SqsLambdaEventRawMessageQueue(
        sqsClient, environment.get("VERIFY_INCOME_IMQ_NAME"));
  }

  @Provides
  @Singleton
  @Named("VerifyIncomeDeadLetterMessageQueue")
  private DeadLetterQueue<SQSMessage> provideVerifyIncomeDeadLetterMessageQueue(
      Environment environment, SqsClient sqsClient) {
    return new SqsLambdaEventRawMessageQueue(
        sqsClient, environment.get("VERIFY_INCOME_DLQ_NAME"));
  }

  /**
   * This method provides the command handler with retry capabilities.
   */
  @Provides
  @Singleton
  public CommandHandler<VerifyPartyCommand, Map<String, String>>
      provideVerifyIncomeCommandHandler(
          DefaultVerifyIncomeCommandHandler commandHandler,
          Config config,
          InternalTransportJsonSerializer jsonSerializer) {

    return new RetryCommandHandler<VerifyPartyCommand, Map<String, String>>(
        commandHandler, getDefaultRetry(config), "handler-verify-income");
  }
}
