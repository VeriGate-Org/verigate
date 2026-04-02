/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.domain.services;

import verigate.adapter.dha.domain.models.HanisPersonDetails;

/**
 * Service interface for HANIS (Home Affairs National Identification System) identity verification.
 * Uses the HANIS SOAP web service to query the NPR (National Population Register).
 */
public interface HanisVerificationService {

  /**
   * Verifies an identity by querying the HANIS NPR via the GetData SOAP operation.
   *
   * @param idNumber      the 13-digit South African ID number
   * @param siteId        the registered HANIS site ID
   * @param workstationId the workstation identifier
   * @return person details from the NPR, or an error response
   * @throws domain.exceptions.TransientException if HANIS/NPR is temporarily unavailable
   * @throws domain.exceptions.PermanentException if the request is permanently invalid
   */
  HanisPersonDetails verifyIdentity(String idNumber, String siteId, String workstationId);
}
