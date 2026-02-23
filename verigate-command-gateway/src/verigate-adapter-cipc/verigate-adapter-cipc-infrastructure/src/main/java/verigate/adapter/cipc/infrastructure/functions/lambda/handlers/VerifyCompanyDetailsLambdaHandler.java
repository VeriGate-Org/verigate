/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.infrastructure.functions.lambda.handlers;

import infrastructure.commands.handler.sqs.ResilientSqsCommandLambdaHandler;
import java.util.Map;
import verigate.adapter.cipc.infrastructure.functions.lambda.di.factories.VerifyCompanyDetailsDependencyFactory;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Handles the Lambda function for {@link VerifyPartyCommand} using CIPC company verification.
 */
public class VerifyCompanyDetailsLambdaHandler
    extends ResilientSqsCommandLambdaHandler<VerifyPartyCommand, Map<String, String>> {

  /** Default no-arg constructor for AWS Lambda init. */
  public VerifyCompanyDetailsLambdaHandler() {
    this(new VerifyCompanyDetailsDependencyFactory());
  }

  /** Allow construction with an arbitrary VerifyCompanyDetailsCommandLambdaHandler. */
  public VerifyCompanyDetailsLambdaHandler(
      VerifyCompanyDetailsDependencyFactory dependencyFactory) {
    super(
        (messageBody) ->
            dependencyFactory.getSerializer().deserialize(messageBody, VerifyPartyCommand.class),
        dependencyFactory.getVerifyCompanyDetailsCommandHandler(),
        dependencyFactory.getVerifyCompanyDetailsInvalidMessageQueue(),
        dependencyFactory.getVerifyCompanyDetailsDeadLetterQueue());
  }
}