/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.domain.models;

/**
 * Represents the detailed person data returned from the HANIS NPR (National Population Register)
 * via the GetData SOAP operation.
 */
public record HanisPersonDetails(
    String tranNo,
    String name,
    String surname,
    boolean smartCardIssued,
    String idIssueDate,
    String idSequenceNo,
    boolean deadIndicator,
    boolean idnBlocked,
    String dateOfDeath,
    String maritalStatus,
    String dateOfMarriage,
    byte[] photo,
    boolean onHanis,
    boolean onNpr,
    String birthPlaceCountryCode,
    int error
) {

  /**
   * Creates a successful response with full person details.
   */
  public static HanisPersonDetails success(
      String tranNo, String name, String surname, boolean smartCardIssued,
      String idIssueDate, String idSequenceNo, boolean deadIndicator, boolean idnBlocked,
      String dateOfDeath, String maritalStatus, String dateOfMarriage,
      byte[] photo, boolean onHanis, boolean onNpr, String birthPlaceCountryCode) {
    return new HanisPersonDetails(
        tranNo, name, surname, smartCardIssued, idIssueDate, idSequenceNo,
        deadIndicator, idnBlocked, dateOfDeath, maritalStatus, dateOfMarriage,
        photo, onHanis, onNpr, birthPlaceCountryCode, 0);
  }

  /**
   * Creates an error response with the given error code.
   */
  public static HanisPersonDetails error(int errorCode) {
    return new HanisPersonDetails(
        null, null, null, false, null, null,
        false, false, null, null, null,
        null, false, false, null, errorCode);
  }

  /**
   * Creates a not-found response.
   */
  public static HanisPersonDetails notFound() {
    return error(800);
  }

  public boolean isSuccess() {
    return error == 0;
  }

  public boolean hasPhoto() {
    return photo != null && photo.length > 0;
  }
}
