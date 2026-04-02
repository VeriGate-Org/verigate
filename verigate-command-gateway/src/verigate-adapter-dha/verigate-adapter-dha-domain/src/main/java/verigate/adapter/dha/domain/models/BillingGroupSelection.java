/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.domain.models;

/**
 * Represents the selection of HANIS billing groups for a bulk verification request.
 * Each group corresponds to a category of data returned from the NPR.
 */
public record BillingGroupSelection(
    boolean group1Demographics,
    boolean group2Identity,
    boolean group3MaritalStatus,
    boolean group4DeathInfo,
    boolean group5SmartCard,
    boolean group6Photo,
    boolean group7Address,
    boolean group8Fingerprint,
    boolean group9Reserved1,
    boolean group10Reserved2
) {

  /**
   * Returns the billing group header string for the CSV file.
   * Format: "Y,Y,N,Y,Y,N,N,N,N,N" (10 comma-separated Y/N values).
   */
  public String toHeaderString() {
    return String.join(",",
        boolToYn(group1Demographics),
        boolToYn(group2Identity),
        boolToYn(group3MaritalStatus),
        boolToYn(group4DeathInfo),
        boolToYn(group5SmartCard),
        boolToYn(group6Photo),
        boolToYn(group7Address),
        boolToYn(group8Fingerprint),
        boolToYn(group9Reserved1),
        boolToYn(group10Reserved2));
  }

  /**
   * Creates a default selection with the most common billing groups enabled.
   */
  public static BillingGroupSelection defaultSelection() {
    return new BillingGroupSelection(
        true, true, true, true, true,
        false, false, false, false, false);
  }

  /**
   * Creates a selection with all billing groups enabled.
   */
  public static BillingGroupSelection all() {
    return new BillingGroupSelection(
        true, true, true, true, true,
        true, true, true, true, true);
  }

  /**
   * Parses a billing group header string into a {@link BillingGroupSelection}.
   *
   * @param header comma-separated Y/N values
   * @return the parsed selection
   */
  public static BillingGroupSelection fromHeaderString(String header) {
    String[] parts = header.split(",");
    if (parts.length != 10) {
      throw new IllegalArgumentException(
          "Billing group header must have 10 values, got " + parts.length);
    }
    return new BillingGroupSelection(
        ynToBool(parts[0]), ynToBool(parts[1]), ynToBool(parts[2]),
        ynToBool(parts[3]), ynToBool(parts[4]), ynToBool(parts[5]),
        ynToBool(parts[6]), ynToBool(parts[7]), ynToBool(parts[8]),
        ynToBool(parts[9]));
  }

  private static String boolToYn(boolean value) {
    return value ? "Y" : "N";
  }

  private static boolean ynToBool(String value) {
    return "Y".equalsIgnoreCase(value.trim());
  }
}
