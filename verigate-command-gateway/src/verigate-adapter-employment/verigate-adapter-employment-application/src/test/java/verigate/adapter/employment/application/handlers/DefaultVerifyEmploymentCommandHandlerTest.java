/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.employment.application.handlers;

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
import verigate.adapter.employment.domain.models.EmploymentStatus;
import verigate.adapter.employment.domain.models.EmploymentType;
import verigate.adapter.employment.domain.models.EmploymentVerificationRequest;
import verigate.adapter.employment.domain.models.EmploymentVerificationResponse;
import verigate.adapter.employment.domain.services.EmploymentVerificationService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;

class DefaultVerifyEmploymentCommandHandlerTest {

    @Mock
    private EmploymentVerificationService employmentVerificationService;

    private DefaultVerifyEmploymentCommandHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new DefaultVerifyEmploymentCommandHandler(employmentVerificationService);
    }

    @Test
    void testHandleSuccessfulEmploymentVerification() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of(
                "idNumber", "8501015009087",
                "employerName", "Acme Corp",
                "employeeNumber", "EMP001"
            )
        );

        EmploymentVerificationResponse employedResponse = EmploymentVerificationResponse.employed(
            "Acme Corp", EmploymentType.FULL_TIME, "Software Engineer",
            "2020-01-15", null, "Engineering");

        when(employmentVerificationService.verifyEmployment(
            any(EmploymentVerificationRequest.class)))
            .thenReturn(employedResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("Employment verified successfully"));
        assertEquals(EmploymentStatus.EMPLOYED.toString(), result.get("employmentStatus"));
        assertEquals("Acme Corp", result.get("employerName"));
        assertEquals("Software Engineer", result.get("jobTitle"));

        verify(employmentVerificationService).verifyEmployment(
            any(EmploymentVerificationRequest.class));
    }

    @Test
    void testHandleTerminatedEmployment() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "8501015009087", "employerName", "Old Corp")
        );

        EmploymentVerificationResponse terminatedResponse =
            EmploymentVerificationResponse.terminated(
                "Old Corp", "Software Engineer", "2020-01-15", "2023-06-30");

        when(employmentVerificationService.verifyEmployment(
            any(EmploymentVerificationRequest.class)))
            .thenReturn(terminatedResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("terminated"));
        assertEquals(EmploymentStatus.TERMINATED.toString(), result.get("employmentStatus"));

        verify(employmentVerificationService).verifyEmployment(
            any(EmploymentVerificationRequest.class));
    }

    @Test
    void testHandleSuspendedEmployee() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "8501015009087", "employerName", "Test Corp")
        );

        EmploymentVerificationResponse suspendedResponse = new EmploymentVerificationResponse(
            EmploymentStatus.SUSPENDED, "Test Corp", null, null, null, null, null);

        when(employmentVerificationService.verifyEmployment(
            any(EmploymentVerificationRequest.class)))
            .thenReturn(suspendedResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("suspended"));
        assertEquals(EmploymentStatus.SUSPENDED.toString(), result.get("employmentStatus"));

        verify(employmentVerificationService).verifyEmployment(
            any(EmploymentVerificationRequest.class));
    }

    @Test
    void testHandleEmploymentNotFound() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "9999999999999", "employerName", "Unknown Corp")
        );

        EmploymentVerificationResponse notFoundResponse =
            EmploymentVerificationResponse.notFound();

        when(employmentVerificationService.verifyEmployment(
            any(EmploymentVerificationRequest.class)))
            .thenReturn(notFoundResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertEquals(EmploymentStatus.NOT_FOUND.toString(), result.get("employmentStatus"));

        verify(employmentVerificationService).verifyEmployment(
            any(EmploymentVerificationRequest.class));
    }

    @Test
    void testHandleOnLeave() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "8501015009087", "employerName", "Good Corp")
        );

        EmploymentVerificationResponse onLeaveResponse = new EmploymentVerificationResponse(
            EmploymentStatus.ON_LEAVE, "Good Corp", EmploymentType.FULL_TIME,
            "Manager", "2019-03-01", null, "Operations");

        when(employmentVerificationService.verifyEmployment(
            any(EmploymentVerificationRequest.class)))
            .thenReturn(onLeaveResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("on leave"));
        assertEquals(EmploymentStatus.ON_LEAVE.toString(), result.get("employmentStatus"));

        verify(employmentVerificationService).verifyEmployment(
            any(EmploymentVerificationRequest.class));
    }

    @Test
    void testHandleUnverifiable() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "8501015009087", "employerName", "Obscure Corp")
        );

        EmploymentVerificationResponse unverifiableResponse = new EmploymentVerificationResponse(
            EmploymentStatus.UNVERIFIABLE, null, null, null, null, null, null);

        when(employmentVerificationService.verifyEmployment(
            any(EmploymentVerificationRequest.class)))
            .thenReturn(unverifiableResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertEquals(EmploymentStatus.UNVERIFIABLE.toString(), result.get("employmentStatus"));

        verify(employmentVerificationService).verifyEmployment(
            any(EmploymentVerificationRequest.class));
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
            Map.of("employerName", "Acme Corp")
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));

        verifyNoInteractions(employmentVerificationService);
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
            Map.of("idNumber", "8501015009087", "employerName", "Acme Corp")
        );

        when(employmentVerificationService.verifyEmployment(
            any(EmploymentVerificationRequest.class)))
            .thenThrow(new PermanentException("Employment API error"));

        // Act & Assert
        assertThrows(PermanentException.class, () -> handler.handle(command));

        verify(employmentVerificationService).verifyEmployment(
            any(EmploymentVerificationRequest.class));
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
            Map.of("idNumber", "8501015009087", "employerName", "Acme Corp")
        );

        when(employmentVerificationService.verifyEmployment(
            any(EmploymentVerificationRequest.class)))
            .thenThrow(new TransientException("Network timeout"));

        // Act & Assert
        assertThrows(TransientException.class, () -> handler.handle(command));

        verify(employmentVerificationService).verifyEmployment(
            any(EmploymentVerificationRequest.class));
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
            Map.of("idNumber", "8501015009087", "employerName", "Acme Corp")
        );

        EmploymentVerificationResponse employedResponse = EmploymentVerificationResponse.employed(
            "Acme Corp", EmploymentType.FULL_TIME, "Developer",
            "2020-01-15", null, "IT");

        when(employmentVerificationService.verifyEmployment(
            any(EmploymentVerificationRequest.class)))
            .thenReturn(employedResponse);

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());
        assertTrue(result.failureReason().contains("Employment verified successfully"));

        verify(employmentVerificationService).verifyEmployment(
            any(EmploymentVerificationRequest.class));
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
            Map.of("idNumber", "8501015009087", "employerName", "Acme Corp")
        );

        when(employmentVerificationService.verifyEmployment(
            any(EmploymentVerificationRequest.class)))
            .thenThrow(new PermanentException("Service error"));

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL, result.outcome());
        assertTrue(result.failureReason().contains("Employment verification failed"));

        verify(employmentVerificationService).verifyEmployment(
            any(EmploymentVerificationRequest.class));
    }
}
