/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.datanamix.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * {@code Result.DirectorResults} in the {@code POST /v1/cipc/director-result} response.
 * The real payload also carries {@code ConsumerDetail}, {@code ConsumerFraudIndicatorsSummary},
 * {@code ConsumerPropertyInformation}, {@code ConsumerEmploymentHistory}, and several other
 * consumer-bureau-style blocks — deliberately not modeled here since story 2.3 only needs
 * the directorship list. Revisit if a future story needs any of that data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DirectorResultsDto(
    @JsonProperty("ConsumerDirectorshipLink")
    List<ConsumerDirectorshipLinkDto> consumerDirectorshipLink
) {

  /**
   * Compact constructor — defaults a null list to an empty list.
   */
  public DirectorResultsDto {
    if (consumerDirectorshipLink == null) {
      consumerDirectorshipLink = List.of();
    }
  }
}
