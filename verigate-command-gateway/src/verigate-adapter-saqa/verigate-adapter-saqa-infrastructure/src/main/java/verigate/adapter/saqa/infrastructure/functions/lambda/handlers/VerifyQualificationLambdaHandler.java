/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.saqa.infrastructure.functions.lambda.handlers;

import infrastructure.commands.handler.sqs.ResilientSqsCommandLambdaHandler;
import java.util.Map;
import verigate.adapter.saqa.infrastructure.functions.lambda.di.factories.VerifyQualificationDependencyFactory;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Handles the Lambda function for {@link VerifyPartyCommand} using SAQA qualification
 * verification.
 */
public class VerifyQualificationLambdaHandler
    extends ResilientSqsCommandLambdaHandler<VerifyPartyCommand, Map<String, String>> {

  /** Default no-arg constructor for AWS Lambda init. */
  public VerifyQualificationLambdaHandler() {
    this(new VerifyQualificationDependencyFactory());
  }

  /** Allow construction with an arbitrary VerifyQualificationDependencyFactory. */
  public VerifyQualificationLambdaHandler(
      VerifyQualificationDependencyFactory dependencyFactory) {
    super(
        (messageBody) ->
            dependencyFactory.getSerializer().deserialize(messageBody, VerifyPartyCommand.class),
        dependencyFactory.getVerifyQualificationCommandHandler(),
        dependencyFactory.getVerifyQualificationInvalidMessageQueue(),
        dependencyFactory.getVerifyQualificationDeadLetterQueue());
  }
}
