/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.domain.mappers;

import java.util.ArrayList;
import java.util.List;
import verigate.adapter.negativenews.domain.constants.DomainConstants;
import verigate.adapter.negativenews.domain.models.NegativeNewsScreeningRequest;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Mapper for converting VerifyPartyCommand to NegativeNewsScreeningRequest.
 */
public interface NegativeNewsScreeningMapper {

  /**
   * Maps a VerifyPartyCommand to a NegativeNewsScreeningRequest.
   *
   * @param command the verification command
   * @return the mapped negative news screening request
   */
  NegativeNewsScreeningRequest mapToScreeningRequest(VerifyPartyCommand command);

  /**
   * Default implementation providing mapping logic.
   *
   * @param command the verification command
   * @return the mapped negative news screening request
   */
  static NegativeNewsScreeningRequest mapToScreeningRequestDefault(VerifyPartyCommand command) {
    String subjectName = extractSubjectName(command);

    if (subjectName == null || subjectName.trim().isEmpty()) {
      throw new IllegalArgumentException(
          "Subject name is required for negative news screening");
    }

    String idNumber = extractIdNumber(command);
    List<String> additionalKeywords = extractAdditionalKeywords(command);
    int dateRangeMonths = extractDateRangeMonths(command);

    return NegativeNewsScreeningRequest.builder()
        .subjectName(subjectName.trim())
        .idNumber(idNumber)
        .additionalKeywords(additionalKeywords)
        .dateRangeMonths(dateRangeMonths)
        .build();
  }

  /**
   * Extracts the subject name from the command metadata by combining firstName and lastName.
   */
  static String extractSubjectName(VerifyPartyCommand command) {
    if (command.getMetadata() == null) {
      return null;
    }

    String firstName = extractMetadataValue(command, "firstName", "first_name");
    String lastName = extractMetadataValue(command, "lastName", "last_name", "surname");

    if (firstName == null && lastName == null) {
      // Try full name as fallback
      return extractMetadataValue(command, "subjectName", "subject_name", "fullName", "full_name");
    }

    StringBuilder name = new StringBuilder();
    if (firstName != null && !firstName.trim().isEmpty()) {
      name.append(firstName.trim());
    }
    if (lastName != null && !lastName.trim().isEmpty()) {
      if (name.length() > 0) {
        name.append(" ");
      }
      name.append(lastName.trim());
    }

    return name.length() > 0 ? name.toString() : null;
  }

  /**
   * Extracts the ID number from the command metadata.
   */
  static String extractIdNumber(VerifyPartyCommand command) {
    if (command.getMetadata() == null) {
      return null;
    }
    return extractMetadataValue(
        command, "idNumber", "id_number", "identityNumber", "identity_number");
  }

  /**
   * Extracts additional keywords from the command metadata.
   */
  @SuppressWarnings("unchecked")
  static List<String> extractAdditionalKeywords(VerifyPartyCommand command) {
    if (command.getMetadata() == null) {
      return List.of();
    }

    Object keywordsObj = command.getMetadata().get("additionalKeywords");
    if (keywordsObj == null) {
      keywordsObj = command.getMetadata().get("additional_keywords");
    }

    if (keywordsObj instanceof List<?>) {
      List<String> keywords = new ArrayList<>();
      for (Object item : (List<?>) keywordsObj) {
        if (item != null) {
          keywords.add(item.toString());
        }
      }
      return keywords;
    }

    if (keywordsObj instanceof String) {
      String keywordsStr = ((String) keywordsObj).trim();
      if (!keywordsStr.isEmpty()) {
        List<String> keywords = new ArrayList<>();
        for (String keyword : keywordsStr.split(",")) {
          String trimmed = keyword.trim();
          if (!trimmed.isEmpty()) {
            keywords.add(trimmed);
          }
        }
        return keywords;
      }
    }

    return List.of();
  }

  /**
   * Extracts the date range in months from the command metadata.
   */
  static int extractDateRangeMonths(VerifyPartyCommand command) {
    if (command.getMetadata() == null) {
      return DomainConstants.DEFAULT_DATE_RANGE_MONTHS;
    }

    String dateRange = extractMetadataValue(
        command, "dateRangeMonths", "date_range_months");

    if (dateRange != null && !dateRange.trim().isEmpty()) {
      try {
        return Integer.parseInt(dateRange.trim());
      } catch (NumberFormatException e) {
        return DomainConstants.DEFAULT_DATE_RANGE_MONTHS;
      }
    }

    return DomainConstants.DEFAULT_DATE_RANGE_MONTHS;
  }

  /**
   * Extracts a metadata value by trying multiple possible field names.
   */
  private static String extractMetadataValue(VerifyPartyCommand command, String... keys) {
    if (command.getMetadata() == null) {
      return null;
    }

    for (String key : keys) {
      Object value = command.getMetadata().get(key);
      if (value != null) {
        return value.toString();
      }
    }

    return null;
  }
}
