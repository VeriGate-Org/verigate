/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.domain.mappers;

import verigate.adapter.document.domain.constants.DomainConstants;
import verigate.adapter.document.domain.models.DocumentType;
import verigate.adapter.document.domain.models.DocumentVerificationRequest;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Mapper for converting VerifyPartyCommand to document verification requests.
 */
public interface DocumentVerificationMapper {

  /**
   * Maps a VerifyPartyCommand to a DocumentVerificationRequest.
   *
   * @param command the verification command
   * @return the mapped document verification request
   */
  DocumentVerificationRequest mapToDocumentVerificationRequest(VerifyPartyCommand command);

  /**
   * Default implementation providing mapping logic.
   */
  static DocumentVerificationRequest mapToDocumentVerificationRequestDefault(
      VerifyPartyCommand command) {

    String documentReference = extractDocumentReference(command);

    if (documentReference == null || documentReference.trim().isEmpty()) {
      throw new IllegalArgumentException(
          "Document reference is required for document verification");
    }

    String documentTypeStr = extractDocumentType(command);
    DocumentType documentType = documentTypeStr != null
        ? DocumentType.fromDescription(documentTypeStr)
        : DocumentType.IDENTITY_DOCUMENT;

    String subjectIdNumber = extractSubjectIdNumber(command);
    String subjectName = extractSubjectName(command);
    String s3BucketName = extractS3BucketName(command);
    String s3ObjectKey = extractS3ObjectKey(command);

    return DocumentVerificationRequest.builder()
        .documentReference(documentReference.trim())
        .documentType(documentType)
        .subjectIdNumber(subjectIdNumber != null ? subjectIdNumber.trim() : null)
        .subjectName(subjectName != null ? subjectName.trim() : null)
        .s3BucketName(s3BucketName != null ? s3BucketName.trim() : null)
        .s3ObjectKey(s3ObjectKey != null ? s3ObjectKey.trim() : null)
        .build();
  }

  /**
   * Extracts the document reference from the command metadata.
   */
  static String extractDocumentReference(VerifyPartyCommand command) {
    if (command.getMetadata() == null) {
      return null;
    }

    Object value = command.getMetadata().get(DomainConstants.METADATA_DOCUMENT_REFERENCE);
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get(DomainConstants.METADATA_DOCUMENT_REFERENCE_ALT);
    return value != null ? value.toString() : null;
  }

  /**
   * Extracts the document type from the command metadata.
   */
  static String extractDocumentType(VerifyPartyCommand command) {
    if (command.getMetadata() == null) {
      return null;
    }

    Object value = command.getMetadata().get(DomainConstants.METADATA_DOCUMENT_TYPE);
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get(DomainConstants.METADATA_DOCUMENT_TYPE_ALT);
    return value != null ? value.toString() : null;
  }

  /**
   * Extracts the subject ID number from the command metadata.
   */
  static String extractSubjectIdNumber(VerifyPartyCommand command) {
    if (command.getMetadata() == null) {
      return null;
    }

    Object value = command.getMetadata().get(DomainConstants.METADATA_SUBJECT_ID_NUMBER);
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get(DomainConstants.METADATA_SUBJECT_ID_NUMBER_ALT);
    return value != null ? value.toString() : null;
  }

  /**
   * Extracts the subject name from the command metadata.
   */
  static String extractSubjectName(VerifyPartyCommand command) {
    if (command.getMetadata() == null) {
      return null;
    }

    Object value = command.getMetadata().get(DomainConstants.METADATA_SUBJECT_NAME);
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get(DomainConstants.METADATA_SUBJECT_NAME_ALT);
    return value != null ? value.toString() : null;
  }

  /**
   * Extracts the S3 bucket name from the command metadata.
   */
  static String extractS3BucketName(VerifyPartyCommand command) {
    if (command.getMetadata() == null) {
      return null;
    }

    Object value = command.getMetadata().get(DomainConstants.METADATA_S3_BUCKET_NAME);
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get(DomainConstants.METADATA_S3_BUCKET_NAME_ALT);
    return value != null ? value.toString() : null;
  }

  /**
   * Extracts the S3 object key from the command metadata.
   */
  static String extractS3ObjectKey(VerifyPartyCommand command) {
    if (command.getMetadata() == null) {
      return null;
    }

    Object value = command.getMetadata().get(DomainConstants.METADATA_S3_OBJECT_KEY);
    if (value != null) {
      return value.toString();
    }

    value = command.getMetadata().get(DomainConstants.METADATA_S3_OBJECT_KEY_ALT);
    return value != null ? value.toString() : null;
  }
}
