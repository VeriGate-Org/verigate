/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.qlink.infrastructure.functions.lambda.handlers;

import infrastructure.commands.handler.sqs.ResilientSqsCommandLambdaHandler;
import java.util.Map;
import verigate.adapter.qlink.infrastructure.functions.lambda.di.factories.VerifyBankAccountDetailsDependencyFactory;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Handles the Lambda function for {@link VerifyPartyCommand} using QLink bank account
 * verification.
 */
public class VerifyBankAccountDetailsLambdaHandler
    extends ResilientSqsCommandLambdaHandler<VerifyPartyCommand, Map<String, String>> {

  /** Default no-arg constructor for AWS Lambda init. */
  public VerifyBankAccountDetailsLambdaHandler() {
    this(new VerifyBankAccountDetailsDependencyFactory());
  }

  /** Allow construction with an arbitrary VerifyBankAccountDetailsDependencyFactory. */
  public VerifyBankAccountDetailsLambdaHandler(
      VerifyBankAccountDetailsDependencyFactory dependencyFactory) {
    super(
        (messageBody) ->
            dependencyFactory.getSerializer().deserialize(messageBody, VerifyPartyCommand.class),
        dependencyFactory.getVerifyBankAccountDetailsCommandHandler(),
        dependencyFactory.getVerifyBankAccountDetailsInvalidMessageQueue(),
        dependencyFactory.getVerifyBankAccountDetailsDeadLetterQueue());
  }
}
