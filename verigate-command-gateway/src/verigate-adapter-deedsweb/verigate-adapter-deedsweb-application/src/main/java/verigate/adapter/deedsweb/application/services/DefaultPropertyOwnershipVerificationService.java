/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.application.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.deedsweb.domain.models.EntityMatches;
import verigate.adapter.deedsweb.domain.models.EntityMatchResponse;
import verigate.adapter.deedsweb.domain.models.OwnershipVerificationResult;
import verigate.adapter.deedsweb.domain.models.PropertyDetails;
import verigate.adapter.deedsweb.domain.models.PropertyOwnershipCheck;
import verigate.adapter.deedsweb.domain.models.ScoredEntity;
import verigate.adapter.deedsweb.domain.services.DeedsWebMatchingService;
import verigate.adapter.deedsweb.domain.services.PropertyOwnershipVerificationService;

/**
 * Default implementation of the property ownership verification service.
 * Uses the DeedsWebMatchingService to search for property records and evaluates
 * ownership by matching subject identifiers against registered owner details.
 */
public class DefaultPropertyOwnershipVerificationService
    implements PropertyOwnershipVerificationService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DefaultPropertyOwnershipVerificationService.class);

  private static final String DEEDS_DATASET = "deeds";
  private static final int DEFAULT_SEARCH_LIMIT = 50;

  private final DeedsWebMatchingService deedsWebMatchingService;

  /**
   * Constructor.
   *
   * @param deedsWebMatchingService the DeedsWeb matching service for property lookups
   */
  public DefaultPropertyOwnershipVerificationService(
      DeedsWebMatchingService deedsWebMatchingService) {
    this.deedsWebMatchingService = deedsWebMatchingService;
  }

  @Override
  public PropertyOwnershipCheck verifyOwnership(
      String subjectIdNumber, String subjectName, String propertyDescription) {

    LOGGER.info("Starting property ownership verification for subject ID: {}",
        maskIdNumber(subjectIdNumber));

    List<PropertyDetails> properties = findPropertiesByOwner(subjectIdNumber);

    LOGGER.info("Found {} properties for subject ID: {}",
        properties.size(), maskIdNumber(subjectIdNumber));

    OwnershipVerificationResult result = checkOwnership(subjectIdNumber, subjectName, properties);

    LOGGER.info("Ownership verification completed. Confirmed: {}, Confidence: {}",
        result.isOwnershipConfirmed(), result.getMatchConfidence());

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
    LOGGER.debug("Searching DeedsWeb for properties by owner ID: {}",
        maskIdNumber(ownerIdNumber));

    try {
      EntityMatchResponse response = deedsWebMatchingService.searchEntities(
          DEEDS_DATASET, ownerIdNumber, DEFAULT_SEARCH_LIMIT);

      if (response == null || response.getResponses() == null
          || response.getResponses().isEmpty()) {
        LOGGER.debug("No property records found for owner ID: {}",
            maskIdNumber(ownerIdNumber));
        return Collections.emptyList();
      }

      List<PropertyDetails> properties = new ArrayList<>();
      for (Map.Entry<String, EntityMatches> entry : response.getResponses().entrySet()) {
        EntityMatches matches = entry.getValue();
        if (matches.getResults() != null) {
          for (ScoredEntity entity : matches.getResults()) {
            PropertyDetails details = mapScoredEntityToPropertyDetails(entity);
            if (details != null) {
              properties.add(details);
            }
          }
        }
      }

      LOGGER.debug("Mapped {} property records from DeedsWeb response", properties.size());
      return properties;

    } catch (Exception e) {
      LOGGER.error("Error searching DeedsWeb for properties by owner ID: {}",
          maskIdNumber(ownerIdNumber), e);
      return Collections.emptyList();
    }
  }

  @Override
  public OwnershipVerificationResult checkOwnership(
      String subjectIdNumber, String subjectName, List<PropertyDetails> properties) {

    LOGGER.debug("Evaluating ownership for subject '{}' across {} properties",
        subjectName, properties.size());

    if (properties == null || properties.isEmpty()) {
      LOGGER.debug("No properties to evaluate, returning not-found result");
      return OwnershipVerificationResult.notFound(subjectName, subjectIdNumber);
    }

    // Search for a property where the registered owner ID matches the subject ID
    for (PropertyDetails property : properties) {
      if (property.isRegisteredTo(subjectIdNumber)) {
        double confidence = calculateMatchConfidence(subjectName, property);

        LOGGER.debug("Ownership confirmed for deed {} with confidence {}",
            property.getDeedNumber(), confidence);

        return OwnershipVerificationResult.confirmed(
            confidence, property, subjectName, subjectIdNumber, properties.size());
      }
    }

    // No ID match found - return owner mismatch with the first property for reference
    PropertyDetails firstProperty = properties.get(0);
    LOGGER.debug("No ownership match found. Registered owner: '{}', Queried: '{}'",
        firstProperty.getRegisteredOwnerName(), subjectName);

    return OwnershipVerificationResult.ownerMismatch(
        firstProperty, subjectName, subjectIdNumber, properties.size());
  }

  /**
   * Maps a ScoredEntity from the DeedsWeb matching response to a PropertyDetails domain model.
   * Extracts known property fields from the entity's properties map.
   *
   * @param entity the scored entity from the DeedsWeb response
   * @return the mapped property details, or null if the entity has no usable data
   */
  private PropertyDetails mapScoredEntityToPropertyDetails(ScoredEntity entity) {
    if (entity == null || entity.getProperties() == null) {
      return null;
    }

    Map<String, List<Object>> props = entity.getProperties();

    PropertyDetails.Builder builder = new PropertyDetails.Builder()
        .deedNumber(extractStringProperty(props, "deedNumber"))
        .titleDeedReference(extractStringProperty(props, "titleDeedReference"))
        .propertyDescription(extractStringProperty(props, "propertyDescription"))
        .registrationDivision(extractStringProperty(props, "registrationDivision"))
        .province(extractStringProperty(props, "province"))
        .extent(extractStringProperty(props, "extent"))
        .registeredOwnerName(extractStringProperty(props, "registeredOwnerName"))
        .registeredOwnerIdNumber(extractStringProperty(props, "registeredOwnerIdNumber"))
        .registrationDate(extractDateProperty(props, "registrationDate"))
        .transferDate(extractDateProperty(props, "transferDate"))
        .purchasePrice(extractDoubleProperty(props, "purchasePrice"))
        .bondHolder(extractStringProperty(props, "bondHolder"))
        .bondAmount(extractDoubleProperty(props, "bondAmount"));

    // Fall back to entity caption for owner name if not in properties
    if (extractStringProperty(props, "registeredOwnerName") == null
        && entity.getCaption() != null) {
      builder.registeredOwnerName(entity.getCaption());
    }

    return builder.build();
  }

  /**
   * Calculates a match confidence score based on how well the subject name
   * matches the registered owner name. ID match is already confirmed at this point.
   *
   * @param subjectName the name of the person being verified
   * @param property the property details with registered owner information
   * @return a confidence score between 0.0 and 1.0
   */
  private double calculateMatchConfidence(String subjectName, PropertyDetails property) {
    // Base confidence from ID number match
    double confidence = 0.7;

    // Boost confidence if names also match
    if (subjectName != null && property.getRegisteredOwnerName() != null) {
      String normalizedSubject = subjectName.trim().toLowerCase();
      String normalizedOwner = property.getRegisteredOwnerName().trim().toLowerCase();

      if (normalizedSubject.equals(normalizedOwner)) {
        confidence = 1.0;
      } else if (normalizedOwner.contains(normalizedSubject)
          || normalizedSubject.contains(normalizedOwner)) {
        confidence = 0.9;
      } else {
        // Partial name matching - check if any name tokens overlap
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

  /**
   * Extracts a string value from the first element of a property list.
   */
  private String extractStringProperty(Map<String, List<Object>> properties, String key) {
    List<Object> values = properties.get(key);
    if (values != null && !values.isEmpty() && values.get(0) != null) {
      return values.get(0).toString();
    }
    return null;
  }

  /**
   * Extracts a LocalDate value from the first element of a property list.
   */
  private LocalDate extractDateProperty(Map<String, List<Object>> properties, String key) {
    String value = extractStringProperty(properties, key);
    if (value != null) {
      try {
        return LocalDate.parse(value);
      } catch (Exception e) {
        LOGGER.debug("Unable to parse date property '{}' with value '{}'", key, value);
        return null;
      }
    }
    return null;
  }

  /**
   * Extracts a Double value from the first element of a property list.
   */
  private Double extractDoubleProperty(Map<String, List<Object>> properties, String key) {
    List<Object> values = properties.get(key);
    if (values != null && !values.isEmpty() && values.get(0) != null) {
      Object value = values.get(0);
      if (value instanceof Number) {
        return ((Number) value).doubleValue();
      }
      try {
        return Double.parseDouble(value.toString());
      } catch (NumberFormatException e) {
        LOGGER.debug("Unable to parse double property '{}' with value '{}'", key, value);
        return null;
      }
    }
    return null;
  }

  /**
   * Masks an ID number for safe logging by showing only the first 4 and last 2 characters.
   */
  private String maskIdNumber(String idNumber) {
    if (idNumber == null || idNumber.length() <= 6) {
      return "****";
    }
    return idNumber.substring(0, 4) + "****" + idNumber.substring(idNumber.length() - 2);
  }
}
