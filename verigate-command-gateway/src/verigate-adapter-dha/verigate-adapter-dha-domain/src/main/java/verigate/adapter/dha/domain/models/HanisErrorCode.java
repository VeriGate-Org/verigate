/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.domain.models;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * HANIS error codes returned by the GetData and RequestStatus SOAP operations.
 * Codes sourced from the HANIS Non-Bio Service Specification.
 */
public enum HanisErrorCode {

  SUCCESS(0, "Success", false, false),
  HANIS_UNAVAILABLE(2, "HANIS service unavailable", true, true),
  INVALID_TRANSACTION_DATE(1056, "Invalid transaction date", false, false),
  INVALID_IDN(10004, "Invalid ID number", false, false),
  INTERNAL_ERROR(10008, "Internal server error", true, false),
  SITE_ID_NOT_SUPPLIED(10012, "Site ID not supplied", false, false),
  WRKSTN_ID_NOT_SUPPLIED(10014, "Workstation ID not supplied", false, false),
  WRKSTN_ID_TOO_LONG(10015, "Workstation ID too long", false, false),
  SITE_ID_TOO_LONG(10016, "Site ID too long", false, false),
  NOT_REGISTERED(10070, "Site not registered", false, false),
  SERVICE_NOT_ALLOWED(10071, "Service not allowed for this site", false, false),
  NPR_UNAVAILABLE(10088, "NPR service unavailable", true, true),
  IDN_NOT_VALID_NPR(500, "ID number not valid on NPR", false, true),
  IDN_MARKED_DELETION(600, "ID number marked for deletion", false, true),
  IDN_NOT_FOUND_NPR(800, "ID number not found on NPR", false, true);

  private static final Map<Integer, HanisErrorCode> CODE_MAP =
      Stream.of(values()).collect(Collectors.toMap(HanisErrorCode::code, e -> e));

  private final int code;
  private final String description;
  private final boolean retriable;
  private final boolean chargeable;

  HanisErrorCode(int code, String description, boolean retriable, boolean chargeable) {
    this.code = code;
    this.description = description;
    this.retriable = retriable;
    this.chargeable = chargeable;
  }

  public int code() {
    return code;
  }

  public String description() {
    return description;
  }

  /**
   * Whether a request that returned this error code may be retried.
   */
  public boolean isRetriable() {
    return retriable;
  }

  /**
   * Whether a transaction that returned this error code is chargeable by DHA.
   */
  public boolean isChargeable() {
    return chargeable;
  }

  /**
   * Looks up an error code by its integer value.
   *
   * @param code the integer error code
   * @return the matching enum constant, or {@code null} if unknown
   */
  public static HanisErrorCode fromCode(int code) {
    return CODE_MAP.get(code);
  }
}
