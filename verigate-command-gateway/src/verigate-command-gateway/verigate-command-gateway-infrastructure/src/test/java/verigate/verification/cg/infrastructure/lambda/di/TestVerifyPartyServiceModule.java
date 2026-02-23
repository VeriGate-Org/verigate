/*
 * Arthmatic + Karisani(c) 2024 - 2025. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.infrastructure.lambda.di;

import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import crosscutting.config.Config;
import crosscutting.environment.Environment;
import domain.commands.CommandHandler;
import domain.commands.RetryCommandHandler;
import domain.messages.DeadLetterQueue;
import domain.messages.EphemeralMessageQueue;
import domain.messages.InvalidMessageQueue;
import infrastructure.functions.lambda.serializers.internal.InternalTransportJsonSerializer;
import java.util.Map;
import verigate.verification.cg.application.handlers.DefaultVerifyPartyCommandHandler;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * This class provides a module for the VerifyPartyServiceModule.
 */
public class TestVerifyPartyServiceModule extends TestServiceModule {

  public TestVerifyPartyServiceModule() {
    super();
  }

  @Provides
  @Singleton
  @Named("VerifyPartyInvalidMessageQueue")
  private InvalidMessageQueue<SQSMessage> provideVerifyPartyInvalidMessageQueue(
      Environment environment) {
    return new EphemeralMessageQueue<>();
  }

  @Provides
  @Singleton
  @Named("VerifyPartyDeadLetterMessageQueue")
  private DeadLetterQueue<SQSMessage> provideVerifyPartyDeadLetterMessageQueue(
      Environment environment) {
    return new EphemeralMessageQueue<>();
  }

  /**
   * Provides the VerifyPartyCommandHandler.
   *
   * @param commandHandler the VerifyPartyCommandHandler
   * @param config the Config
   * @param jsonSerializer the InternalTransportJsonSerializer
   * @return the VerifyPartyCommandHandler
   */
  @Provides
  @Singleton
  public CommandHandler<VerifyPartyCommand, Map<String, String>> provideVerifyPartyCommandHandler(
      DefaultVerifyPartyCommandHandler commandHandler,
      Config config,
      InternalTransportJsonSerializer jsonSerializer) {

    return new RetryCommandHandler<VerifyPartyCommand, Map<String, String>>(
        commandHandler, getDefaultRetry(config), "handler-verify-party-command");
  }
}
