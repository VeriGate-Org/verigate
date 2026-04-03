/*
 * Arthmatic + Karisani(c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.application.factories;

import com.google.inject.Inject;
import domain.invariants.Specification;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.specifications.CompanyVerificationSpecification;
import verigate.verification.cg.domain.specifications.CreditCheckSpecification;
import verigate.verification.cg.domain.specifications.MetadataRequiredSpecification;
import verigate.verification.cg.domain.specifications.SanctionsScreeningSpecification;
import verigate.verification.cg.domain.specifications.SpecificationFactory;
import verigate.verification.cg.domain.specifications.TaxComplianceVerificationSpecification;
import verigate.verification.cg.domain.specifications.VerificationOfBankDetailsSpecification;
import verigate.verification.cg.domain.specifications.VerificationOfPersonalDetailsSpecification;

/**
 * Represents a factory for creating a specification for a given type.
 *
 * @param <T> The type for which the specification is created.
 */
public class VerifyPartySpecificationFactory implements SpecificationFactory<VerifyPartyCommand> {

  @Inject
  public VerifyPartySpecificationFactory() {}

  @Override
  public Specification<VerifyPartyCommand> createSpecification(VerifyPartyCommand command) {
    return switch (command.getVerificationType()) {
      case VERIFICATION_OF_PERSONAL_DETAILS, IDENTITY_VERIFICATION ->
          new VerificationOfPersonalDetailsSpecification();
      case VERIFICATION_OF_BANK_DETAILS, BANK_ACCOUNT_VERIFICATION ->
          new VerificationOfBankDetailsSpecification();
      case SANCTIONS_SCREENING, WATCHLIST_SCREENING, NEGATIVE_NEWS_SCREENING,
          FRAUD_WATCHLIST_SCREENING -> new SanctionsScreeningSpecification();
      case COMPANY_VERIFICATION -> new CompanyVerificationSpecification();
      case CREDIT_CHECK -> new CreditCheckSpecification();
      case TAX_COMPLIANCE_VERIFICATION -> new TaxComplianceVerificationSpecification();
      case PROPERTY_OWNERSHIP_VERIFICATION, EMPLOYMENT_VERIFICATION, DOCUMENT_VERIFICATION,
          QUALIFICATION_VERIFICATION, INCOME_VERIFICATION, FULL_VERIFICATION,
          BIOMETRIC_VERIFICATION, LIVENESS_CHECK ->
          new MetadataRequiredSpecification();
    };
  }
}
