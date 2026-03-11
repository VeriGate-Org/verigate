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
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import verigate.adapter.sars.domain.enums.TaxClearanceType;
import verigate.adapter.sars.domain.enums.TaxComplianceStatus;
import verigate.adapter.sars.domain.models.TaxClearanceCertificate;
import verigate.adapter.sars.domain.models.TaxComplianceRequest;
import verigate.adapter.sars.domain.models.TaxComplianceResponse;
import verigate.adapter.sars.domain.services.SarsTaxComplianceService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;

class DefaultVerifyTaxComplianceCommandHandlerTest {

    @Mock
    private SarsTaxComplianceService sarsTaxComplianceService;

    private DefaultVerifyTaxComplianceCommandHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new DefaultVerifyTaxComplianceCommandHandler(sarsTaxComplianceService);
    }

    @Test
    void testHandleCompliantVerification() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "9001015009087")
        );

        TaxClearanceCertificate certificate = TaxClearanceCertificate.validCertificate(
            "TCC-2025-001",
            LocalDate.of(2025, 1, 15),
            LocalDate.of(2026, 1, 15),
            TaxClearanceType.GOOD_STANDING);

        TaxComplianceResponse compliantResponse = TaxComplianceResponse.compliant(certificate);

        when(sarsTaxComplianceService.verifyTaxCompliance(any(TaxComplianceRequest.class)))
            .thenReturn(compliantResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("compliance verified successfully")
            || result.get("details").contains("good standing"));
        assertEquals("COMPLIANT", result.get("taxComplianceStatus"));
        assertEquals("TCC-2025-001", result.get("certificateNumber"));
        assertEquals("true", result.get("certificateValid"));

        verify(sarsTaxComplianceService).verifyTaxCompliance(any(TaxComplianceRequest.class));
    }

    @Test
    void testHandleNonCompliant() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "9001015009087")
        );

        TaxComplianceResponse nonCompliantResponse =
            TaxComplianceResponse.nonCompliant("Outstanding tax returns for 2023 and 2024");

        when(sarsTaxComplianceService.verifyTaxCompliance(any(TaxComplianceRequest.class)))
            .thenReturn(nonCompliantResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("non-compliant"));
        assertEquals("NON_COMPLIANT", result.get("taxComplianceStatus"));

        verify(sarsTaxComplianceService).verifyTaxCompliance(any(TaxComplianceRequest.class));
    }

    @Test
    void testHandleTccExpired() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "9001015009087")
        );

        TaxClearanceCertificate expiredCertificate = TaxClearanceCertificate.expiredCertificate(
            "TCC-2023-001",
            LocalDate.of(2023, 1, 15),
            LocalDate.of(2024, 1, 15),
            TaxClearanceType.GOOD_STANDING);

        TaxComplianceResponse tccExpiredResponse =
            TaxComplianceResponse.tccExpired(expiredCertificate);

        when(sarsTaxComplianceService.verifyTaxCompliance(any(TaxComplianceRequest.class)))
            .thenReturn(tccExpiredResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("expired"));
        assertEquals("TCC_EXPIRED", result.get("taxComplianceStatus"));
        assertEquals("false", result.get("certificateValid"));

        verify(sarsTaxComplianceService).verifyTaxCompliance(any(TaxComplianceRequest.class));
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
            Map.of("idNumber", "9001015009087")
        );

        TaxComplianceResponse notFoundResponse = TaxComplianceResponse.notFound();

        when(sarsTaxComplianceService.verifyTaxCompliance(any(TaxComplianceRequest.class)))
            .thenReturn(notFoundResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("No taxpayer record found"));
        assertEquals("NOT_FOUND", result.get("taxComplianceStatus"));

        verify(sarsTaxComplianceService).verifyTaxCompliance(any(TaxComplianceRequest.class));
    }

    @Test
    void testHandleMissingIdNumberAndTaxReference() {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("someOtherKey", "value")
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, () -> handler.handle(command));
        assertTrue(exception.getMessage().contains(
            "Either ID number or tax reference number is required for tax compliance verification"));

        verifyNoInteractions(sarsTaxComplianceService);
    }

    @Test
    void testHandleServiceError() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "9001015009087")
        );

        TaxComplianceResponse errorResponse =
            TaxComplianceResponse.error("SARS eFiling API unavailable");

        when(sarsTaxComplianceService.verifyTaxCompliance(any(TaxComplianceRequest.class)))
            .thenReturn(errorResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SYSTEM_OUTAGE.toString(), result.get("outcome"));
        assertEquals("ERROR", result.get("taxComplianceStatus"));

        verify(sarsTaxComplianceService).verifyTaxCompliance(any(TaxComplianceRequest.class));
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
            Map.of("idNumber", "9001015009087")
        );

        when(sarsTaxComplianceService.verifyTaxCompliance(any(TaxComplianceRequest.class)))
            .thenThrow(new TransientException("Network timeout"));

        // Act & Assert
        assertThrows(TransientException.class, () -> handler.handle(command));

        verify(sarsTaxComplianceService).verifyTaxCompliance(any(TaxComplianceRequest.class));
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
            Map.of("idNumber", "9001015009087")
        );

        TaxClearanceCertificate certificate = TaxClearanceCertificate.validCertificate(
            "TCC-2025-002",
            LocalDate.of(2025, 3, 1),
            LocalDate.of(2026, 3, 1),
            TaxClearanceType.GOOD_STANDING);

        TaxComplianceResponse compliantResponse = TaxComplianceResponse.compliant(certificate);

        when(sarsTaxComplianceService.verifyTaxCompliance(any(TaxComplianceRequest.class)))
            .thenReturn(compliantResponse);

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());
        assertTrue(result.failureReason().contains("compliance verified successfully")
            || result.failureReason().contains("good standing"));

        verify(sarsTaxComplianceService).verifyTaxCompliance(any(TaxComplianceRequest.class));
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
            Map.of("idNumber", "9001015009087")
        );

        when(sarsTaxComplianceService.verifyTaxCompliance(any(TaxComplianceRequest.class)))
            .thenThrow(new PermanentException("Service error"));

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.SYSTEM_OUTAGE, result.outcome());
        assertTrue(result.failureReason().contains("SARS tax compliance verification failed"));

        verify(sarsTaxComplianceService).verifyTaxCompliance(any(TaxComplianceRequest.class));
    }
}
