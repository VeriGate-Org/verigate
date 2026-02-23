/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.factories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verigate.watchlist.domain.models.EntityType;
import verigate.watchlist.domain.models.ScreeningRequest;
import verigate.watchlist.domain.models.WatchlistScreeningAggregateRoot;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DefaultCreateWatchlistScreeningFactoryTest {

    private CreateWatchlistScreeningFactory factory;
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String DOB = "1980-01-01";
    private static final String COUNTRY = "US";
    private static final String COMPANY_NAME = "Acme Corp";
    private static final String COUNTRY_INC = "UK";
    private static final String PARTNER_ID = "test-partner";

    @BeforeEach
    void setUp() {
        factory = new DefaultCreateWatchlistScreeningFactory();
    }

    @Test
    void shouldCreatePersonScreeningRequest() {
        // When
        ScreeningRequest request = factory.createPersonScreeningRequest(
            FIRST_NAME, LAST_NAME, DOB, COUNTRY);
        
        // Then
        assertEquals(FIRST_NAME, request.firstName());
        assertEquals(LAST_NAME, request.lastName());
        assertEquals(DOB, request.dateOfBirth());
        assertEquals(COUNTRY, request.countryOfResidence());
        assertEquals(EntityType.PERSON, request.entityType());
        assertTrue(request.additionalFields().isEmpty());
    }

    @Test
    void shouldCreatePersonScreeningRequestWithAdditionalFields() {
        // Given
        Map<String, String> additionalFields = Map.of("occupation", "Engineer", "nationality", "Canadian");
        
        // When
        ScreeningRequest request = factory.createPersonScreeningRequest(
            FIRST_NAME, LAST_NAME, DOB, COUNTRY, additionalFields);
        
        // Then
        assertEquals(FIRST_NAME, request.firstName());
        assertEquals(LAST_NAME, request.lastName());
        assertEquals(DOB, request.dateOfBirth());
        assertEquals(COUNTRY, request.countryOfResidence());
        assertEquals(EntityType.PERSON, request.entityType());
        assertEquals(2, request.additionalFields().size());
        assertEquals("Engineer", request.additionalFields().get("occupation"));
        assertEquals("Canadian", request.additionalFields().get("nationality"));
    }

    @Test
    void shouldCreateCompanyScreeningRequest() {
        // When
        ScreeningRequest request = factory.createCompanyScreeningRequest(
            COMPANY_NAME, COUNTRY_INC);
        
        // Then
        assertNull(request.firstName());
        assertEquals(COMPANY_NAME, request.lastName());
        assertNull(request.dateOfBirth());
        assertEquals(COUNTRY_INC, request.countryOfResidence());
        assertEquals(EntityType.COMPANY, request.entityType());
        assertTrue(request.additionalFields().isEmpty());
    }

    @Test
    void shouldCreateCompanyScreeningRequestWithAdditionalFields() {
        // Given
        Map<String, String> additionalFields = Map.of("registrationNumber", "123456789", "industry", "Technology");
        
        // When
        ScreeningRequest request = factory.createCompanyScreeningRequest(
            COMPANY_NAME, COUNTRY_INC, additionalFields);
        
        // Then
        assertNull(request.firstName());
        assertEquals(COMPANY_NAME, request.lastName());
        assertNull(request.dateOfBirth());
        assertEquals(COUNTRY_INC, request.countryOfResidence());
        assertEquals(EntityType.COMPANY, request.entityType());
        assertEquals(2, request.additionalFields().size());
        assertEquals("123456789", request.additionalFields().get("registrationNumber"));
        assertEquals("Technology", request.additionalFields().get("industry"));
    }

    @Test
    void shouldCreateWatchlistScreening() {
        // Given
        UUID screeningId = UUID.randomUUID();
        ScreeningRequest request = factory.createPersonScreeningRequest(
            FIRST_NAME, LAST_NAME, DOB, COUNTRY);
        
        // When
        WatchlistScreeningAggregateRoot aggregateRoot = factory.createWatchlistScreening(
            screeningId, PARTNER_ID, request);
        
        // Then
        assertEquals(screeningId, aggregateRoot.getScreeningId());
        assertEquals(PARTNER_ID, aggregateRoot.getPartnerId());
        assertFalse(aggregateRoot.isCompleted());
    }
}
