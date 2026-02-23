/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.infrastructure.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import verigate.adapter.opensanctions.domain.models.EntityMatchRequest;
import verigate.adapter.opensanctions.domain.models.EntityMatchResponse;
import verigate.adapter.opensanctions.domain.models.EntityExample;
import verigate.adapter.opensanctions.infrastructure.http.OpenSanctionsApiAdapter;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DefaultOpenSanctionsMatchingServiceTest {

    @Mock
    private OpenSanctionsApiAdapter mockApiAdapter;

    private DefaultOpenSanctionsMatchingService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new DefaultOpenSanctionsMatchingService(mockApiAdapter);
    }

    @Test
    void matchEntities_validRequest_returnsResponse() throws Exception {
        // Arrange
        EntityExample entityExample = new EntityExample.Builder()
            .id("test-entity")
            .schema("Person")
            .properties(Map.of("name", List.of("John Doe")))
            .build();

        EntityMatchRequest request = new EntityMatchRequest.Builder()
            .dataset("sanctions")
            .queries(Map.of("entity1", entityExample))
            .limit(10)
            .threshold(0.7)
            .cutoff(0.5)
            .algorithm("logic-v1")
            .build();

        EntityMatchResponse expectedResponse = new EntityMatchResponse(Map.of(), Map.of(), 10);
        when(mockApiAdapter.matchEntities(any())).thenReturn(expectedResponse);

        // Act
        EntityMatchResponse result = service.matchEntities(request);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(mockApiAdapter).matchEntities(request);
    }

    @Test
    void matchEntities_nullRequest_throwsPermanentException() {
        // Act & Assert
        assertThrows(PermanentException.class, () -> service.matchEntities(null));
        verifyNoInteractions(mockApiAdapter);
    }

    @Test
    void matchEntities_emptyDataset_throwsPermanentException() {
        // Arrange
        EntityMatchRequest request = new EntityMatchRequest.Builder()
            .dataset("")
            .queries(Map.of())
            .build();

        // Act & Assert
        assertThrows(PermanentException.class, () -> service.matchEntities(request));
        verifyNoInteractions(mockApiAdapter);
    }

    @Test
    void searchEntities_validParameters_returnsResponse() throws Exception {
        // Arrange
        EntityMatchResponse expectedResponse = new EntityMatchResponse(Map.of(), Map.of(), 5);
        when(mockApiAdapter.searchEntities(eq("sanctions"), eq("John Doe"), eq(5)))
            .thenReturn(expectedResponse);

        // Act
        EntityMatchResponse result = service.searchEntities("sanctions", "John Doe", 5);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(mockApiAdapter).searchEntities("sanctions", "John Doe", 5);
    }

    @Test
    void searchEntities_nullDataset_throwsPermanentException() {
        // Act & Assert
        assertThrows(PermanentException.class, () -> 
            service.searchEntities(null, "John Doe", 5));
        verifyNoInteractions(mockApiAdapter);
    }

    @Test
    void searchEntities_emptyQuery_throwsPermanentException() {
        // Act & Assert
        assertThrows(PermanentException.class, () -> 
            service.searchEntities("sanctions", "", 5));
        verifyNoInteractions(mockApiAdapter);
    }

    @Test
    void isServiceHealthy_healthyService_returnsTrue() throws Exception {
        // Arrange
        when(mockApiAdapter.checkServiceHealth()).thenReturn(true);

        // Act
        boolean result = service.isServiceHealthy();

        // Assert
        assertTrue(result);
        verify(mockApiAdapter).checkServiceHealth();
    }

    @Test
    void isServiceHealthy_transientError_throwsTransientException() throws Exception {
        // Arrange
        when(mockApiAdapter.checkServiceHealth()).thenThrow(new TransientException("Service unavailable"));

        // Act & Assert
        assertThrows(TransientException.class, () -> service.isServiceHealthy());
        verify(mockApiAdapter).checkServiceHealth();
    }
}