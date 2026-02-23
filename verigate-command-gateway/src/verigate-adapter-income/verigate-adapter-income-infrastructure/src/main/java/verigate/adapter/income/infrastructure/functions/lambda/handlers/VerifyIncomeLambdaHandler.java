/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.income.infrastructure.functions.lambda.handlers;

import infrastructure.commands.handler.sqs.ResilientSqsCommandLambdaHandler;
import java.util.Map;
import verigate.adapter.income.infrastructure.functions.lambda.di.factories.VerifyIncomeDependencyFactory;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Handles the Lambda function for {@link VerifyPartyCommand} using income verification.
 */
public class VerifyIncomeLambdaHandler
    extends ResilientSqsCommandLambdaHandler<VerifyPartyCommand, Map<String, String>> {

  /** Default no-arg constructor for AWS Lambda init. */
  public VerifyIncomeLambdaHandler() {
    this(new VerifyIncomeDependencyFactory());
  }

  /** Allow construction with an arbitrary VerifyIncomeDependencyFactory. */
  public VerifyIncomeLambdaHandler(
      VerifyIncomeDependencyFactory dependencyFactory) {
    super(
        (messageBody) ->
            dependencyFactory.getSerializer().deserialize(messageBody, VerifyPartyCommand.class),
        dependencyFactory.getVerifyIncomeCommandHandler(),
        dependencyFactory.getVerifyIncomeInvalidMessageQueue(),
        dependencyFactory.getVerifyIncomeDeadLetterQueue());
  }
}
