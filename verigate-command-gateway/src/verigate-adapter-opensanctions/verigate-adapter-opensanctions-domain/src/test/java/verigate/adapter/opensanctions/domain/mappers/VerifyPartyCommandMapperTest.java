/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.domain.mappers;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import verigate.adapter.opensanctions.domain.constants.DomainConstants;
import verigate.adapter.opensanctions.domain.models.EntityMatchRequest;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationType;

class VerifyPartyCommandMapperTest {

    @Test
    void mapToEntityMatchRequest_personWithAllFields_mapsCorrectly() {
        // Arrange
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("firstName", "John");
        metadata.put("lastName", "Doe");
        metadata.put("dateOfBirth", "1985-06-15");
        metadata.put("nationality", "South African");
        metadata.put("address", "123 Main Street, Johannesburg");
        metadata.put("idNumber", "8506155009087");

        VerifyPartyCommand command = createCommand(metadata);

        // Act
        EntityMatchRequest result = VerifyPartyCommandMapper.mapToEntityMatchRequest(command);

        // Assert
        assertNotNull(result);
        assertEquals(DomainConstants.DEFAULT_DATASET, result.getDataset());
        assertEquals(DomainConstants.DEFAULT_LIMIT, result.getLimit());
        assertEquals(DomainConstants.DEFAULT_THRESHOLD, result.getThreshold());
        assertEquals(DomainConstants.DEFAULT_CUTOFF, result.getCutoff());
        assertEquals(DomainConstants.DEFAULT_ALGORITHM, result.getAlgorithm());
        assertEquals(List.of(DomainConstants.SANCTIONS_TOPIC, DomainConstants.PEP_TOPIC), result.getTopics());

        // Verify entity example
        assertNotNull(result.getQueries());
        assertTrue(result.getQueries().containsKey("entity1"));

        var entity = result.getQueries().get("entity1");
        assertEquals(DomainConstants.PERSON_SCHEMA, entity.getSchema());
        assertEquals("query-entity", entity.getId());

        // Verify properties
        var props = entity.getProperties();
        assertTrue(props.get("name").contains("John Doe"));
        assertTrue(props.get("name").contains("John"));
        assertTrue(props.get("name").contains("Doe"));
        assertEquals(List.of("1985-06-15"), props.get("birthDate"));
        assertEquals(List.of("South African"), props.get("nationality"));
        assertEquals(List.of("123 Main Street, Johannesburg"), props.get("address"));
        assertEquals(List.of("8506155009087"), props.get("idNumber"));
    }

    @Test
    void mapToEntityMatchRequest_personMinimalFields_mapsCorrectly() {
        // Arrange
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("firstName", "Jane");
        metadata.put("lastName", "Smith");

        VerifyPartyCommand command = createCommand(metadata);

        // Act
        EntityMatchRequest result = VerifyPartyCommandMapper.mapToEntityMatchRequest(command);

        // Assert
        var entity = result.getQueries().get("entity1");
        assertEquals(DomainConstants.PERSON_SCHEMA, entity.getSchema());

        var props = entity.getProperties();
        assertTrue(props.get("name").contains("Jane Smith"));
        assertNull(props.get("birthDate"));
        assertNull(props.get("nationality"));
        assertNull(props.get("address"));
        assertNull(props.get("idNumber"));
    }

    @Test
    void mapToEntityMatchRequest_firstNameOnly_mapsNameWithoutFullCombination() {
        // Arrange
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("firstName", "Alice");

        VerifyPartyCommand command = createCommand(metadata);

        // Act
        EntityMatchRequest result = VerifyPartyCommandMapper.mapToEntityMatchRequest(command);

        // Assert
        var entity = result.getQueries().get("entity1");
        var names = entity.getProperties().get("name");
        assertEquals(1, names.size());
        assertEquals("Alice", names.get(0));
    }

    @Test
    void mapToEntityMatchRequest_lastNameOnly_mapsNameWithoutFullCombination() {
        // Arrange
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("lastName", "Williams");

        VerifyPartyCommand command = createCommand(metadata);

        // Act
        EntityMatchRequest result = VerifyPartyCommandMapper.mapToEntityMatchRequest(command);

        // Assert
        var entity = result.getQueries().get("entity1");
        var names = entity.getProperties().get("name");
        assertEquals(1, names.size());
        assertEquals("Williams", names.get(0));
    }

    @Test
    void mapToEntityMatchRequest_companyEntityType_usesCompanySchema() {
        // Arrange
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("firstName", "Acme");
        metadata.put("lastName", "Corp");
        metadata.put("entityType", "COMPANY");

        VerifyPartyCommand command = createCommand(metadata);

        // Act
        EntityMatchRequest result = VerifyPartyCommandMapper.mapToEntityMatchRequest(command);

        // Assert
        var entity = result.getQueries().get("entity1");
        assertEquals(DomainConstants.COMPANY_SCHEMA, entity.getSchema());
    }

