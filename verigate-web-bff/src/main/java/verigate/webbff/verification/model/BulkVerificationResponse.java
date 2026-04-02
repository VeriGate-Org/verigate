package verigate.webbff.verification.model;

import java.time.Instant;

/**
 * Response DTO for a bulk verification job.
 */
public record BulkVerificationResponse(
    String jobId,
    String partnerId,
    String status,
    String requestId,
    String billingGroups,
    int idCount,
    Instant createdAt,
    Instant uploadedAt,
    Instant completedAt,
    int errorCode,
    String errorDescription
) {}
