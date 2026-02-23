/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.models;

/**
 * Status of a watchlist screening job.
 */
public enum ScreeningStatus {
    PENDING,    // Screening has been initiated but no results yet
    PROCESSING, // Screening is actively being processed by providers
    COMPLETED,  // Screening completed successfully
    FAILED      // Screening failed due to system issues
}