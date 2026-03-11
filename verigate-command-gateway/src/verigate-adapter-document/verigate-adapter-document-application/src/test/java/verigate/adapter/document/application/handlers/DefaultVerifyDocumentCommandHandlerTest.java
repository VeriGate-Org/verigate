/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.application.handlers;

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
import verigate.adapter.document.domain.models.DocumentType;
import verigate.adapter.document.domain.models.DocumentVerificationRequest;
import verigate.adapter.document.domain.models.DocumentVerificationResponse;
import verigate.adapter.document.domain.models.DocumentVerificationStatus;
import verigate.adapter.document.domain.services.DocumentVerificationService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;

class DefaultVerifyDocumentCommandHandlerTest {

    @Mock
    private DocumentVerificationService documentVerificationService;

    private DefaultVerifyDocumentCommandHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new DefaultVerifyDocumentCommandHandler(documentVerificationService);
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
            Map.of("documentReference", "DOC-123", "documentType", "ID_DOCUMENT")
        );

        Map<String, String> extractedData = Map.of(
            "fullName", "John Doe",
            "idNumber", "9001015009087",
            "dateOfBirth", "1990-01-01"
        );

        DocumentVerificationResponse verifiedResponse = DocumentVerificationResponse.verified(
            DocumentType.IDENTITY_DOCUMENT, extractedData, 0.95, "Full match on all fields");

        when(documentVerificationService.verifyDocument(any(DocumentVerificationRequest.class)))
            .thenReturn(verifiedResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertEquals("VERIFIED", result.get("status"));
        assertEquals("IDENTITY_DOCUMENT", result.get("documentType"));
        assertEquals("0.95", result.get("confidenceScore"));
        assertEquals("Full match on all fields", result.get("matchDetails"));
        assertEquals("3 fields", result.get("extractedFields"));

        verify(documentVerificationService).verifyDocument(any(DocumentVerificationRequest.class));
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
            Map.of("documentReference", "DOC-456", "documentType", "ID_DOCUMENT")
        );

        Map<String, String> extractedData = Map.of(
            "fullName", "Jane Smith",
            "idNumber", "8505015009087"
        );

        DocumentVerificationResponse mismatchResponse = DocumentVerificationResponse.mismatch(
            DocumentType.IDENTITY_DOCUMENT, extractedData, 0.4, "Name mismatch detected");

        when(documentVerificationService.verifyDocument(any(DocumentVerificationRequest.class)))
            .thenReturn(mismatchResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertEquals("MISMATCH", result.get("status"));
        assertEquals("Name mismatch detected", result.get("matchDetails"));
        assertEquals("0.4", result.get("confidenceScore"));

        verify(documentVerificationService).verifyDocument(any(DocumentVerificationRequest.class));
    }

    @Test
    void testHandleSuspectedFraud() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("documentReference", "DOC-789", "documentType", "ID_DOCUMENT")
        );

        DocumentVerificationResponse fraudResponse = DocumentVerificationResponse.suspectedFraud(
            DocumentType.IDENTITY_DOCUMENT,
            "Document appears to be tampered with",
            "Suspected fraud detected");

        when(documentVerificationService.verifyDocument(any(DocumentVerificationRequest.class)))
            .thenReturn(fraudResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertEquals("SUSPECTED_FRAUD", result.get("status"));
        assertEquals("0.0", result.get("confidenceScore"));

        verify(documentVerificationService).verifyDocument(any(DocumentVerificationRequest.class));
    }

    @Test
    void testHandleUnreadable() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("documentReference", "DOC-999", "documentType", "ID_DOCUMENT")
        );

        DocumentVerificationResponse unreadableResponse = DocumentVerificationResponse.unreadable(
            DocumentType.IDENTITY_DOCUMENT, "Document image is too blurry to process");

        when(documentVerificationService.verifyDocument(any(DocumentVerificationRequest.class)))
            .thenReturn(unreadableResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertEquals("UNREADABLE", result.get("status"));
        assertEquals("0.0", result.get("confidenceScore"));

        verify(documentVerificationService).verifyDocument(any(DocumentVerificationRequest.class));
    }

    @Test
    void testHandleMissingDocumentReference() {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("documentType", "ID_DOCUMENT")
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, () -> handler.handle(command));
        assertTrue(exception.getMessage().contains("Document reference is required for document verification"));

        verifyNoInteractions(documentVerificationService);
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
            Map.of("documentReference", "DOC-123", "documentType", "ID_DOCUMENT")
        );

        DocumentVerificationResponse errorResponse =
            DocumentVerificationResponse.error("Document verification service unavailable");

        when(documentVerificationService.verifyDocument(any(DocumentVerificationRequest.class)))
            .thenReturn(errorResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SYSTEM_OUTAGE.toString(), result.get("outcome"));
        assertEquals("ERROR", result.get("status"));

        verify(documentVerificationService).verifyDocument(any(DocumentVerificationRequest.class));
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
            Map.of("documentReference", "DOC-123", "documentType", "ID_DOCUMENT")
        );

        when(documentVerificationService.verifyDocument(any(DocumentVerificationRequest.class)))
            .thenThrow(new TransientException("Network timeout"));

        // Act & Assert
        assertThrows(TransientException.class, () -> handler.handle(command));

        verify(documentVerificationService).verifyDocument(any(DocumentVerificationRequest.class));
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
            Map.of("documentReference", "DOC-123", "documentType", "ID_DOCUMENT")
        );

        Map<String, String> extractedData = Map.of(
            "fullName", "John Doe",
            "idNumber", "9001015009087"
        );

        DocumentVerificationResponse verifiedResponse = DocumentVerificationResponse.verified(
            DocumentType.IDENTITY_DOCUMENT, extractedData, 0.98, "All fields match");

        when(documentVerificationService.verifyDocument(any(DocumentVerificationRequest.class)))
            .thenReturn(verifiedResponse);

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());
        assertEquals("All fields match", result.failureReason());

        verify(documentVerificationService).verifyDocument(any(DocumentVerificationRequest.class));
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
            Map.of("documentReference", "DOC-123", "documentType", "ID_DOCUMENT")
        );

        when(documentVerificationService.verifyDocument(any(DocumentVerificationRequest.class)))
            .thenThrow(new PermanentException("Service error"));

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL, result.outcome());
        assertTrue(result.failureReason().contains("Document verification failed"));

        verify(documentVerificationService).verifyDocument(any(DocumentVerificationRequest.class));
    }
}
