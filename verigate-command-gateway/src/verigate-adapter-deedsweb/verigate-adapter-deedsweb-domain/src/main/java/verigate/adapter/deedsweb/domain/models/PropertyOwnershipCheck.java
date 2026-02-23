/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.domain.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Aggregate representing a property ownership verification check.
 * Encapsulates the subject details, all properties found, and the verification result.
 */
public class PropertyOwnershipCheck {

  private final String subjectIdNumber;
  private final String subjectName;
  private final String propertyDescription;
  private final List<PropertyDetails> propertiesFound;
  private final OwnershipVerificationResult result;

  private PropertyOwnershipCheck(Builder builder) {
    this.subjectIdNumber = builder.subjectIdNumber;
    this.subjectName = builder.subjectName;
    this.propertyDescription = builder.propertyDescription;
    this.propertiesFound = builder.propertiesFound != null
        ? Collections.unmodifiableList(new ArrayList<>(builder.propertiesFound))
        : Collections.emptyList();
    this.result = builder.result;
  }

  // Getters
  public String getSubjectIdNumber() {
    return subjectIdNumber;
  }

  public String getSubjectName() {
    return subjectName;
  }

  public String getPropertyDescription() {
    return propertyDescription;
  }

  public List<PropertyDetails> getPropertiesFound() {
    return propertiesFound;
  }

  public OwnershipVerificationResult getResult() {
    return result;
  }

  /**
   * Checks whether ownership was verified successfully.
   *
   * @return true if the verification result confirms ownership
   */
  public boolean isOwnershipVerified() {
    return result != null && result.isOwnershipConfirmed();
  }

  /**
   * Returns the total count of properties found for the subject.
   *
   * @return the number of properties found
   */
  public int getPropertyCount() {
    return propertiesFound.size();
  }

  /**
   * Returns all properties that have a mortgage (bond) registered against them.
   *
   * @return a list of properties with active mortgages
   */
  public List<PropertyDetails> getMortgagedProperties() {
    return propertiesFound.stream()
        .filter(PropertyDetails::hasMortgage)
        .collect(Collectors.toList());
  }

  /**
   * Finds a specific property by its deed number.
   *
   * @param deedNumber the deed registration number to search for
   * @return an Optional containing the property details if found
   */
  public Optional<PropertyDetails> findProperty(String deedNumber) {
    if (deedNumber == null) {
      return Optional.empty();
    }
    return propertiesFound.stream()
        .filter(p -> deedNumber.equals(p.getDeedNumber()))
        .findFirst();
  }

  /**
   * Builder for PropertyOwnershipCheck.
   */
  public static class Builder {
    private String subjectIdNumber;
    private String subjectName;
    private String propertyDescription;
    private List<PropertyDetails> propertiesFound;
    private OwnershipVerificationResult result;

    public Builder subjectIdNumber(String subjectIdNumber) {
      this.subjectIdNumber = subjectIdNumber;
      return this;
    }

    public Builder subjectName(String subjectName) {
      this.subjectName = subjectName;
      return this;
    }

    public Builder propertyDescription(String propertyDescription) {
      this.propertyDescription = propertyDescription;
      return this;
    }

    public Builder propertiesFound(List<PropertyDetails> propertiesFound) {
      this.propertiesFound = propertiesFound;
      return this;
    }

    public Builder result(OwnershipVerificationResult result) {
      this.result = result;
      return this;
    }

    public PropertyOwnershipCheck build() {
      return new PropertyOwnershipCheck(this);
    }
  }
}
