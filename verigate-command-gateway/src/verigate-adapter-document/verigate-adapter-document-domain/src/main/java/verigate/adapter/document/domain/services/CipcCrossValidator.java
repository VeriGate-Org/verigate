/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.domain.services;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import verigate.adapter.document.domain.constants.DomainConstants;
import verigate.adapter.document.domain.models.CipcCompanyLookupResult;
import verigate.adapter.document.domain.models.CipcCrossValidationResult;
import verigate.adapter.document.domain.models.FieldMatchStatus;

/**
 * Cross-validates fields extracted from an uploaded CIPC registration document against a live
 * CIPC company lookup.
 *
 * <p>CIPC data and AI-extracted OCR text rarely match byte-for-byte — company names carry legal
 * suffixes inconsistently, addresses are formatted completely differently between a printed
 * certificate and CIPC's structured record, and company-type vocabulary differs ("Pty Ltd" vs.
 * "Private Company"). This class applies normalization heuristics rather than exact-string
 * comparison; see the per-field methods below for the specific rules. These are a starting point
 * (story 2.1 flagged this as needing tuning against real documents) — if mismatches prove noisy
 * in practice, revisit the thresholds/rules here rather than the calling code.
 */
public class CipcCrossValidator {

  private static final double ADDRESS_TOKEN_OVERLAP_THRESHOLD = 0.5;

  private static final Set<String> COMPANY_SUFFIXES = Set.of(
      "PTY", "PTY LTD", "PROPRIETARY LIMITED", "PROPRIETARY", "LIMITED", "LTD", "LTD.",
      "NPC", "INC", "INCORPORATED", "CC");

  private static final Set<String> ACTIVE_STATUS_TOKENS = Set.of(
      "ACTIVE", "IN BUSINESS", "IN_BUSINESS");

  private static final Set<String> INACTIVE_STATUS_TOKENS = Set.of(
      "DEREGISTERED", "IN LIQUIDATION", "IN_LIQUIDATION", "BUSINESS RESCUE",
      // "IN BUSINESS RESCUE" is the exact normalized form of the value the CIPC extraction
      // prompt (document-analysis-cipc.txt) is instructed to produce -- must match precisely,
      // not just the CIPC adapter's own "(UNDER_)BUSINESS_RESCUE" wording, or a document
      // correctly extracted per-prompt gets wrongly flagged as a status mismatch.
      "IN BUSINESS RESCUE", "IN_BUSINESS_RESCUE",
      "UNDER BUSINESS RESCUE", "UNDER_BUSINESS_RESCUE", "FINAL DEREGISTRATION",
      "FINAL_DEREGISTRATION");

