/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.domain.enums;

/**
 * Status values for a VAT vendor returned by the SARS VAT Vendor Search service.
 */
public enum VatVendorStatus {

  ACTIVE,
  INACTIVE,
  DEREGISTERED,
  NOT_FOUND,
  ERROR;

  /**
   * Maps a description string from the SARS response to a {@link VatVendorStatus}.
   *
   * @param description the raw status description from the SARS SOAP response
   * @return the matching status, or {@link #ERROR} if unrecognised
   */
  public static VatVendorStatus fromDescription(String description) {
    if (description == null || description.isBlank()) {
      return NOT_FOUND;
    }
    String upper = description.trim().toUpperCase();
    if (upper.contains("ACTIVE") && !upper.contains("INACTIVE")) {
      return ACTIVE;
    }
    if (upper.contains("INACTIVE")) {
      return INACTIVE;
    }
    if (upper.contains("DEREGIST")) {
      return DEREGISTERED;
    }
    if (upper.contains("NOT FOUND") || upper.contains("NO RECORD")) {
      return NOT_FOUND;
    }
    return ERROR;
  }
}
