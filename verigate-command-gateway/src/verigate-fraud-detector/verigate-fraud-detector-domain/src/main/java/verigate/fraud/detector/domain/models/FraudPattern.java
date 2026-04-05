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

  public enum PatternType {
    VELOCITY_ATTACK,
    MULTI_PARTNER_ABUSE,
    SYNTHETIC_IDENTITY,
    IDENTITY_THEFT,
    NORMAL
  }

  public enum Severity {
    HIGH,
    MEDIUM,
    LOW
  }

  public enum RecommendedAction {
    BLOCK,
    FLAG_FOR_REVIEW,
    MONITOR,
    NONE
  }
}