    @Test
    void mapToEntityMatchRequest_organizationEntityType_usesOrganizationSchema() {
        // Arrange
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("firstName", "United");
        metadata.put("lastName", "Nations");
        metadata.put("entityType", "ORGANIZATION");

        VerifyPartyCommand command = createCommand(metadata);

        // Act
        EntityMatchRequest result = VerifyPartyCommandMapper.mapToEntityMatchRequest(command);

        // Assert
        var entity = result.getQueries().get("entity1");
        assertEquals(DomainConstants.ORGANIZATION_SCHEMA, entity.getSchema());
    }

    @Test
    void mapToEntityMatchRequest_organisationEntityType_usesOrganizationSchema() {
        // Arrange - British spelling variant
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("firstName", "Red");
        metadata.put("lastName", "Cross");
        metadata.put("entityType", "ORGANISATION");

        VerifyPartyCommand command = createCommand(metadata);

        // Act
        EntityMatchRequest result = VerifyPartyCommandMapper.mapToEntityMatchRequest(command);

        // Assert
        var entity = result.getQueries().get("entity1");
        assertEquals(DomainConstants.ORGANIZATION_SCHEMA, entity.getSchema());
    }

    @Test
    void mapToEntityMatchRequest_vesselEntityType_usesVesselSchema() {
        // Arrange
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("firstName", "MV");
        metadata.put("lastName", "Serenity");
        metadata.put("entityType", "VESSEL");

        VerifyPartyCommand command = createCommand(metadata);

        // Act
        EntityMatchRequest result = VerifyPartyCommandMapper.mapToEntityMatchRequest(command);

        // Assert
        var entity = result.getQueries().get("entity1");
        assertEquals(DomainConstants.VESSEL_SCHEMA, entity.getSchema());
    }

    @Test
    void mapToEntityMatchRequest_noEntityType_defaultsToPerson() {
        // Arrange
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("firstName", "Bob");
        metadata.put("lastName", "Jones");

        VerifyPartyCommand command = createCommand(metadata);

        // Act
        EntityMatchRequest result = VerifyPartyCommandMapper.mapToEntityMatchRequest(command);

        // Assert
        var entity = result.getQueries().get("entity1");
        assertEquals(DomainConstants.PERSON_SCHEMA, entity.getSchema());
    }

    @Test
    void mapToEntityMatchRequest_unknownEntityType_defaultsToPerson() {
        // Arrange
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("firstName", "Bob");
        metadata.put("lastName", "Jones");
        metadata.put("entityType", "UNKNOWN_TYPE");

        VerifyPartyCommand command = createCommand(metadata);

        // Act
        EntityMatchRequest result = VerifyPartyCommandMapper.mapToEntityMatchRequest(command);

        // Assert
        var entity = result.getQueries().get("entity1");
        assertEquals(DomainConstants.PERSON_SCHEMA, entity.getSchema());
    }

    @Test
    void mapToEntityMatchRequest_entityTypeCaseInsensitive_resolves() {
        // Arrange
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("firstName", "Test");
        metadata.put("lastName", "Entity");
        metadata.put("entityType", "company");

        VerifyPartyCommand command = createCommand(metadata);

        // Act
        EntityMatchRequest result = VerifyPartyCommandMapper.mapToEntityMatchRequest(command);

        // Assert
        var entity = result.getQueries().get("entity1");
        assertEquals(DomainConstants.COMPANY_SCHEMA, entity.getSchema());
    }

    @Test
    void mapToEntityMatchRequest_emptyMetadata_noNameProperties() {
        // Arrange
        Map<String, Object> metadata = new HashMap<>();
        VerifyPartyCommand command = createCommand(metadata);

        // Act
        EntityMatchRequest result = VerifyPartyCommandMapper.mapToEntityMatchRequest(command);

        // Assert
        var entity = result.getQueries().get("entity1");
        assertNull(entity.getProperties().get("name"));
    }

    @Test
    void mapToEntityMatchRequest_nullMetadataValues_treatedAsEmpty() {
        // Arrange
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("firstName", null);
        metadata.put("lastName", null);

        VerifyPartyCommand command = createCommand(metadata);

        // Act
        EntityMatchRequest result = VerifyPartyCommandMapper.mapToEntityMatchRequest(command);

        // Assert
        var entity = result.getQueries().get("entity1");
        assertNull(entity.getProperties().get("name"));
    }

    private VerifyPartyCommand createCommand(Map<String, Object> metadata) {
        return new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            VerificationType.SANCTIONS_SCREENING,
            null,
            metadata
        );
    }
}
