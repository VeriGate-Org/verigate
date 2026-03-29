/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.functions.lambda.di.modules;

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
import verigate.adapter.deedsweb.application.handlers.DefaultPropertyVerificationCommandHandler;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/** Provides queue and handler bindings for property ownership verification. */
public final class VerifyPropertyOwnershipServiceModule extends ServiceModule {

  @Provides
  @Singleton
  @Named("VerifyPropertyOwnershipInvalidMessageQueue")
  private InvalidMessageQueue<SQSMessage> provideInvalidMessageQueue(
      Environment environment, SqsClient sqsClient) {
    return new SqsLambdaEventRawMessageQueue(
        sqsClient, environment.get("VERIFY_PROPERTY_OWNERSHIP_IMQ_NAME"));
  }

  @Provides
  @Singleton
  @Named("VerifyPropertyOwnershipDeadLetterMessageQueue")
  private DeadLetterQueue<SQSMessage> provideDeadLetterMessageQueue(
      Environment environment, SqsClient sqsClient) {
    return new SqsLambdaEventRawMessageQueue(
        sqsClient, environment.get("VERIFY_PROPERTY_OWNERSHIP_DLQ_NAME"));
  }

  @Provides
  @Singleton
  public CommandHandler<VerifyPartyCommand, Map<String, String>>
      provideVerifyPropertyOwnershipCommandHandler(
          DefaultPropertyVerificationCommandHandler commandHandler,
          Config config,
          InternalTransportJsonSerializer jsonSerializer) {
    return new RetryCommandHandler<>(
        commandHandler, getDefaultRetry(config), "handler-verify-property-ownership");
  }
}
