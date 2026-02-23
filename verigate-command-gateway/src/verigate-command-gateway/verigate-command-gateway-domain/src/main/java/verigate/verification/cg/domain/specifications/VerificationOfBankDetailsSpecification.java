/*
 * Arthmatic + Karisani(c) 2025. All rights reserved.
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
 * command to be considered valid for a VerificationOfBankDetails Verification Type.
 */
public class VerificationOfBankDetailsSpecification implements Specification<VerifyPartyCommand> {

  private static final RequiredFieldSpecification<VerifyPartyCommand> metadataRequired =
      new RequiredFieldSpecification<>("metadata", VerifyPartyCommand::getMetadata);

  private static final RequiredFieldSpecification<VerifyPartyCommand> bankNameRequired =
      new RequiredFieldSpecification<>("metaData.bankName", event -> event.getMetadata());

  private static final RequiredFieldSpecification<VerifyPartyCommand> bankAccountNumberRequired =
      new RequiredFieldSpecification<>("metaData.bankAccountNumber", event -> event.getMetadata());

  private static final RequiredFieldSpecification<VerifyPartyCommand> bankAccountTypeRequired =
      new RequiredFieldSpecification<>("metaData.bankAccountType", event -> event.getMetadata());

  @Override
  public SpecificationResult isSatisfiedBy(VerifyPartyCommand command) {
    // TODO: Add more fields to verify
    return metadataRequired
        .conditionalAnd(
            bankNameRequired.and(bankAccountNumberRequired).and(bankAccountTypeRequired),
            metadataRequired.isSatisfiedBy(command).satisfied())
        .isSatisfiedBy(command);
  }
}
