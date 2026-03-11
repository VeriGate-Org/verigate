/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.creditbureau.application.handlers;

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
import verigate.adapter.creditbureau.domain.models.CreditAssessment;
import verigate.adapter.creditbureau.domain.models.CreditBureauProvider;
import verigate.adapter.creditbureau.domain.models.CreditCheckRequest;
import verigate.adapter.creditbureau.domain.models.CreditCheckResponse;
import verigate.adapter.creditbureau.domain.models.CreditCheckStatus;
import verigate.adapter.creditbureau.domain.models.CreditScoreBand;
import verigate.adapter.creditbureau.domain.services.CreditBureauService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;

class DefaultPerformCreditCheckCommandHandlerTest {

    @Mock
    private CreditBureauService creditBureauService;

    private DefaultPerformCreditCheckCommandHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new DefaultPerformCreditCheckCommandHandler(creditBureauService);
    }

    @Test
    void testHandleSuccessfulCreditCheck() throws Exception {
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
                "consentReference", "CONSENT-001"
            )
        );

        CreditAssessment assessment = new CreditAssessment(
            720,
            CreditScoreBand.GOOD,
            CreditBureauProvider.TRANSUNION,
            LocalDate.now(),
            50000.0,
            30000.0,
            0.25,
            false,
            false,
            List.of()
        );

        CreditCheckResponse passedResponse = CreditCheckResponse.passed(
            assessment, true, "LOW", "Credit check passed");

        when(creditBureauService.performCreditCheck(any(CreditCheckRequest.class)))
            .thenReturn(passedResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertEquals(CreditCheckStatus.PASSED.toString(), result.get("status"));
        assertEquals("720", result.get("creditScore"));
        assertEquals(CreditScoreBand.GOOD.toString(), result.get("scoreBand"));
        assertEquals("false", result.get("hasJudgments"));
        assertEquals("false", result.get("hasDefaults"));

        verify(creditBureauService).performCreditCheck(any(CreditCheckRequest.class));
    }

    @Test
    void testHandleCreditCheckFailed() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "8501015009087", "consentReference", "CONSENT-002")
        );

        CreditCheckResponse failedResponse = CreditCheckResponse.failed(
            null, "HIGH", "Credit score below threshold");

        when(creditBureauService.performCreditCheck(any(CreditCheckRequest.class)))
            .thenReturn(failedResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertEquals(CreditCheckStatus.FAILED.toString(), result.get("status"));

        verify(creditBureauService).performCreditCheck(any(CreditCheckRequest.class));
    }

    @Test
    void testHandleReviewRequiredWithAdverseFindings() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "8501015009087", "consentReference", "CONSENT-003")
        );

        CreditAssessment assessment = new CreditAssessment(
            550,
            CreditScoreBand.POOR,
            CreditBureauProvider.TRANSUNION,
            LocalDate.now(),
            200000.0,
            25000.0,
            0.65,
            true,
            true,
            List.of()
        );

        CreditCheckResponse reviewResponse = CreditCheckResponse.reviewRequired(
            assessment, false, "HIGH", "Review required - adverse findings");

        when(creditBureauService.performCreditCheck(any(CreditCheckRequest.class)))
            .thenReturn(reviewResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertEquals(CreditCheckStatus.REVIEW_REQUIRED.toString(), result.get("status"));
        assertEquals("true", result.get("hasJudgments"));
        assertEquals("true", result.get("hasDefaults"));

        verify(creditBureauService).performCreditCheck(any(CreditCheckRequest.class));
    }

    @Test
    void testHandleReviewRequiredWithoutAdverseFindings() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "8501015009087", "consentReference", "CONSENT-004")
        );

        CreditAssessment assessment = new CreditAssessment(
            620,
            CreditScoreBand.FAIR,
            CreditBureauProvider.TRANSUNION,
            LocalDate.now(),
            80000.0,
            28000.0,
            0.40,
            false,
            false,
            List.of()
        );

        CreditCheckResponse reviewResponse = CreditCheckResponse.reviewRequired(
            assessment, true, "MEDIUM", "Review required - no adverse findings");

        when(creditBureauService.performCreditCheck(any(CreditCheckRequest.class)))
            .thenReturn(reviewResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertEquals(CreditCheckStatus.REVIEW_REQUIRED.toString(), result.get("status"));
        assertEquals("false", result.get("hasJudgments"));
        assertEquals("false", result.get("hasDefaults"));

        verify(creditBureauService).performCreditCheck(any(CreditCheckRequest.class));
    }

    @Test
    void testHandleInsufficientData() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "8501015009087", "consentReference", "CONSENT-005")
        );

        CreditCheckResponse insufficientResponse = CreditCheckResponse.insufficientData(
                "No credit history found");

        when(creditBureauService.performCreditCheck(any(CreditCheckRequest.class)))
            .thenReturn(insufficientResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertEquals(CreditCheckStatus.INSUFFICIENT_DATA.toString(), result.get("status"));

        verify(creditBureauService).performCreditCheck(any(CreditCheckRequest.class));
    }

    @Test
    void testHandleBureauUnavailable() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "8501015009087", "consentReference", "CONSENT-006")
        );

        CreditCheckResponse errorResponse = CreditCheckResponse.error("Bureau unavailable");

        when(creditBureauService.performCreditCheck(any(CreditCheckRequest.class)))
            .thenReturn(errorResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SYSTEM_OUTAGE.toString(), result.get("outcome"));

        verify(creditBureauService).performCreditCheck(any(CreditCheckRequest.class));
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

        verifyNoInteractions(creditBureauService);
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
            Map.of("idNumber", "8501015009087", "consentReference", "CONSENT-007")
        );

        when(creditBureauService.performCreditCheck(any(CreditCheckRequest.class)))
            .thenThrow(new PermanentException("Credit bureau API error"));

        // Act & Assert
        assertThrows(PermanentException.class, () -> handler.handle(command));

        verify(creditBureauService).performCreditCheck(any(CreditCheckRequest.class));
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
            Map.of("idNumber", "8501015009087", "consentReference", "CONSENT-008")
        );

        when(creditBureauService.performCreditCheck(any(CreditCheckRequest.class)))
            .thenThrow(new TransientException("Network timeout"));

        // Act & Assert
        assertThrows(TransientException.class, () -> handler.handle(command));

        verify(creditBureauService).performCreditCheck(any(CreditCheckRequest.class));
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
            Map.of("idNumber", "8501015009087", "consentReference", "CONSENT-009")
        );

        CreditAssessment assessment = new CreditAssessment(
            750,
            CreditScoreBand.EXCELLENT,
            CreditBureauProvider.TRANSUNION,
            LocalDate.now(),
            30000.0,
            40000.0,
            0.15,
            false,
            false,
            List.of()
        );

        CreditCheckResponse passedResponse = CreditCheckResponse.passed(
            assessment, true, "LOW", "Excellent credit");

        when(creditBureauService.performCreditCheck(any(CreditCheckRequest.class)))
            .thenReturn(passedResponse);

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());

        verify(creditBureauService).performCreditCheck(any(CreditCheckRequest.class));
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
            Map.of("idNumber", "8501015009087", "consentReference", "CONSENT-010")
        );

        when(creditBureauService.performCreditCheck(any(CreditCheckRequest.class)))
            .thenThrow(new PermanentException("Service error"));

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL, result.outcome());
        assertTrue(result.failureReason().contains("Credit check failed"));

        verify(creditBureauService).performCreditCheck(any(CreditCheckRequest.class));
    }
}
