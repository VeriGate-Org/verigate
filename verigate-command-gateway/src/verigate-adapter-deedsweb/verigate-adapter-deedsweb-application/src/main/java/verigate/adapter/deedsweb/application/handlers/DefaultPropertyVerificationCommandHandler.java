/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.application.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.deedsweb.domain.handlers.PropertyVerificationCommandHandler;
import verigate.adapter.deedsweb.domain.models.PropertyDetails;
import verigate.adapter.deedsweb.domain.models.PropertySearchRequest;
import verigate.adapter.deedsweb.domain.services.PropertyOwnershipVerificationService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;
import verigate.verification.cg.domain.models.VerificationResult;

/**
 * Default implementation of property verification command handling.
 */
public class DefaultPropertyVerificationCommandHandler
    implements PropertyVerificationCommandHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DefaultPropertyVerificationCommandHandler.class);
  private static final Pattern ERF_PATTERN = Pattern.compile("(?i)erf\\s*(\\d+)");
  private static final Pattern PORTION_PATTERN = Pattern.compile("(?i)portion\\s*(\\d+)");

  private final PropertyOwnershipVerificationService propertyOwnershipVerificationService;
  private final ObjectMapper objectMapper;

  /**
   * Constructor.
   *
   * @param propertyOwnershipVerificationService property ownership search service
   */
  public DefaultPropertyVerificationCommandHandler(
      PropertyOwnershipVerificationService propertyOwnershipVerificationService) {
    this.propertyOwnershipVerificationService = propertyOwnershipVerificationService;
    this.objectMapper = new ObjectMapper();
  }

  @Override
  public Map<String, String> handle(VerifyPartyCommand command) {
    LOGGER.info("Processing property verification command for {}", maskSensitiveData(command));

    try {
      return performPropertySearch(command);
    } catch (TransientException e) {
      LOGGER.warn("Transient error during property verification", e);
      throw e;
    } catch (PermanentException e) {
      LOGGER.error("Permanent error during property verification", e);
      throw e;
    } catch (Exception e) {
      LOGGER.error("Unexpected error during property verification", e);
      throw new PermanentException("Unexpected error during property verification", e);
    }
  }

  @Override
  public CompletableFuture<VerificationResult> handleAsync(VerifyPartyCommand command) {
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            Map<String, String> resultMap = handle(command);
            return new VerificationResult(
                VerificationOutcome.valueOf(resultMap.get("outcome")),
                resultMap.get("failureReason"));
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
  }

  private Map<String, String> performPropertySearch(VerifyPartyCommand command)
      throws TransientException, PermanentException {
    String searchType = metadataValue(command, "searchType", "ownerId");
    String query =
        firstNonBlank(
            metadataValue(command, "query", null),
            metadataValue(command, "idNumber", null),
            metadataValue(command, "ownerIdNumber", null),
            metadataValue(command, "propertyDescription", null));
    String province = metadataValue(command, "province", null);
    String officeCode = metadataValue(command, "officeCode", null);

    if (query == null || query.isBlank()) {
      throw new PermanentException("Property search requires a query value");
    }

    PropertySearchRequest request =
        PropertySearchRequest.builder()
            .searchType(searchType)
            .query(query)
            .province(province)
            .officeCode(officeCode)
            .build();
    List<PropertyDetails> properties =
        propertyOwnershipVerificationService.searchProperties(request);

    PropertySearchResult payload = buildSearchPayload(searchType, query, province, properties);
    String payloadJson = serializePayload(payload);

    Map<String, String> result = new HashMap<>();
    result.put("outcome", VerificationOutcome.SUCCEEDED.toString());
    result.put("provider", "DeedsWeb");
    result.put("searchType", searchType);
    result.put("recordCount", String.valueOf(properties.size()));
    result.put("searchResultJson", payloadJson);
    if (properties.isEmpty()) {
      result.put("failureReason", "No matching properties found");
    }
    return result;
  }

  private PropertySearchResult buildSearchPayload(
      String searchType, String query, String province, List<PropertyDetails> properties) {
    List<PropertyItem> items = new ArrayList<>();
    int totalActiveBonds = 0;

    for (int index = 0; index < properties.size(); index++) {
      PropertyDetails property = properties.get(index);
      ParsedPropertyDescription parsedDescription = parseDescription(property.getPropertyDescription());

      List<BondItem> bonds = new ArrayList<>();
      if (property.hasMortgage()) {
        bonds.add(
            new BondItem(
                defaultString(property.getBondHolder(), "Unknown lender"),
                property.getBondAmount(),
                toIsoDate(property.getRegistrationDate())));
        totalActiveBonds += 1;
      }

      items.add(
          new PropertyItem(
              firstNonBlank(property.getDeedNumber(), property.getTitleDeedReference(), "property-" + index),
              parsedDescription.erfNumber(),
              parsedDescription.portion(),
              parsedDescription.township(),
              firstNonBlank(property.getProvince(), province, ""),
              firstNonBlank(property.getTitleDeedReference(), property.getDeedNumber(), ""),
              defaultString(property.getDeedNumber(), ""),
              toIsoDate(property.getRegistrationDate()),
              defaultString(property.getRegisteredOwnerName(), ""),
              defaultString(property.getRegisteredOwnerIdNumber(), ""),
              buildStreetAddress(property, parsedDescription),
              List.of(),
              bonds,
              new LastTransferItem(toIsoDate(property.getTransferDate()), property.getPurchasePrice()),
              new MunicipalItem(
                  firstNonBlank(property.getDeedNumber(), property.getTitleDeedReference(), ""),
                  0.0,
                  false)));
    }

    return new PropertySearchResult(
        new SummaryItem(items.size(), totalActiveBonds, 0),
        items,
        new CriteriaItem(searchType, query, province != null ? province : ""));
  }

  private String serializePayload(PropertySearchResult payload) throws PermanentException {
    try {
      return objectMapper.writeValueAsString(payload);
    } catch (Exception e) {
      throw new PermanentException("Failed to serialize property search response", e);
    }
  }

  private String metadataValue(VerifyPartyCommand command, String key, String defaultValue) {
    Object value = command.getMetadata() != null ? command.getMetadata().get(key) : null;
    return value != null ? value.toString() : defaultValue;
  }

  private String firstNonBlank(String... values) {
    for (String value : values) {
      if (value != null && !value.isBlank()) {
        return value;
      }
    }
    return null;
  }

  private String maskSensitiveData(VerifyPartyCommand command) {
    return "VerificationRequest[id="
        + command.getId()
        + ", type="
        + command.getVerificationType()
        + "]";
  }

  private ParsedPropertyDescription parseDescription(String description) {
    if (description == null || description.isBlank()) {
      return new ParsedPropertyDescription(0, 0, "");
    }

    int erfNumber = extractInt(description, ERF_PATTERN, 0);
    int portion = extractInt(description, PORTION_PATTERN, 0);
    String township = description;
    int commaIndex = description.indexOf(',');
    if (commaIndex > 0) {
      township = description.substring(0, commaIndex).trim();
    }

    return new ParsedPropertyDescription(erfNumber, portion, township);
  }

  private int extractInt(String value, Pattern pattern, int defaultValue) {
    Matcher matcher = pattern.matcher(value);
    if (matcher.find()) {
      try {
        return Integer.parseInt(matcher.group(1));
      } catch (NumberFormatException ignored) {
        return defaultValue;
      }
    }
    return defaultValue;
  }

  private String toIsoDate(LocalDate date) {
    return date != null ? date.toString() : null;
  }

  private String defaultString(String value, String fallback) {
    return value != null ? value : fallback;
  }

  private String buildStreetAddress(
      PropertyDetails property, ParsedPropertyDescription parsedDescription) {
    String township = firstNonBlank(parsedDescription.township(), property.getProvince(), "Property");
    int erfNumber = parsedDescription.erfNumber() > 0 ? parsedDescription.erfNumber() : 1;
    return erfNumber + " Registry Avenue, " + township;
  }

  private record PropertySearchResult(
      SummaryItem summary,
      List<PropertyItem> items,
      CriteriaItem criteria) {}

  private record SummaryItem(
      int totalProperties,
      int totalActiveBonds,
      int totalMunicipalFlags) {}

  private record CriteriaItem(
      String searchType,
      String query,
      String province) {}

  private record PropertyItem(
      String propertyId,
      int erfNumber,
      int portion,
      String township,
      String province,
      String titleDeed,
      String deedNumber,
      String registrationDate,
      String ownerName,
      String ownerIdNumber,
      String streetAddress,
      List<String> coOwners,
      List<BondItem> currentBonds,
      LastTransferItem lastTransfer,
      MunicipalItem municipal) {}

  private record BondItem(
      String bondholder,
      Double amount,
      String registered) {}

  private record LastTransferItem(
      String date,
      Double amount) {}

  private record MunicipalItem(
      String accountNumber,
      Double arrears,
      boolean ratesFlag) {}

  private record ParsedPropertyDescription(
      int erfNumber,
      int portion,
      String township) {}
}
