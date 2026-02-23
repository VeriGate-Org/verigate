/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.domain.models;

/**
 * Represents a request to verify a document.
 */
public record DocumentVerificationRequest(
    String documentReference,
    DocumentType documentType,
    String subjectIdNumber,
    String subjectName,
    String s3BucketName,
    String s3ObjectKey
) {

  /**
   * Builder for convenient request construction.
   */
  public static class Builder {
    private String documentReference;
    private DocumentType documentType;
    private String subjectIdNumber;
    private String subjectName;
    private String s3BucketName;
    private String s3ObjectKey;

    public Builder documentReference(String documentReference) {
      this.documentReference = documentReference;
      return this;
    }

    public Builder documentType(DocumentType documentType) {
      this.documentType = documentType;
      return this;
    }

    public Builder subjectIdNumber(String subjectIdNumber) {
      this.subjectIdNumber = subjectIdNumber;
      return this;
    }

    public Builder subjectName(String subjectName) {
      this.subjectName = subjectName;
      return this;
    }

    public Builder s3BucketName(String s3BucketName) {
      this.s3BucketName = s3BucketName;
      return this;
    }

    public Builder s3ObjectKey(String s3ObjectKey) {
      this.s3ObjectKey = s3ObjectKey;
      return this;
    }

    /**
     * Builds the immutable document verification request instance.
     */
    public DocumentVerificationRequest build() {
      return new DocumentVerificationRequest(
          documentReference,
          documentType,
          subjectIdNumber,
          subjectName,
          s3BucketName,
          s3ObjectKey
      );
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
