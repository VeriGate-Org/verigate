/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.application.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import verigate.adapter.sars.domain.enums.VatVendorStatus;
import verigate.adapter.sars.domain.models.VatVendorDetails;
import verigate.adapter.sars.domain.models.VatVendorSearchRequest;
import verigate.adapter.sars.domain.models.VatVendorSearchResponse;
import verigate.adapter.sars.domain.services.SarsVatVendorService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;

class DefaultVerifyVatVendorCommandHandlerTest {

    @Mock
    private SarsVatVendorService sarsVatVendorService;

    private DefaultVerifyVatVendorCommandHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new DefaultVerifyVatVendorCommandHandler(sarsVatVendorService);
    }

    @Test
    void testHandleActiveVendor() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("vatNumber", "4123456789")
        );

        VatVendorDetails vendorDetails = VatVendorDetails.builder()
            .vatNumber("4123456789")
            .vendorName("Acme Trading (Pty) Ltd")
            .tradingName("Acme Trading")
            .registrationDate("2015/03/01")
            .vendorStatus("Active")
            .activityCode("47110")
            .physicalAddress("123 Main Rd, Sandton, 2196")
            .build();

        VatVendorSearchResponse activeResponse =
            VatVendorSearchResponse.found(vendorDetails, VatVendorStatus.ACTIVE);

        when(sarsVatVendorService.searchVatVendor(any(VatVendorSearchRequest.class)))
            .thenReturn(activeResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("active"));
        assertEquals(VatVendorStatus.ACTIVE.toString(), result.get("vatVendorStatus"));
        assertEquals("4123456789", result.get("vatNumber"));
        assertEquals("Acme Trading (Pty) Ltd", result.get("vendorName"));
        assertEquals("Acme Trading", result.get("tradingName"));

        verify(sarsVatVendorService).searchVatVendor(any(VatVendorSearchRequest.class));
    }

    @Test
    void testHandleInactiveVendor() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("vatNumber", "4999999999")
        );

        VatVendorDetails vendorDetails = VatVendorDetails.builder()
            .vatNumber("4999999999")
            .vendorName("Defunct Corp")
            .vendorStatus("Inactive")
            .build();

        VatVendorSearchResponse inactiveResponse =
            VatVendorSearchResponse.found(vendorDetails, VatVendorStatus.INACTIVE);

        when(sarsVatVendorService.searchVatVendor(any(VatVendorSearchRequest.class)))
            .thenReturn(inactiveResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("inactive"));
        assertEquals(VatVendorStatus.INACTIVE.toString(), result.get("vatVendorStatus"));

        verify(sarsVatVendorService).searchVatVendor(any(VatVendorSearchRequest.class));
    }

    @Test
    void testHandleDeregisteredVendor() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("vatNumber", "4888888888")
        );

        VatVendorDetails vendorDetails = VatVendorDetails.builder()
            .vatNumber("4888888888")
            .vendorName("Old Co")
            .vendorStatus("Deregistered")
            .build();

        VatVendorSearchResponse deregisteredResponse =
            VatVendorSearchResponse.found(vendorDetails, VatVendorStatus.DEREGISTERED);

        when(sarsVatVendorService.searchVatVendor(any(VatVendorSearchRequest.class)))
            .thenReturn(deregisteredResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("deregistered"));
        assertEquals(VatVendorStatus.DEREGISTERED.toString(), result.get("vatVendorStatus"));

        verify(sarsVatVendorService).searchVatVendor(any(VatVendorSearchRequest.class));
    }

    @Test
    void testHandleNotFound() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("vatNumber", "0000000000")
        );

        VatVendorSearchResponse notFoundResponse = VatVendorSearchResponse.notFound();

        when(sarsVatVendorService.searchVatVendor(any(VatVendorSearchRequest.class)))
            .thenReturn(notFoundResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("No VAT vendor record found"));
        assertEquals(VatVendorStatus.NOT_FOUND.toString(), result.get("vatVendorStatus"));

        verify(sarsVatVendorService).searchVatVendor(any(VatVendorSearchRequest.class));
    }

    @Test
    void testHandleSystemError() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("vatNumber", "4123456789")
        );

        VatVendorSearchResponse errorResponse =
            VatVendorSearchResponse.error("SARS eFiling service unavailable");

        when(sarsVatVendorService.searchVatVendor(any(VatVendorSearchRequest.class)))
            .thenReturn(errorResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SYSTEM_OUTAGE.toString(), result.get("outcome"));
        assertEquals(VatVendorStatus.ERROR.toString(), result.get("vatVendorStatus"));

        verify(sarsVatVendorService).searchVatVendor(any(VatVendorSearchRequest.class));
    }

    @Test
    void testHandleMissingVatNumber() {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("firstName", "John", "lastName", "Doe")
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));

        verifyNoInteractions(sarsVatVendorService);
    }

    @Test
    void testHandleTransientException() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("vatNumber", "4123456789")
        );

        when(sarsVatVendorService.searchVatVendor(any(VatVendorSearchRequest.class)))
            .thenThrow(new TransientException("Network timeout"));

        // Act & Assert
        assertThrows(TransientException.class, () -> handler.handle(command));

        verify(sarsVatVendorService).searchVatVendor(any(VatVendorSearchRequest.class));
    }

    @Test
    void testHandlePermanentException() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("vatNumber", "4123456789")
        );

        when(sarsVatVendorService.searchVatVendor(any(VatVendorSearchRequest.class)))
            .thenThrow(new PermanentException("SARS SOAP fault"));

        // Act & Assert
        assertThrows(PermanentException.class, () -> handler.handle(command));

        verify(sarsVatVendorService).searchVatVendor(any(VatVendorSearchRequest.class));
    }

    @Test
    void testHandleAsyncSuccess() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("vatNumber", "4123456789")
        );

        VatVendorDetails vendorDetails = VatVendorDetails.builder()
            .vatNumber("4123456789")
            .vendorName("Acme Trading (Pty) Ltd")
            .vendorStatus("Active")
            .build();

        VatVendorSearchResponse activeResponse =
            VatVendorSearchResponse.found(vendorDetails, VatVendorStatus.ACTIVE);

        when(sarsVatVendorService.searchVatVendor(any(VatVendorSearchRequest.class)))
            .thenReturn(activeResponse);

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());

        verify(sarsVatVendorService).searchVatVendor(any(VatVendorSearchRequest.class));
    }

    @Test
    void testHandleAsyncFailure() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("vatNumber", "4123456789")
        );

        when(sarsVatVendorService.searchVatVendor(any(VatVendorSearchRequest.class)))
            .thenThrow(new PermanentException("Service error"));

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.SYSTEM_OUTAGE, result.outcome());
        assertTrue(result.failureReason().contains(
            "SARS VAT vendor verification failed"));

        verify(sarsVatVendorService).searchVatVendor(any(VatVendorSearchRequest.class));
    }
}
