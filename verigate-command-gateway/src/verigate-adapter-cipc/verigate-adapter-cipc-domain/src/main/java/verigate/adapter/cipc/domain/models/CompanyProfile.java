/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.domain.models;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a complete company profile from CIPC.
 */
public record CompanyProfile(
    String enterpriseNumber,
    String enterpriseName,
    String enterpriseShortName,
    String tradingName,
    String translatedName,
    String enterpriseTypeDescription,
    String businessActivity,
    LocalDate registrationDate,
    LocalDate businessStartDate,
    CompanyStatus enterpriseStatus,
    String financialYearEnd,
    LocalDate financialYearEffectiveDate,
    String taxNumber,
    Address officeAddress,
    Address postalAddress,
    List<Director> directors,
    List<Secretary> secretaries,
    List<Auditor> auditors,
    List<Capital> capital,
    List<ChangeHistory> history) {

  /**
   * Builder for convenient company profile construction.
   */
  public static class Builder {
    private String enterpriseNumber;
    private String enterpriseName;
    private String enterpriseShortName;
    private String tradingName;
    private String translatedName;
    private String enterpriseTypeDescription;
    private String businessActivity;
    private LocalDate registrationDate;
    private LocalDate businessStartDate;
    private CompanyStatus enterpriseStatus;
    private String financialYearEnd;
    private LocalDate financialYearEffectiveDate;
    private String taxNumber;
    private Address officeAddress;
    private Address postalAddress;
    private List<Director> directors = List.of();
    private List<Secretary> secretaries = List.of();
    private List<Auditor> auditors = List.of();
    private List<Capital> capital = List.of();
    private List<ChangeHistory> history = List.of();

    public Builder enterpriseNumber(String enterpriseNumber) {
      this.enterpriseNumber = enterpriseNumber;
      return this;
    }

    public Builder enterpriseName(String enterpriseName) {
      this.enterpriseName = enterpriseName;
      return this;
    }

    public Builder enterpriseShortName(String enterpriseShortName) {
      this.enterpriseShortName = enterpriseShortName;
      return this;
    }

    public Builder tradingName(String tradingName) {
      this.tradingName = tradingName;
      return this;
    }

    public Builder translatedName(String translatedName) {
      this.translatedName = translatedName;
      return this;
    }

    public Builder enterpriseTypeDescription(String enterpriseTypeDescription) {
      this.enterpriseTypeDescription = enterpriseTypeDescription;
      return this;
    }

    public Builder businessActivity(String businessActivity) {
      this.businessActivity = businessActivity;
      return this;
    }

    public Builder registrationDate(LocalDate registrationDate) {
      this.registrationDate = registrationDate;
      return this;
    }

    public Builder businessStartDate(LocalDate businessStartDate) {
      this.businessStartDate = businessStartDate;
      return this;
    }

    public Builder enterpriseStatus(CompanyStatus enterpriseStatus) {
      this.enterpriseStatus = enterpriseStatus;
      return this;
    }

    public Builder financialYearEnd(String financialYearEnd) {
      this.financialYearEnd = financialYearEnd;
      return this;
    }

    public Builder financialYearEffectiveDate(LocalDate financialYearEffectiveDate) {
      this.financialYearEffectiveDate = financialYearEffectiveDate;
      return this;
    }

    public Builder taxNumber(String taxNumber) {
      this.taxNumber = taxNumber;
      return this;
    }

    public Builder officeAddress(Address officeAddress) {
      this.officeAddress = officeAddress;
      return this;
    }

    public Builder postalAddress(Address postalAddress) {
      this.postalAddress = postalAddress;
      return this;
    }

    public Builder directors(List<Director> directors) {
      this.directors = directors != null ? directors : List.of();
      return this;
    }

    public Builder secretaries(List<Secretary> secretaries) {
      this.secretaries = secretaries != null ? secretaries : List.of();
      return this;
    }

    public Builder auditors(List<Auditor> auditors) {
      this.auditors = auditors != null ? auditors : List.of();
      return this;
    }

    public Builder capital(List<Capital> capital) {
      this.capital = capital != null ? capital : List.of();
      return this;
    }

    public Builder history(List<ChangeHistory> history) {
      this.history = history != null ? history : List.of();
      return this;
    }

    /**
     * Builds the immutable company profile instance.
     */
    public CompanyProfile build() {
      return new CompanyProfile(
          enterpriseNumber,
          enterpriseName,
          enterpriseShortName,
          tradingName,
          translatedName,
          enterpriseTypeDescription,
          businessActivity,
          registrationDate,
          businessStartDate,
          enterpriseStatus,
          financialYearEnd,
          financialYearEffectiveDate,
          taxNumber,
          officeAddress,
          postalAddress,
          directors,
          secretaries,
          auditors,
          capital,
          history);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Checks if the company is currently active.
   */
  public boolean isActive() {
    return enterpriseStatus == CompanyStatus.ACTIVE
        || enterpriseStatus == CompanyStatus.IN_BUSINESS;
  }

  /**
   * Gets the list of active directors.
   */
  public List<Director> getActiveDirectors() {
    return directors.stream().filter(Director::isActive).toList();
  }

  /**
   * Gets the company name to use for display (prefers trading name over enterprise name).
   */
  public String getDisplayName() {
    if (tradingName != null && !tradingName.trim().isEmpty()) {
      return tradingName.trim();
    }
    return enterpriseName != null ? enterpriseName.trim() : "";
  }
}
