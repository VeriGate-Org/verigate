/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.saqa.application.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import verigate.adapter.saqa.domain.models.QualificationRecord;
import verigate.adapter.saqa.domain.models.QualificationType;
import verigate.adapter.saqa.domain.models.QualificationVerificationRequest;
import verigate.adapter.saqa.domain.models.QualificationVerificationResponse;
import verigate.adapter.saqa.domain.models.QualificationVerificationStatus;
import verigate.adapter.saqa.domain.services.QualificationVerificationService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;

class DefaultVerifyQualificationCommandHandlerTest {

    @Mock
    private QualificationVerificationService qualificationVerificationService;

    private DefaultVerifyQualificationCommandHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new DefaultVerifyQualificationCommandHandler(qualificationVerificationService);
    }

    @Test
    void testHandleSuccessfulVerification() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "9001015009087")
        );

        QualificationRecord matchedQual = QualificationRecord.builder()
            .qualificationTitle("Bachelor of Science")
            .qualificationType(QualificationType.BACHELORS_DEGREE)
            .institution("University of Cape Town")
            .nqfLevel(7)
            .dateConferred(LocalDate.of(2020, 6, 15))
            .saqaId("SAQA-12345")
            .status("ACTIVE")
            .build();

        QualificationVerificationResponse verifiedResponse =
            QualificationVerificationResponse.verified(
                List.of(matchedQual), matchedQual, 0.95);

        when(qualificationVerificationService.verifyQualification(
            any(QualificationVerificationRequest.class)))
            .thenReturn(verifiedResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertEquals("VERIFIED", result.get("status"));
        assertEquals("Bachelor of Science", result.get("qualificationTitle"));
        assertEquals("University of Cape Town", result.get("institution"));
        assertEquals("7", result.get("nqfLevel"));
        assertEquals("0.95", result.get("matchConfidence"));

        verify(qualificationVerificationService).verifyQualification(
            any(QualificationVerificationRequest.class));
    }

    @Test
    void testHandleQualificationNotFound() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "9001015009087")
        );

        QualificationVerificationResponse notFoundResponse =
            QualificationVerificationResponse.notFound(
                "No qualification records found for the provided ID number");

        when(qualificationVerificationService.verifyQualification(
            any(QualificationVerificationRequest.class)))
            .thenReturn(notFoundResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertEquals("NOT_FOUND", result.get("status"));
        assertEquals("0.0", result.get("matchConfidence"));

        verify(qualificationVerificationService).verifyQualification(
            any(QualificationVerificationRequest.class));
    }

    @Test
    void testHandleRevokedQualification() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "9001015009087")
        );

        QualificationRecord revokedQual = QualificationRecord.builder()
            .qualificationTitle("National Diploma")
            .qualificationType(QualificationType.NATIONAL_DIPLOMA)
            .institution("Tshwane University of Technology")
            .nqfLevel(6)
            .dateConferred(LocalDate.of(2018, 12, 1))
            .saqaId("SAQA-67890")
            .status("REVOKED")
            .build();

        QualificationVerificationResponse revokedResponse =
            QualificationVerificationResponse.revoked(
                revokedQual, "Qualification has been revoked by institution");

        when(qualificationVerificationService.verifyQualification(
            any(QualificationVerificationRequest.class)))
            .thenReturn(revokedResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertEquals("REVOKED", result.get("status"));
        assertEquals("1.0", result.get("matchConfidence"));

        verify(qualificationVerificationService).verifyQualification(
            any(QualificationVerificationRequest.class));
    }

    @Test
    void testHandleMismatch() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "9001015009087")
        );

        QualificationVerificationResponse mismatchResponse =
            QualificationVerificationResponse.mismatch(
                List.of(), 0.3, "Qualification details do not match SAQA records");

        when(qualificationVerificationService.verifyQualification(
            any(QualificationVerificationRequest.class)))
            .thenReturn(mismatchResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertEquals("MISMATCH", result.get("status"));
        assertEquals("0.3", result.get("matchConfidence"));

        verify(qualificationVerificationService).verifyQualification(
            any(QualificationVerificationRequest.class));
    }

    @Test
    void testHandleMissingIdNumber() {
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
        assertTrue(exception.getMessage().contains("ID number is required for SAQA qualification verification"));

        verifyNoInteractions(qualificationVerificationService);
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

        QualificationVerificationResponse errorResponse =
            QualificationVerificationResponse.error("SAQA API service unavailable");

        when(qualificationVerificationService.verifyQualification(
            any(QualificationVerificationRequest.class)))
            .thenReturn(errorResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SYSTEM_OUTAGE.toString(), result.get("outcome"));
        assertEquals("ERROR", result.get("status"));

        verify(qualificationVerificationService).verifyQualification(
            any(QualificationVerificationRequest.class));
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

        when(qualificationVerificationService.verifyQualification(
            any(QualificationVerificationRequest.class)))
            .thenThrow(new TransientException("Network timeout"));

        // Act & Assert
        assertThrows(TransientException.class, () -> handler.handle(command));

        verify(qualificationVerificationService).verifyQualification(
            any(QualificationVerificationRequest.class));
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

        QualificationRecord matchedQual = QualificationRecord.builder()
            .qualificationTitle("Bachelor of Commerce")
            .qualificationType(QualificationType.BACHELORS_DEGREE)
            .institution("University of Pretoria")
            .nqfLevel(7)
            .dateConferred(LocalDate.of(2019, 3, 20))
            .saqaId("SAQA-11111")
            .status("ACTIVE")
            .build();

        QualificationVerificationResponse verifiedResponse =
            QualificationVerificationResponse.verified(
                List.of(matchedQual), matchedQual, 0.98);

        when(qualificationVerificationService.verifyQualification(
            any(QualificationVerificationRequest.class)))
            .thenReturn(verifiedResponse);

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());
        assertEquals("VERIFIED", result.failureReason());

        verify(qualificationVerificationService).verifyQualification(
            any(QualificationVerificationRequest.class));
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

        when(qualificationVerificationService.verifyQualification(
            any(QualificationVerificationRequest.class)))
            .thenThrow(new PermanentException("Service error"));

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL, result.outcome());
        assertTrue(result.failureReason().contains("Qualification verification failed"));

        verify(qualificationVerificationService).verifyQualification(
            any(QualificationVerificationRequest.class));
    }
}
