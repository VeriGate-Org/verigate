/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.commands;

import org.junit.jupiter.api.Test;
import verigate.watchlist.domain.models.EntityType;
import verigate.watchlist.domain.models.ScreeningRequest;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CreateWatchlistScreeningTest {

    private static final String PARTNER_ID = "test-partner";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String DOB = "1980-01-01";
    private static final String COUNTRY = "US";

    @Test
    void shouldCreateCommandWithAllRequiredFields() {
        // Given
        ScreeningRequest request = new ScreeningRequest(
            FIRST_NAME, LAST_NAME, DOB, COUNTRY, EntityType.PERSON, Map.of());
        
        // When
        CreateWatchlistScreeningCommand command = new CreateWatchlistScreeningCommand(PARTNER_ID, request);
        
        // Then
        assertNotNull(command.getCommandId());
        assertEquals(PARTNER_ID, command.getPartnerId());
        assertNotNull(command.getTimestamp());
        assertEquals(request, command.getScreeningRequest());
        assertNotNull(command.getCorrelationId());
    }
    
    @Test
    void shouldCreateCommandWithProvidedCorrelationId() {
        // Given
        ScreeningRequest request = new ScreeningRequest(
            FIRST_NAME, LAST_NAME, DOB, COUNTRY, EntityType.PERSON, Map.of());
        UUID correlationId = UUID.randomUUID();
        
        // When
        CreateWatchlistScreeningCommand command = new CreateWatchlistScreeningCommand(PARTNER_ID, request, correlationId);
        
        // Then
        assertEquals(PARTNER_ID, command.getPartnerId());
        assertEquals(request, command.getScreeningRequest());
        assertEquals(correlationId, command.getCorrelationId());
    }
}
