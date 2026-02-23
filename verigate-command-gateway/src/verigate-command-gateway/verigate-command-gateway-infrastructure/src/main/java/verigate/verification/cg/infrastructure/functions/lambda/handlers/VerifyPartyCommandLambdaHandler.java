/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.infrastructure.functions.lambda.handlers;

import infrastructure.commands.handler.sqs.ResilientSqsCommandLambdaHandler;
import java.util.Map;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.infrastructure.functions.lambda.di.factories.VerifyPartyDependencyFactory;

/**
 * Handles the Lambda function for {@link VerifyPartyCommand}.
 */
public class VerifyPartyCommandLambdaHandler
    extends ResilientSqsCommandLambdaHandler<VerifyPartyCommand, Map<String, String>> {

  /** Default no-arg constructor for AWS Lambda init. */
  public VerifyPartyCommandLambdaHandler() {
    this(new VerifyPartyDependencyFactory());
  }

  /** Allow construction with an arbitrary VerifyPartyCommandLambdaHandler. */
  public VerifyPartyCommandLambdaHandler(VerifyPartyDependencyFactory dependencyFactory) {
    super(
        messageBody ->
            dependencyFactory
                .getSerializer()
                .deserialize(messageBody, VerifyPartyCommand.class),
        dependencyFactory.getVerifyPartyCommandHandler(),
        dependencyFactory.getVerifyPartyInvalidMessageQueue(),
        dependencyFactory.getVerifyPartyDeadLetterQueue());
  }
}
