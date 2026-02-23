/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.infrastructure.functions.lambda.handlers;

import infrastructure.commands.handler.sqs.ResilientSqsCommandLambdaHandler;
import java.util.Map;
import verigate.adapter.dha.infrastructure.functions.lambda.di.factories.VerifyIdentityDependencyFactory;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Handles the Lambda function for {@link VerifyPartyCommand} using DHA identity verification.
 */
public class VerifyIdentityLambdaHandler
    extends ResilientSqsCommandLambdaHandler<VerifyPartyCommand, Map<String, String>> {

  /** Default no-arg constructor for AWS Lambda init. */
  public VerifyIdentityLambdaHandler() {
    this(new VerifyIdentityDependencyFactory());
  }

  /** Allow construction with an arbitrary VerifyIdentityDependencyFactory. */
  public VerifyIdentityLambdaHandler(
      VerifyIdentityDependencyFactory dependencyFactory) {
    super(
        (messageBody) ->
            dependencyFactory.getSerializer().deserialize(messageBody, VerifyPartyCommand.class),
        dependencyFactory.getVerifyIdentityCommandHandler(),
        dependencyFactory.getVerifyIdentityInvalidMessageQueue(),
        dependencyFactory.getVerifyIdentityDeadLetterQueue());
  }
}
