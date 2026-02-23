/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.saqa.domain.models;

/**
 * Enumeration of qualification types recognised by SAQA.
 */
public enum QualificationType {
  NATIONAL_CERTIFICATE,
  NATIONAL_DIPLOMA,
  BACHELORS_DEGREE,
  HONOURS_DEGREE,
  MASTERS_DEGREE,
  DOCTORAL_DEGREE,
  PROFESSIONAL_QUALIFICATION,
  TRADE_CERTIFICATE;

  /**
   * Parse a SAQA qualification type description to the corresponding enum value.
   */
  public static QualificationType fromDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return null;
    }

    String normalized = description.trim().toUpperCase().replace(" ", "_").replace("'", "");

    try {
      return valueOf(normalized);
    } catch (IllegalArgumentException e) {
      // Handle common variations
      return switch (normalized) {
        case "NATIONAL_CERTIFICATE", "CERTIFICATE", "NC" -> NATIONAL_CERTIFICATE;
        case "NATIONAL_DIPLOMA", "DIPLOMA", "ND" -> NATIONAL_DIPLOMA;
        case "BACHELORS_DEGREE", "BACHELOR", "BACHELORS", "BACCALAUREUS", "B_DEGREE"
            -> BACHELORS_DEGREE;
        case "HONOURS_DEGREE", "HONOURS", "HONOR", "HONORS", "HONS" -> HONOURS_DEGREE;
        case "MASTERS_DEGREE", "MASTERS", "MASTER", "M_DEGREE" -> MASTERS_DEGREE;
        case "DOCTORAL_DEGREE", "DOCTORATE", "PHD", "DOCTOR", "D_DEGREE" -> DOCTORAL_DEGREE;
        case "PROFESSIONAL_QUALIFICATION", "PROFESSIONAL", "PROF_QUAL"
            -> PROFESSIONAL_QUALIFICATION;
        case "TRADE_CERTIFICATE", "TRADE", "TRADE_CERT" -> TRADE_CERTIFICATE;
        default -> null;
      };
    }
  }
}
