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
    void testHandleSuccessfulQualificationVerification() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of(
                "idNumber", "8501015009087",
                "firstName", "John",
                "lastName", "Doe",
                "qualificationTitle", "Bachelor of Commerce",
                "institution", "University of Cape Town",
                "yearCompleted", "2010"
            )
        );

        QualificationRecord matchedRecord = QualificationRecord.builder()
            .qualificationTitle("Bachelor of Commerce")
            .qualificationType(QualificationType.BACHELORS_DEGREE)
            .institution("University of Cape Town")
            .nqfLevel(7)
            .dateConferred(LocalDate.of(2010, 12, 15))
            .saqaId("SAQA-12345")
            .status("ACTIVE")
            .build();

        QualificationVerificationResponse verifiedResponse =
            QualificationVerificationResponse.verified(
                List.of(matchedRecord), matchedRecord, 0.95);

        when(qualificationVerificationService.verifyQualification(
            any(QualificationVerificationRequest.class)))
            .thenReturn(verifiedResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertEquals(QualificationVerificationStatus.VERIFIED.toString(), result.get("status"));
        assertEquals("Bachelor of Commerce", result.get("qualificationTitle"));
        assertEquals("University of Cape Town", result.get("institution"));
        assertEquals("7", result.get("nqfLevel"));

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
            Map.of(
                "idNumber", "9999999999999",
                "firstName", "Unknown",
                "lastName", "Person",
                "qualificationTitle", "PhD in Quantum Physics"
            )
        );

        QualificationVerificationResponse notFoundResponse =
            QualificationVerificationResponse.notFound("No qualification record found");

        when(qualificationVerificationService.verifyQualification(
            any(QualificationVerificationRequest.class)))
            .thenReturn(notFoundResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertEquals(QualificationVerificationStatus.NOT_FOUND.toString(), result.get("status"));

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
            Map.of(
                "idNumber", "8501015009087",
                "qualificationTitle", "Masters in Business"
            )
        );

        QualificationRecord revokedRecord = QualificationRecord.builder()
            .qualificationTitle("Masters in Business")
            .qualificationType(QualificationType.BACHELORS_DEGREE)
            .institution("University of Johannesburg")
            .nqfLevel(9)
            .saqaId("SAQA-99999")
            .status("REVOKED")
            .build();

        QualificationVerificationResponse revokedResponse =
            QualificationVerificationResponse.revoked(
                revokedRecord, "Qualification revoked due to academic misconduct");

        when(qualificationVerificationService.verifyQualification(
            any(QualificationVerificationRequest.class)))
            .thenReturn(revokedResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertEquals(QualificationVerificationStatus.REVOKED.toString(), result.get("status"));

        verify(qualificationVerificationService).verifyQualification(
            any(QualificationVerificationRequest.class));
    }

    @Test
    void testHandleExpiredQualification() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of(
                "idNumber", "8501015009087",
                "qualificationTitle", "First Aid Certificate"
            )
        );

        QualificationVerificationResponse expiredResponse = new QualificationVerificationResponse(
            QualificationVerificationStatus.EXPIRED, List.of(), null, 0.0,
            "Qualification has expired");

        when(qualificationVerificationService.verifyQualification(
            any(QualificationVerificationRequest.class)))
            .thenReturn(expiredResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertEquals(QualificationVerificationStatus.EXPIRED.toString(), result.get("status"));

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
            Map.of(
                "idNumber", "8501015009087",
                "qualificationTitle", "Bachelor of Science"
            )
        );

        QualificationVerificationResponse mismatchResponse =
            QualificationVerificationResponse.mismatch(
                List.of(), 0.3, "Details do not match SAQA records");

        when(qualificationVerificationService.verifyQualification(
            any(QualificationVerificationRequest.class)))
            .thenReturn(mismatchResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertEquals(QualificationVerificationStatus.MISMATCH.toString(), result.get("status"));

        verify(qualificationVerificationService).verifyQualification(
            any(QualificationVerificationRequest.class));
    }

    @Test
    void testHandlePendingVerification() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "8501015009087", "qualificationTitle", "Diploma")
        );

        QualificationVerificationResponse pendingResponse = new QualificationVerificationResponse(
            QualificationVerificationStatus.PENDING_VERIFICATION, List.of(), null, 0.0,
            "Awaiting institution confirmation");

        when(qualificationVerificationService.verifyQualification(
            any(QualificationVerificationRequest.class)))
            .thenReturn(pendingResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertEquals(QualificationVerificationStatus.PENDING_VERIFICATION.toString(),
            result.get("status"));

        verify(qualificationVerificationService).verifyQualification(
            any(QualificationVerificationRequest.class));
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
            Map.of("idNumber", "8501015009087", "qualificationTitle", "Degree")
        );

        QualificationVerificationResponse errorResponse =
            QualificationVerificationResponse.error("SAQA system unavailable");

        when(qualificationVerificationService.verifyQualification(
            any(QualificationVerificationRequest.class)))
            .thenReturn(errorResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SYSTEM_OUTAGE.toString(), result.get("outcome"));
        assertEquals(QualificationVerificationStatus.ERROR.toString(), result.get("status"));

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
            Map.of("qualificationTitle", "Bachelor of Commerce")
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));

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
            Map.of("idNumber", "8501015009087", "qualificationTitle", "Degree")
        );

        when(qualificationVerificationService.verifyQualification(
            any(QualificationVerificationRequest.class)))
            .thenThrow(new PermanentException("SAQA API error"));

        // Act & Assert
        assertThrows(PermanentException.class, () -> handler.handle(command));

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
            Map.of("idNumber", "8501015009087", "qualificationTitle", "Degree")
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
            Map.of(
                "idNumber", "8501015009087",
                "qualificationTitle", "Bachelor of Commerce",
                "institution", "UCT"
            )
        );

        QualificationRecord matchedRecord = QualificationRecord.builder()
            .qualificationTitle("Bachelor of Commerce")
            .qualificationType(QualificationType.BACHELORS_DEGREE)
            .institution("University of Cape Town")
            .nqfLevel(7)
            .dateConferred(LocalDate.of(2010, 12, 15))
            .saqaId("SAQA-12345")
            .status("ACTIVE")
            .build();

        QualificationVerificationResponse verifiedResponse =
            QualificationVerificationResponse.verified(
                List.of(matchedRecord), matchedRecord, 0.95);

        when(qualificationVerificationService.verifyQualification(
            any(QualificationVerificationRequest.class)))
            .thenReturn(verifiedResponse);

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());

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
            Map.of("idNumber", "8501015009087", "qualificationTitle", "Degree")
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
