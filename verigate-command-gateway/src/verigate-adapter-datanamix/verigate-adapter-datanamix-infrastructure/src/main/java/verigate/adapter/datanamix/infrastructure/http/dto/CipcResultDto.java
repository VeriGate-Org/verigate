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
 * {@code CIPCResult} in the {@code POST /v1/cipc/company-result} response. Unlike the
 * director-result endpoint (where {@code Success}/{@code Messages}/{@code ResponseCode}
 * sit at the top level), Datanamix nests these one level deeper here, inside
 * {@code CIPCResult} itself — confirmed from the fully-expanded "CIPC Result Sandbox"
 * doc example, not assumed.
 *
 * <p>The real payload also carries {@code ActiveDirectorSummary}, {@code
 * InactiveDirectorSummary}, {@code CommercialAddressInformation}, {@code
 * CommercialActivePrincipalInformation}, and {@code CommercialAuditorInformation} —
 * deliberately not modeled here since story 2.2 only needs business identity/status and
 * the director list. Revisit if a future story needs any of that data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CipcResultDto(
    @JsonProperty("CommercialBusinessInformation")
    CommercialBusinessInformationDto commercialBusinessInformation,
    @JsonProperty("CommercialDirectorInformation")
    List<CommercialDirectorInformationDto> commercialDirectorInformation,
    @JsonProperty("Success") boolean success,
    @JsonProperty("Messages") List<String> messages,
    @JsonProperty("ResponseCode") int responseCode
) {

  /**
   * Compact constructor — defaults null lists to empty lists.
   */
  public CipcResultDto {
    if (commercialDirectorInformation == null) {
      commercialDirectorInformation = List.of();
    }
    if (messages == null) {
      messages = List.of();
    }
  }
}
