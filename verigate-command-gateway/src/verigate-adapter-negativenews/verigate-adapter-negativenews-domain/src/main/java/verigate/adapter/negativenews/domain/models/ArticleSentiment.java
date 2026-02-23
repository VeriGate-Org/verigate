/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.domain.models;

/**
 * Enumeration of possible article sentiment values for negative news articles.
 */
public enum ArticleSentiment {
  POSITIVE,
  NEUTRAL,
  NEGATIVE,
  HIGHLY_NEGATIVE;

  /**
   * Parse a sentiment description to the corresponding enum value.
   *
   * @param description the description to parse
   * @return the corresponding ArticleSentiment, defaults to NEUTRAL if unknown
   */
  public static ArticleSentiment fromDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return NEUTRAL;
    }

    String normalized = description.trim().toUpperCase().replace(" ", "_");

    try {
      return valueOf(normalized);
    } catch (IllegalArgumentException e) {
      return switch (normalized) {
        case "POS", "POSITIVE" -> POSITIVE;
        case "NEU", "NEUTRAL" -> NEUTRAL;
        case "NEG", "NEGATIVE" -> NEGATIVE;
        case "HIGHLY_NEG", "HIGHLY_NEGATIVE", "VERY_NEGATIVE" -> HIGHLY_NEGATIVE;
        default -> NEUTRAL;
      };
    }
  }
}
