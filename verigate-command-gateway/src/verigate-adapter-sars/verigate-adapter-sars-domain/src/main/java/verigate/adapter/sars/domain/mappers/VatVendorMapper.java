/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.domain.mappers;

import java.util.Map;
import verigate.adapter.sars.domain.constants.DomainConstants;
import verigate.adapter.sars.domain.models.VatVendorSearchRequest;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Maps {@link VerifyPartyCommand} metadata to {@link VatVendorSearchRequest}.
 */
public interface VatVendorMapper {

  /**
   * Extracts a VAT vendor search request from a verification command.
   *
   * @param command the verification command
   * @return the mapped VAT vendor search request
   * @throws IllegalArgumentException if the VAT number is missing
   */
  static VatVendorSearchRequest mapToVatVendorSearchRequest(VerifyPartyCommand command) {
    Map<String, Object> metadata = command.getMetadata();
    if (metadata == null) {
      throw new IllegalArgumentException("Command metadata is null");
    }

    String vatNumber = getMetadataValue(metadata,
        DomainConstants.METADATA_KEY_VAT_NUMBER,
        DomainConstants.METADATA_KEY_VAT_NUMBER_ALT);

    if (vatNumber == null || vatNumber.isBlank()) {
      throw new IllegalArgumentException(
          "VAT number is required for VAT vendor verification");
    }

    String description = getMetadataValue(metadata,
        DomainConstants.METADATA_KEY_VENDOR_DESCRIPTION,
        DomainConstants.METADATA_KEY_VENDOR_DESCRIPTION_ALT);

    return VatVendorSearchRequest.builder()
        .vatNumber(vatNumber.trim())
        .description(description != null ? description.trim() : "")
        .build();
  }

  private static String getMetadataValue(Map<String, Object> metadata, String... keys) {
    for (String key : keys) {
      Object raw = metadata.get(key);
      if (raw != null) {
        String value = raw.toString();
        if (!value.isBlank()) {
          return value;
        }
      }
    }
    return null;
  }
}
