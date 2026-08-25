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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import verigate.adapter.document.domain.models.CipcCompanyLookupResult;
import verigate.adapter.document.domain.models.CipcCrossValidationResult;
import verigate.adapter.document.domain.models.CipcDocumentAnalysisResult;
import verigate.adapter.document.domain.models.DocumentType;
import verigate.adapter.document.domain.models.DocumentVerificationRequest;
import verigate.adapter.document.domain.models.DocumentVerificationResponse;
import verigate.adapter.document.domain.models.DocumentVerificationStatus;
import verigate.adapter.document.domain.models.FieldMatchStatus;
import verigate.adapter.document.domain.services.DocumentImageFetcher;
import verigate.adapter.document.domain.services.DocumentPageRasterizer;
import verigate.adapter.document.domain.services.DocumentVerificationService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;

class DefaultVerifyDocumentCommandHandlerTest {

    @Mock
    private DocumentVerificationService documentVerificationService;

    @Mock
    private DocumentImageFetcher imageFetcher;

    @Mock
    private DocumentPageRasterizer pageRasterizer;

    private DefaultVerifyDocumentCommandHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new DefaultVerifyDocumentCommandHandler(documentVerificationService);
    }

    private VerifyPartyCommand cipcRegistrationCommand(String documentReference) {
        return cipcRegistrationCommand(documentReference, "uploads/cipc-001.jpg");
    }

    private VerifyPartyCommand cipcRegistrationCommand(String documentReference, String s3ObjectKey) {
        return new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of(
                "documentReference", documentReference,
                "documentType", "CIPC_REGISTRATION",
                "s3BucketName", "verigate-docs",
                "s3ObjectKey", s3ObjectKey
            )
        );
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

    @Test
    void testHandleCipcRegistrationVerified() throws Exception {
        // Arrange
        handler = new DefaultVerifyDocumentCommandHandler(documentVerificationService, imageFetcher);
        VerifyPartyCommand command = cipcRegistrationCommand("DOC-CIPC-001");

        when(imageFetcher.fetch("verigate-docs", "uploads/cipc-001.jpg"))
            .thenReturn(new byte[] {1, 2, 3});

        CipcCrossValidationResult crossValidation = new CipcCrossValidationResult(
            true, true, true,
            Map.of("companyName", FieldMatchStatus.MATCH),
            Map.of("companyName", "ACME TRADING PROPRIETARY LIMITED"),
            null);

        CipcDocumentAnalysisResult analysis = new CipcDocumentAnalysisResult(
            Map.of("companyName", "Acme Trading (Pty) Ltd"),
            0.95, 0.9, List.of(), List.of(), 95,
            crossValidation, null);

        when(documentVerificationService.verifyCipcRegistrationDocument(
            any(DocumentVerificationRequest.class), any(byte[].class), anyString()))
            .thenReturn(analysis);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertEquals(DocumentVerificationStatus.VERIFIED.toString(), result.get("status"));
        assertEquals(DocumentType.CIPC_REGISTRATION.toString(), result.get("documentType"));

        verify(imageFetcher).fetch("verigate-docs", "uploads/cipc-001.jpg");
    }

    @Test
    void testHandleCipcRegistrationMismatchWhenCipcFieldsDisagree() throws Exception {
        handler = new DefaultVerifyDocumentCommandHandler(documentVerificationService, imageFetcher);
        VerifyPartyCommand command = cipcRegistrationCommand("DOC-CIPC-002");

        when(imageFetcher.fetch(anyString(), anyString())).thenReturn(new byte[] {1});

        CipcCrossValidationResult crossValidation = new CipcCrossValidationResult(
            true, true, true,
            Map.of("companyName", FieldMatchStatus.MISMATCH),
            Map.of("companyName", "Some Other Company"),
            null);

        CipcDocumentAnalysisResult analysis = new CipcDocumentAnalysisResult(
            Map.of("companyName", "Acme Trading (Pty) Ltd"),
            0.9, 0.9, List.of(), List.of(), 90,
            crossValidation, null);

        when(documentVerificationService.verifyCipcRegistrationDocument(
            any(DocumentVerificationRequest.class), any(byte[].class), anyString()))
            .thenReturn(analysis);

        Map<String, String> result = handler.handle(command);

        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertEquals(DocumentVerificationStatus.MISMATCH.toString(), result.get("status"));
    }

    @Test
    void testHandleCipcRegistrationCompanyNotFoundInCipc() throws Exception {
        handler = new DefaultVerifyDocumentCommandHandler(documentVerificationService, imageFetcher);
        VerifyPartyCommand command = cipcRegistrationCommand("DOC-CIPC-003");

        when(imageFetcher.fetch(anyString(), anyString())).thenReturn(new byte[] {1});

        CipcCrossValidationResult crossValidation =
            CipcCrossValidationResult.unavailable("No CIPC company found");

        CipcDocumentAnalysisResult analysis = new CipcDocumentAnalysisResult(
            Map.of("companyName", "Acme Trading (Pty) Ltd"),
            0.9, 0.9, List.of(), List.of(), 90,
            crossValidation, null);

        when(documentVerificationService.verifyCipcRegistrationDocument(
            any(DocumentVerificationRequest.class), any(byte[].class), anyString()))
            .thenReturn(analysis);

        Map<String, String> result = handler.handle(command);

        // CIPC unavailable/company-not-found degrades to reporting AI-only results rather than
        // failing the whole verification (story 2.1 design decision).
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertEquals(DocumentVerificationStatus.VERIFIED.toString(), result.get("status"));
        assertTrue(result.get("cipcCrossValidation").contains("unavailable"));
    }

    @Test
    void testHandleCipcRegistrationSuspectedFraudOnLowAuthenticity() throws Exception {
        handler = new DefaultVerifyDocumentCommandHandler(documentVerificationService, imageFetcher);
        VerifyPartyCommand command = cipcRegistrationCommand("DOC-CIPC-004");

        when(imageFetcher.fetch(anyString(), anyString())).thenReturn(new byte[] {1});

        CipcCrossValidationResult crossValidation =
            CipcCrossValidationResult.unavailable("not checked");

        CipcDocumentAnalysisResult analysis = new CipcDocumentAnalysisResult(
            Map.of("companyName", "Acme Trading (Pty) Ltd"),
            0.2, 0.5, List.of("Font inconsistency detected"), List.of("Font mismatch"), 20,
            crossValidation, null);

        when(documentVerificationService.verifyCipcRegistrationDocument(
            any(DocumentVerificationRequest.class), any(byte[].class), anyString()))
            .thenReturn(analysis);

        Map<String, String> result = handler.handle(command);

        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertEquals(DocumentVerificationStatus.SUSPECTED_FRAUD.toString(), result.get("status"));
    }

    @Test
    void testHandleCipcRegistrationAiAnalysisUnavailable() throws Exception {
        handler = new DefaultVerifyDocumentCommandHandler(documentVerificationService, imageFetcher);
        VerifyPartyCommand command = cipcRegistrationCommand("DOC-CIPC-005");

        when(imageFetcher.fetch(anyString(), anyString())).thenReturn(new byte[] {1});

        CipcDocumentAnalysisResult analysis =
            CipcDocumentAnalysisResult.aiUnavailable("Bedrock unavailable");

        when(documentVerificationService.verifyCipcRegistrationDocument(
            any(DocumentVerificationRequest.class), any(byte[].class), anyString()))
            .thenReturn(analysis);

        Map<String, String> result = handler.handle(command);

        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertEquals(DocumentVerificationStatus.UNREADABLE.toString(), result.get("status"));
    }

    @Test
    void testHandleCipcRegistrationMissingS3DetailsThrows() {
        handler = new DefaultVerifyDocumentCommandHandler(documentVerificationService, imageFetcher);
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of(
                "documentReference", "DOC-CIPC-006",
                "documentType", "CIPC_REGISTRATION"
            )
        );

        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));

        verifyNoInteractions(imageFetcher, documentVerificationService);
    }

    @Test
    void testHandleCipcRegistrationWithoutImageFetcherConfiguredThrows() {
        // Default constructor (no imageFetcher) — CIPC_REGISTRATION should fail clearly rather
        // than NPE.
        VerifyPartyCommand command = cipcRegistrationCommand("DOC-CIPC-007");

        assertThrows(PermanentException.class, () -> handler.handle(command));
    }

    @Test
    void testHandleCipcRegistrationPdfUploadRasterizesFirstPage() throws Exception {
        handler = new DefaultVerifyDocumentCommandHandler(
            documentVerificationService, imageFetcher, pageRasterizer);
        VerifyPartyCommand command =
            cipcRegistrationCommand("DOC-CIPC-008", "uploads/cipc-008.pdf");

        byte[] pdfBytes = "fake-pdf-bytes".getBytes();
        byte[] pngBytes = "fake-png-bytes".getBytes();
        when(imageFetcher.fetch("verigate-docs", "uploads/cipc-008.pdf")).thenReturn(pdfBytes);
        when(pageRasterizer.rasterizeFirstPage(pdfBytes)).thenReturn(pngBytes);

        CipcCrossValidationResult crossValidation = new CipcCrossValidationResult(
            true, true, true,
            Map.of("companyName", FieldMatchStatus.MATCH),
            Map.of("companyName", "ACME TRADING PROPRIETARY LIMITED"),
            null);
        CipcDocumentAnalysisResult analysis = new CipcDocumentAnalysisResult(
            Map.of("companyName", "Acme Trading (Pty) Ltd"),
            0.95, 0.9, List.of(), List.of(), 95,
            crossValidation, null);

        when(documentVerificationService.verifyCipcRegistrationDocument(
            any(DocumentVerificationRequest.class), eq(pngBytes), eq("image/png")))
            .thenReturn(analysis);

        Map<String, String> result = handler.handle(command);

        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertEquals(DocumentVerificationStatus.VERIFIED.toString(), result.get("status"));

        verify(pageRasterizer).rasterizeFirstPage(pdfBytes);
        // The raw PDF bytes should never reach the AI analysis call directly.
        verify(documentVerificationService, never()).verifyCipcRegistrationDocument(
            any(DocumentVerificationRequest.class), eq(pdfBytes), anyString());
    }

    @Test
    void testHandleCipcRegistrationPdfWithoutRasterizerConfiguredThrows() {
        // 2-arg constructor (no pageRasterizer) — a .pdf upload should fail clearly rather
        // than send raw PDF bytes to Bedrock mislabeled as an image.
        handler = new DefaultVerifyDocumentCommandHandler(documentVerificationService, imageFetcher);
        VerifyPartyCommand command =
            cipcRegistrationCommand("DOC-CIPC-009", "uploads/cipc-009.pdf");

        when(imageFetcher.fetch("verigate-docs", "uploads/cipc-009.pdf"))
            .thenReturn("fake-pdf-bytes".getBytes());

        assertThrows(PermanentException.class, () -> handler.handle(command));

        verifyNoInteractions(documentVerificationService);
    }
}
