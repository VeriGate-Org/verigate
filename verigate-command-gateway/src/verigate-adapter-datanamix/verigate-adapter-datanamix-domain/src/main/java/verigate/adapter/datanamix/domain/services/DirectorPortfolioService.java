/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.datanamix.domain.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import verigate.adapter.datanamix.domain.models.DirectorPortfolio;

/**
 * Port for the reverse director lookup story 2.3 needs — given a South African ID
 * number, find every company that person is or was a director of. Neither CIPC's own
 * government API nor {@code verigate-adapter-cipc} can answer this direction at all
 * (every CIPC endpoint is keyed by enterprise number); Datanamix's CIPC Director
 * Verification API is the real data source.
 */
public interface DirectorPortfolioService {

  /**
   * Searches for every directorship record associated with a South African ID number.
   *
   * @param idNumber the 13-digit South African ID number to search for
   * @return the director portfolio; {@link DirectorPortfolio#found()} is false if no
   *     identity match was found
   * @throws TransientException if the Datanamix API is temporarily unreachable
   * @throws PermanentException if the request is invalid or Datanamix returns a
   *     non-retriable error
   */
  DirectorPortfolio searchByIdNumber(String idNumber)
      throws TransientException, PermanentException;
}
