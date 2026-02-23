/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.domain.models;

import java.time.LocalDate;
import verigate.adapter.sars.domain.enums.TaxClearanceType;

/**
 * Represents a Tax Clearance Certificate (TCC) issued by SARS.
 *
 * @param certificateNumber the unique TCC certificate number
 * @param issueDate the date the certificate was issued
 * @param expiryDate the date the certificate expires
 * @param type the type of tax clearance certificate
 * @param valid whether the certificate is currently valid
 */
public record TaxClearanceCertificate(
    String certificateNumber,
    LocalDate issueDate,
    LocalDate expiryDate,
    TaxClearanceType type,
    boolean valid
) {

  /**
   * Checks whether the certificate has expired based on the current date.
   *
   * @return true if the certificate expiry date is before today
   */
  public boolean isExpired() {
    return expiryDate != null && expiryDate.isBefore(LocalDate.now());
  }

  /**
   * Creates a valid certificate instance.
   *
   * @param certificateNumber the certificate number
   * @param issueDate the issue date
   * @param expiryDate the expiry date
   * @param type the clearance type
   * @return a new valid TaxClearanceCertificate
   */
  public static TaxClearanceCertificate validCertificate(
      String certificateNumber,
      LocalDate issueDate,
      LocalDate expiryDate,
      TaxClearanceType type) {
    return new TaxClearanceCertificate(certificateNumber, issueDate, expiryDate, type, true);
  }

  /**
   * Creates an expired certificate instance.
   *
   * @param certificateNumber the certificate number
   * @param issueDate the issue date
   * @param expiryDate the expiry date
   * @param type the clearance type
   * @return a new expired TaxClearanceCertificate
   */
  public static TaxClearanceCertificate expiredCertificate(
      String certificateNumber,
      LocalDate issueDate,
      LocalDate expiryDate,
      TaxClearanceType type) {
    return new TaxClearanceCertificate(certificateNumber, issueDate, expiryDate, type, false);
  }
}
