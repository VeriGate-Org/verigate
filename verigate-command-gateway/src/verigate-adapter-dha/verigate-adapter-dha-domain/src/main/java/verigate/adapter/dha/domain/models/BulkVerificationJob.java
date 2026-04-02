/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.domain.models;

import java.time.Instant;

/**
 * Represents a bulk identity verification job submitted to HANIS.
 */
public record BulkVerificationJob(
    String jobId,
    String partnerId,
    BulkJobStatus status,
    String requestId,
    BillingGroupSelection billingGroups,
    int idCount,
    Instant createdAt,
    Instant uploadedAt,
    Instant completedAt,
    int errorCode,
    String errorDescription
) {

  public enum BulkJobStatus {
    CREATED,
    UPLOADED,
    PROCESSING,
    COMPLETED,
    DOWNLOADED,
    EXPIRED,
    FAILED
  }

  public static BulkVerificationJob create(
      String jobId, String partnerId, BillingGroupSelection billingGroups, int idCount) {
    return new BulkVerificationJob(
        jobId, partnerId, BulkJobStatus.CREATED, null, billingGroups,
        idCount, Instant.now(), null, null, 0, null);
  }

  public BulkVerificationJob withStatus(BulkJobStatus newStatus) {
    return new BulkVerificationJob(
        jobId, partnerId, newStatus, requestId, billingGroups,
        idCount, createdAt, uploadedAt, completedAt, errorCode, errorDescription);
  }

  public BulkVerificationJob withRequestId(String newRequestId) {
    return new BulkVerificationJob(
        jobId, partnerId, BulkJobStatus.UPLOADED, newRequestId, billingGroups,
        idCount, createdAt, Instant.now(), null, 0, null);
  }

  public BulkVerificationJob withCompletion() {
    return new BulkVerificationJob(
        jobId, partnerId, BulkJobStatus.COMPLETED, requestId, billingGroups,
        idCount, createdAt, uploadedAt, Instant.now(), 0, null);
  }

  public BulkVerificationJob withError(int code, String description) {
    return new BulkVerificationJob(
        jobId, partnerId, BulkJobStatus.FAILED, requestId, billingGroups,
        idCount, createdAt, uploadedAt, Instant.now(), code, description);
  }
}
