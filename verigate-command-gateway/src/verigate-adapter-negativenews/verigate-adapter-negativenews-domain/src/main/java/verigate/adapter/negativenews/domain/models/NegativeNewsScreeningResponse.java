/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.domain.models;

import java.util.List;

/**
 * Represents the response from a negative news screening request.
 */
public record NegativeNewsScreeningResponse(
    ScreeningOutcome outcome,
    List<NewsArticle> articles,
    int totalArticlesFound,
    int adverseArticlesCount,
    double highestSeverityScore,
    String screeningSummary
) {

  /**
   * Creates a clear response indicating no adverse news was found.
   *
   * @param totalArticlesFound the total number of articles found
   * @param screeningSummary a summary of the screening
   * @return a clear NegativeNewsScreeningResponse
   */
  public static NegativeNewsScreeningResponse clear(
      int totalArticlesFound, String screeningSummary) {
    return new NegativeNewsScreeningResponse(
        ScreeningOutcome.CLEAR, List.of(), totalArticlesFound, 0, 0.0, screeningSummary);
  }

  /**
   * Creates an adverse found response indicating negative news was discovered.
   *
   * @param articles the adverse articles found
   * @param totalArticlesFound the total number of articles found
   * @param adverseArticlesCount the number of adverse articles found
   * @param highestSeverityScore the highest severity score among adverse articles
   * @param screeningSummary a summary of the screening
   * @return an adverse found NegativeNewsScreeningResponse
   */
  public static NegativeNewsScreeningResponse adverseFound(
      List<NewsArticle> articles,
      int totalArticlesFound,
      int adverseArticlesCount,
      double highestSeverityScore,
      String screeningSummary) {
    return new NegativeNewsScreeningResponse(
        ScreeningOutcome.ADVERSE_FOUND,
        articles,
        totalArticlesFound,
        adverseArticlesCount,
        highestSeverityScore,
        screeningSummary);
  }

  /**
   * Creates an inconclusive response when screening could not determine a definitive result.
   *
   * @param screeningSummary a summary of the screening
   * @return an inconclusive NegativeNewsScreeningResponse
   */
  public static NegativeNewsScreeningResponse inconclusive(String screeningSummary) {
    return new NegativeNewsScreeningResponse(
        ScreeningOutcome.INCONCLUSIVE, List.of(), 0, 0, 0.0, screeningSummary);
  }

  /**
   * Creates an error response when an error occurred during screening.
   *
   * @param screeningSummary a summary of the error
   * @return an error NegativeNewsScreeningResponse
   */
  public static NegativeNewsScreeningResponse error(String screeningSummary) {
    return new NegativeNewsScreeningResponse(
        ScreeningOutcome.ERROR, List.of(), 0, 0, 0.0, screeningSummary);
  }
}
