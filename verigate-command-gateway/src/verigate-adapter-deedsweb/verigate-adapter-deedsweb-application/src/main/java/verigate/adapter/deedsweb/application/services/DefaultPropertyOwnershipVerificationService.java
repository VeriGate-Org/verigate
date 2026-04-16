/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.application.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.deedsweb.domain.models.OwnershipVerificationResult;
import verigate.adapter.deedsweb.domain.models.PropertyDetails;
import verigate.adapter.deedsweb.domain.models.PropertyOwnershipCheck;
import verigate.adapter.deedsweb.domain.models.PropertySearchRequest;
import verigate.adapter.deedsweb.domain.services.DeedsRegistryClient;
import verigate.adapter.deedsweb.domain.services.PropertyOwnershipVerificationService;

/**
 * Default implementation of the property ownership verification service. Dispatches to the
 * appropriate {@link DeedsRegistryClient} operation based on {@code searchType} and applies
 * province / search-type filters before returning. Ownership scoring (ID + name match) is
 * preserved verbatim from the previous REST-based implementation.
 */
public class DefaultPropertyOwnershipVerificationService
    implements PropertyOwnershipVerificationService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DefaultPropertyOwnershipVerificationService.class);

  private final DeedsRegistryClient registryClient;

  public DefaultPropertyOwnershipVerificationService(DeedsRegistryClient registryClient) {
    this.registryClient = registryClient;
  }

  @Override
  public List<PropertyDetails> searchProperties(PropertySearchRequest request) {
    if (request == null) {
      return Collections.emptyList();
    }
    String searchType = request.getSearchType();
    String query = request.getQuery();
    if (query == null || query.trim().isEmpty()) {
      return Collections.emptyList();
    }

    String sanitizedQuery = query.trim();
    String officeCode = request.getOfficeCode();
    String province = request.getProvince();

    LOGGER.debug(
        "Searching DeedsWeb: searchType={}, office={}, province={}",
        searchType,
        officeCode != null ? officeCode : "<all>",
        province != null ? province : "<any>");

    List<PropertyDetails> raw;
    try {
      raw = dispatch(searchType, sanitizedQuery, officeCode);
    } catch (TransientException | PermanentException e) {
      // Caller (handler) decides retry vs. hard-fail; surface as runtime so this preserves
      // the existing return-empty-list contract for soft errors.
      LOGGER.error(
          "Error searching DeedsWeb for searchType={}, query={}, office={}",
          searchType,
          sanitizedQuery,
          officeCode,
          e);
      throw new RuntimeException(e);
    }

    if (raw == null || raw.isEmpty()) {
      return Collections.emptyList();
    }

    List<PropertyDetails> provinceFiltered = filterByProvince(raw, province);
    if (provinceFiltered.isEmpty()) {
      provinceFiltered = raw;
    }

    List<PropertyDetails> typeFiltered =
        applySearchTypeFilter(provinceFiltered, searchType, sanitizedQuery);
    if (typeFiltered.isEmpty()) {
      return provinceFiltered;
    }
    return typeFiltered;
  }

  @Override
  public PropertyOwnershipCheck verifyOwnership(
      String subjectIdNumber, String subjectName, String propertyDescription) {

    LOGGER.info(
        "Starting property ownership verification for subject ID: {}",
        maskIdNumber(subjectIdNumber));

    List<PropertyDetails> properties = findPropertiesByOwner(subjectIdNumber);

    LOGGER.info(
        "Found {} properties for subject ID: {}",
        properties.size(),
        maskIdNumber(subjectIdNumber));

    OwnershipVerificationResult result = checkOwnership(subjectIdNumber, subjectName, properties);

    LOGGER.info(
        "Ownership verification completed. Confirmed: {}, Confidence: {}",
        result.isOwnershipConfirmed(),
        result.getMatchConfidence());

    return new PropertyOwnershipCheck.Builder()
        .subjectIdNumber(subjectIdNumber)
        .subjectName(subjectName)
        .propertyDescription(propertyDescription)
        .propertiesFound(properties)
        .result(result)
        .build();
  }

  @Override
  public List<PropertyDetails> findPropertiesByOwner(String ownerIdNumber) {
    LOGGER.debug("Searching DeedsWeb for properties by owner ID: {}", maskIdNumber(ownerIdNumber));
    return searchProperties(
        PropertySearchRequest.builder().searchType("ownerId").query(ownerIdNumber).build());
  }

  @Override
  public OwnershipVerificationResult checkOwnership(
      String subjectIdNumber, String subjectName, List<PropertyDetails> properties) {

    LOGGER.debug(
        "Evaluating ownership for subject '{}' across {} properties",
        subjectName,
        properties == null ? 0 : properties.size());

    if (properties == null || properties.isEmpty()) {
      LOGGER.debug("No properties to evaluate, returning not-found result");
      return OwnershipVerificationResult.notFound(subjectName, subjectIdNumber);
    }

    for (PropertyDetails property : properties) {
      if (property.isRegisteredTo(subjectIdNumber)) {
        double confidence = calculateMatchConfidence(subjectName, property);
        LOGGER.debug(
            "Ownership confirmed for deed {} with confidence {}",
            property.getDeedNumber(),
            confidence);
        return OwnershipVerificationResult.confirmed(
            confidence, property, subjectName, subjectIdNumber, properties.size());
      }
    }

    PropertyDetails firstProperty = properties.get(0);
    LOGGER.debug(
        "No ownership match found. Registered owner: '{}', Queried: '{}'",
        firstProperty.getRegisteredOwnerName(),
        subjectName);
    return OwnershipVerificationResult.ownerMismatch(
        firstProperty, subjectName, subjectIdNumber, properties.size());
  }

  // --------------------------------------------------------------------------------------
  // Dispatch by search type
  // --------------------------------------------------------------------------------------

  private List<PropertyDetails> dispatch(String searchType, String query, String officeCode)
      throws TransientException, PermanentException {
    String normalized =
        searchType == null || searchType.isBlank()
            ? "ownerid"
            : searchType.trim().toLowerCase(Locale.ROOT);
    return switch (normalized) {
      case "ownerid", "idnumber" ->
          registryClient.findPropertiesByIdNumber(query, officeCode);
      case "ownername" ->
          // No native "by owner name" SOAP op — fall back to ID-based lookup is impossible,
          // so return empty. The handler can short-circuit when search type is unsupported.
          Collections.emptyList();
      case "company", "companyname", "companynumber" ->
          registryClient.findPropertiesByCompany(query, query, officeCode);
      case "erf", "title", "property", "propertydetails" ->
          // Without township + portion + propertyTypeCode the only useful action is to
          // skip the call and let the handler report no results.
          Collections.emptyList();
      default -> registryClient.findPropertiesByIdNumber(query, officeCode);
    };
  }

  // --------------------------------------------------------------------------------------
  // Filtering helpers (preserved from previous implementation)
  // --------------------------------------------------------------------------------------

  private List<PropertyDetails> filterByProvince(List<PropertyDetails> properties, String province) {
    if (province == null || province.trim().isEmpty()) {
      return properties;
    }
    String normalizedProvince = province.trim().toLowerCase(Locale.ROOT);
    return properties.stream()
        .filter(
            property ->
                property.getProvince() != null
                    && property
                        .getProvince()
                        .trim()
                        .toLowerCase(Locale.ROOT)
                        .contains(normalizedProvince))
        .toList();
  }

  private List<PropertyDetails> applySearchTypeFilter(
      List<PropertyDetails> properties, String searchType, String query) {
    if (searchType == null || searchType.isBlank()) {
      return properties;
    }
    String normalizedSearchType = searchType.trim().toLowerCase(Locale.ROOT);
    String normalizedQuery = query.trim().toLowerCase(Locale.ROOT);

    return switch (normalizedSearchType) {
      case "ownerid", "idnumber" ->
          properties.stream()
              .filter(
                  property ->
                      property.getRegisteredOwnerIdNumber() != null
                          && property
                              .getRegisteredOwnerIdNumber()
                              .trim()
                              .equalsIgnoreCase(query.trim()))
              .toList();
      case "ownername" ->
          properties.stream()
              .filter(
                  property ->
                      containsIgnoreCase(property.getRegisteredOwnerName(), normalizedQuery)
                          || containsIgnoreCase(property.getPropertyDescription(), normalizedQuery))
              .toList();
      case "erf", "title", "property", "propertydetails" ->
          properties.stream()
              .filter(
                  property ->
                      containsIgnoreCase(property.getPropertyDescription(), normalizedQuery)
                          || containsIgnoreCase(property.getTitleDeedReference(), normalizedQuery)
                          || containsIgnoreCase(property.getDeedNumber(), normalizedQuery))
              .toList();
      default -> properties;
    };
  }

  private boolean containsIgnoreCase(String value, String normalizedQuery) {
    return value != null && value.trim().toLowerCase(Locale.ROOT).contains(normalizedQuery);
  }

  /**
   * Calculates a match confidence score based on how well the subject name matches the
   * registered owner name. ID match is already confirmed at this point.
   */
  private double calculateMatchConfidence(String subjectName, PropertyDetails property) {
    double confidence = 0.7;

    if (subjectName != null && property.getRegisteredOwnerName() != null) {
      String normalizedSubject = subjectName.trim().toLowerCase(Locale.ROOT);
      String normalizedOwner = property.getRegisteredOwnerName().trim().toLowerCase(Locale.ROOT);

      if (normalizedSubject.equals(normalizedOwner)) {
        confidence = 1.0;
      } else if (normalizedOwner.contains(normalizedSubject)
          || normalizedSubject.contains(normalizedOwner)) {
        confidence = 0.9;
      } else {
        String[] subjectTokens = normalizedSubject.split("\\s+");
        String[] ownerTokens = normalizedOwner.split("\\s+");
        int matchingTokens = 0;
        for (String subjectToken : subjectTokens) {
          for (String ownerToken : ownerTokens) {
            if (subjectToken.equals(ownerToken)) {
              matchingTokens++;
              break;
            }
          }
        }
        int maxTokens = Math.max(subjectTokens.length, ownerTokens.length);
        if (maxTokens > 0 && matchingTokens > 0) {
          double tokenRatio = (double) matchingTokens / maxTokens;
          confidence = 0.7 + (0.3 * tokenRatio);
        }
      }
    }

    return Math.min(confidence, 1.0);
  }

  private String maskIdNumber(String idNumber) {
    if (idNumber == null || idNumber.length() <= 6) {
      return "****";
    }
    return idNumber.substring(0, 4) + "****" + idNumber.substring(idNumber.length() - 2);
  }
}
