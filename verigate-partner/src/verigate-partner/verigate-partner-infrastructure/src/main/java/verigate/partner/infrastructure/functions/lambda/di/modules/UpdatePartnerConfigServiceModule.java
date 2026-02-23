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

public final class UpdatePartnerConfigServiceModule extends PartnerServiceModule {

    public UpdatePartnerConfigServiceModule() {
        super();
    }

    @Provides
    @Singleton
    @Named("UpdatePartnerConfigInvalidMessageQueue")
    private InvalidMessageQueue<SQSMessage> provideUpdatePartnerConfigInvalidMessageQueue(
            Environment environment, SqsClient sqsClient) {
        return new SqsLambdaEventRawMessageQueue(
            sqsClient, environment.get("UPDATE_PARTNER_CONFIG_IMQ_NAME"));
    }

    @Provides
    @Singleton
    @Named("UpdatePartnerConfigDeadLetterMessageQueue")
    private DeadLetterQueue<SQSMessage> provideUpdatePartnerConfigDeadLetterMessageQueue(
            Environment environment, SqsClient sqsClient) {
        return new SqsLambdaEventRawMessageQueue(
            sqsClient, environment.get("UPDATE_PARTNER_CONFIG_DLQ_NAME"));
    }
}
