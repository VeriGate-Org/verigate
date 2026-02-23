/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.domain.models;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a single step in the verification process.
 */
public class VerificationStep {

    private final UUID stepId;
    private final VerificationStepType stepType;
    private final Instant createdAt;
    private Instant startedAt;
    private Instant completedAt;
    private VerificationStepStatus status;
    private VerificationResult result;
    private String failureReason;

    public VerificationStep(UUID stepId, VerificationStepType stepType) {
        this.stepId = stepId;
        this.stepType = stepType;
        this.status = VerificationStepStatus.PENDING;
        this.result = VerificationResult.PENDING;
        this.createdAt = Instant.now();
    }

    /**
     * Starts the verification step.
     */
    public void start() {
        if (status != VerificationStepStatus.PENDING) {
            throw new IllegalStateException("Step can only be started from PENDING status");
        }

        this.status = VerificationStepStatus.IN_PROGRESS;
        this.startedAt = Instant.now();
    }

    /**
     * Completes the verification step with a result.
     */
    public void complete(VerificationResult result) {
        if (status != VerificationStepStatus.IN_PROGRESS) {
            throw new IllegalStateException("Step can only be completed from IN_PROGRESS status");
        }

        this.status = VerificationStepStatus.COMPLETED;
        this.result = result;
        this.completedAt = Instant.now();
    }

    /**
     * Fails the verification step with a reason.
     */
    public void fail(String reason) {
        if (status != VerificationStepStatus.IN_PROGRESS) {
            throw new IllegalStateException("Step can only be failed from IN_PROGRESS status");
        }

        this.status = VerificationStepStatus.FAILED;
        this.result = VerificationResult.FAILED;
        this.failureReason = reason;
        this.completedAt = Instant.now();
    }

    /**
     * Resets the step for retry.
     */
    public void reset() {
        this.status = VerificationStepStatus.PENDING;
        this.result = VerificationResult.PENDING;
        this.startedAt = null;
        this.completedAt = null;
        this.failureReason = null;
    }

    // Getters
    public UUID getStepId() {
        return stepId;
    }

    public VerificationStepType getStepType() {
        return stepType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public VerificationStepStatus getStatus() {
        return status;
    }

    public VerificationResult getResult() {
        return result;
    }

    public String getFailureReason() {
        return failureReason;
    }
}