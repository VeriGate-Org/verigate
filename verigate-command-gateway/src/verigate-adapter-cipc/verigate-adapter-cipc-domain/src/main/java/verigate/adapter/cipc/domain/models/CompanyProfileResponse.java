/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.domain.models;

/**
 * Represents the response from a CIPC company profile request.
 */
public record CompanyProfileResponse(
    CompanyProfile company,
    boolean found,
    String errorMessage
) {

  /**
   * Creates a successful response with company profile data.
   */
  public static CompanyProfileResponse success(CompanyProfile company) {
    return new CompanyProfileResponse(company, true, null);
  }

  /**
   * Creates a not found response.
   */
  public static CompanyProfileResponse notFound() {
    return new CompanyProfileResponse(null, false, "Company not found");
  }

  /**
   * Creates an error response with the specified error message.
   */
  public static CompanyProfileResponse error(String errorMessage) {
    return new CompanyProfileResponse(null, false, errorMessage);
  }

  /**
   * Checks if the response contains a valid company profile.
   */
  public boolean isSuccess() {
    return found && company != null;
  }

  /**
   * Checks if the response indicates an error occurred.
   */
  public boolean isError() {
    return !found && errorMessage != null;
  }
}
