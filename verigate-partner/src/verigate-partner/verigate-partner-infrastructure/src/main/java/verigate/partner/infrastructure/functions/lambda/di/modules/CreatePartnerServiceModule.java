/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.infrastructure.functions.lambda.di.modules;

import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import crosscutting.environment.Environment;
import domain.messages.DeadLetterQueue;
import domain.messages.InvalidMessageQueue;
import infrastructure.sqs.SqsLambdaEventRawMessageQueue;
import software.amazon.awssdk.services.sqs.SqsClient;

public final class CreatePartnerServiceModule extends PartnerServiceModule {

    public CreatePartnerServiceModule() {
        super();
    }

    @Provides
    @Singleton
    @Named("CreatePartnerInvalidMessageQueue")
    private InvalidMessageQueue<SQSMessage> provideCreatePartnerInvalidMessageQueue(
            Environment environment, SqsClient sqsClient) {
        return new SqsLambdaEventRawMessageQueue(
            sqsClient, environment.get("CREATE_PARTNER_IMQ_NAME"));
    }

    @Provides
    @Singleton
    @Named("CreatePartnerDeadLetterMessageQueue")
    private DeadLetterQueue<SQSMessage> provideCreatePartnerDeadLetterMessageQueue(
            Environment environment, SqsClient sqsClient) {
        return new SqsLambdaEventRawMessageQueue(
            sqsClient, environment.get("CREATE_PARTNER_DLQ_NAME"));
    }
}
