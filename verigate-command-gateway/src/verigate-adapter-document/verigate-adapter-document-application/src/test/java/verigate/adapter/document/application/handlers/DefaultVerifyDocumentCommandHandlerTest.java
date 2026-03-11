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
    void testHandleSuccessfulDocumentVerification() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of(
                "documentReference", "DOC-2024-001",
                "documentType", "IDENTITY_DOCUMENT",
                "subjectIdNumber", "8501015009087",
                "subjectName", "John Doe",
                "s3BucketName", "verigate-docs",
                "s3ObjectKey", "uploads/doc-001.pdf"
            )
        );

        DocumentVerificationResponse verifiedResponse = DocumentVerificationResponse.verified(
            DocumentType.IDENTITY_DOCUMENT,
            Map.of("fullName", "John Doe", "idNumber", "8501015009087"),
            0.98,
            "All fields match submitted data");

        when(documentVerificationService.verifyDocument(any(DocumentVerificationRequest.class)))
            .thenReturn(verifiedResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertEquals(DocumentVerificationStatus.VERIFIED.toString(), result.get("status"));
        assertEquals(DocumentType.IDENTITY_DOCUMENT.toString(), result.get("documentType"));
        assertEquals("0.98", result.get("confidenceScore"));
        assertEquals("2 fields", result.get("extractedFields"));

        verify(documentVerificationService).verifyDocument(
            any(DocumentVerificationRequest.class));
    }

    @Test
    void testHandleDocumentMismatch() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of(
                "documentReference", "DOC-2024-002",
                "documentType", "IDENTITY_DOCUMENT",
                "subjectIdNumber", "8501015009087"
            )
        );

        DocumentVerificationResponse mismatchResponse = DocumentVerificationResponse.mismatch(
            DocumentType.IDENTITY_DOCUMENT,
            Map.of("fullName", "Jane Smith"),
            0.45,
            "Name does not match submitted ID number");

        when(documentVerificationService.verifyDocument(any(DocumentVerificationRequest.class)))
            .thenReturn(mismatchResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertEquals(DocumentVerificationStatus.MISMATCH.toString(), result.get("status"));

        verify(documentVerificationService).verifyDocument(
            any(DocumentVerificationRequest.class));
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
            Map.of(
                "documentReference", "DOC-2024-003",
                "documentType", "IDENTITY_DOCUMENT",
                "subjectIdNumber", "8501015009087"
            )
        );

        DocumentVerificationResponse fraudResponse = DocumentVerificationResponse.suspectedFraud(
            DocumentType.IDENTITY_DOCUMENT,
            "Document appears to be digitally altered",
            "Signs of digital manipulation detected");

        when(documentVerificationService.verifyDocument(any(DocumentVerificationRequest.class)))
            .thenReturn(fraudResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertEquals(DocumentVerificationStatus.SUSPECTED_FRAUD.toString(), result.get("status"));

        verify(documentVerificationService).verifyDocument(
            any(DocumentVerificationRequest.class));
    }

    @Test
    void testHandleUnreadableDocument() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of(
                "documentReference", "DOC-2024-004",
                "documentType", "PASSPORT",
                "subjectIdNumber", "8501015009087"
            )
        );

        DocumentVerificationResponse unreadableResponse = DocumentVerificationResponse.unreadable(
            DocumentType.PASSPORT, "Image too blurry to process");

        when(documentVerificationService.verifyDocument(any(DocumentVerificationRequest.class)))
            .thenReturn(unreadableResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertEquals(DocumentVerificationStatus.UNREADABLE.toString(), result.get("status"));

        verify(documentVerificationService).verifyDocument(
            any(DocumentVerificationRequest.class));
    }

    @Test
    void testHandleExpiredDocument() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of(
                "documentReference", "DOC-2024-005",
                "documentType", "DRIVERS_LICENSE",
                "subjectIdNumber", "8501015009087"
            )
        );

        DocumentVerificationResponse expiredResponse = new DocumentVerificationResponse(
            DocumentVerificationStatus.EXPIRED,
            DocumentType.DRIVERS_LICENSE,
            Map.of("expiryDate", "2020-01-01"),
            0.92,
            "Document has expired",
            null
        );

        when(documentVerificationService.verifyDocument(any(DocumentVerificationRequest.class)))
            .thenReturn(expiredResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertEquals(DocumentVerificationStatus.EXPIRED.toString(), result.get("status"));

        verify(documentVerificationService).verifyDocument(
            any(DocumentVerificationRequest.class));
    }

    @Test
    void testHandleDocumentNotFound() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of(
                "documentReference", "DOC-NONEXISTENT",
                "documentType", "IDENTITY_DOCUMENT",
                "subjectIdNumber", "8501015009087"
            )
        );

        DocumentVerificationResponse notFoundResponse = new DocumentVerificationResponse(
            DocumentVerificationStatus.NOT_FOUND, null, null, 0.0,
            "Document not found in storage", null);

        when(documentVerificationService.verifyDocument(any(DocumentVerificationRequest.class)))
            .thenReturn(notFoundResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertEquals(DocumentVerificationStatus.NOT_FOUND.toString(), result.get("status"));

        verify(documentVerificationService).verifyDocument(
            any(DocumentVerificationRequest.class));
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
            Map.of(
                "documentReference", "DOC-2024-006",
                "documentType", "BANK_STATEMENT",
                "subjectIdNumber", "8501015009087"
            )
        );

        DocumentVerificationResponse pendingResponse = new DocumentVerificationResponse(
            DocumentVerificationStatus.PENDING, DocumentType.BANK_STATEMENT,
            null, 0.0, "Verification in progress", null);

        when(documentVerificationService.verifyDocument(any(DocumentVerificationRequest.class)))
            .thenReturn(pendingResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertEquals(DocumentVerificationStatus.PENDING.toString(), result.get("status"));

        verify(documentVerificationService).verifyDocument(
            any(DocumentVerificationRequest.class));
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
            Map.of(
                "documentReference", "DOC-2024-007",
                "documentType", "IDENTITY_DOCUMENT",
                "subjectIdNumber", "8501015009087"
            )
        );

        DocumentVerificationResponse errorResponse =
            DocumentVerificationResponse.error("OCR service unavailable");

        when(documentVerificationService.verifyDocument(any(DocumentVerificationRequest.class)))
            .thenReturn(errorResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SYSTEM_OUTAGE.toString(), result.get("outcome"));
        assertEquals(DocumentVerificationStatus.ERROR.toString(), result.get("status"));

        verify(documentVerificationService).verifyDocument(
            any(DocumentVerificationRequest.class));
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
            Map.of("documentType", "IDENTITY_DOCUMENT")
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));

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
            Map.of(
                "documentReference", "DOC-2024-008",
                "documentType", "IDENTITY_DOCUMENT",
                "subjectIdNumber", "8501015009087"
            )
        );

        when(documentVerificationService.verifyDocument(any(DocumentVerificationRequest.class)))
            .thenThrow(new PermanentException("Document verification API error"));

        // Act & Assert
        assertThrows(PermanentException.class, () -> handler.handle(command));

        verify(documentVerificationService).verifyDocument(
            any(DocumentVerificationRequest.class));
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
                "documentReference", "DOC-2024-009",
                "documentType", "IDENTITY_DOCUMENT",
                "subjectIdNumber", "8501015009087"
            )
        );

        when(documentVerificationService.verifyDocument(any(DocumentVerificationRequest.class)))
            .thenThrow(new TransientException("Network timeout"));

        // Act & Assert
        assertThrows(TransientException.class, () -> handler.handle(command));

        verify(documentVerificationService).verifyDocument(
            any(DocumentVerificationRequest.class));
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
                "documentReference", "DOC-2024-010",
                "documentType", "IDENTITY_DOCUMENT",
                "subjectIdNumber", "8501015009087"
            )
        );

        DocumentVerificationResponse verifiedResponse = DocumentVerificationResponse.verified(
            DocumentType.IDENTITY_DOCUMENT,
            Map.of("fullName", "John Doe"),
            0.95,
            "All fields verified");

        when(documentVerificationService.verifyDocument(any(DocumentVerificationRequest.class)))
            .thenReturn(verifiedResponse);

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());

        verify(documentVerificationService).verifyDocument(
            any(DocumentVerificationRequest.class));
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
                "documentReference", "DOC-2024-011",
                "documentType", "IDENTITY_DOCUMENT",
                "subjectIdNumber", "8501015009087"
            )
        );

        when(documentVerificationService.verifyDocument(any(DocumentVerificationRequest.class)))
            .thenThrow(new PermanentException("Service error"));

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL, result.outcome());
        assertTrue(result.failureReason().contains("Document verification failed"));

        verify(documentVerificationService).verifyDocument(
            any(DocumentVerificationRequest.class));
    }
}
