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
 * Response DTO for {@code POST /v1/cipc/director-result}. {@code Header} and
 * {@code PDFReport} are present in the real response but not needed by story 2.3's
 * logic (no PDF is ever requested), so neither is modeled.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DirectorResultResponseDto(
    @JsonProperty("Result") DirectorResultResultDto result,
    @JsonProperty("Success") boolean success,
    @JsonProperty("Messages") List<String> messages
) {

  /**
   * Compact constructor — defaults a null list to an empty list.
   */
  public DirectorResultResponseDto {
    if (messages == null) {
      messages = List.of();
    }
  }
}
