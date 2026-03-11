/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.income.application.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import verigate.adapter.income.domain.enums.ConfidenceLevel;
import verigate.adapter.income.domain.enums.IncomeVerificationStatus;
import verigate.adapter.income.domain.models.IncomeAssessment;
import verigate.adapter.income.domain.models.IncomeVerificationRequest;
import verigate.adapter.income.domain.models.IncomeVerificationResponse;
import verigate.adapter.income.domain.services.IncomeVerificationService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;

class DefaultVerifyIncomeCommandHandlerTest {

    @Mock
    private IncomeVerificationService incomeVerificationService;

    private DefaultVerifyIncomeCommandHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new DefaultVerifyIncomeCommandHandler(incomeVerificationService);
    }

    @Test
    void testHandleSuccessfulIncomeVerification() throws Exception {
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
                "declaredMonthlyIncome", "25000.00",
                "incomeSourceType", "SALARY"
            )
        );

        IncomeAssessment assessment = new IncomeAssessment(
            new BigDecimal("25500.00"),
            new BigDecimal("25000.00"),
            new BigDecimal("2.00"),
            ConfidenceLevel.HIGH,
            List.of("PAYSLIP", "BANK_STATEMENT"),
            true
        );

        IncomeVerificationResponse verifiedResponse = IncomeVerificationResponse.verified(
            assessment, "Income verification successful");

        when(incomeVerificationService.verifyIncome(any(IncomeVerificationRequest.class)))
            .thenReturn(verifiedResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("Income verified successfully"));
        assertEquals(IncomeVerificationStatus.VERIFIED.toString(),
            result.get("verificationStatus"));
        assertEquals("25500.00", result.get("verifiedMonthlyIncome"));
        assertEquals("true", result.get("affordabilityConfirmed"));

        verify(incomeVerificationService).verifyIncome(any(IncomeVerificationRequest.class));
    }

    @Test
    void testHandleIncomeMismatch() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of(
                "idNumber", "8501015009087",
                "declaredMonthlyIncome", "50000.00"
            )
        );

        IncomeAssessment assessment = new IncomeAssessment(
            new BigDecimal("20000.00"),
            new BigDecimal("50000.00"),
            new BigDecimal("60.00"),
            ConfidenceLevel.HIGH,
            List.of("BANK_STATEMENT"),
            false
        );

        IncomeVerificationResponse mismatchResponse = IncomeVerificationResponse.mismatch(
                assessment, "Declared income significantly exceeds verified income");

        when(incomeVerificationService.verifyIncome(any(IncomeVerificationRequest.class)))
            .thenReturn(mismatchResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("mismatch"));
        assertEquals(IncomeVerificationStatus.MISMATCH.toString(),
            result.get("verificationStatus"));

        verify(incomeVerificationService).verifyIncome(any(IncomeVerificationRequest.class));
    }

    @Test
    void testHandleInsufficientEvidence() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "8501015009087", "declaredMonthlyIncome", "30000.00")
        );

        IncomeVerificationResponse insufficientResponse =
            IncomeVerificationResponse.insufficientEvidence(
                new BigDecimal("30000.00"), "No payslip or bank statement available");

        when(incomeVerificationService.verifyIncome(any(IncomeVerificationRequest.class)))
            .thenReturn(insufficientResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("Insufficient evidence"));
        assertEquals(IncomeVerificationStatus.INSUFFICIENT_EVIDENCE.toString(),
            result.get("verificationStatus"));

        verify(incomeVerificationService).verifyIncome(any(IncomeVerificationRequest.class));
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
            Map.of("idNumber", "8501015009087", "declaredMonthlyIncome", "15000.00")
        );

        IncomeVerificationResponse unverifiableResponse =
            IncomeVerificationResponse.unverifiable(
                new BigDecimal("15000.00"), "Income source type not supported");

        when(incomeVerificationService.verifyIncome(any(IncomeVerificationRequest.class)))
            .thenReturn(unverifiableResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertEquals(IncomeVerificationStatus.UNVERIFIABLE.toString(),
            result.get("verificationStatus"));

        verify(incomeVerificationService).verifyIncome(any(IncomeVerificationRequest.class));
    }

    @Test
    void testHandlePending() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "8501015009087", "declaredMonthlyIncome", "20000.00")
        );

        IncomeVerificationResponse pendingResponse =
            IncomeVerificationResponse.pending(
                new BigDecimal("20000.00"), "Awaiting employer confirmation");

        when(incomeVerificationService.verifyIncome(any(IncomeVerificationRequest.class)))
            .thenReturn(pendingResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("pending"));
        assertEquals(IncomeVerificationStatus.PENDING.toString(),
            result.get("verificationStatus"));

        verify(incomeVerificationService).verifyIncome(any(IncomeVerificationRequest.class));
    }

    @Test
    void testHandleError() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "8501015009087", "declaredMonthlyIncome", "20000.00")
        );

        IncomeVerificationResponse errorResponse =
            IncomeVerificationResponse.error(
                new BigDecimal("20000.00"), "Internal system error");

        when(incomeVerificationService.verifyIncome(any(IncomeVerificationRequest.class)))
            .thenReturn(errorResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SYSTEM_OUTAGE.toString(), result.get("outcome"));
        assertEquals(IncomeVerificationStatus.ERROR.toString(),
            result.get("verificationStatus"));

        verify(incomeVerificationService).verifyIncome(any(IncomeVerificationRequest.class));
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
            Map.of("declaredMonthlyIncome", "25000.00")
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));

        verifyNoInteractions(incomeVerificationService);
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
            Map.of("idNumber", "8501015009087", "declaredMonthlyIncome", "25000.00")
        );

        when(incomeVerificationService.verifyIncome(any(IncomeVerificationRequest.class)))
            .thenThrow(new PermanentException("Income verification API error"));

        // Act & Assert
        assertThrows(PermanentException.class, () -> handler.handle(command));

        verify(incomeVerificationService).verifyIncome(any(IncomeVerificationRequest.class));
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
            Map.of("idNumber", "8501015009087", "declaredMonthlyIncome", "25000.00")
        );

        when(incomeVerificationService.verifyIncome(any(IncomeVerificationRequest.class)))
            .thenThrow(new TransientException("Network timeout"));

        // Act & Assert
        assertThrows(TransientException.class, () -> handler.handle(command));

        verify(incomeVerificationService).verifyIncome(any(IncomeVerificationRequest.class));
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
            Map.of("idNumber", "8501015009087", "declaredMonthlyIncome", "25000.00")
        );

        IncomeAssessment assessment = new IncomeAssessment(
            new BigDecimal("25000.00"),
            new BigDecimal("25000.00"),
            BigDecimal.ZERO,
            ConfidenceLevel.HIGH,
            List.of("PAYSLIP"),
            true
        );

        IncomeVerificationResponse verifiedResponse = IncomeVerificationResponse.verified(
            assessment, "Income verified");

        when(incomeVerificationService.verifyIncome(any(IncomeVerificationRequest.class)))
            .thenReturn(verifiedResponse);

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());

        verify(incomeVerificationService).verifyIncome(any(IncomeVerificationRequest.class));
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
            Map.of("idNumber", "8501015009087", "declaredMonthlyIncome", "25000.00")
        );

        when(incomeVerificationService.verifyIncome(any(IncomeVerificationRequest.class)))
            .thenThrow(new PermanentException("Service error"));

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.SYSTEM_OUTAGE, result.outcome());
        assertTrue(result.failureReason().contains("Income verification failed"));

        verify(incomeVerificationService).verifyIncome(any(IncomeVerificationRequest.class));
    }
}
