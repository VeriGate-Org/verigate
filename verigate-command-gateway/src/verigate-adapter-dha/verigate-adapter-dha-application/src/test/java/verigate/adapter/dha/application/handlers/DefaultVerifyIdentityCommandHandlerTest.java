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
            Map.of(
                "idNumber", "8501015009087",
                "firstName", "John",
                "lastName", "Doe",
                "dateOfBirth", "1985-01-01",
                "gender", "MALE"
            )
        );

        IdentityVerificationResponse verifiedResponse = IdentityVerificationResponse.verified(
            CitizenshipStatus.CITIZEN, "Single", VitalStatus.ALIVE, "All details match");

        when(identityVerificationService.verifyIdentity(any(IdentityVerificationRequest.class)))
            .thenReturn(verifiedResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("Identity verified successfully"));
        assertEquals(IdVerificationStatus.VERIFIED.toString(), result.get("verificationStatus"));

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
            Map.of(
                "idNumber", "9999999999999",
                "firstName", "Unknown",
                "lastName", "Person"
            )
        );

        IdentityVerificationResponse notFoundResponse = IdentityVerificationResponse.notFound();

        when(identityVerificationService.verifyIdentity(any(IdentityVerificationRequest.class)))
            .thenReturn(notFoundResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertEquals(IdVerificationStatus.NOT_FOUND.toString(), result.get("verificationStatus"));

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
            Map.of(
                "idNumber", "7001015009087",
                "firstName", "Jane",
                "lastName", "Doe"
            )
        );

        IdentityVerificationResponse deceasedResponse = IdentityVerificationResponse.deceased(
            "Individual recorded as deceased on 2020-05-15");

        when(identityVerificationService.verifyIdentity(any(IdentityVerificationRequest.class)))
            .thenReturn(deceasedResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("deceased"));
        assertEquals(IdVerificationStatus.DECEASED.toString(), result.get("verificationStatus"));

        verify(identityVerificationService).verifyIdentity(any(IdentityVerificationRequest.class));
    }

    @Test
    void testHandleExpiredId() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of(
                "idNumber", "6501015009087",
                "firstName", "Test",
                "lastName", "User"
            )
        );

        IdentityVerificationResponse expiredResponse = new IdentityVerificationResponse(
            IdVerificationStatus.EXPIRED_ID,
            CitizenshipStatus.CITIZEN,
            null,
            VitalStatus.ALIVE,
            "ID document has expired"
        );

        when(identityVerificationService.verifyIdentity(any(IdentityVerificationRequest.class)))
            .thenReturn(expiredResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("expired"));
        assertEquals(IdVerificationStatus.EXPIRED_ID.toString(), result.get("verificationStatus"));

        verify(identityVerificationService).verifyIdentity(any(IdentityVerificationRequest.class));
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
                "firstName", "Wrong",
                "lastName", "Name"
            )
        );

        IdentityVerificationResponse mismatchResponse = IdentityVerificationResponse.mismatch(
            "Name does not match DHA records");

        when(identityVerificationService.verifyIdentity(any(IdentityVerificationRequest.class)))
            .thenReturn(mismatchResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("do not match"));
        assertEquals(IdVerificationStatus.MISMATCH.toString(), result.get("verificationStatus"));

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
            Map.of("firstName", "John", "lastName", "Doe")
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
            Map.of("idNumber", "8501015009087")
        );

        when(identityVerificationService.verifyIdentity(any(IdentityVerificationRequest.class)))
            .thenThrow(new PermanentException("DHA API service unavailable"));

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
            Map.of("idNumber", "8501015009087")
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
            Map.of("idNumber", "8501015009087", "firstName", "John", "lastName", "Doe")
        );

        IdentityVerificationResponse verifiedResponse = IdentityVerificationResponse.verified(
            CitizenshipStatus.CITIZEN, "Single", VitalStatus.ALIVE, "All details match");

        when(identityVerificationService.verifyIdentity(any(IdentityVerificationRequest.class)))
            .thenReturn(verifiedResponse);

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());

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
            Map.of("idNumber", "8501015009087")
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

    @Test
    void testHandleChecksumInvalidId() throws Exception {
        // Arrange - 8501015009088 fails Luhn (changed last digit from 7 to 8)
        // Handler does not validate Luhn; the service call proceeds but returns
        // NOT_FOUND for invalid IDs.
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of(
                "idNumber", "8501015009088",
                "firstName", "John",
                "lastName", "Doe"
            )
        );

        IdentityVerificationResponse notFoundResponse = IdentityVerificationResponse.notFound();
        when(identityVerificationService.verifyIdentity(any(IdentityVerificationRequest.class)))
            .thenReturn(notFoundResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert - invalid ID returns SOFT_FAIL (not found)
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        verify(identityVerificationService).verifyIdentity(any(IdentityVerificationRequest.class));
    }

    @Test
    void testHandleChecksumInvalidIdAllZeros() throws Exception {
        // Arrange - 1234567890123 has valid format but fails Luhn
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of(
                "idNumber", "1234567890123",
                "firstName", "Test",
                "lastName", "User"
            )
        );

        IdentityVerificationResponse notFoundResponse = IdentityVerificationResponse.notFound();
        when(identityVerificationService.verifyIdentity(any(IdentityVerificationRequest.class)))
            .thenReturn(notFoundResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert - invalid ID returns SOFT_FAIL (not found)
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        verify(identityVerificationService).verifyIdentity(any(IdentityVerificationRequest.class));
    }
}
