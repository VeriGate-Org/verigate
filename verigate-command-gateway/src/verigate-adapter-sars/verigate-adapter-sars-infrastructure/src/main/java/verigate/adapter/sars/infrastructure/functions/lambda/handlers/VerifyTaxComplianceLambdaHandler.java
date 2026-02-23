/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.infrastructure.functions.lambda.handlers;

import infrastructure.commands.handler.sqs.ResilientSqsCommandLambdaHandler;
import java.util.Map;
import verigate.adapter.sars.infrastructure.functions.lambda.di.factories.VerifyTaxComplianceDependencyFactory;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Handles the Lambda function for {@link VerifyPartyCommand} using SARS tax compliance
 * verification.
 */
public class VerifyTaxComplianceLambdaHandler
    extends ResilientSqsCommandLambdaHandler<VerifyPartyCommand, Map<String, String>> {

  /** Default no-arg constructor for AWS Lambda init. */
  public VerifyTaxComplianceLambdaHandler() {
    this(new VerifyTaxComplianceDependencyFactory());
  }

  /** Allow construction with an arbitrary VerifyTaxComplianceDependencyFactory. */
  public VerifyTaxComplianceLambdaHandler(
      VerifyTaxComplianceDependencyFactory dependencyFactory) {
    super(
        (messageBody) ->
            dependencyFactory.getSerializer().deserialize(messageBody, VerifyPartyCommand.class),
        dependencyFactory.getVerifyTaxComplianceCommandHandler(),
        dependencyFactory.getVerifyTaxComplianceInvalidMessageQueue(),
        dependencyFactory.getVerifyTaxComplianceDeadLetterQueue());
  }
}
