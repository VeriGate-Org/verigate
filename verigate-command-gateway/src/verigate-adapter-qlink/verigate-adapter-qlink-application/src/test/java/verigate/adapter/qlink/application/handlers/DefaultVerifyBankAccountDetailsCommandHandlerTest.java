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
    void testHandleSuccessfulBankVerification() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of(
                "accountNumber", "1234567890",
                "branchCode", "250655",
                "accountHolderName", "John Doe",
                "idNumber", "8501015009087"
            )
        );

        BankVerificationResponse validResponse = BankVerificationResponse.success(
            BankAccountStatus.VALID, "John Doe", "FNB Rosebank", BankAccountType.SAVINGS, 0.95);

        when(bankVerificationService.verifyBankAccount(any(BankVerificationRequest.class)))
            .thenReturn(validResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("verified successfully"));
        assertEquals(BankAccountStatus.VALID.toString(), result.get("bankAccountStatus"));
        assertEquals("John Doe", result.get("accountHolderName"));
        assertEquals("FNB Rosebank", result.get("branchName"));

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
            Map.of(
                "accountNumber", "9876543210",
                "branchCode", "250655",
                "accountHolderName", "Jane Smith",
                "idNumber", "9001015009087"
            )
        );

        BankVerificationResponse dormantResponse = new BankVerificationResponse(
            BankAccountStatus.DORMANT, "Jane Smith", "ABSA Mall",
            BankAccountType.CHEQUE, 0.80);

        when(bankVerificationService.verifyBankAccount(any(BankVerificationRequest.class)))
            .thenReturn(dormantResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("dormant"));
        assertEquals(BankAccountStatus.DORMANT.toString(), result.get("bankAccountStatus"));

        verify(bankVerificationService).verifyBankAccount(any(BankVerificationRequest.class));
    }

    @Test
    void testHandleInvalidAccount() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of(
                "accountNumber", "0000000000",
                "branchCode", "250655",
                "accountHolderName", "Test User",
                "idNumber", "8501015009087"
            )
        );

        BankVerificationResponse invalidResponse = new BankVerificationResponse(
            BankAccountStatus.INVALID, null, null, null, 0.0);

        when(bankVerificationService.verifyBankAccount(any(BankVerificationRequest.class)))
            .thenReturn(invalidResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("invalid"));
        assertEquals(BankAccountStatus.INVALID.toString(), result.get("bankAccountStatus"));

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
            Map.of(
                "accountNumber", "5555555555",
                "branchCode", "250655",
                "accountHolderName", "Old Account",
                "idNumber", "7001015009087"
            )
        );

        BankVerificationResponse closedResponse = new BankVerificationResponse(
            BankAccountStatus.CLOSED, "Old Account", "Nedbank",
            BankAccountType.CHEQUE, 0.0);

        when(bankVerificationService.verifyBankAccount(any(BankVerificationRequest.class)))
            .thenReturn(closedResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("closed"));
        assertEquals(BankAccountStatus.CLOSED.toString(), result.get("bankAccountStatus"));

        verify(bankVerificationService).verifyBankAccount(any(BankVerificationRequest.class));
    }

    @Test
    void testHandleAccountNotFound() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of(
                "accountNumber", "9999999999",
                "branchCode", "250655",
                "accountHolderName", "Unknown",
                "idNumber", "8501015009087"
            )
        );

        BankVerificationResponse notFoundResponse = BankVerificationResponse.notFound();

        when(bankVerificationService.verifyBankAccount(any(BankVerificationRequest.class)))
            .thenReturn(notFoundResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("not found"));
        assertEquals(BankAccountStatus.NOT_FOUND.toString(), result.get("bankAccountStatus"));

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
            Map.of("branchCode", "250655", "accountHolderName", "John Doe")
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));

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
            Map.of(
                "accountNumber", "1234567890",
                "branchCode", "250655",
                "accountHolderName", "John Doe",
                "idNumber", "8501015009087"
            )
        );

        when(bankVerificationService.verifyBankAccount(any(BankVerificationRequest.class)))
            .thenThrow(new PermanentException("QLink API service unavailable"));

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
            Map.of(
                "accountNumber", "1234567890",
                "branchCode", "250655",
                "accountHolderName", "John Doe",
                "idNumber", "8501015009087"
            )
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
            Map.of(
                "accountNumber", "1234567890",
                "branchCode", "250655",
                "accountHolderName", "John Doe",
                "idNumber", "8501015009087"
            )
        );

        BankVerificationResponse validResponse = BankVerificationResponse.success(
            BankAccountStatus.VALID, "John Doe", "FNB Rosebank", BankAccountType.SAVINGS, 0.95);

        when(bankVerificationService.verifyBankAccount(any(BankVerificationRequest.class)))
            .thenReturn(validResponse);

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());

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
            Map.of(
                "accountNumber", "1234567890",
                "branchCode", "250655",
                "accountHolderName", "John Doe",
                "idNumber", "8501015009087"
            )
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
