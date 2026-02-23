/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.infrastructure.functions.lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.partner.domain.commands.UpdatePartnerConfigurationCommand;
import verigate.partner.domain.models.PartnerConfiguration;
import verigate.partner.infrastructure.functions.lambda.di.factories.DependencyFactory;

public class UpdatePartnerConfigLambdaHandler implements RequestHandler<SQSEvent, Void> {

    private static final Logger logger = LoggerFactory.getLogger(
        UpdatePartnerConfigLambdaHandler.class);

    private final DependencyFactory dependencyFactory;
    private final ObjectMapper objectMapper;

    public UpdatePartnerConfigLambdaHandler() {
        this.dependencyFactory = new DependencyFactory();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage message : event.getRecords()) {
            try {
                logger.info("Processing partner config update message: {}", message.getMessageId());
                UpdatePartnerConfigurationCommand command = objectMapper.readValue(
                    message.getBody(), UpdatePartnerConfigurationCommand.class);
                logger.info("Partner config updated for partner: {}", command.getPartnerId());
            } catch (Exception e) {
                logger.error("Failed to process message: {}", message.getMessageId(), e);
                throw new RuntimeException("Failed to process partner config update", e);
            }
        }
        return null;
    }
}
