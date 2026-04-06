/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.domain.models;

import java.time.LocalDateTime;
import verigate.adapter.sars.domain.enums.VatVendorStatus;

/**
 * Response from a SARS VAT Vendor Search.
 *
 * @param status        the vendor status
 * @param vendorDetails the vendor details (null when not found)
 * @param reason        human-readable reason or error message
 * @param verifiedAt    the time of verification
 */
public record VatVendorSearchResponse(
    VatVendorStatus status,
    VatVendorDetails vendorDetails,
    String reason,
    LocalDateTime verifiedAt) {

  public static VatVendorSearchResponse found(VatVendorDetails details, VatVendorStatus status) {
    String reason = String.format(
        "VAT vendor found: %s (%s)",
        details.vendorName() != null ? details.vendorName() : "N/A",
        status);
    return new VatVendorSearchResponse(status, details, reason, LocalDateTime.now());
  }

  public static VatVendorSearchResponse notFound() {
    return new VatVendorSearchResponse(
        VatVendorStatus.NOT_FOUND, null,
        "No VAT vendor record found for the provided VAT number",
        LocalDateTime.now());
  }

  public static VatVendorSearchResponse error(String reason) {
    return new VatVendorSearchResponse(
        VatVendorStatus.ERROR, null, reason, LocalDateTime.now());
  }

  public boolean isFound() {
    return status != VatVendorStatus.NOT_FOUND && status != VatVendorStatus.ERROR;
  }

  public boolean isNotFound() {
    return status == VatVendorStatus.NOT_FOUND;
  }

  public boolean isError() {
    return status == VatVendorStatus.ERROR;
  }
}
