/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.qlink.infrastructure.functions.lambda.di.modules;

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
import verigate.adapter.qlink.application.handlers.DefaultVerifyBankAccountDetailsCommandHandler;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Configures dependency injection bindings for the QLink adapter.
 * Extends ServiceModule with specific bindings for bank account verification.
 */
public final class VerifyBankAccountDetailsServiceModule extends ServiceModule {

  /**
   * Configures the bindings for the QLink adapter. This method is called by the Guice framework to
   * set up the bindings.
   */
  public VerifyBankAccountDetailsServiceModule() {
    super();
  }

  @Provides
  @Singleton
  @Named("VerifyBankAccountDetailsInvalidMessageQueue")
  private InvalidMessageQueue<SQSMessage> provideVerifyBankAccountDetailsInvalidMessageQueue(
      Environment environment, SqsClient sqsClient) {
    return new SqsLambdaEventRawMessageQueue(
        sqsClient, environment.get("VERIFY_BANK_ACCOUNT_DETAILS_IMQ_NAME"));
  }

  @Provides
  @Singleton
  @Named("VerifyBankAccountDetailsDeadLetterMessageQueue")
  private DeadLetterQueue<SQSMessage> provideVerifyBankAccountDetailsDeadLetterMessageQueue(
      Environment environment, SqsClient sqsClient) {
    return new SqsLambdaEventRawMessageQueue(
        sqsClient, environment.get("VERIFY_BANK_ACCOUNT_DETAILS_DLQ_NAME"));
  }

  /**
   * This method provides the command handler with retry capabilities.
   */
  @Provides
  @Singleton
  public CommandHandler<VerifyPartyCommand, Map<String, String>>
      provideVerifyBankAccountDetailsCommandHandler(
          DefaultVerifyBankAccountDetailsCommandHandler commandHandler,
          Config config,
          InternalTransportJsonSerializer jsonSerializer) {

    return new RetryCommandHandler<VerifyPartyCommand, Map<String, String>>(
        commandHandler, getDefaultRetry(config), "handler-verify-bank-account-details");
  }
}
