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
 * Base specification that validates the {@link VerifyPartyCommand} has metadata present.
 * Used for verification types where specific field requirements are not yet defined.
 */
public class MetadataRequiredSpecification implements Specification<VerifyPartyCommand> {

  private static final RequiredFieldSpecification<VerifyPartyCommand> metadataRequired =
      new RequiredFieldSpecification<>("metadata", VerifyPartyCommand::getMetadata);

  @Override
  public SpecificationResult isSatisfiedBy(VerifyPartyCommand command) {
    return metadataRequired.isSatisfiedBy(command);
  }
}
