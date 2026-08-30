/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.datanamix.domain.models;

import java.time.LocalDate;
import java.util.List;

/**
 * The full CIPC company record story 2.2 needs, resolved via Datanamix's CIPC Result
 * API ({@code CIPCResult.CommercialBusinessInformation} +
 * {@code CIPCResult.CommercialDirectorInformation[]}).
 *
 * @param found whether a company record was actually resolved
 * @param businessName the company's registered name
 * @param tradeName the company's trading name, if different from the registered name
 * @param registrationNumber the CIPC enterprise/registration number
 * @param businessStatus the company's registration status (e.g. "In Business", "Deregistered")
 * @param businessType the company type (e.g. "Private Company")
 * @param registrationDate the date the company was registered with CIPC
 * @param directors every director on record for this company
 */
public record CompanyProfile(
    boolean found,
    String businessName,
    String tradeName,
    String registrationNumber,
    String businessStatus,
    String businessType,
    LocalDate registrationDate,
    List<CompanyDirector> directors
) {

  /**
   * Compact constructor — defaults a null directors list to an empty list.
   */
  public CompanyProfile {
    if (directors == null) {
      directors = List.of();
    }
  }

  /**
   * Creates a result indicating no company record was found for the given search.
   */
  public static CompanyProfile notFound(String registrationNumber) {
    return new CompanyProfile(false, null, null, registrationNumber, null, null, null, List.of());
  }
}
