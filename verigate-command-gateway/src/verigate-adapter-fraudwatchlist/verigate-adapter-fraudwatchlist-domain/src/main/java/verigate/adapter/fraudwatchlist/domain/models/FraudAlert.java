/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.fraudwatchlist.domain.models;

import java.time.LocalDate;

/**
 * Represents a fraud alert returned by the watchlist screening service.
 */
public record FraudAlert(
    FraudSource source,
    String alertType,
    LocalDate alertDate,
    double severity,
    String description,
    String referenceNumber
) {
}
