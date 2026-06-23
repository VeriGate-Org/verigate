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
 * command to be considered valid for a SanctionsScreening Verification Type.
 *
 * <p>Rules:
 * <ul>
 *   <li>{@code metadata} — always required</li>
 *   <li>{@code metadata.firstName} — always required; primary name field for all
 *       entity schemas</li>
 *   <li>{@code metadata.lastName} — required only for Person entities; company, organisation,
 *       and vessel schemas use a single name field passed as {@code firstName}</li>
 * </ul>
 */
public class SanctionsScreeningSpecification implements Specification<VerifyPartyCommand> {

  private static final RequiredFieldSpecification<VerifyPartyCommand> metadataRequired =
      new RequiredFieldSpecification<>("metadata", VerifyPartyCommand::getMetadata);

  private static final RequiredFieldSpecification<VerifyPartyCommand> firstNameRequired =
      new RequiredFieldSpecification<>(
          "metaData.firstName", event -> event.getMetadata().get("firstName"));

  private static final RequiredFieldSpecification<VerifyPartyCommand> lastNameRequired =
      new RequiredFieldSpecification<>(
          "metaData.lastName", event -> event.getMetadata().get("lastName"));

  @Override
  public SpecificationResult isSatisfiedBy(VerifyPartyCommand command) {
    SpecificationResult metadataCheck = metadataRequired.isSatisfiedBy(command);
    if (!metadataCheck.satisfied()) {
      return metadataCheck;
    }

    SpecificationResult firstNameCheck = firstNameRequired.isSatisfiedBy(command);
    if (!firstNameCheck.satisfied()) {
      return firstNameCheck;
    }

    // lastName is only required for person entities; non-person schemas (Company, Organisation,
    // Vessel) pass the entity name via firstName and do not have a separate lastName.
    if (isPersonEntity(command)) {
      return lastNameRequired.isSatisfiedBy(command);
    }

    return SpecificationResult.success();
  }

  private static boolean isPersonEntity(VerifyPartyCommand command) {
    Object entityType = command.getMetadata().get("entityType");
    if (entityType == null) {
      return true; // default schema is Person when entityType is not specified
    }
    String typeStr = entityType.toString().trim();
    return typeStr.isEmpty() || "person".equalsIgnoreCase(typeStr);
  }
}
