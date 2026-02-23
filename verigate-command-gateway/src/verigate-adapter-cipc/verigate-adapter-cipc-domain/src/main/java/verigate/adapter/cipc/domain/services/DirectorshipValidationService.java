/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.domain.services;

import verigate.adapter.cipc.domain.models.CompanyProfile;
import verigate.adapter.cipc.domain.models.Director;
import verigate.adapter.cipc.domain.models.DirectorshipVerificationResult;

/**
 * Service interface for validating directorship status within a company profile.
 * Provides methods to match a subject's identity against the company's director records
 * and determine their current status.
 */
public interface DirectorshipValidationService {

  /**
   * Validates whether a subject identified by their identity number is a director
   * of the given company, and returns detailed verification results including
   * the director's status and appointment details.
   *
   * @param profile the company profile containing director records
   * @param subjectIdNumber the identity number of the person to validate
   * @return the directorship verification result
   */
  DirectorshipVerificationResult validateDirectorship(
      CompanyProfile profile,
      String subjectIdNumber);

  /**
   * Checks if a specific director record matches the given identity number.
   *
   * @param director the director record to compare
   * @param subjectIdNumber the identity number to match against
   * @return true if the director's identity number matches
   */
  boolean isDirectorMatch(Director director, String subjectIdNumber);
}
