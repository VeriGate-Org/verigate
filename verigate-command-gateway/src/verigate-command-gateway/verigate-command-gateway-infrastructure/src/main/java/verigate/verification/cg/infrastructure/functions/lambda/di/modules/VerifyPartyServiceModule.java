/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.infrastructure.functions.lambda.di.modules;

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
import infrastructure.commands.commandstore.AggregateCommandStoreDao;
import infrastructure.commands.handler.decorators.AggregateCommandHandlerCommandStoreDecorator;
import infrastructure.functions.lambda.serializers.internal.InternalTransportJsonSerializer;
import infrastructure.sqs.SqsLambdaEventRawMessageQueue;
import java.util.Map;
import software.amazon.awssdk.services.sqs.SqsClient;
import verigate.verification.cg.application.handlers.DefaultVerifyPartyCommandHandler;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.infrastructure.commandstore.VerificationCommandStoreDao;

/**
 * This class is responsible for configuring the dependency injection bindings
 * for the application.
 * It extends the AbstractModule class from the Google Guice library.
 */
public final class VerifyPartyServiceModule extends ServiceModule {
  @Provides
  @Singleton
  @Named("VerifyPartyInvalidMessageQueue")
  private InvalidMessageQueue<SQSMessage> provideVerifyPartyInvalidMessageQueue(
      Environment environment, SqsClient sqsClient) {
    return new SqsLambdaEventRawMessageQueue(sqsClient, environment.get("VERIFY_PARTY_IMQ_NAME"));
  }

  @Provides
  @Singleton
  @Named("VerifyPartyDeadLetterMessageQueue")
  private DeadLetterQueue<SQSMessage> provideVerifyPartyDeadLetterMessageQueue(
      Environment environment, SqsClient sqsClient) {
    return new SqsLambdaEventRawMessageQueue(sqsClient, environment.get("VERIFY_PARTY_DLQ_NAME"));
  }

  /**
   * This method provides the command handler.
   */
  @Provides
  @Singleton
  public CommandHandler<VerifyPartyCommand, Map<String, String>> provideVerifyPartyCommandHandler(
      DefaultVerifyPartyCommandHandler commandHandler,
      Config config,
      InternalTransportJsonSerializer jsonSerializer,
      AggregateCommandStoreDao commandStoreDao) {

    final CommandHandler<VerifyPartyCommand, Map<String, String>> decoratedWithCommandStore =
        new AggregateCommandHandlerCommandStoreDecorator<
            VerifyPartyCommand, Map<String, String>, VerificationCommandStoreDao>(
            commandHandler, commandStoreDao);

    return new RetryCommandHandler<VerifyPartyCommand, Map<String, String>>(
        decoratedWithCommandStore, getDefaultRetry(config), "handler-verify-party-command");
  }
}
