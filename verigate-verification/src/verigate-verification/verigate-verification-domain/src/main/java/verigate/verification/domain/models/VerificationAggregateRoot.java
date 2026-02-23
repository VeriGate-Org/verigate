/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.domain.models;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import domain.AggregateRoot;
import domain.invariants.GenericInvariantError;
import domain.invariants.SpecificationResult;
import domain.invariants.SpecificationUtils;

/**
 * Verification aggregate root representing the orchestration of a verification
 * flow.
 * This is the main aggregate that coordinates multiple verification steps and
 * manages
 * the overall verification lifecycle.
 */
public class VerificationAggregateRoot extends AggregateRoot<UUID, VerificationAggregateRoot> {

    private final UUID verificationId;
    private final String partnerId;
    private final String verificationRequestId;
    private final VerificationType verificationType;
    private final List<VerificationStep> steps;
    private final Instant createdAt;
    private Instant updatedAt;
    private VerificationStatus status;
    private VerificationResult overallResult;
    private String failureReason;
    private int retryCount;

    public VerificationAggregateRoot(UUID verificationId, String partnerId, String verificationRequestId,
            VerificationType verificationType) {
        super(verificationId, true, null, 1, null);
        this.verificationId = verificationId;
        this.partnerId = partnerId;
        this.verificationRequestId = verificationRequestId;
        this.verificationType = verificationType;
        this.steps = new ArrayList<>();
        this.status = VerificationStatus.INITIATED;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.retryCount = 0;

        // Initialize steps based on verification type
        initializeSteps();
    }

    /**
     * Initiates the verification flow by starting the first step.
     */
    public void start() {
        if (status != VerificationStatus.INITIATED) {
            throw new IllegalStateException("Verification can only be started from INITIATED status");
        }

        this.status = VerificationStatus.IN_PROGRESS;
        this.updatedAt = Instant.now();

        // Start the first step
        if (!steps.isEmpty()) {
            steps.get(0).start();
        }
    }

    /**
     * Processes the completion of a verification step.
     */
    public void completeStep(UUID stepId, VerificationResult result) {
        VerificationStep step = findStepById(stepId);
        if (step == null) {
            throw new IllegalArgumentException("Step not found: " + stepId);
        }

        step.complete(result);
        this.updatedAt = Instant.now();

        // Check if verification is complete
        if (areAllStepsCompleted()) {
            completeVerification();
        } else if (result == VerificationResult.FAILED) {
            failVerification("Step failed: " + step.getStepType());
        } else {
            // Start next step if current step passed
            startNextStep(step);
        }
    }

    /**
     * Fails a verification step and handles retry logic.
     */
    public void failStep(UUID stepId, String reason) {
        VerificationStep step = findStepById(stepId);
        if (step == null) {
            throw new IllegalArgumentException("Step not found: " + stepId);
        }

        step.fail(reason);
        this.updatedAt = Instant.now();

        // Check if we should retry or fail the entire verification
        if (canRetry()) {
            scheduleRetry();
        } else {
            failVerification("Step failed after retries: " + reason);
        }
    }

    /**
     * Retries the verification flow.
     */
    public void retry() {
        if (status != VerificationStatus.RETRY_PENDING) {
            throw new IllegalStateException("Verification can only be retried from RETRY_PENDING status");
        }

        this.retryCount++;
        this.status = VerificationStatus.IN_PROGRESS;
        this.updatedAt = Instant.now();

        // Reset all steps and restart
        resetSteps();
        start();
    }

    /**
     * Cancels the verification flow.
     */
    public void cancel() {
        if (status == VerificationStatus.COMPLETED || status == VerificationStatus.FAILED) {
            throw new IllegalStateException("Cannot cancel completed or failed verification");
        }

        this.status = VerificationStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    private void initializeSteps() {
        switch (verificationType) {
            case BANK_ACCOUNT_VERIFICATION:
                steps.add(new VerificationStep(UUID.randomUUID(), VerificationStepType.BANK_ACCOUNT_VERIFICATION));
                break;
            case IDENTITY_VERIFICATION:
                steps.add(new VerificationStep(UUID.randomUUID(), VerificationStepType.IDENTITY_VERIFICATION));
                break;
            case FULL_VERIFICATION:
                steps.add(new VerificationStep(UUID.randomUUID(), VerificationStepType.BANK_ACCOUNT_VERIFICATION));
                steps.add(new VerificationStep(UUID.randomUUID(), VerificationStepType.IDENTITY_VERIFICATION));
                steps.add(new VerificationStep(UUID.randomUUID(), VerificationStepType.WATCHLIST_SCREENING));
                break;
            case WATCHLIST_SCREENING:
                steps.add(new VerificationStep(UUID.randomUUID(), VerificationStepType.WATCHLIST_SCREENING));
                break;
            default:
                throw new IllegalArgumentException("Unknown verification type: " + verificationType);
        }
    }

    private VerificationStep findStepById(UUID stepId) {
        return steps.stream()
                .filter(step -> step.getStepId().equals(stepId))
                .findFirst()
                .orElse(null);
    }

    private boolean areAllStepsCompleted() {
        return steps.stream().allMatch(step -> step.getStatus() == VerificationStepStatus.COMPLETED &&
                step.getResult() == VerificationResult.PASSED);
    }

    private void completeVerification() {
        this.status = VerificationStatus.COMPLETED;
        this.overallResult = VerificationResult.PASSED;
        this.updatedAt = Instant.now();
    }

    private void failVerification(String reason) {
        this.status = VerificationStatus.FAILED;
        this.overallResult = VerificationResult.FAILED;
        this.failureReason = reason;
        this.updatedAt = Instant.now();
    }

    private void startNextStep(VerificationStep currentStep) {
        int currentIndex = steps.indexOf(currentStep);
        if (currentIndex >= 0 && currentIndex < steps.size() - 1) {
            steps.get(currentIndex + 1).start();
        }
    }

    private boolean canRetry() {
        return retryCount < 3; // Max 3 retries
    }

    private void scheduleRetry() {
        this.status = VerificationStatus.RETRY_PENDING;
        this.updatedAt = Instant.now();
    }

    private void resetSteps() {
        steps.forEach(VerificationStep::reset);
    }

    // Getters
    public UUID getVerificationId() {
        return verificationId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public String getVerificationRequestId() {
        return verificationRequestId;
    }

    public VerificationType getVerificationType() {
        return verificationType;
    }

    public List<VerificationStep> getSteps() {
        return new ArrayList<>(steps);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public VerificationStatus getStatus() {
        return status;
    }

    public VerificationResult getOverallResult() {
        return overallResult;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public int getRetryCount() {
        return retryCount;
    }

    @Override
    public SpecificationResult checkSpecification() {
        // Basic validation rules for verification
        if (verificationId == null) {
            return SpecificationUtils.getResult(false, VerificationAggregateRoot.class,
                    GenericInvariantError.FIELD_IS_REQUIRED, "verificationId");
        }
        if (partnerId == null || partnerId.trim().isEmpty()) {
            return SpecificationUtils.getResult(false, VerificationAggregateRoot.class,
                    GenericInvariantError.FIELD_IS_REQUIRED, "partnerId");
        }
        if (verificationType == null) {
            return SpecificationUtils.getResult(false, VerificationAggregateRoot.class,
                    GenericInvariantError.FIELD_IS_REQUIRED, "verificationType");
        }
        return SpecificationResult.success();
    }
}