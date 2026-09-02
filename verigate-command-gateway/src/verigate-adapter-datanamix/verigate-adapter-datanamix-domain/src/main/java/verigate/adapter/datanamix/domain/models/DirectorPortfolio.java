/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.datanamix.domain.models;

import java.util.List;

/**
 * The result of a director portfolio search: every company a person is or was a
 * director of, resolved from a South African ID number via Datanamix's CIPC Director
 * Verification API (story 2.3).
 *
 * @param found whether any director identity match was found for the ID number
 * @param idNumber the ID number that was searched for
 * @param firstName the matched person's first name, if found
 * @param surname the matched person's surname, if found
 * @param directorships every directorship record found across all identity matches for
 *     this ID number, flattened into one list
 */
public record DirectorPortfolio(
    boolean found,
    String idNumber,
    String firstName,
    String surname,
    List<DirectorshipEntry> directorships
) {

  /**
   * Compact constructor — defaults a null directorships list to an empty list.
   */
  public DirectorPortfolio {
    if (directorships == null) {
      directorships = List.of();
    }
  }

  /**
   * Creates a result indicating no director identity match was found for the ID number.
   */
  public static DirectorPortfolio notFound(String idNumber) {
    return new DirectorPortfolio(false, idNumber, null, null, List.of());
  }
}
