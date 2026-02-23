/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.domain.models;

/**
 * Represents the result of a property ownership verification against DeedsWeb records.
 * Contains the outcome of the verification, match confidence, and details about
 * both the queried and registered ownership information.
 */
public class OwnershipVerificationResult {

  private final boolean ownershipConfirmed;
  private final double matchConfidence;
  private final String registeredOwnerName;
  private final String queriedOwnerName;
  private final String registeredOwnerIdNumber;
  private final String queriedIdNumber;
  private final PropertyDetails propertyDetails;
  private final String failureReason;
  private final int totalPropertiesFound;

  private OwnershipVerificationResult(
      boolean ownershipConfirmed,
      double matchConfidence,
      String registeredOwnerName,
      String queriedOwnerName,
      String registeredOwnerIdNumber,
      String queriedIdNumber,
      PropertyDetails propertyDetails,
      String failureReason,
      int totalPropertiesFound) {
    this.ownershipConfirmed = ownershipConfirmed;
    this.matchConfidence = matchConfidence;
    this.registeredOwnerName = registeredOwnerName;
    this.queriedOwnerName = queriedOwnerName;
    this.registeredOwnerIdNumber = registeredOwnerIdNumber;
    this.queriedIdNumber = queriedIdNumber;
    this.propertyDetails = propertyDetails;
    this.failureReason = failureReason;
    this.totalPropertiesFound = totalPropertiesFound;
  }

  /**
   * Creates a confirmed ownership verification result.
   *
   * @param confidence the match confidence score between 0.0 and 1.0
   * @param details the property details from the deed records
   * @param queriedName the name that was queried
   * @param queriedId the ID number that was queried
   * @param totalProperties the total number of properties found for the individual
   * @return a confirmed ownership verification result
   */
  public static OwnershipVerificationResult confirmed(
      double confidence,
      PropertyDetails details,
      String queriedName,
      String queriedId,
      int totalProperties) {
    return new OwnershipVerificationResult(
        true,
        confidence,
        details.getRegisteredOwnerName(),
        queriedName,
        details.getRegisteredOwnerIdNumber(),
        queriedId,
        details,
        null,
        totalProperties);
  }

  /**
   * Creates a not-found ownership verification result when no properties
   * were found for the queried individual.
   *
   * @param queriedName the name that was queried
   * @param queriedId the ID number that was queried
   * @return a not-found ownership verification result
   */
  public static OwnershipVerificationResult notFound(String queriedName, String queriedId) {
    return new OwnershipVerificationResult(
        false,
        0.0,
        null,
        queriedName,
        null,
        queriedId,
        null,
        "No properties found registered to the queried individual",
        0);
  }

  /**
   * Creates an owner-mismatch verification result when properties were found
   * but the registered owner does not match the queried individual.
   *
   * @param details the property details from the deed records
   * @param queriedName the name that was queried
   * @param queriedId the ID number that was queried
   * @param totalProperties the total number of properties found
   * @return an owner-mismatch ownership verification result
   */
  public static OwnershipVerificationResult ownerMismatch(
      PropertyDetails details,
      String queriedName,
      String queriedId,
      int totalProperties) {
    return new OwnershipVerificationResult(
        false,
        0.0,
        details.getRegisteredOwnerName(),
        queriedName,
        details.getRegisteredOwnerIdNumber(),
        queriedId,
        details,
        "Registered owner does not match the queried individual",
        totalProperties);
  }

  // Getters
  public boolean isOwnershipConfirmed() {
    return ownershipConfirmed;
  }

  public double getMatchConfidence() {
    return matchConfidence;
  }

  public String getRegisteredOwnerName() {
    return registeredOwnerName;
  }

  public String getQueriedOwnerName() {
    return queriedOwnerName;
  }

  public String getRegisteredOwnerIdNumber() {
    return registeredOwnerIdNumber;
  }

  public String getQueriedIdNumber() {
    return queriedIdNumber;
  }

  public PropertyDetails getPropertyDetails() {
    return propertyDetails;
  }

  public String getFailureReason() {
    return failureReason;
  }

  public int getTotalPropertiesFound() {
    return totalPropertiesFound;
  }
}
