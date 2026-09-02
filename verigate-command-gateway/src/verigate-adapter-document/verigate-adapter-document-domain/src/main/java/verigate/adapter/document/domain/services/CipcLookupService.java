/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.domain.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import verigate.adapter.document.domain.models.CipcCompanyLookupResult;

/**
 * Port for looking up live CIPC company data to cross-validate against an uploaded CIPC
 * registration document. Deliberately independent of {@code verigate-adapter-cipc} — see story
 * 2.1 design notes for why this adapter owns its own thin CIPC client rather than depending on
 * the CIPC adapter's modules directly.
 */
public interface CipcLookupService {

  /**
   * Looks up a company by CIPC enterprise/registration number.
   *
   * @param registrationNumber the CIPC enterprise number (format {@code YYYY/NNNNNN/NN})
   * @return the lookup result; {@link CipcCompanyLookupResult#found()} is false if no company
   *     was found for the given number
   * @throws TransientException if the CIPC API is temporarily unreachable (retriable)
   * @throws PermanentException if the request is invalid or CIPC returns a non-retriable error
   */
  CipcCompanyLookupResult lookupCompany(String registrationNumber)
      throws TransientException, PermanentException;
}