  private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^A-Z0-9 ]");
  private static final Pattern WHITESPACE = Pattern.compile("\\s+");

  /**
   * Cross-validates extracted document fields against a CIPC lookup result.
   *
   * @param extractedFields flattened extracted fields from AI analysis, keyed by the
   *     {@code CIPC_FIELD_*} constants in {@link DomainConstants}
   * @param cipcResult the live CIPC lookup result to compare against
   * @return the cross-validation result
   */
  public CipcCrossValidationResult validate(
      Map<String, String> extractedFields, CipcCompanyLookupResult cipcResult) {

    Map<String, String> fields = extractedFields != null ? extractedFields : Map.of();

    if (cipcResult == null || !cipcResult.found()) {
      return CipcCrossValidationResult.unavailable(
          "No CIPC company found for the extracted registration number");
    }

    Map<String, FieldMatchStatus> statuses = new LinkedHashMap<>();
    Map<String, String> cipcValues = new LinkedHashMap<>();

    compareCompanyName(fields, cipcResult, statuses, cipcValues);
    compareCompanyStatus(fields, cipcResult, statuses, cipcValues);
    compareCompanyType(fields, cipcResult, statuses, cipcValues);
    compareAddress(fields, cipcResult, statuses, cipcValues);
    compareDirectors(fields, cipcResult, statuses, cipcValues);

    boolean companyActive = ACTIVE_STATUS_TOKENS.contains(
        normalizeSimple(cipcResult.companyStatus()));

    return new CipcCrossValidationResult(
        true, true, companyActive, statuses, cipcValues, null);
  }

  private void compareCompanyName(
      Map<String, String> fields, CipcCompanyLookupResult cipcResult,
      Map<String, FieldMatchStatus> statuses, Map<String, String> cipcValues) {

    String extracted = fields.get(DomainConstants.CIPC_FIELD_COMPANY_NAME);
    String cipcValue = cipcResult.companyName();
    cipcValues.put(DomainConstants.CIPC_FIELD_COMPANY_NAME, cipcValue);

    statuses.put(DomainConstants.CIPC_FIELD_COMPANY_NAME,
        matchStatusFor(extracted, cipcValue, this::namesMatch));
  }

  private void compareCompanyStatus(
      Map<String, String> fields, CipcCompanyLookupResult cipcResult,
      Map<String, FieldMatchStatus> statuses, Map<String, String> cipcValues) {

    String extracted = fields.get(DomainConstants.CIPC_FIELD_COMPANY_STATUS);
    String cipcValue = cipcResult.companyStatus();
    cipcValues.put(DomainConstants.CIPC_FIELD_COMPANY_STATUS, cipcValue);

    statuses.put(DomainConstants.CIPC_FIELD_COMPANY_STATUS,
        matchStatusFor(extracted, cipcValue, this::statusesCompatible));
  }

  private void compareCompanyType(
      Map<String, String> fields, CipcCompanyLookupResult cipcResult,
      Map<String, FieldMatchStatus> statuses, Map<String, String> cipcValues) {

    String extracted = fields.get(DomainConstants.CIPC_FIELD_COMPANY_TYPE);
    String cipcValue = cipcResult.companyType();
    cipcValues.put(DomainConstants.CIPC_FIELD_COMPANY_TYPE, cipcValue);

    // Company-type vocabularies differ meaningfully between printed certificates ("Pty Ltd")
    // and CIPC's own descriptions ("Private Company") — normalized substring match is a
    // deliberately loose heuristic here, not a strict comparison.
    statuses.put(DomainConstants.CIPC_FIELD_COMPANY_TYPE,
        matchStatusFor(extracted, cipcValue, this::looseContains));
  }

  private void compareAddress(
      Map<String, String> fields, CipcCompanyLookupResult cipcResult,
      Map<String, FieldMatchStatus> statuses, Map<String, String> cipcValues) {

    String extracted = fields.get(DomainConstants.CIPC_FIELD_REGISTERED_ADDRESS);
    String cipcValue = cipcResult.registeredAddress();
    cipcValues.put(DomainConstants.CIPC_FIELD_REGISTERED_ADDRESS, cipcValue);

    statuses.put(DomainConstants.CIPC_FIELD_REGISTERED_ADDRESS,
        matchStatusFor(extracted, cipcValue, this::addressesOverlapSufficiently));
  }

  private void compareDirectors(
      Map<String, String> fields, CipcCompanyLookupResult cipcResult,
      Map<String, FieldMatchStatus> statuses, Map<String, String> cipcValues) {

    String extracted = fields.get(DomainConstants.CIPC_FIELD_DIRECTORS);
    String cipcValue = String.join(", ", cipcResult.directorNames());
    cipcValues.put(DomainConstants.CIPC_FIELD_DIRECTORS, cipcValue);

    if (isBlank(extracted)) {
      statuses.put(DomainConstants.CIPC_FIELD_DIRECTORS, FieldMatchStatus.NOT_EXTRACTED);
      return;
    }
    if (cipcResult.directorNames().isEmpty()) {
      statuses.put(DomainConstants.CIPC_FIELD_DIRECTORS, FieldMatchStatus.NOT_IN_CIPC_RECORD);
      return;
    }

    // The prompt extracts directors as a free-text list; check that at least one extracted
    // name overlaps with at least one CIPC director name.
    boolean anyMatch = false;
    for (String extractedName : extracted.split("[,;\\n]")) {
      for (String cipcName : cipcResult.directorNames()) {
        if (namesMatch(extractedName, cipcName)) {
          anyMatch = true;
          break;
        }
      }
      if (anyMatch) {
        break;
      }
    }
    statuses.put(DomainConstants.CIPC_FIELD_DIRECTORS,
        anyMatch ? FieldMatchStatus.MATCH : FieldMatchStatus.MISMATCH);
  }

  @FunctionalInterface
  private interface FieldComparator {
    boolean matches(String extracted, String cipcValue);
  }

  private FieldMatchStatus matchStatusFor(
      String extracted, String cipcValue, FieldComparator comparator) {
    if (isBlank(extracted)) {
      return FieldMatchStatus.NOT_EXTRACTED;
    }
    if (isBlank(cipcValue)) {
      return FieldMatchStatus.NOT_IN_CIPC_RECORD;
    }
    return comparator.matches(extracted, cipcValue)
        ? FieldMatchStatus.MATCH
        : FieldMatchStatus.MISMATCH;
  }

  private boolean namesMatch(String a, String b) {
    String normA = normalizeCompanyOrPersonName(a);
    String normB = normalizeCompanyOrPersonName(b);
    if (normA.isEmpty() || normB.isEmpty()) {
      return false;
    }
    return normA.equals(normB) || normA.contains(normB) || normB.contains(normA);
  }

  private boolean statusesCompatible(String a, String b) {
    String normA = normalizeSimple(a);
    String normB = normalizeSimple(b);
    if (normA.equals(normB)) {
      return true;
    }
    boolean firstActive = ACTIVE_STATUS_TOKENS.contains(normA);
    boolean secondActive = ACTIVE_STATUS_TOKENS.contains(normB);
    boolean firstInactive = INACTIVE_STATUS_TOKENS.contains(normA);
    boolean secondInactive = INACTIVE_STATUS_TOKENS.contains(normB);
    return (firstActive && secondActive) || (firstInactive && secondInactive);
  }

  private boolean looseContains(String a, String b) {
    String normA = normalizeSimple(a);
    String normB = normalizeSimple(b);
    return normA.contains(normB) || normB.contains(normA);
  }

  private boolean addressesOverlapSufficiently(String a, String b) {
    Set<String> tokensA = tokenize(a);
    Set<String> tokensB = tokenize(b);
    if (tokensA.isEmpty() || tokensB.isEmpty()) {
      return false;
    }
    Set<String> intersection = new HashSet<>(tokensA);
    intersection.retainAll(tokensB);
    Set<String> union = new HashSet<>(tokensA);
    union.addAll(tokensB);
    double jaccard = (double) intersection.size() / union.size();
    return jaccard >= ADDRESS_TOKEN_OVERLAP_THRESHOLD;
  }

  private Set<String> tokenize(String value) {
    if (isBlank(value)) {
      return Set.of();
    }
    String normalized = normalizeSimple(value);
    Set<String> tokens = new HashSet<>();
    for (String token : normalized.split(" ")) {
      if (!token.isBlank()) {
        tokens.add(token);
      }
    }
    return tokens;
  }

  private String normalizeSimple(String value) {
    if (value == null) {
      return "";
    }
    String upper = value.trim().toUpperCase();
    String stripped = NON_ALPHANUMERIC.matcher(upper).replaceAll(" ");
    return WHITESPACE.matcher(stripped).replaceAll(" ").trim();
  }

  private String normalizeCompanyOrPersonName(String value) {
    String normalized = normalizeSimple(value);
    for (String suffix : COMPANY_SUFFIXES) {
      normalized = normalized.replace(" " + suffix, "");
      if (normalized.endsWith(suffix)) {
        normalized = normalized.substring(0, normalized.length() - suffix.length()).trim();
      }
    }
    return WHITESPACE.matcher(normalized).replaceAll(" ").trim();
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim());
  }
}
