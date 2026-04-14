/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.adapter.document.infrastructure.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SaIdValidatorTest {

  // Valid SA ID: 8501015009087 (Luhn-valid)
  @Test
  void shouldPassAllChecksForValidSaId() {
    List<AiDocumentAnalyzer.ValidationCheck> checks =
        SaIdValidator.validate("8501015009087");

    assertEquals(5, checks.size());
    assertTrue(checks.stream().allMatch(c -> "PASS".equals(c.status())));

    var luhn = checks.stream().filter(c -> "LUHN_CHECK".equals(c.name())).findFirst();
    assertTrue(luhn.isPresent());
    assertEquals("PASS", luhn.get().status());
  }

  @Test
  void shouldFailLuhnForInvalidChecksum() {
    // Change last digit to make Luhn fail
    List<AiDocumentAnalyzer.ValidationCheck> checks =
        SaIdValidator.validate("8501015009086");

    var luhn = checks.stream().filter(c -> "LUHN_CHECK".equals(c.name())).findFirst();
    assertTrue(luhn.isPresent());
    assertEquals("FAIL", luhn.get().status());
  }

  @Test
  void shouldFailForWrongLength() {
    List<AiDocumentAnalyzer.ValidationCheck> checks =
        SaIdValidator.validate("123456");

    assertEquals(1, checks.size());
    assertEquals("FORMAT_CHECK", checks.get(0).name());
    assertEquals("FAIL", checks.get(0).status());
  }

  @Test
  void shouldFailForNonNumeric() {
    List<AiDocumentAnalyzer.ValidationCheck> checks =
        SaIdValidator.validate("850101500908A");

    assertEquals(1, checks.size());
    assertEquals("FAIL", checks.get(0).status());
  }

  @Test
  void shouldFailForEmptyOrNull() {
    List<AiDocumentAnalyzer.ValidationCheck> nullChecks = SaIdValidator.validate(null);
    assertEquals(1, nullChecks.size());
    assertEquals("FAIL", nullChecks.get(0).status());

    List<AiDocumentAnalyzer.ValidationCheck> emptyChecks = SaIdValidator.validate("");
    assertEquals(1, emptyChecks.size());
    assertEquals("FAIL", emptyChecks.get(0).status());
  }

  @Test
  void shouldDetectGenderFromId() {
    // 5009 >= 5000 → Male
    List<AiDocumentAnalyzer.ValidationCheck> checks =
        SaIdValidator.validate("8501015009087");

    var gender = checks.stream().filter(c -> "GENDER_CHECK".equals(c.name())).findFirst();
    assertTrue(gender.isPresent());
    assertTrue(gender.get().detail().contains("Male"));
  }

  @Test
  void shouldDetectFemaleGender() {
    // 0009 < 5000 → Female
    List<AiDocumentAnalyzer.ValidationCheck> checks =
        SaIdValidator.validate("8501010009083");

    var gender = checks.stream().filter(c -> "GENDER_CHECK".equals(c.name())).findFirst();
    assertTrue(gender.isPresent());
    assertTrue(gender.get().detail().contains("Female"));
  }

  @Test
  void shouldDetectCitizenship() {
    // Digit at index 10 = 0 → SA Citizen
    List<AiDocumentAnalyzer.ValidationCheck> checks =
        SaIdValidator.validate("8501015009087");

    var citizenship = checks.stream()
        .filter(c -> "CITIZENSHIP_CHECK".equals(c.name())).findFirst();
    assertTrue(citizenship.isPresent());
    assertTrue(citizenship.get().detail().contains("SA Citizen"));
  }

  @Test
  void shouldExtractDemographics() {
    Map<String, String> demographics = SaIdValidator.extractDemographics("8501015009087");

    assertEquals("1985-01-01", demographics.get("dateOfBirth"));
    assertEquals("Male", demographics.get("gender"));
    assertEquals("SA Citizen", demographics.get("citizenship"));
  }

  @Test
  void shouldReturnEmptyDemographicsForInvalidId() {
    Map<String, String> demographics = SaIdValidator.extractDemographics("invalid");
    assertTrue(demographics.isEmpty());
  }

  @Test
  void shouldHandleIdWithSpaces() {
    List<AiDocumentAnalyzer.ValidationCheck> checks =
        SaIdValidator.validate("8501 0150 0908 7");

    assertTrue(checks.stream().anyMatch(c -> "FORMAT_CHECK".equals(c.name()) && "PASS".equals(c.status())));
  }

  @Test
  void luhnCheckShouldWorkForKnownValues() {
    assertTrue(SaIdValidator.luhnCheck("8501015009087"));
    assertFalse(SaIdValidator.luhnCheck("8501015009086"));
  }

  @Test
  void shouldDetectInvalidDateOfBirth() {
    // Month 13 is invalid
    List<AiDocumentAnalyzer.ValidationCheck> checks =
        SaIdValidator.validate("8513015009084");

    var dob = checks.stream().filter(c -> "DOB_CHECK".equals(c.name())).findFirst();
    assertTrue(dob.isPresent());
    assertEquals("FAIL", dob.get().status());
  }
}
