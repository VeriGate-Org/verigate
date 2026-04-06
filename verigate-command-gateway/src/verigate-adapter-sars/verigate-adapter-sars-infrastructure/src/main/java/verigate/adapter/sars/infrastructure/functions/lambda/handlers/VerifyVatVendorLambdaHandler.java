/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.infrastructure.functions.lambda.handlers;

import infrastructure.commands.handler.sqs.ResilientSqsCommandLambdaHandler;
import java.util.Map;
import verigate.adapter.sars.infrastructure.functions.lambda.di.factories.VerifyVatVendorDependencyFactory;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Handles the Lambda function for {@link VerifyPartyCommand} using SARS VAT vendor
 * search verification.
 */
public class VerifyVatVendorLambdaHandler
    extends ResilientSqsCommandLambdaHandler<VerifyPartyCommand, Map<String, String>> {

  /** Default no-arg constructor for AWS Lambda init. */
  public VerifyVatVendorLambdaHandler() {
    this(new VerifyVatVendorDependencyFactory());
  }

  /** Allow construction with an arbitrary VerifyVatVendorDependencyFactory. */
  public VerifyVatVendorLambdaHandler(
      VerifyVatVendorDependencyFactory dependencyFactory) {
    super(
        (messageBody) ->
            dependencyFactory.getSerializer().deserialize(messageBody, VerifyPartyCommand.class),
        dependencyFactory.getVerifyVatVendorCommandHandler(),
        dependencyFactory.getVerifyVatVendorInvalidMessageQueue(),
        dependencyFactory.getVerifyVatVendorDeadLetterQueue());
  }
}
