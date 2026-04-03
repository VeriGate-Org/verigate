/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.infrastructure.functions.lambda.di.modules;

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
import infrastructure.sqs.SqsLambdaEventRawMessageQueue;
import java.util.Map;
import software.amazon.awssdk.services.sqs.SqsClient;
import verigate.adapter.opensanctions.application.handlers.DefaultSanctionsScreeningCommandHandler;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Configures the dependency injection bindings for the OpenSanctions sanctions screening adapter.
 * Extends ServiceModule and provides specific bindings for sanctions screening.
 */
public final class ScreenSanctionsServiceModule extends ServiceModule {

  public ScreenSanctionsServiceModule() {
    super();
  }

  @Provides
  @Singleton
  @Named("ScreenSanctionsInvalidMessageQueue")
  private InvalidMessageQueue<SQSMessage> provideScreenSanctionsInvalidMessageQueue(
      Environment environment, SqsClient sqsClient) {
    return new SqsLambdaEventRawMessageQueue(
        sqsClient, environment.get("SCREEN_SANCTIONS_IMQ_NAME"));
  }

  @Provides
  @Singleton
  @Named("ScreenSanctionsDeadLetterMessageQueue")
  private DeadLetterQueue<SQSMessage> provideScreenSanctionsDeadLetterMessageQueue(
      Environment environment, SqsClient sqsClient) {
    return new SqsLambdaEventRawMessageQueue(
        sqsClient, environment.get("SCREEN_SANCTIONS_DLQ_NAME"));
  }

  @Provides
  @Singleton
  public CommandHandler<VerifyPartyCommand, Map<String, String>>
      provideScreenSanctionsCommandHandler(
          DefaultSanctionsScreeningCommandHandler commandHandler,
          Config config) {

    return new RetryCommandHandler<VerifyPartyCommand, Map<String, String>>(
        commandHandler, getDefaultRetry(config), "handler-screen-sanctions");
  }
}
