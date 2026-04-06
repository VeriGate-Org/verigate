/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.domain.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import verigate.adapter.sars.domain.models.VatVendorSearchRequest;
import verigate.adapter.sars.domain.models.VatVendorSearchResponse;

/**
 * Service interface for SARS VAT Vendor Search operations.
 */
public interface SarsVatVendorService {

  /**
   * Searches for a VAT vendor using the SARS eFiling SOAP service.
   *
   * @param request the search request containing the VAT number
   * @return the search response with vendor details
   * @throws TransientException if the service is temporarily unavailable
   * @throws PermanentException if the request is permanently invalid
   */
  VatVendorSearchResponse searchVatVendor(VatVendorSearchRequest request)
      throws TransientException, PermanentException;
}
