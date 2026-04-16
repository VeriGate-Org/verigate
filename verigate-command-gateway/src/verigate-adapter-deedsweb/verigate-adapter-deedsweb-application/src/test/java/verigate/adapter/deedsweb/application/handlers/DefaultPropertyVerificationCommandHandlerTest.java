/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.application.handlers;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import verigate.adapter.deedsweb.domain.models.PropertyDetails;
import verigate.adapter.deedsweb.domain.models.PropertySearchRequest;
import verigate.adapter.deedsweb.domain.services.PropertyOwnershipVerificationService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationResult;
import verigate.verification.cg.domain.models.VerificationOutcome;
import verigate.verification.cg.domain.models.VerificationType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class DefaultPropertyVerificationCommandHandlerTest {

    @Mock
    private PropertyOwnershipVerificationService propertyOwnershipVerificationService;

    private DefaultPropertyVerificationCommandHandler handler;
    private VerifyPartyCommand testCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new DefaultPropertyVerificationCommandHandler(propertyOwnershipVerificationService);

        // Create test command instance
        Map<String, Object> testMetadata = new HashMap<>();
        testMetadata.put("searchType", "ownerId");
        testMetadata.put("query", "8001015009087");
        testMetadata.put("province", "KwaZulu-Natal");
        
        testCommand = new VerifyPartyCommand(
            UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
            Instant.now(),
            "test-user",
            VerificationType.PROPERTY_OWNERSHIP_VERIFICATION,
            null,
            testMetadata
        );
    }

    @Test
    void handle_searchResults_returnsStructuredPropertyPayload() throws Exception {
        // Arrange
        List<PropertyDetails> properties = List.of(
            new PropertyDetails.Builder()
                .deedNumber("T12345/2024")
                .titleDeedReference("T12345/2024")
                .propertyDescription("Erf 101 Portion 2, Newcastle Central")
                .province("KwaZulu-Natal")
                .registrationDivision("Newcastle Central")
                .registeredOwnerName("Jane Doe")
                .registeredOwnerIdNumber("8001015009087")
                .registrationDate(LocalDate.of(2024, 3, 11))
                .transferDate(LocalDate.of(2024, 3, 11))
                .purchasePrice(1250000.0)
                .bondHolder("ABSA")
                .bondAmount(900000.0)
                .build()
        );
        when(propertyOwnershipVerificationService.searchProperties(any(PropertySearchRequest.class)))
            .thenReturn(properties);

        // Act
        Map<String, String> result = handler.handle(testCommand);

        // Assert
        assertNotNull(result);
        assertEquals("DeedsWeb", result.get("provider"));
        assertEquals("SUCCEEDED", result.get("outcome"));
        assertEquals("1", result.get("recordCount"));
        assertNotNull(result.get("searchResultJson"));
        assertTrue(result.get("searchResultJson").contains("\"titleDeed\":\"T12345/2024\""));
        assertTrue(result.get("searchResultJson").contains("\"ownerName\":\"Jane Doe\""));

        verify(propertyOwnershipVerificationService)
            .searchProperties(argThat((PropertySearchRequest req) ->
                "ownerId".equals(req.getSearchType())
                    && "8001015009087".equals(req.getQuery())
                    && "KwaZulu-Natal".equals(req.getProvince())));
    }

    @Test
    void handle_transientException_throwsAndPublishesEvent() throws Exception {
        // Arrange
        TransientException transientError = new TransientException("Service temporarily unavailable");
        when(propertyOwnershipVerificationService.searchProperties(any(PropertySearchRequest.class)))
            .thenThrow(transientError);

        // Act & Assert
        assertThrows(TransientException.class, () -> handler.handle(testCommand));

        verify(propertyOwnershipVerificationService).searchProperties(any(PropertySearchRequest.class));
    }

    @Test
    void handle_permanentException_throwsAndPublishesEvent() throws Exception {
        // Arrange
        PermanentException permanentError = new PermanentException("Invalid request data");
        when(propertyOwnershipVerificationService.searchProperties(any(PropertySearchRequest.class)))
            .thenThrow(permanentError);

        // Act & Assert
        assertThrows(PermanentException.class, () -> handler.handle(testCommand));

        verify(propertyOwnershipVerificationService).searchProperties(any(PropertySearchRequest.class));
    }

    @Test
    void handleAsync_validCommand_returnsCompletableFuture() throws Exception {
        // Arrange
        when(propertyOwnershipVerificationService.searchProperties(any(PropertySearchRequest.class)))
            .thenReturn(List.of());

        // Act
        var futureResult = handler.handleAsync(testCommand);

        // Assert
        assertNotNull(futureResult);
        VerificationResult result = futureResult.get();
        assertNotNull(result);
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());

        verify(propertyOwnershipVerificationService).searchProperties(any(PropertySearchRequest.class));
    }
}
