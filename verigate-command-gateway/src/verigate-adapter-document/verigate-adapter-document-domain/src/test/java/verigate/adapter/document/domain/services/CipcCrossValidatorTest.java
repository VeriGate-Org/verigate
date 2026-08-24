/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.adapter.document.domain.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import verigate.adapter.document.domain.constants.DomainConstants;
import verigate.adapter.document.domain.models.CipcCompanyLookupResult;
import verigate.adapter.document.domain.models.CipcCrossValidationResult;
import verigate.adapter.document.domain.models.FieldMatchStatus;

class CipcCrossValidatorTest {

  private final CipcCrossValidator validator = new CipcCrossValidator();

  @Test
  void shouldReturnUnavailableWhenCompanyNotFound() {
    Map<String, String> extracted = Map.of(
        DomainConstants.CIPC_FIELD_COMPANY_NAME, "Acme Trading (Pty) Ltd");

    CipcCrossValidationResult result =
        validator.validate(extracted, CipcCompanyLookupResult.notFound("2020/939681/07"));

    assertFalse(result.cipcLookupPerformed());
    assertFalse(result.companyFound());
  }

  @Test
  void shouldMatchCompanyNameDespiteLegalSuffixDifferences() {
    Map<String, String> extracted = Map.of(
        DomainConstants.CIPC_FIELD_COMPANY_NAME, "Acme Trading (Pty) Ltd");

    CipcCompanyLookupResult cipcResult = new CipcCompanyLookupResult(
        true, "2020/939681/07", "ACME TRADING PROPRIETARY LIMITED",
        "In Business", "Private Company", "1 Main Street, Cape Town", List.of());

    CipcCrossValidationResult result = validator.validate(extracted, cipcResult);

    assertEquals(FieldMatchStatus.MATCH,
        result.fieldStatuses().get(DomainConstants.CIPC_FIELD_COMPANY_NAME));
  }

  @Test
  void shouldMismatchOnClearlyDifferentCompanyNames() {
    Map<String, String> extracted = Map.of(
        DomainConstants.CIPC_FIELD_COMPANY_NAME, "Zebra Holdings (Pty) Ltd");

    CipcCompanyLookupResult cipcResult = new CipcCompanyLookupResult(
        true, "2020/939681/07", "ACME TRADING PROPRIETARY LIMITED",
        "In Business", "Private Company", "1 Main Street, Cape Town", List.of());

    CipcCrossValidationResult result = validator.validate(extracted, cipcResult);

    assertEquals(FieldMatchStatus.MISMATCH,
        result.fieldStatuses().get(DomainConstants.CIPC_FIELD_COMPANY_NAME));
    assertTrue(result.hasMismatch());
  }

  @Test
  void shouldTreatActiveAndInBusinessStatusesAsCompatible() {
    Map<String, String> extracted = Map.of(
        DomainConstants.CIPC_FIELD_COMPANY_STATUS, "Active");

    CipcCompanyLookupResult cipcResult = new CipcCompanyLookupResult(
        true, "2020/939681/07", "ACME TRADING PROPRIETARY LIMITED",
        "In Business", "Private Company", null, List.of());

    CipcCrossValidationResult result = validator.validate(extracted, cipcResult);

    assertEquals(FieldMatchStatus.MATCH,
        result.fieldStatuses().get(DomainConstants.CIPC_FIELD_COMPANY_STATUS));
    assertTrue(result.companyActive());
  }

  @Test
  void shouldFlagDeregisteredCompanyAsNotActive() {
    Map<String, String> extracted = Map.of(
        DomainConstants.CIPC_FIELD_COMPANY_STATUS, "Active");

    CipcCompanyLookupResult cipcResult = new CipcCompanyLookupResult(
        true, "2020/939681/07", "ACME TRADING PROPRIETARY LIMITED",
        "Deregistered", "Private Company", null, List.of());

    CipcCrossValidationResult result = validator.validate(extracted, cipcResult);

    assertFalse(result.companyActive());
    assertEquals(FieldMatchStatus.MISMATCH,
        result.fieldStatuses().get(DomainConstants.CIPC_FIELD_COMPANY_STATUS));
  }

