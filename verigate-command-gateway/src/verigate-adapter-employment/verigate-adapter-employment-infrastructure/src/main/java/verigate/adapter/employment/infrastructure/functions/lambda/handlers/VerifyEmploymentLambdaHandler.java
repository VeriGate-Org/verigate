/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.employment.infrastructure.functions.lambda.handlers;

import infrastructure.commands.handler.sqs.ResilientSqsCommandLambdaHandler;
import java.util.Map;
import verigate.adapter.employment.infrastructure.functions.lambda.di.factories.VerifyEmploymentDependencyFactory;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Handles the Lambda function for {@link VerifyPartyCommand} using employment verification.
 */
public class VerifyEmploymentLambdaHandler
    extends ResilientSqsCommandLambdaHandler<VerifyPartyCommand, Map<String, String>> {

  /** Default no-arg constructor for AWS Lambda init. */
  public VerifyEmploymentLambdaHandler() {
    this(new VerifyEmploymentDependencyFactory());
  }

  /** Allow construction with an arbitrary VerifyEmploymentDependencyFactory. */
  public VerifyEmploymentLambdaHandler(
      VerifyEmploymentDependencyFactory dependencyFactory) {
    super(
        (messageBody) ->
            dependencyFactory.getSerializer().deserialize(messageBody, VerifyPartyCommand.class),
        dependencyFactory.getVerifyEmploymentCommandHandler(),
        dependencyFactory.getVerifyEmploymentInvalidMessageQueue(),
        dependencyFactory.getVerifyEmploymentDeadLetterQueue());
  }
}
