/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.infrastructure.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import domain.commands.CommandHandler;
import verigate.verification.domain.commands.incoming.InitiateVerificationCommand;
import verigate.verification.domain.commands.incoming.RetryVerificationCommand;
import verigate.verification.infrastructure.configuration.VerificationModule;

/**
 * AWS Lambda handler for verification orchestration commands.
 */
public class VerificationOrchestrationLambdaHandler implements RequestHandler<SQSEvent, Void> {
    
    private final Injector injector;
    private final ObjectMapper objectMapper;
    
    public VerificationOrchestrationLambdaHandler() {
        this.injector = Guice.createInjector(new VerificationModule());
        this.objectMapper = injector.getInstance(ObjectMapper.class);
    }
    
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage message : event.getRecords()) {
            try {
                processMessage(message.getBody(), context);
            } catch (Exception e) {
                context.getLogger().log("Error processing message: " + e.getMessage());
                throw new RuntimeException("Failed to process SQS message", e);
            }
        }
        return null;
    }
    
    private void processMessage(String messageBody, Context context) throws Exception {
        // Parse the message to determine command type
        var messageNode = objectMapper.readTree(messageBody);
        String commandType = messageNode.get("commandType").asText();
        
        switch (commandType) {
            case "InitiateVerificationCommand":
                InitiateVerificationCommand initiateCommand = objectMapper.readValue(messageBody, InitiateVerificationCommand.class);
                CommandHandler<InitiateVerificationCommand, Void> initiateHandler = 
                    injector.getInstance(verigate.verification.application.handlers.InitiateVerificationCommandHandler.class);
                initiateHandler.handle(initiateCommand);
                break;
                
            case "RetryVerificationCommand":
                RetryVerificationCommand retryCommand = objectMapper.readValue(messageBody, RetryVerificationCommand.class);
                CommandHandler<RetryVerificationCommand, Void> retryHandler = 
                    injector.getInstance(verigate.verification.application.handlers.RetryVerificationCommandHandler.class);
                retryHandler.handle(retryCommand);
                break;
                
            default:
                context.getLogger().log("Unknown command type: " + commandType);
                throw new IllegalArgumentException("Unsupported command type: " + commandType);
        }
    }
}