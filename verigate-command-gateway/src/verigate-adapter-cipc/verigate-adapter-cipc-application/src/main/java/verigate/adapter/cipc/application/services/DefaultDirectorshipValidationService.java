/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.cipc.domain.models.CompanyProfile;
import verigate.adapter.cipc.domain.models.Director;
import verigate.adapter.cipc.domain.models.DirectorshipVerificationResult;
import verigate.adapter.cipc.domain.services.DirectorshipValidationService;

/**
 * Default implementation of {@link DirectorshipValidationService}.
 * Validates directorship by iterating through the company's director records
 * and matching against the subject's identity number.
 */
public class DefaultDirectorshipValidationService implements DirectorshipValidationService {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultDirectorshipValidationService.class);

  /**
   * Validates whether a subject is a director of the given company by searching
   * through the director records for a matching identity number.
   *
   * @param profile the company profile containing director records
   * @param subjectIdNumber the identity number of the person to validate
   * @return the verification result indicating match status and director details
   */
  @Override
  public DirectorshipVerificationResult validateDirectorship(
      CompanyProfile profile,
      String subjectIdNumber) {

    if (profile == null) {
      logger.warn("Cannot validate directorship: company profile is null");
      return DirectorshipVerificationResult.directorNotFound(subjectIdNumber, "unknown");
    }

    if (subjectIdNumber == null || subjectIdNumber.trim().isEmpty()) {
      logger.warn("Cannot validate directorship: subject identity number is null or empty");
      return DirectorshipVerificationResult.directorNotFound(
          subjectIdNumber, profile.enterpriseNumber());
    }

    if (!profile.isActive()) {
      logger.info(
          "Company {} is not active (status: {}), skipping directorship validation",
          profile.enterpriseNumber(),
          profile.enterpriseStatus());
      return DirectorshipVerificationResult.companyInactive(
          profile.enterpriseStatus(), profile.enterpriseNumber());
    }

    if (profile.directors() == null || profile.directors().isEmpty()) {
      logger.info("Company {} has no directors on record", profile.enterpriseNumber());
      return DirectorshipVerificationResult.directorNotFound(
          subjectIdNumber, profile.enterpriseNumber());
    }

    logger.info(
        "Searching for director with ID {} among {} directors for enterprise {}",
        maskIdentityNumber(subjectIdNumber),
        profile.directors().size(),
        profile.enterpriseNumber());

    for (Director director : profile.directors()) {
      if (isDirectorMatch(director, subjectIdNumber)) {
        logger.info(
            "Director match found: {} (status: {}, type: {})",
            director.getFullName(),
            director.directorStatus(),
            director.directorType());

        if (director.isActive()) {
          return DirectorshipVerificationResult.success(
              director.designation(),
              director.directorType(),
              director.appointmentDate());
        }

        logger.info(
            "Director {} matched but is not active (status: {})",
            director.getFullName(),
            director.directorStatus());
        return DirectorshipVerificationResult.directorInactive(director);
      }
    }

    logger.info(
        "No director match found for ID {} in enterprise {}",
        maskIdentityNumber(subjectIdNumber),
        profile.enterpriseNumber());
    return DirectorshipVerificationResult.directorNotFound(
        subjectIdNumber, profile.enterpriseNumber());
  }

  /**
   * Compares a director's identity number with the subject's identity number.
   * The comparison is case-insensitive and ignores leading/trailing whitespace.
   *
   * @param director the director record to compare
   * @param subjectIdNumber the identity number to match against
   * @return true if the identity numbers match
   */
  @Override
  public boolean isDirectorMatch(Director director, String subjectIdNumber) {
    if (director == null || director.identityNumber() == null || subjectIdNumber == null) {
      return false;
    }

    String directorId = director.identityNumber().trim();
    String subjectId = subjectIdNumber.trim();

    return directorId.equalsIgnoreCase(subjectId);
  }

  /**
   * Masks an identity number for safe logging, showing only the last 4 characters.
   */
  private static String maskIdentityNumber(String identityNumber) {
    if (identityNumber == null || identityNumber.length() <= 4) {
      return "****";
    }
    return "****" + identityNumber.substring(identityNumber.length() - 4);
  }
}
