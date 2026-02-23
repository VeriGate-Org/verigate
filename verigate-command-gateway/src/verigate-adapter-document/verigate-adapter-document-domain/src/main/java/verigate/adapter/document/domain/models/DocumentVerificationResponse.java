/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.domain.models;

import java.util.Map;

/**
 * Represents the response from a document verification request.
 */
public record DocumentVerificationResponse(
    DocumentVerificationStatus status,
    DocumentType documentType,
    Map<String, String> extractedData,
    double confidenceScore,
    String matchDetails,
    String verificationNotes
) {

  /**
   * Creates a verified response with extracted data and confidence score.
   */
  public static DocumentVerificationResponse verified(
      DocumentType documentType,
      Map<String, String> extractedData,
      double confidenceScore,
      String matchDetails) {
    return new DocumentVerificationResponse(
        DocumentVerificationStatus.VERIFIED,
        documentType,
        extractedData,
        confidenceScore,
        matchDetails,
        "Document verified successfully"
    );
  }

  /**
   * Creates a mismatch response indicating data inconsistencies.
   */
  public static DocumentVerificationResponse mismatch(
      DocumentType documentType,
      Map<String, String> extractedData,
      double confidenceScore,
      String matchDetails) {
    return new DocumentVerificationResponse(
        DocumentVerificationStatus.MISMATCH,
        documentType,
        extractedData,
        confidenceScore,
        matchDetails,
        "Document data does not match subject information"
    );
  }

  /**
   * Creates a suspected fraud response.
   */
  public static DocumentVerificationResponse suspectedFraud(
      DocumentType documentType,
      String matchDetails,
      String verificationNotes) {
    return new DocumentVerificationResponse(
        DocumentVerificationStatus.SUSPECTED_FRAUD,
        documentType,
        Map.of(),
        0.0,
        matchDetails,
        verificationNotes
    );
  }

  /**
   * Creates an unreadable response when the document cannot be processed.
   */
  public static DocumentVerificationResponse unreadable(
      DocumentType documentType,
      String verificationNotes) {
    return new DocumentVerificationResponse(
        DocumentVerificationStatus.UNREADABLE,
        documentType,
        Map.of(),
        0.0,
        "Document could not be read",
        verificationNotes
    );
  }

  /**
   * Creates an error response with the specified error message.
   */
  public static DocumentVerificationResponse error(String errorMessage) {
    return new DocumentVerificationResponse(
        DocumentVerificationStatus.ERROR,
        null,
        Map.of(),
        0.0,
        null,
        errorMessage
    );
  }

  /**
   * Checks if the response indicates a successful verification.
   */
  public boolean isSuccess() {
    return status == DocumentVerificationStatus.VERIFIED;
  }

  /**
   * Checks if the response indicates an error occurred.
   */
  public boolean isError() {
    return status == DocumentVerificationStatus.ERROR;
  }
}
