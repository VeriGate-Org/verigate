/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.domain.models;

import java.time.LocalDate;

/**
 * Represents a single news article returned from a negative news screening search.
 */
public record NewsArticle(
    String title,
    String source,
    LocalDate publishedDate,
    String url,
    String snippet,
    ArticleSentiment sentiment,
    double relevanceScore
) {
}
