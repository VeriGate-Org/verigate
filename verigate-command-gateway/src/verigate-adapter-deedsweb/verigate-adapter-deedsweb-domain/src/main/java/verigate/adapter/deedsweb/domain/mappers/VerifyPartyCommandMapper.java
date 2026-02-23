/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.domain.mappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import verigate.adapter.deedsweb.domain.constants.DomainConstants;
import verigate.adapter.deedsweb.domain.models.EntityExample;
import verigate.adapter.deedsweb.domain.models.EntityMatchRequest;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Maps VeriGate VerifyPartyCommand to OpenSanctions EntityMatchRequest.
 */
public class VerifyPartyCommandMapper {

  /**
   * Maps a VerifyPartyCommand to an EntityMatchRequest for OpenSanctions API.
   *
   * @param command the VeriGate verification command
   * @return the OpenSanctions entity match request
   */
  public static EntityMatchRequest mapToEntityMatchRequest(VerifyPartyCommand command) {

    // Create entity example from command details
    EntityExample entityExample = createEntityExample(command);

    // Create queries map with the entity example
    Map<String, EntityExample> queries = new HashMap<>();
    queries.put("entity1", entityExample);

    return new EntityMatchRequest.Builder()
        .dataset(DomainConstants.DEFAULT_DATASET)
        .queries(queries)
        .limit(DomainConstants.DEFAULT_LIMIT)
        .threshold(DomainConstants.DEFAULT_THRESHOLD)
        .cutoff(DomainConstants.DEFAULT_CUTOFF)
        .algorithm(DomainConstants.DEFAULT_ALGORITHM)
        .topics(List.of(DomainConstants.SANCTIONS_TOPIC, DomainConstants.PEP_TOPIC))
        .build();
  }

  private static EntityExample createEntityExample(VerifyPartyCommand command) {
    Map<String, List<String>> properties = new HashMap<>();

    // Extract party details from command metadata
    String firstName = extractMetadata(command, "firstName");
    String lastName = extractMetadata(command, "lastName");

    // Map names
    if (!firstName.isEmpty() || !lastName.isEmpty()) {
      List<String> names = new ArrayList<>();
      if (!firstName.isEmpty() && !lastName.isEmpty()) {
        names.add(firstName + " " + lastName);
      }
      if (!firstName.isEmpty()) {
        names.add(firstName);
      }
      if (!lastName.isEmpty()) {
        names.add(lastName);
      }
      properties.put("name", names);
    }

    // Map birth date
    String dateOfBirth = extractMetadata(command, "dateOfBirth");
    if (!dateOfBirth.isEmpty()) {
      properties.put("birthDate", List.of(dateOfBirth));
    }

    // Map nationality/country
    String nationality = extractMetadata(command, "nationality");
    if (!nationality.isEmpty()) {
      properties.put("nationality", List.of(nationality));
    }

    // Map address if available
    String address = extractMetadata(command, "address");
    if (!address.isEmpty()) {
      properties.put("address", List.of(address));
    }

    // Map ID numbers if available
    String idNumber = extractMetadata(command, "idNumber");
    if (!idNumber.isEmpty()) {
      properties.put("idNumber", List.of(idNumber));
    }

    // Determine schema based on party type
    String schema = determineSchema(command);

    return new EntityExample.Builder()
        .id("query-entity")
        .schema(schema)
        .properties(properties)
        .build();
  }

  /**
   * Safely extracts metadata value as string.
   */
  private static String extractMetadata(VerifyPartyCommand command, String key) {
    Object value = command.getMetadata().get(key);
    return value != null ? value.toString() : "";
  }

  /**
   * Determines entity schema based on command metadata.
   */
  private static String determineSchema(VerifyPartyCommand command) {
    String entityTypeHint = extractMetadata(command, "entityType");

    if ("ORGANIZATION".equalsIgnoreCase(entityTypeHint)
        || "ORGANISATION".equalsIgnoreCase(entityTypeHint)) {
      return DomainConstants.ORGANIZATION_SCHEMA;
    } else if ("COMPANY".equalsIgnoreCase(entityTypeHint)) {
      return DomainConstants.COMPANY_SCHEMA;
    } else {
      return DomainConstants.PERSON_SCHEMA; // Default to Person
    }
  }
}
