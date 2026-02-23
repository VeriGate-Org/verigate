/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.fraudwatchlist.infrastructure.functions.lambda.di.modules;

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
import verigate.adapter.fraudwatchlist.application.handlers.DefaultScreenFraudWatchlistCommandHandler;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * This class is responsible for configuring the dependency injection bindings for the Fraud
 * Watchlist adapter. It extends the ServiceModule class and provides specific bindings for fraud
 * watchlist screening.
 */
public final class ScreenFraudWatchlistServiceModule extends ServiceModule {

  /**
   * Configures the bindings for the Fraud Watchlist adapter. This method is called by the Guice
   * framework to set up the bindings.
   */
  public ScreenFraudWatchlistServiceModule() {
    super();
  }

  @Provides
  @Singleton
  @Named("ScreenFraudWatchlistInvalidMessageQueue")
  private InvalidMessageQueue<SQSMessage> provideScreenFraudWatchlistInvalidMessageQueue(
      Environment environment, SqsClient sqsClient) {
    return new SqsLambdaEventRawMessageQueue(
        sqsClient, environment.get("SCREEN_FRAUD_WATCHLIST_IMQ_NAME"));
  }

  @Provides
  @Singleton
  @Named("ScreenFraudWatchlistDeadLetterMessageQueue")
  private DeadLetterQueue<SQSMessage> provideScreenFraudWatchlistDeadLetterMessageQueue(
      Environment environment, SqsClient sqsClient) {
    return new SqsLambdaEventRawMessageQueue(
        sqsClient, environment.get("SCREEN_FRAUD_WATCHLIST_DLQ_NAME"));
  }

  /**
   * This method provides the command handler with retry capabilities.
   */
  @Provides
  @Singleton
  public CommandHandler<VerifyPartyCommand, Map<String, String>>
      provideScreenFraudWatchlistCommandHandler(
          DefaultScreenFraudWatchlistCommandHandler commandHandler,
          Config config,
          InternalTransportJsonSerializer jsonSerializer) {

    return new RetryCommandHandler<VerifyPartyCommand, Map<String, String>>(
        commandHandler, getDefaultRetry(config), "handler-screen-fraud-watchlist");
  }
}