  @Test
  void shouldMatchDirectorWhenAtLeastOneExtractedNameOverlaps() {
    Map<String, String> extracted = Map.of(
        DomainConstants.CIPC_FIELD_DIRECTORS, "John Smith, Someone Else");

    CipcCompanyLookupResult cipcResult = new CipcCompanyLookupResult(
        true, "2020/939681/07", "ACME TRADING PROPRIETARY LIMITED",
        "In Business", "Private Company", null, List.of("John Smith", "Jane Doe"));

    CipcCrossValidationResult result = validator.validate(extracted, cipcResult);

    assertEquals(FieldMatchStatus.MATCH,
        result.fieldStatuses().get(DomainConstants.CIPC_FIELD_DIRECTORS));
  }

  @Test
  void shouldMismatchDirectorsWhenNoneOverlap() {
    Map<String, String> extracted = Map.of(
        DomainConstants.CIPC_FIELD_DIRECTORS, "Someone Unrelated");

    CipcCompanyLookupResult cipcResult = new CipcCompanyLookupResult(
        true, "2020/939681/07", "ACME TRADING PROPRIETARY LIMITED",
        "In Business", "Private Company", null, List.of("John Smith", "Jane Doe"));

    CipcCrossValidationResult result = validator.validate(extracted, cipcResult);

    assertEquals(FieldMatchStatus.MISMATCH,
        result.fieldStatuses().get(DomainConstants.CIPC_FIELD_DIRECTORS));
  }

  @Test
  void shouldReturnNotExtractedWhenFieldMissingFromDocument() {
    Map<String, String> extracted = Map.of();

    CipcCompanyLookupResult cipcResult = new CipcCompanyLookupResult(
        true, "2020/939681/07", "ACME TRADING PROPRIETARY LIMITED",
        "In Business", "Private Company", "1 Main Street, Cape Town", List.of());

    CipcCrossValidationResult result = validator.validate(extracted, cipcResult);

    assertEquals(FieldMatchStatus.NOT_EXTRACTED,
        result.fieldStatuses().get(DomainConstants.CIPC_FIELD_COMPANY_NAME));
  }

  @Test
  void shouldReturnNotInCipcRecordWhenCipcHasNoDirectors() {
    Map<String, String> extracted = Map.of(
        DomainConstants.CIPC_FIELD_DIRECTORS, "John Smith");

    CipcCompanyLookupResult cipcResult = new CipcCompanyLookupResult(
        true, "2020/939681/07", "ACME TRADING PROPRIETARY LIMITED",
        "In Business", "Private Company", null, List.of());

    CipcCrossValidationResult result = validator.validate(extracted, cipcResult);

    assertEquals(FieldMatchStatus.NOT_IN_CIPC_RECORD,
        result.fieldStatuses().get(DomainConstants.CIPC_FIELD_DIRECTORS));
  }

  @Test
  void shouldMatchAddressesWithSufficientTokenOverlapDespiteFormatting() {
    Map<String, String> extracted = Map.of(
        DomainConstants.CIPC_FIELD_REGISTERED_ADDRESS, "1 Main Street, Cape Town, 8001");

    CipcCompanyLookupResult cipcResult = new CipcCompanyLookupResult(
        true, "2020/939681/07", "ACME TRADING PROPRIETARY LIMITED",
        "In Business", "Private Company", "Main Street 1, Cape Town, Western Cape, 8001",
        List.of());

    CipcCrossValidationResult result = validator.validate(extracted, cipcResult);

    assertEquals(FieldMatchStatus.MATCH,
        result.fieldStatuses().get(DomainConstants.CIPC_FIELD_REGISTERED_ADDRESS));
  }

  @Test
  void shouldMismatchCompletelyDifferentAddresses() {
    Map<String, String> extracted = Map.of(
        DomainConstants.CIPC_FIELD_REGISTERED_ADDRESS, "1 Main Street, Cape Town, 8001");

    CipcCompanyLookupResult cipcResult = new CipcCompanyLookupResult(
        true, "2020/939681/07", "ACME TRADING PROPRIETARY LIMITED",
        "In Business", "Private Company", "99 Ocean Drive, Durban, 4001", List.of());

    CipcCrossValidationResult result = validator.validate(extracted, cipcResult);

    assertEquals(FieldMatchStatus.MISMATCH,
        result.fieldStatuses().get(DomainConstants.CIPC_FIELD_REGISTERED_ADDRESS));
  }
}
