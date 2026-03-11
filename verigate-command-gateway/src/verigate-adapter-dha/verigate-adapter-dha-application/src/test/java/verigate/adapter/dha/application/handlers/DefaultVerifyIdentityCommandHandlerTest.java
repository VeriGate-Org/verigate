/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.application.handlers;

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
import verigate.adapter.dha.domain.models.CitizenshipStatus;
import verigate.adapter.dha.domain.models.IdVerificationStatus;
import verigate.adapter.dha.domain.models.IdentityVerificationRequest;
import verigate.adapter.dha.domain.models.IdentityVerificationResponse;
import verigate.adapter.dha.domain.models.VitalStatus;
import verigate.adapter.dha.domain.services.DhaIdentityVerificationService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;

class DefaultVerifyIdentityCommandHandlerTest {

    @Mock
    private DhaIdentityVerificationService identityVerificationService;

    private DefaultVerifyIdentityCommandHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new DefaultVerifyIdentityCommandHandler(identityVerificationService);
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

        IdentityVerificationResponse verifiedResponse =
            IdentityVerificationResponse.verified(
                CitizenshipStatus.CITIZEN, "Single", VitalStatus.ALIVE, "Full match");

        when(identityVerificationService.verifyIdentity(any(IdentityVerificationRequest.class)))
            .thenReturn(verifiedResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("Identity verified successfully"));
        assertEquals("VERIFIED", result.get("verificationStatus"));
        assertEquals("CITIZEN", result.get("citizenshipStatus"));
        assertEquals("ALIVE", result.get("vitalStatus"));
        assertEquals("Full match", result.get("matchDetails"));

        verify(identityVerificationService).verifyIdentity(any(IdentityVerificationRequest.class));
    }

    @Test
    void testHandleIdentityNotFound() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "9001015009087")
        );

        IdentityVerificationResponse notFoundResponse =
            IdentityVerificationResponse.notFound();

        when(identityVerificationService.verifyIdentity(any(IdentityVerificationRequest.class)))
            .thenReturn(notFoundResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("Identity not found in DHA population register"));
        assertEquals("NOT_FOUND", result.get("verificationStatus"));

        verify(identityVerificationService).verifyIdentity(any(IdentityVerificationRequest.class));
    }

    @Test
    void testHandleDeceasedIndividual() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "9001015009087")
        );

        IdentityVerificationResponse deceasedResponse =
            IdentityVerificationResponse.deceased("Deceased per DHA records");

        when(identityVerificationService.verifyIdentity(any(IdentityVerificationRequest.class)))
            .thenReturn(deceasedResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("deceased"));
        assertEquals("DECEASED", result.get("verificationStatus"));
        assertEquals("DECEASED", result.get("vitalStatus"));

        verify(identityVerificationService).verifyIdentity(any(IdentityVerificationRequest.class));
    }

    @Test
    void testHandleIdentityMismatch() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "9001015009087")
        );

        IdentityVerificationResponse mismatchResponse =
            IdentityVerificationResponse.mismatch("Name does not match");

        when(identityVerificationService.verifyIdentity(any(IdentityVerificationRequest.class)))
            .thenReturn(mismatchResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("do not match"));
        assertEquals("MISMATCH", result.get("verificationStatus"));

        verify(identityVerificationService).verifyIdentity(any(IdentityVerificationRequest.class));
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
            Map.of("firstName", "John")
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));

        verifyNoInteractions(identityVerificationService);
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

        when(identityVerificationService.verifyIdentity(any(IdentityVerificationRequest.class)))
            .thenThrow(new PermanentException("DHA service unavailable"));

        // Act & Assert
        assertThrows(PermanentException.class, () -> handler.handle(command));

        verify(identityVerificationService).verifyIdentity(any(IdentityVerificationRequest.class));
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

        when(identityVerificationService.verifyIdentity(any(IdentityVerificationRequest.class)))
            .thenThrow(new TransientException("Network timeout"));

        // Act & Assert
        assertThrows(TransientException.class, () -> handler.handle(command));

        verify(identityVerificationService).verifyIdentity(any(IdentityVerificationRequest.class));
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

        IdentityVerificationResponse verifiedResponse =
            IdentityVerificationResponse.verified(
                CitizenshipStatus.CITIZEN, "Single", VitalStatus.ALIVE, "Full match");

        when(identityVerificationService.verifyIdentity(any(IdentityVerificationRequest.class)))
            .thenReturn(verifiedResponse);

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());
        assertTrue(result.failureReason().contains("Identity verified successfully"));

        verify(identityVerificationService).verifyIdentity(any(IdentityVerificationRequest.class));
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

        when(identityVerificationService.verifyIdentity(any(IdentityVerificationRequest.class)))
            .thenThrow(new PermanentException("Service error"));

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL, result.outcome());
        assertTrue(result.failureReason().contains("Identity verification failed"));

        verify(identityVerificationService).verifyIdentity(any(IdentityVerificationRequest.class));
    }
}
