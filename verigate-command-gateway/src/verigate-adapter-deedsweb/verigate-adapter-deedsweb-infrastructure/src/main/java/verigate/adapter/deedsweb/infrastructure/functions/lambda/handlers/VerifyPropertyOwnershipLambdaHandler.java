/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.functions.lambda.handlers;

import infrastructure.commands.handler.sqs.ResilientSqsCommandLambdaHandler;
import java.util.Map;
import verigate.adapter.deedsweb.infrastructure.functions.lambda.di.factories.VerifyPropertyOwnershipDependencyFactory;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/** Handles the Lambda function for property ownership verification commands. */
public class VerifyPropertyOwnershipLambdaHandler
    extends ResilientSqsCommandLambdaHandler<VerifyPartyCommand, Map<String, String>> {

  /** Default no-arg constructor for AWS Lambda init. */
  public VerifyPropertyOwnershipLambdaHandler() {
    this(new VerifyPropertyOwnershipDependencyFactory());
  }

  /** Allow construction with an arbitrary dependency factory. */
  public VerifyPropertyOwnershipLambdaHandler(
      VerifyPropertyOwnershipDependencyFactory dependencyFactory) {
    super(
        (messageBody) ->
            dependencyFactory.getSerializer().deserialize(messageBody, VerifyPartyCommand.class),
        dependencyFactory.getVerifyPropertyOwnershipCommandHandler(),
        dependencyFactory.getVerifyPropertyOwnershipInvalidMessageQueue(),
        dependencyFactory.getVerifyPropertyOwnershipDeadLetterQueue());
  }
}
