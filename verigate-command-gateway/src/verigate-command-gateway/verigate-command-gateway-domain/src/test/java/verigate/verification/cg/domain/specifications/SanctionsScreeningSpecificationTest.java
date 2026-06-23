/*
 * Arthmatic + Karisani(c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium
 * is strictly prohibited. Proprietary and confidential.
 */

package verigate.verification.cg.domain.specifications;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import domain.invariants.SpecificationResult;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.Origination;
import verigate.verification.cg.domain.models.OriginationType;
import verigate.verification.cg.domain.models.VerificationType;

class SanctionsScreeningSpecificationTest {

  private static final SanctionsScreeningSpecification SPEC = new SanctionsScreeningSpecification();

  private static final Origination ORIGINATION =
      new Origination(OriginationType.ADHOC, UUID.randomUUID());

  // ── Person entity tests ─────────────────────────────────────────────

  @Test
  void person_withFirstAndLastName_satisfied() {
    VerifyPartyCommand command = command(Map.of(
        "entityType", "person",
        "firstName", "Mandla",
        "lastName", "Tshabalala"
    ));
    assertTrue(SPEC.isSatisfiedBy(command).satisfied());
  }

  @Test
  void person_missingLastName_notSatisfied() {
    VerifyPartyCommand command = command(Map.of(
        "entityType", "person",
        "firstName", "Mandla"
    ));
    assertFalse(SPEC.isSatisfiedBy(command).satisfied());
  }

  @Test
  void person_missingFirstName_notSatisfied() {
    VerifyPartyCommand command = command(Map.of(
        "entityType", "person",
        "lastName", "Tshabalala"
    ));
    assertFalse(SPEC.isSatisfiedBy(command).satisfied());
  }

  @Test
  void noEntityType_defaultsToPerson_requiresLastName() {
    // When entityType is absent the schema defaults to Person, so lastName is required
    VerifyPartyCommand command = command(Map.of("firstName", "Mandla"));
    assertFalse(SPEC.isSatisfiedBy(command).satisfied());
  }

  @Test
  void noEntityType_withBothNames_satisfied() {
    VerifyPartyCommand command = command(Map.of(
        "firstName", "Mandla",
        "lastName", "Tshabalala"
    ));
    assertTrue(SPEC.isSatisfiedBy(command).satisfied());
  }

  // ── Non-person entity tests ─────────────────────────────────────────

  @Test
  void company_withNameAsFirstName_satisfied() {
    VerifyPartyCommand command = command(Map.of(
        "entityType", "company",
        "firstName", "Acme Corp Ltd"
    ));
    assertTrue(SPEC.isSatisfiedBy(command).satisfied());
  }

  @Test
  void organization_withNameAsFirstName_satisfied() {
    VerifyPartyCommand command = command(Map.of(
        "entityType", "organization",
        "firstName", "United Nations"
    ));
    assertTrue(SPEC.isSatisfiedBy(command).satisfied());
  }

  @Test
  void vessel_withNameAsFirstName_satisfied() {
    VerifyPartyCommand command = command(Map.of(
        "entityType", "vessel",
        "firstName", "MV Stellenbosch"
    ));
    assertTrue(SPEC.isSatisfiedBy(command).satisfied());
  }

  @Test
  void company_missingFirstName_notSatisfied() {
    VerifyPartyCommand command = command(Map.of("entityType", "company"));
    assertFalse(SPEC.isSatisfiedBy(command).satisfied());
  }

  @Test
  void entityTypeCaseInsensitive_PERSON_requiresLastName() {
    VerifyPartyCommand command = command(Map.of(
        "entityType", "PERSON",
        "firstName", "Mandla"
    ));
    SpecificationResult result = SPEC.isSatisfiedBy(command);
    assertFalse(result.satisfied());
  }

  @Test
  void entityTypeCaseInsensitive_COMPANY_noLastNameRequired() {
    VerifyPartyCommand command = command(Map.of(
        "entityType", "COMPANY",
        "firstName", "Pyongyang Trading"
    ));
    assertTrue(SPEC.isSatisfiedBy(command).satisfied());
  }

  // ── Null metadata guard ─────────────────────────────────────────────

  @Test
  void nullMetadata_notSatisfied() {
    VerifyPartyCommand command = new VerifyPartyCommand(
        UUID.randomUUID(), Instant.now(), "test-user",
        VerificationType.SANCTIONS_SCREENING, ORIGINATION, null);
    assertFalse(SPEC.isSatisfiedBy(command).satisfied());
  }

  // ── Helper ─────────────────────────────────────────────────────────

  private static VerifyPartyCommand command(Map<String, Object> metadata) {
    return new VerifyPartyCommand(
        UUID.randomUUID(), Instant.now(), "test-user",
        VerificationType.SANCTIONS_SCREENING, ORIGINATION,
        new HashMap<>(metadata));
  }
}
