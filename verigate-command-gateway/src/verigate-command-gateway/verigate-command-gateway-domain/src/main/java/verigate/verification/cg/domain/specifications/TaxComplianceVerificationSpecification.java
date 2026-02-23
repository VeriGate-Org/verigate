/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.domain.specifications;

import domain.invariants.Specification;
import domain.invariants.SpecificationResult;
import domain.invariants.rules.RequiredFieldSpecification;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Specification for fields on the {@link VerifyPartyCommand} that are required for the
 * command to be considered valid for a TaxComplianceVerification Verification Type.
 */
public class TaxComplianceVerificationSpecification implements Specification<VerifyPartyCommand> {

  private static final RequiredFieldSpecification<VerifyPartyCommand> metadataRequired =
      new RequiredFieldSpecification<>("metadata", VerifyPartyCommand::getMetadata);

  private static final RequiredFieldSpecification<VerifyPartyCommand> taxReferenceRequired =
      new RequiredFieldSpecification<>(
          "metadata.taxReferenceNumber",
          cmd -> cmd.getMetadata().get("taxReferenceNumber"));

  @Override
  public SpecificationResult isSatisfiedBy(VerifyPartyCommand command) {
    return metadataRequired
        .conditionalAnd(
            taxReferenceRequired,
            metadataRequired.isSatisfiedBy(command).satisfied())
        .isSatisfiedBy(command);
  }
}
