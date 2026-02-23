/*
 * Arthmatic + Karisani(c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.domain.models;

/**
 * Represents the result of a verification process.
 * Contains the outcome of the verification and an optional failure reason.
 */
public record VerificationResult(VerificationOutcome outcome, String failureReason) {}
