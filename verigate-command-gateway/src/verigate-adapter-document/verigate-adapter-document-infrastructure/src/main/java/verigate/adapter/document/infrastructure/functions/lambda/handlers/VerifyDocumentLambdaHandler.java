/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.infrastructure.functions.lambda.handlers;

import infrastructure.commands.handler.sqs.ResilientSqsCommandLambdaHandler;
import java.util.Map;
import verigate.adapter.document.infrastructure.functions.lambda.di.factories.VerifyDocumentDependencyFactory;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Handles the Lambda function for {@link VerifyPartyCommand} using document verification.
 */
public class VerifyDocumentLambdaHandler
    extends ResilientSqsCommandLambdaHandler<VerifyPartyCommand, Map<String, String>> {

  /** Default no-arg constructor for AWS Lambda init. */
  public VerifyDocumentLambdaHandler() {
    this(new VerifyDocumentDependencyFactory());
  }

  /** Allow construction with an arbitrary VerifyDocumentDependencyFactory. */
  public VerifyDocumentLambdaHandler(
      VerifyDocumentDependencyFactory dependencyFactory) {
    super(
        (messageBody) ->
            dependencyFactory.getSerializer().deserialize(messageBody, VerifyPartyCommand.class),
        dependencyFactory.getVerifyDocumentCommandHandler(),
        dependencyFactory.getVerifyDocumentInvalidMessageQueue(),
        dependencyFactory.getVerifyDocumentDeadLetterQueue());
  }
}
