/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.datanamix.domain.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.util.List;
import verigate.adapter.datanamix.domain.models.CompanyProfile;
import verigate.adapter.datanamix.domain.models.CompanySearchMatch;

/**
 * Port for story 2.2's company search — search by registration number or by name.
 * CIPC's own government API has no name-search endpoint at all (every endpoint is keyed
 * by enterprise number, confirmed by auditing {@code verigate-adapter-cipc}); Datanamix's
 * CIPC Search / CIPC Result API is the real data source for both search modes.
 */
public interface CompanySearchService {

  /**
   * Searches for companies by business name. Names aren't unique in CIPC, so this can
   * return multiple candidates for the caller to disambiguate.
   *
   * @param businessName the business name to search for
   * @return every matching candidate; empty if none were found (not an exception)
   * @throws TransientException if the Datanamix API is temporarily unreachable
   * @throws PermanentException if the request is invalid or Datanamix returns a
   *     non-retriable error
   */
  List<CompanySearchMatch> searchByName(String businessName)
      throws TransientException, PermanentException;

  /**
   * Searches for a single company by its exact CIPC registration number and resolves
   * its full profile in one call.
   *
   * @param registrationNumber the CIPC registration number to search for
   * @return the company profile; {@link CompanyProfile#found()} is false if no record
   *     was found
   * @throws TransientException if the Datanamix API is temporarily unreachable
   * @throws PermanentException if the request is invalid or Datanamix returns a
   *     non-retriable error
   */
  CompanyProfile searchByRegistrationNumber(String registrationNumber)
      throws TransientException, PermanentException;

  /**
   * Resolves the full company profile for a specific search match.
   *
   * @param enquiryId the {@code EnquiryID} from a prior search result
   * @param enquiryResultId the {@code EnquiryResultID} from a prior search result
   * @return the company profile; {@link CompanyProfile#found()} is false if no record
   *     was found
   * @throws TransientException if the Datanamix API is temporarily unreachable
   * @throws PermanentException if the request is invalid or Datanamix returns a
   *     non-retriable error
   */
  CompanyProfile getProfile(long enquiryId, long enquiryResultId)
      throws TransientException, PermanentException;
}
