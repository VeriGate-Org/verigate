/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.datanamix.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@code CIPCResult.CommercialBusinessInformation} from {@code POST /v1/cipc/company-result}.
 * The real payload also carries fields like {@code TaxNumber}, {@code Website},
 * {@code AuthorisedCapitalAmt}, and several others — deliberately not modeled here since
 * story 2.2 only needs the core identity/status fields. Revisit if a future story needs
 * any of that data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CommercialBusinessInformationDto(
    @JsonProperty("BusinessName") String businessName,
    @JsonProperty("TradeName") String tradeName,
    @JsonProperty("RegistrationNumber") String registrationNumber,
    @JsonProperty("BusinessStatus") String businessStatus,
    @JsonProperty("BusinessType") String businessType,
    @JsonProperty("RegistrationDate") String registrationDate
) {
}
