/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.domain.models;

import java.util.List;

/**
 * The subset of a live CIPC company record needed to cross-validate a CIPC registration document
 * upload. This is intentionally a narrower view than the full {@code CompanyProfile} modeled in
 * {@code verigate-adapter-cipc} — the document adapter calls the CIPC public API directly (see
 * story 2.1 design notes) rather than depending on that adapter's domain model.
 *
 * @param found whether a company was found for the looked-up registration number
 * @param registrationNumber the CIPC enterprise number that was looked up
 * @param companyName the registered enterprise name (or trading name, if preferred for display)
 * @param companyStatus the enterprise status description, e.g. "In Business", "Deregistered"
 * @param companyType the enterprise type description, e.g. "Private Company"
 * @param registeredAddress the registered office address as a single formatted line
 * @param directorNames full names of directors on record, for name cross-checks
 */
public record CipcCompanyLookupResult(
    boolean found,
    String registrationNumber,
    String companyName,
    String companyStatus,
    String companyType,
    String registeredAddress,
    List<String> directorNames
) {

  /**
   * Compact constructor — defaults a null director list to an empty list.
   */
  public CipcCompanyLookupResult {
    if (directorNames == null) {
      directorNames = List.of();
    }
  }

  /**
   * Creates a result indicating no company was found for the given registration number.
   */
  public static CipcCompanyLookupResult notFound(String registrationNumber) {
    return new CipcCompanyLookupResult(
        false, registrationNumber, null, null, null, null, List.of());
  }
}
