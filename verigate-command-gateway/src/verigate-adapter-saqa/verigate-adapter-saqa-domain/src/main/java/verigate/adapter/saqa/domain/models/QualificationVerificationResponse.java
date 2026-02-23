/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.saqa.domain.models;

import java.util.List;

/**
 * Represents the response from a SAQA qualification verification request.
 */
public record QualificationVerificationResponse(
    QualificationVerificationStatus status,
    List<QualificationRecord> qualifications,
    QualificationRecord matchedQualification,
    double matchConfidence,
    String verificationNotes
) {

  /**
   * Creates a verified response with matched qualification details.
   */
  public static QualificationVerificationResponse verified(
      List<QualificationRecord> qualifications,
      QualificationRecord matchedQualification,
      double matchConfidence) {
    return new QualificationVerificationResponse(
        QualificationVerificationStatus.VERIFIED,
        qualifications,
        matchedQualification,
        matchConfidence,
        "Qualification successfully verified against SAQA registry");
  }

  /**
   * Creates a not found response.
   */
  public static QualificationVerificationResponse notFound(String verificationNotes) {
    return new QualificationVerificationResponse(
        QualificationVerificationStatus.NOT_FOUND,
        List.of(),
        null,
        0.0,
        verificationNotes);
  }

  /**
   * Creates a revoked response.
   */
  public static QualificationVerificationResponse revoked(
      QualificationRecord matchedQualification,
      String verificationNotes) {
    return new QualificationVerificationResponse(
        QualificationVerificationStatus.REVOKED,
        List.of(matchedQualification),
        matchedQualification,
        1.0,
        verificationNotes);
  }

  /**
   * Creates a mismatch response.
   */
  public static QualificationVerificationResponse mismatch(
      List<QualificationRecord> qualifications,
      double matchConfidence,
      String verificationNotes) {
    return new QualificationVerificationResponse(
        QualificationVerificationStatus.MISMATCH,
        qualifications,
        null,
        matchConfidence,
        verificationNotes);
  }

  /**
   * Creates an error response with the specified message.
   */
  public static QualificationVerificationResponse error(String verificationNotes) {
    return new QualificationVerificationResponse(
        QualificationVerificationStatus.ERROR,
        List.of(),
        null,
        0.0,
        verificationNotes);
  }
}
