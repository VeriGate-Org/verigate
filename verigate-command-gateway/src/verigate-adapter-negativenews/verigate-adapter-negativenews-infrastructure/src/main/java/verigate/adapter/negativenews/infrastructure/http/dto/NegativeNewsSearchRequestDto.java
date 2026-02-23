/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO for Negative News search request.
 */
public record NegativeNewsSearchRequestDto(
    @JsonProperty("subject_name") String subjectName,
    @JsonProperty("id_number") String idNumber,
    @JsonProperty("additional_keywords") List<String> additionalKeywords,
    @JsonProperty("date_range_months") int dateRangeMonths,
    @JsonProperty("max_articles") int maxArticles
) {
}
