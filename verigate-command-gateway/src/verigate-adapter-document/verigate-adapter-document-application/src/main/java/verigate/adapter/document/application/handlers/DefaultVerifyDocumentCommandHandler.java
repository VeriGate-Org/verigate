/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.application.handlers;

import domain.exceptions.InvariantViolationException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.document.domain.constants.DomainConstants;
import verigate.adapter.document.domain.handlers.VerifyDocumentCommandHandler;
import verigate.adapter.document.domain.mappers.DocumentVerificationMapper;
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
import verigate.verification.cg.domain.models.VerificationResult;

/**
 * Default implementation for processing document verification commands.
 */
public class DefaultVerifyDocumentCommandHandler
    implements VerifyDocumentCommandHandler {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultVerifyDocumentCommandHandler.class);

  // Thresholds for flagging suspected fraud from AI analysis (story 2.1). See
  // CipcDocumentAnalysisResult javadoc for the caveat on overallTamperingScore's direction —
  // these are a starting point, not tuned against real documents yet.
  //
  // authenticityScore's direction is unambiguous (the prompt directly defines it as a 0.0-1.0
  // authenticity assessment), so it alone can decisively flag fraud. overallTamperingScore's
  // direction is NOT confirmed against real model output (see the javadoc caveat) -- until it
  // is, it's only used to corroborate an already-borderline authenticity score, never as a
  // sole/independent trigger. This bounds the damage if the direction assumption turns out to
  // be backwards: a wrongly-interpreted tampering score can no longer, by itself, HARD_FAIL an
  // otherwise clean-looking document.
  private static final double SUSPECTED_FRAUD_AUTHENTICITY_THRESHOLD = 0.5;
  private static final double SUSPECTED_FRAUD_AUTHENTICITY_CORROBORATION_THRESHOLD = 0.75;
  private static final int SUSPECTED_FRAUD_TAMPERING_THRESHOLD = 40;

  private final DocumentVerificationService documentVerificationService;
  private final DocumentImageFetcher imageFetcher;
  private final DocumentPageRasterizer pageRasterizer;

  /**
   * Constructor for default document verification handler, with CIPC registration document
   * support (story 2.1) including PDF uploads.
   *
   * @param documentVerificationService the document verification service
   * @param imageFetcher fetches uploaded document image bytes for AI analysis
   * @param pageRasterizer converts a PDF's first page to an image for PDF uploads
   */
  public DefaultVerifyDocumentCommandHandler(
      DocumentVerificationService documentVerificationService,
      DocumentImageFetcher imageFetcher,
      DocumentPageRasterizer pageRasterizer) {
    this.documentVerificationService = documentVerificationService;
    this.imageFetcher = imageFetcher;
    this.pageRasterizer = pageRasterizer;
  }

  /**
   * Constructor without PDF support. A {@code CIPC_REGISTRATION} command for a {@code .pdf}
   * upload will fail on an instance built this way — kept for existing callers/tests that don't
   * exercise that path.
   *
   * @param documentVerificationService the document verification service
   * @param imageFetcher fetches uploaded document image bytes for AI analysis
   */
  public DefaultVerifyDocumentCommandHandler(
      DocumentVerificationService documentVerificationService,
      DocumentImageFetcher imageFetcher) {
    this(documentVerificationService, imageFetcher, null);
  }

  /**
   * Constructor without CIPC registration document support at all. {@code CIPC_REGISTRATION}
   * commands will fail on an instance built this way — kept for existing callers/tests that
   * don't exercise that path.
   *
   * @param documentVerificationService the document verification service
   */
  public DefaultVerifyDocumentCommandHandler(
      DocumentVerificationService documentVerificationService) {
    this(documentVerificationService, null, null);
  }

  /**
   * Handles document verification using the document verification API.
   *
   * @param command the command to handle
   * @return map containing verification results
   */
  @Override
  public Map<String, String> handle(VerifyPartyCommand command)
      throws TransientException, PermanentException, InvariantViolationException {

    logger.info("Starting document verification for command: {}", command.getId());

    try {
      DocumentVerificationRequest verificationRequest =
          DocumentVerificationMapper.mapToDocumentVerificationRequestDefault(command);

      logger.info(
          "Mapped command to document verification request for reference: [MASKED]");

      if (verificationRequest.documentType() == DocumentType.CIPC_REGISTRATION) {
        return handleCipcRegistrationVerification(command, verificationRequest);
      }
      return handleStandardVerification(command, verificationRequest);

    } catch (TransientException | PermanentException e) {
      logger.error(
          "Document verification failed for command {}: {}",
          command.getId(),
          e.getMessage(),
          e);
      throw e;
    } catch (IllegalArgumentException e) {
      logger.error(
          "Invalid document verification request for command {}: {}",
          command.getId(),
          e.getMessage());
      throw e;
    } catch (Exception e) {
      logger.error(
          "Unexpected error during document verification for command {}: {}",
          command.getId(),
          e.getMessage(),
          e);
      throw new PermanentException("Unexpected document verification error", e);
    }
  }

  private Map<String, String> handleStandardVerification(
      VerifyPartyCommand command, DocumentVerificationRequest verificationRequest) {

    DocumentVerificationResponse verificationResponse =
        documentVerificationService.verifyDocument(verificationRequest);

    VerificationOutcome outcome = mapStatusToOutcome(verificationResponse.status());

    logger.info(
        "Document verification completed with outcome: {} for status: {}",
        outcome,
        verificationResponse.status());

    String extractedFieldsSummary = verificationResponse.extractedData() != null
        ? String.valueOf(verificationResponse.extractedData().size()) + " fields"
        : "0 fields";

    // Prefer the original metadata documentType (e.g. "passport") since the BFF
    // label map expects these keys; fall back to the response's enum value.
    String originalDocType = DocumentVerificationMapper.extractDocumentType(command);
    String resolvedDocType = originalDocType != null
        ? originalDocType
        : (verificationResponse.documentType() != null
            ? verificationResponse.documentType().toString()
            : "");

    String documentNumber = DocumentVerificationMapper.extractDocumentReference(command);

    Map<String, String> result = new HashMap<>();
    result.put(DomainConstants.RESULT_OUTCOME, outcome.toString());
    result.put(DomainConstants.RESULT_STATUS, verificationResponse.status().toString());
    result.put(DomainConstants.RESULT_DOCUMENT_TYPE, resolvedDocType);
    result.put(DomainConstants.RESULT_DOCUMENT_NUMBER,
        documentNumber != null ? documentNumber : "");
    result.put(DomainConstants.RESULT_CONFIDENCE_SCORE,
        String.valueOf(verificationResponse.confidenceScore()));
    result.put(DomainConstants.RESULT_MATCH_DETAILS,
        verificationResponse.matchDetails() != null
            ? verificationResponse.matchDetails() : "");
    result.put(DomainConstants.RESULT_EXTRACTED_FIELDS, extractedFieldsSummary);

    return result;
  }

  /**
   * Handles {@code CIPC_REGISTRATION} document verification (story 2.1): fetches the uploaded
   * image from S3, runs AI extraction with authenticity/tampering scoring, and cross-validates
   * the extracted fields against a live CIPC lookup. Bypasses the placeholder
   * {@code DocumentApiAdapter} call entirely for this document type.
   */
  private Map<String, String> handleCipcRegistrationVerification(
      VerifyPartyCommand command, DocumentVerificationRequest verificationRequest) {

    if (imageFetcher == null) {
      throw new PermanentException(
          "CIPC registration document verification requires an image fetcher, but none was "
              + "configured");
    }
    if (verificationRequest.s3BucketName() == null || verificationRequest.s3ObjectKey() == null) {
      throw new IllegalArgumentException(
          "S3 bucket name and object key are required for CIPC_REGISTRATION document "
              + "verification");
    }

    byte[] documentBytes = imageFetcher.fetch(
        verificationRequest.s3BucketName(), verificationRequest.s3ObjectKey());

    byte[] imageBytes;
    String mediaType;
    if (isPdf(verificationRequest.s3ObjectKey())) {
      if (pageRasterizer == null) {
        throw new PermanentException(
            "CIPC registration document verification requires a PDF page rasterizer for PDF "
                + "uploads, but none was configured");
      }
      imageBytes = pageRasterizer.rasterizeFirstPage(documentBytes);
      mediaType = "image/png";
    } else {
      imageBytes = documentBytes;
      mediaType = resolveMediaType(verificationRequest.s3ObjectKey());
    }

    CipcDocumentAnalysisResult analysis = documentVerificationService
        .verifyCipcRegistrationDocument(verificationRequest, imageBytes, mediaType);

    DocumentVerificationStatus status = mapAnalysisToStatus(analysis);
    VerificationOutcome outcome = mapStatusToOutcome(status);

    logger.info(
        "CIPC registration document verification completed with outcome: {} for status: {}",
        outcome,
        status);

    String documentNumber = DocumentVerificationMapper.extractDocumentReference(command);

    Map<String, String> result = new HashMap<>();
    result.put(DomainConstants.RESULT_OUTCOME, outcome.toString());
    result.put(DomainConstants.RESULT_STATUS, status.toString());
    result.put(DomainConstants.RESULT_DOCUMENT_TYPE, DocumentType.CIPC_REGISTRATION.toString());
    result.put(DomainConstants.RESULT_DOCUMENT_NUMBER,
        documentNumber != null ? documentNumber : "");
    result.put(DomainConstants.RESULT_CONFIDENCE_SCORE,
        String.valueOf(analysis.overallConfidence()));
    result.put(DomainConstants.RESULT_MATCH_DETAILS, buildMatchDetails(analysis));
    result.put(DomainConstants.RESULT_EXTRACTED_FIELDS,
        analysis.extractedFields().size() + " fields");
    result.put(DomainConstants.RESULT_CIPC_CROSS_VALIDATION,
        summarizeCrossValidation(analysis.crossValidation()));

    return result;
  }

  private DocumentVerificationStatus mapAnalysisToStatus(CipcDocumentAnalysisResult analysis) {
    if (!analysis.isAiAnalysisAvailable()) {
      return DocumentVerificationStatus.UNREADABLE;
    }

    if (isLowConfidenceExtraction(analysis)) {
      // Don't trust fraud/cross-validation signals derived from a poorly-extracted read — a
      // blurry or badly-captured upload should be flagged for re-upload rather than silently
      // scored against thresholds tuned for a properly-read document.
      return DocumentVerificationStatus.UNREADABLE;
    }

    // authenticityScore alone can decisively flag fraud (its direction is well-defined).
    // overallTamperingScore only corroborates an already-borderline authenticity score — see
    // the field javadoc above for why it can't be trusted as an independent trigger yet.
    boolean tamperingSuspected =
        analysis.authenticityScore() < SUSPECTED_FRAUD_AUTHENTICITY_THRESHOLD
            || (analysis.authenticityScore() < SUSPECTED_FRAUD_AUTHENTICITY_CORROBORATION_THRESHOLD
                && analysis.overallTamperingScore() < SUSPECTED_FRAUD_TAMPERING_THRESHOLD);
    if (tamperingSuspected) {
      return DocumentVerificationStatus.SUSPECTED_FRAUD;
    }

    CipcCrossValidationResult crossValidation = analysis.crossValidation();
    if (crossValidation.cipcLookupPerformed()
        && (!crossValidation.companyFound()
            || !crossValidation.companyActive()
            || crossValidation.hasMismatch())) {
      // Company-not-found / deregistered / mismatched-field: treated as a soft-fail-level
      // mismatch for now, not a hard fail. Whether a deregistered company should hard-fail is
      // an open business decision (story 2.1, deferred to R1) — revisit mapStatusToOutcome's
      // MISMATCH -> SOFT_FAIL mapping if that decision changes.
      return DocumentVerificationStatus.MISMATCH;
    }

    return DocumentVerificationStatus.VERIFIED;
  }

  /**
   * Checks whether the AI's average field-extraction confidence is too low to trust — e.g. a
   * blurry, low-resolution, or poorly-cropped upload. Reuses {@code
   * DomainConstants.DEFAULT_CONFIDENCE_THRESHOLD}, which existed in this codebase but was never
   * wired into any actual check until now.
   *
   * <p>Known limitation: {@code overallConfidence} averages confidence only across fields the
   * AI actually extracted a value for — a document where most fields came back null (extraction
   * failed outright) but the one or two extracted fields happen to be high-confidence would not
   * be caught by this check. Revisit if that proves to be a real-world gap.
   */
  private boolean isLowConfidenceExtraction(CipcDocumentAnalysisResult analysis) {
    return analysis.overallConfidence() < DomainConstants.DEFAULT_CONFIDENCE_THRESHOLD;
  }

  private String buildMatchDetails(CipcDocumentAnalysisResult analysis) {
    if (!analysis.isAiAnalysisAvailable()) {
      return "AI document analysis unavailable: " + analysis.errorMessage();
    }
    if (isLowConfidenceExtraction(analysis)) {
      return String.format(
          "Extraction confidence too low to trust (%.0f%%, threshold %.0f%%) — document may be "
              + "blurry, low resolution, or improperly captured; request a clearer upload",
          analysis.overallConfidence() * 100,
          DomainConstants.DEFAULT_CONFIDENCE_THRESHOLD * 100);
    }
    StringBuilder details = new StringBuilder();
    details.append(summarizeCrossValidation(analysis.crossValidation()));
    if (!analysis.anomalies().isEmpty()) {
      details.append("; anomalies: ").append(String.join("; ", analysis.anomalies()));
    }
    if (!analysis.tamperingFlags().isEmpty()) {
      details.append("; tampering flags: ").append(String.join("; ", analysis.tamperingFlags()));
    }
    return details.toString();
  }

  private String summarizeCrossValidation(CipcCrossValidationResult crossValidation) {
    if (!crossValidation.cipcLookupPerformed()) {
      return "CIPC cross-check unavailable: " + crossValidation.unavailableReason();
    }
    if (!crossValidation.companyFound()) {
      return "CIPC: no company found for extracted registration number";
    }
    long matched = crossValidation.fieldStatuses().values().stream()
        .filter(fieldStatus -> fieldStatus == FieldMatchStatus.MATCH)
        .count();
    return String.format(
        "CIPC: company found, %s, %d/%d fields matched",
        crossValidation.companyActive() ? "active" : "not active",
        matched,
        crossValidation.fieldStatuses().size());
  }

  /**
   * Resolves the image media type from the S3 object key's extension. PDF uploads are handled
   * separately via {@link #pageRasterizer} (see {@link #handleCipcRegistrationVerification}) —
   * this method is only used for direct image uploads.
   */
  private String resolveMediaType(String s3ObjectKey) {
    if (s3ObjectKey == null) {
      return "image/jpeg";
    }
    String lower = s3ObjectKey.toLowerCase();
    if (lower.endsWith(".png")) {
      return "image/png";
    }
    if (lower.endsWith(".webp")) {
      return "image/webp";
    }
    return "image/jpeg";
  }

  /**
   * Checks whether the S3 object key refers to a PDF document (by extension). CIPC registration
   * certificates are effectively single-page, so only the first page is analyzed for PDF
   * uploads — see {@link DocumentPageRasterizer} javadoc.
   */
  private boolean isPdf(String s3ObjectKey) {
    return s3ObjectKey != null && s3ObjectKey.toLowerCase().endsWith(".pdf");
  }

  /**
   * Maps a document verification status to a verification outcome.
   *
   * @param status the document verification status
   * @return the corresponding verification outcome
   */
  private VerificationOutcome mapStatusToOutcome(DocumentVerificationStatus status) {
    return switch (status) {
      case VERIFIED -> VerificationOutcome.SUCCEEDED;
      case MISMATCH -> VerificationOutcome.SOFT_FAIL;
      case SUSPECTED_FRAUD -> VerificationOutcome.HARD_FAIL;
      case UNREADABLE -> VerificationOutcome.SOFT_FAIL;
      case EXPIRED -> VerificationOutcome.SOFT_FAIL;
      case NOT_FOUND -> VerificationOutcome.HARD_FAIL;
      case PENDING -> VerificationOutcome.SOFT_FAIL;
      case ERROR -> VerificationOutcome.SYSTEM_OUTAGE;
    };
  }

  /**
   * Alternative async method for non-blocking verification. This method can be used for future
   * async support.
   */
  @Override
  public CompletableFuture<VerificationResult> handleAsync(VerifyPartyCommand command) {
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            Map<String, String> result = handle(command);
            VerificationOutcome outcome = VerificationOutcome.valueOf(
                result.get(DomainConstants.RESULT_OUTCOME));
            String details = result.get(DomainConstants.RESULT_MATCH_DETAILS);
            return new VerificationResult(outcome, details);
          } catch (Exception e) {
            logger.error(
                "Async document verification failed for command {}: {}",
                command.getId(),
                e.getMessage(),
                e);
            return new VerificationResult(
                VerificationOutcome.HARD_FAIL,
                "Document verification failed: " + e.getMessage());
          }
        });
  }
}
