/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.fraud.detector.domain.models;

/**
 * Represents a velocity tracking record for fraud detection.
 */
public record VelocityRecord(
    String identifierHash,
    String windowKey,
    int count,
    int uniquePartners,
    long ttl
) {
}
