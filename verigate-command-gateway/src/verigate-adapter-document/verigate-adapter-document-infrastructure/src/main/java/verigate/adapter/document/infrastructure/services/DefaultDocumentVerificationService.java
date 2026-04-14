/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.infrastructure.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.document.domain.models.DocumentType;
import verigate.adapter.document.domain.models.DocumentVerificationRequest;
import verigate.adapter.document.domain.models.DocumentVerificationResponse;
import verigate.adapter.document.domain.services.DocumentVerificationService;
import verigate.adapter.document.infrastructure.http.DocumentApiAdapter;
import verigate.adapter.document.infrastructure.http.dto.DocumentVerificationRequestDto;
import verigate.adapter.document.infrastructure.http.dto.DocumentVerificationResponseDto;
import verigate.adapter.document.infrastructure.mappers.DocumentDtoMapper;

/**
 * Default implementation of the {@link DocumentVerificationService} using the infrastructure HTTP
 * adapter.
 */
public class DefaultDocumentVerificationService implements DocumentVerificationService {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultDocumentVerificationService.class);

  private final DocumentApiAdapter apiAdapter;
  private final DocumentDtoMapper dtoMapper;
  private final AiDocumentAnalyzer aiDocumentAnalyzer;

  public DefaultDocumentVerificationService(
      DocumentApiAdapter apiAdapter, DocumentDtoMapper dtoMapper,
      AiDocumentAnalyzer aiDocumentAnalyzer) {
    this.apiAdapter = apiAdapter;
    this.dtoMapper = dtoMapper;
    this.aiDocumentAnalyzer = aiDocumentAnalyzer;
  }

  public DefaultDocumentVerificationService(
      DocumentApiAdapter apiAdapter, DocumentDtoMapper dtoMapper) {
    this(apiAdapter, dtoMapper, null);
  }

  @Override
  public DocumentVerificationResponse verifyDocument(DocumentVerificationRequest request) {
    logger.info(
        "Verifying document of type: {} for reference: [MASKED]",
        request.documentType());

    try {
      DocumentVerificationRequestDto requestDto = dtoMapper.mapToRequestDto(request);

      DocumentVerificationResponseDto responseDto = apiAdapter.verifyDocument(requestDto);

      DocumentVerificationResponse response = dtoMapper.mapToResponse(responseDto);

      logger.info(
          "Document verification completed with status: {}, confidence: {}",
          response.status(),
          response.confidenceScore());
      return response;

    } catch (PermanentException e) {
      logger.error(
          "Permanent error during document verification: {}",
          e.getMessage());
      return DocumentVerificationResponse.error(
          "Document verification failed: " + e.getMessage());

    } catch (TransientException e) {
      logger.warn(
          "Transient error during document verification: {}",
          e.getMessage());
      throw e;

    } catch (Exception e) {
      logger.error(
          "Unexpected error during document verification: {}",
          e.getMessage(),
          e);
      return DocumentVerificationResponse.error("Unexpected error: " + e.getMessage());
    }
  }

  /**
   * Performs AI-powered document analysis on raw image bytes. This is separate from the main
   * verification flow because image bytes must be fetched from S3 by the caller.
   *
   * @param imageBytes the raw image bytes
   * @param mediaType the image media type (e.g., "image/jpeg")
   * @param request the original verification request for cross-validation
   * @return the AI analysis result, or null if analyzer is not available
   */
  public AiDocumentAnalyzer.DocumentAnalysisResult analyzeWithAi(
      byte[] imageBytes, String mediaType, DocumentVerificationRequest request) {
    if (aiDocumentAnalyzer == null) {
      return null;
    }

    try {
      AiDocumentAnalyzer.DocumentAnalysisResult aiResult =
          aiDocumentAnalyzer.analyze(
              imageBytes,
              mediaType != null ? mediaType : "image/jpeg",
              request.subjectName(),
              request.subjectIdNumber(),
              request.documentType().name());

      logger.info("AI document analysis: authenticityScore={}, overallConfidence={}, anomalies={}",
          aiResult.authenticityScore(), aiResult.overallConfidence(),
          aiResult.anomalies().size());

      // Run SA ID validation on identity documents
      aiResult = applyPostExtractionValidation(aiResult, request.documentType());

      return aiResult;

    } catch (Exception e) {
      logger.warn("AI document analysis failed (non-blocking): {}", e.getMessage());
      return null;
    }
  }

  private static final Set<DocumentType> SA_ID_DOCUMENT_TYPES = Set.of(
      DocumentType.IDENTITY_DOCUMENT, DocumentType.PASSPORT, DocumentType.DRIVERS_LICENSE);

  private AiDocumentAnalyzer.DocumentAnalysisResult applyPostExtractionValidation(
      AiDocumentAnalyzer.DocumentAnalysisResult result, DocumentType documentType) {

    if (!SA_ID_DOCUMENT_TYPES.contains(documentType)) {
      return result;
    }

    AiDocumentAnalyzer.ExtractedField idField = result.extractedFields().get("idNumber");
    if (idField == null || idField.value() == null) {
      return result;
    }

    List<AiDocumentAnalyzer.ValidationCheck> checks = SaIdValidator.validate(idField.value());
    logger.info("SA ID validation for extracted ID: {} checks, pass={}",
        checks.size(), checks.stream().allMatch(c -> "PASS".equals(c.status())));

    return result.withValidationChecks(checks);
  }
}
