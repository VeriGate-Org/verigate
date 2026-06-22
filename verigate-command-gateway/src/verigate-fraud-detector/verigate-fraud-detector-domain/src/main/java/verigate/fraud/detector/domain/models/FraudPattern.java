/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.fraud.detector.domain.models;

import java.time.Instant;

/**
 * Represents a detected fraud pattern.
 */
public record FraudPattern(
    String patternId,
    String identifierHash,
    PatternType patternType,
    Severity severity,
    String description,
    RecommendedAction recommendedAction,
    Instant detectedAt,
    long ttl
) {

  /** Classification of the detected fraud pattern. */
  public enum PatternType {
    VELOCITY_ATTACK,
    MULTI_PARTNER_ABUSE,
    SYNTHETIC_IDENTITY,
    IDENTITY_THEFT,
    NORMAL
  }

  /** Severity level of the fraud pattern. */
  public enum Severity {
    HIGH,
    MEDIUM,
    LOW
  }

  /** Recommended action to take for the detected fraud pattern. */
  public enum RecommendedAction {
    BLOCK,
    FLAG_FOR_REVIEW,
    MONITOR,
    NONE
  }
}
