/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.fraudwatchlist.domain.models;

/**
 * Enumeration of fraud data sources used for watchlist screening.
 */
public enum FraudSource {
  COMPUSCAN,
  TRANSUNION,
  SABRIC,
  INTERNAL_BLACKLIST,
  SAFPS;

  /**
   * Parse a fraud source description to the corresponding enum value.
   */
  public static FraudSource fromDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return null;
    }

    String normalized = description.trim().toUpperCase().replace(" ", "_").replace("-", "_");

    try {
      return valueOf(normalized);
    } catch (IllegalArgumentException e) {
      return switch (normalized) {
        case "COMPUSCAN", "COMPU_SCAN" -> COMPUSCAN;
        case "TRANSUNION", "TRANS_UNION", "TU" -> TRANSUNION;
        case "SABRIC", "SA_BANKING_RISK" -> SABRIC;
        case "INTERNAL_BLACKLIST", "INTERNAL", "BLACKLIST" -> INTERNAL_BLACKLIST;
        case "SAFPS", "SA_FRAUD_PREVENTION" -> SAFPS;
        default -> null;
      };
    }
  }
}
