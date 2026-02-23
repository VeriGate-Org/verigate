/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.cipc.domain.models.CompanyComplianceScore;
import verigate.adapter.cipc.domain.models.CompanyProfile;
import verigate.adapter.cipc.domain.models.CompanyProfileRequest;
import verigate.adapter.cipc.domain.models.CompanyProfileResponse;
import verigate.adapter.cipc.domain.models.DirectorshipCheck;
import verigate.adapter.cipc.domain.models.DirectorshipVerificationResult;
import verigate.adapter.cipc.domain.services.CipcCompanyService;
import verigate.adapter.cipc.domain.services.CompanyVerificationService;
import verigate.adapter.cipc.domain.services.DirectorshipValidationService;

/**
 * Default implementation of {@link CompanyVerificationService}.
 * Orchestrates the full directorship verification workflow by fetching the company profile,
 * validating directorship, and calculating compliance scores.
 */
public class DefaultCompanyVerificationService implements CompanyVerificationService {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultCompanyVerificationService.class);

  private final CipcCompanyService companyService;
  private final DirectorshipValidationService directorshipValidationService;

  /**
   * Constructs the company verification service with required dependencies.
   *
   * @param companyService the CIPC company service for profile retrieval
   * @param directorshipValidationService the service for directorship validation
   */
  public DefaultCompanyVerificationService(
      CipcCompanyService companyService,
      DirectorshipValidationService directorshipValidationService) {
    this.companyService = companyService;
    this.directorshipValidationService = directorshipValidationService;
  }

  /**
   * Performs a full directorship verification by retrieving the company profile,
   * validating directorship, and calculating the compliance score.
   *
   * @param enterpriseNumber the CIPC enterprise registration number
   * @param subjectIdNumber the identity number of the person being verified
   * @param subjectName the full name of the person being verified
   * @return a directorship check aggregate with complete verification results
   */
  @Override
  public DirectorshipCheck verifyDirectorship(
      String enterpriseNumber,
      String subjectIdNumber,
      String subjectName) {

    logger.info(
        "Starting directorship verification for enterprise {} and subject {}",
        enterpriseNumber,
        subjectName);

    CompanyProfileRequest profileRequest = CompanyProfileRequest.builder()
        .enterpriseNumber(enterpriseNumber)
        .build();

    logger.info("Fetching company profile for enterprise: {}", enterpriseNumber);
    CompanyProfileResponse profileResponse = companyService.getCompanyProfile(profileRequest);

    if (!profileResponse.isSuccess()) {
      logger.warn(
          "Company profile retrieval failed for enterprise {}: {}",
          enterpriseNumber,
          profileResponse.errorMessage());

      return DirectorshipCheck.builder()
          .enterpriseNumber(enterpriseNumber)
          .subjectIdNumber(subjectIdNumber)
          .subjectName(subjectName)
          .companyProfile(null)
          .result(DirectorshipVerificationResult.directorNotFound(
              subjectIdNumber, enterpriseNumber))
          .complianceScore(null)
          .build();
    }

    CompanyProfile companyProfile = profileResponse.company();
    logger.info(
        "Company profile retrieved successfully for enterprise: {} ({})",
        enterpriseNumber,
        companyProfile.getDisplayName());

    DirectorshipVerificationResult verificationResult =
        directorshipValidationService.validateDirectorship(companyProfile, subjectIdNumber);

    logger.info(
        "Directorship validation completed for enterprise {}: found={}, active={}",
        enterpriseNumber,
        verificationResult.directorFound(),
        verificationResult.directorActive());

    CompanyComplianceScore complianceScore = assessCompliance(companyProfile);

    logger.info(
        "Compliance assessment completed for enterprise {}: score={}, threshold={}",
        enterpriseNumber,
        String.format("%.0f%%", complianceScore.overallScore() * 100),
        complianceScore.meetsComplianceThreshold() ? "met" : "not met");

    DirectorshipCheck check = DirectorshipCheck.builder()
        .enterpriseNumber(enterpriseNumber)
        .subjectIdNumber(subjectIdNumber)
        .subjectName(subjectName)
        .companyProfile(companyProfile)
        .result(verificationResult)
        .complianceScore(complianceScore)
        .build();

    logger.info(
        "Directorship verification completed for enterprise {}: "
            + "subjectIsDirector={}, subjectIsActive={}, companyCompliant={}",
        enterpriseNumber,
        check.isSubjectDirector(),
        check.isSubjectActiveDirector(),
        check.isCompanyCompliant());

    return check;
  }

  /**
   * Assesses the compliance score of a company based on its profile data.
   *
   * @param profile the company profile to assess
   * @return the calculated compliance score
   */
  @Override
  public CompanyComplianceScore assessCompliance(CompanyProfile profile) {
    logger.info(
        "Assessing compliance for enterprise: {}",
        profile.enterpriseNumber());

    CompanyComplianceScore score = CompanyComplianceScore.calculate(profile);

    logger.info(
        "Compliance assessment for enterprise {}: {}",
        profile.enterpriseNumber(),
        score.complianceSummary());

    return score;
  }
}
