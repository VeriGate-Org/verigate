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
 * command to be considered valid for a CompanyVerification Verification Type.
 */
public class CompanyVerificationSpecification implements Specification<VerifyPartyCommand> {

  private static final RequiredFieldSpecification<VerifyPartyCommand> metadataRequired =
      new RequiredFieldSpecification<>("metadata", VerifyPartyCommand::getMetadata);

  private static final RequiredFieldSpecification<VerifyPartyCommand> enterpriseNumberRequired =
      new RequiredFieldSpecification<>(
          "metadata.enterpriseNumber", cmd -> cmd.getMetadata().get("enterpriseNumber"));

  @Override
  public SpecificationResult isSatisfiedBy(VerifyPartyCommand command) {
    return metadataRequired
        .conditionalAnd(
            enterpriseNumberRequired,
            metadataRequired.isSatisfiedBy(command).satisfied())
        .isSatisfiedBy(command);
  }
}
