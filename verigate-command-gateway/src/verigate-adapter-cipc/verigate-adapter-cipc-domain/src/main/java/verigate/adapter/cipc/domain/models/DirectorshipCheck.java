/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.domain.models;

import java.util.Objects;

/**
 * Aggregate representing a complete directorship verification check.
 * Combines the company profile retrieval, directorship validation, and compliance
 * scoring into a single cohesive verification result.
 */
public class DirectorshipCheck {

  private final String enterpriseNumber;
  private final String subjectIdNumber;
  private final String subjectName;
  private final CompanyProfile companyProfile;
  private final DirectorshipVerificationResult result;
  private final CompanyComplianceScore complianceScore;

  /**
   * Constructs a directorship check with all verification components.
   *
   * @param enterpriseNumber the company registration number
   * @param subjectIdNumber the ID number of the person being checked
   * @param subjectName the full name of the person being checked
   * @param companyProfile the retrieved company profile
   * @param result the directorship verification result
   * @param complianceScore the company compliance score
   */
  public DirectorshipCheck(
      String enterpriseNumber,
      String subjectIdNumber,
      String subjectName,
      CompanyProfile companyProfile,
      DirectorshipVerificationResult result,
      CompanyComplianceScore complianceScore) {
    this.enterpriseNumber = Objects.requireNonNull(
        enterpriseNumber, "enterpriseNumber is required");
    this.subjectIdNumber = Objects.requireNonNull(subjectIdNumber, "subjectIdNumber is required");
    this.subjectName = Objects.requireNonNull(subjectName, "subjectName is required");
    this.companyProfile = companyProfile;
    this.result = result;
    this.complianceScore = complianceScore;
  }

  /**
   * Checks if the subject was found among the company directors.
   *
   * @return true if the subject is a director of the company
   */
  public boolean isSubjectDirector() {
    return result != null && result.directorFound();
  }

  /**
   * Checks if the subject is an active director of the company.
   *
   * @return true if the subject is an active director
   */
  public boolean isSubjectActiveDirector() {
    return result != null && result.directorFound() && result.directorActive();
  }

  /**
   * Finds the director record that matches the subject's identity number.
   *
   * @return the matching director, or null if no match was found
   */
  public Director findMatchingDirector() {
    if (companyProfile == null || companyProfile.directors() == null) {
      return null;
    }

    return companyProfile.directors().stream()
        .filter(director -> director.identityNumber() != null
            && director.identityNumber().trim().equalsIgnoreCase(subjectIdNumber.trim()))
        .findFirst()
        .orElse(null);
  }

  /**
   * Checks if the company passes all compliance criteria: the company is active,
   * has at least one active director, and the compliance score meets the threshold.
   *
   * @return true if the company is considered compliant
   */
  public boolean isCompanyCompliant() {
    if (companyProfile == null || complianceScore == null) {
      return false;
    }

    boolean companyActive = companyProfile.isActive();
    boolean hasActiveDirectors = !companyProfile.getActiveDirectors().isEmpty();
    boolean meetsThreshold = complianceScore.meetsComplianceThreshold();

    return companyActive && hasActiveDirectors && meetsThreshold;
  }

  public String getEnterpriseNumber() {
    return enterpriseNumber;
  }

  public String getSubjectIdNumber() {
    return subjectIdNumber;
  }

  public String getSubjectName() {
    return subjectName;
  }

  public CompanyProfile getCompanyProfile() {
    return companyProfile;
  }

  public DirectorshipVerificationResult getResult() {
    return result;
  }

  public CompanyComplianceScore getComplianceScore() {
    return complianceScore;
  }

  /**
   * Builder for convenient directorship check construction.
   */
  public static class Builder {
    private String enterpriseNumber;
    private String subjectIdNumber;
    private String subjectName;
    private CompanyProfile companyProfile;
    private DirectorshipVerificationResult result;
    private CompanyComplianceScore complianceScore;

    public Builder enterpriseNumber(String enterpriseNumber) {
      this.enterpriseNumber = enterpriseNumber;
      return this;
    }

    public Builder subjectIdNumber(String subjectIdNumber) {
      this.subjectIdNumber = subjectIdNumber;
      return this;
    }

    public Builder subjectName(String subjectName) {
      this.subjectName = subjectName;
      return this;
    }

    public Builder companyProfile(CompanyProfile companyProfile) {
      this.companyProfile = companyProfile;
      return this;
    }

    public Builder result(DirectorshipVerificationResult result) {
      this.result = result;
      return this;
    }

    public Builder complianceScore(CompanyComplianceScore complianceScore) {
      this.complianceScore = complianceScore;
      return this;
    }

    /**
     * Builds the immutable directorship check instance.
     */
    public DirectorshipCheck build() {
      return new DirectorshipCheck(
          enterpriseNumber,
          subjectIdNumber,
          subjectName,
          companyProfile,
          result,
          complianceScore);
    }
  }

  /**
   * Creates a new directorship check builder.
   */
  public static Builder builder() {
    return new Builder();
  }
}
