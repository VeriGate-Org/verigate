/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.domain.services;

import verigate.adapter.cipc.domain.models.CompanyComplianceScore;
import verigate.adapter.cipc.domain.models.CompanyProfile;
import verigate.adapter.cipc.domain.models.DirectorshipCheck;

/**
 * Service interface for company verification operations against CIPC data.
 * Provides methods for verifying directorship and assessing company compliance.
 */
public interface CompanyVerificationService {

  /**
   * Performs a full directorship verification by retrieving the company profile from CIPC,
   * validating whether the subject is a director, and calculating compliance scores.
   *
   * @param enterpriseNumber the CIPC enterprise registration number
   * @param subjectIdNumber the identity number of the person being verified
   * @param subjectName the full name of the person being verified
   * @return a directorship check aggregate with full verification results
   */
  DirectorshipCheck verifyDirectorship(
      String enterpriseNumber,
      String subjectIdNumber,
      String subjectName);

  /**
   * Assesses the compliance score of a company based on its profile data.
   * Evaluates factors such as company standing, active directors, registered address,
   * auditor assignment, and tax registration.
   *
   * @param profile the company profile to assess
   * @return the calculated compliance score
   */
  CompanyComplianceScore assessCompliance(CompanyProfile profile);
}
