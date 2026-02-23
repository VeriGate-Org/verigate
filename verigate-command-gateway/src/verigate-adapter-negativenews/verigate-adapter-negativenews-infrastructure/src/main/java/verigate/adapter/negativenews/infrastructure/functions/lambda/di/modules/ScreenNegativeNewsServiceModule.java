/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.infrastructure.functions.lambda.di.modules;

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
import verigate.adapter.negativenews.application.handlers.DefaultScreenNegativeNewsCommandHandler;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * This class is responsible for configuring the dependency injection bindings for the Negative News
 * adapter. It extends the ServiceModule class and provides specific bindings for negative news
 * screening.
 */
public final class ScreenNegativeNewsServiceModule extends ServiceModule {

  /**
   * Configures the bindings for the Negative News adapter. This method is called by the Guice
   * framework to set up the bindings.
   */
  public ScreenNegativeNewsServiceModule() {
    super();
  }

  @Provides
  @Singleton
  @Named("ScreenNegativeNewsInvalidMessageQueue")
  private InvalidMessageQueue<SQSMessage> provideScreenNegativeNewsInvalidMessageQueue(
      Environment environment, SqsClient sqsClient) {
    return new SqsLambdaEventRawMessageQueue(
        sqsClient, environment.get("SCREEN_NEGATIVE_NEWS_IMQ_NAME"));
  }

  @Provides
  @Singleton
  @Named("ScreenNegativeNewsDeadLetterMessageQueue")
  private DeadLetterQueue<SQSMessage> provideScreenNegativeNewsDeadLetterMessageQueue(
      Environment environment, SqsClient sqsClient) {
    return new SqsLambdaEventRawMessageQueue(
        sqsClient, environment.get("SCREEN_NEGATIVE_NEWS_DLQ_NAME"));
  }

  /**
   * This method provides the command handler with retry capabilities.
   */
  @Provides
  @Singleton
  public CommandHandler<VerifyPartyCommand, Map<String, String>>
      provideScreenNegativeNewsCommandHandler(
          DefaultScreenNegativeNewsCommandHandler commandHandler,
          Config config,
          InternalTransportJsonSerializer jsonSerializer) {

    return new RetryCommandHandler<VerifyPartyCommand, Map<String, String>>(
        commandHandler, getDefaultRetry(config), "handler-screen-negative-news");
  }
}
