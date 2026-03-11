/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.qlink.application.handlers;

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
import verigate.adapter.qlink.domain.models.BankAccountStatus;
import verigate.adapter.qlink.domain.models.BankAccountType;
import verigate.adapter.qlink.domain.models.BankVerificationRequest;
import verigate.adapter.qlink.domain.models.BankVerificationResponse;
import verigate.adapter.qlink.domain.services.QLinkBankVerificationService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;

class DefaultVerifyBankAccountDetailsCommandHandlerTest {

    @Mock
    private QLinkBankVerificationService bankVerificationService;

    private DefaultVerifyBankAccountDetailsCommandHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new DefaultVerifyBankAccountDetailsCommandHandler(bankVerificationService);
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
            Map.of("accountNumber", "1234567890", "branchCode", "250655", "idNumber", "9001015009087")
        );

        BankVerificationResponse successResponse = BankVerificationResponse.success(
            BankAccountStatus.VALID, "John Doe", "Main Branch", BankAccountType.SAVINGS, 0.95);

        when(bankVerificationService.verifyBankAccount(any(BankVerificationRequest.class)))
            .thenReturn(successResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("verified successfully"));
        assertEquals("VALID", result.get("bankAccountStatus"));
        assertEquals("John Doe", result.get("accountHolderName"));
        assertEquals("Main Branch", result.get("branchName"));
        assertEquals("0.95", result.get("matchScore"));

        verify(bankVerificationService).verifyBankAccount(any(BankVerificationRequest.class));
    }

    @Test
    void testHandleDormantAccount() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("accountNumber", "1234567890", "branchCode", "250655", "idNumber", "9001015009087")
        );

        BankVerificationResponse dormantResponse = BankVerificationResponse.success(
            BankAccountStatus.DORMANT, "John Doe", "Main Branch", BankAccountType.SAVINGS, 0.80);

        when(bankVerificationService.verifyBankAccount(any(BankVerificationRequest.class)))
            .thenReturn(dormantResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("dormant"));
        assertEquals("DORMANT", result.get("bankAccountStatus"));

        verify(bankVerificationService).verifyBankAccount(any(BankVerificationRequest.class));
    }

    @Test
    void testHandleNotFoundAccount() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("accountNumber", "1234567890", "branchCode", "250655", "idNumber", "9001015009087")
        );

        BankVerificationResponse notFoundResponse = BankVerificationResponse.notFound();

        when(bankVerificationService.verifyBankAccount(any(BankVerificationRequest.class)))
            .thenReturn(notFoundResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("not found"));
        assertEquals("NOT_FOUND", result.get("bankAccountStatus"));

        verify(bankVerificationService).verifyBankAccount(any(BankVerificationRequest.class));
    }

    @Test
    void testHandleClosedAccount() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("accountNumber", "1234567890", "branchCode", "250655", "idNumber", "9001015009087")
        );

        BankVerificationResponse closedResponse = BankVerificationResponse.success(
            BankAccountStatus.CLOSED, null, null, null, 0.0);

        when(bankVerificationService.verifyBankAccount(any(BankVerificationRequest.class)))
            .thenReturn(closedResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("closed"));
        assertEquals("CLOSED", result.get("bankAccountStatus"));

        verify(bankVerificationService).verifyBankAccount(any(BankVerificationRequest.class));
    }

    @Test
    void testHandleMissingAccountNumber() {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("branchCode", "250655")
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, () -> handler.handle(command));
        assertTrue(exception.getMessage().contains("Account number is required for QLink bank verification"));

        verifyNoInteractions(bankVerificationService);
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
            Map.of("accountNumber", "1234567890", "branchCode", "250655", "idNumber", "9001015009087")
        );

        when(bankVerificationService.verifyBankAccount(any(BankVerificationRequest.class)))
            .thenThrow(new PermanentException("Bank verification service unavailable"));

        // Act & Assert
        assertThrows(PermanentException.class, () -> handler.handle(command));

        verify(bankVerificationService).verifyBankAccount(any(BankVerificationRequest.class));
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
            Map.of("accountNumber", "1234567890", "branchCode", "250655", "idNumber", "9001015009087")
        );

        when(bankVerificationService.verifyBankAccount(any(BankVerificationRequest.class)))
            .thenThrow(new TransientException("Network timeout"));

        // Act & Assert
        assertThrows(TransientException.class, () -> handler.handle(command));

        verify(bankVerificationService).verifyBankAccount(any(BankVerificationRequest.class));
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
            Map.of("accountNumber", "1234567890", "branchCode", "250655", "idNumber", "9001015009087")
        );

        BankVerificationResponse successResponse = BankVerificationResponse.success(
            BankAccountStatus.VALID, "John Doe", "Main Branch", BankAccountType.CHEQUE, 0.98);

        when(bankVerificationService.verifyBankAccount(any(BankVerificationRequest.class)))
            .thenReturn(successResponse);

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());
        assertTrue(result.failureReason().contains("verified successfully"));

        verify(bankVerificationService).verifyBankAccount(any(BankVerificationRequest.class));
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
            Map.of("accountNumber", "1234567890", "branchCode", "250655", "idNumber", "9001015009087")
        );

        when(bankVerificationService.verifyBankAccount(any(BankVerificationRequest.class)))
            .thenThrow(new PermanentException("Service error"));

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL, result.outcome());
        assertTrue(result.failureReason().contains("Bank account verification failed"));

        verify(bankVerificationService).verifyBankAccount(any(BankVerificationRequest.class));
    }
}
