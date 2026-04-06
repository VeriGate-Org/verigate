/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.infrastructure.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.sars.domain.enums.VatVendorStatus;
import verigate.adapter.sars.domain.models.VatVendorDetails;
import verigate.adapter.sars.domain.models.VatVendorSearchRequest;
import verigate.adapter.sars.domain.models.VatVendorSearchResponse;
import verigate.adapter.sars.domain.services.SarsVatVendorService;
import verigate.adapter.sars.infrastructure.soap.SarsVatSoapClient;

/**
 * Default implementation of {@link SarsVatVendorService} using the SARS
 * VAT Vendor Search SOAP web service.
 */
public class DefaultSarsVatVendorService implements SarsVatVendorService {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultSarsVatVendorService.class);

  private final SarsVatSoapClient soapClient;

  public DefaultSarsVatVendorService(SarsVatSoapClient soapClient) {
    this.soapClient = soapClient;
  }

  @Override
  public VatVendorSearchResponse searchVatVendor(VatVendorSearchRequest request)
      throws TransientException, PermanentException {

    logger.info("Searching SARS VAT vendor for VAT ending ...{}",
        maskVatNumber(request.vatNumber()));

    try {
      VatVendorDetails details = soapClient.search(
          request.vatNumber(), request.description());

      if (details == null) {
        logger.info("No VAT vendor found for VAT ending ...{}",
            maskVatNumber(request.vatNumber()));
        return VatVendorSearchResponse.notFound();
      }

      VatVendorStatus status = VatVendorStatus.fromDescription(details.vendorStatus());
      logger.info("VAT vendor found with status: {} for VAT ending ...{}",
          status, maskVatNumber(request.vatNumber()));

      return VatVendorSearchResponse.found(details, status);

    } catch (PermanentException e) {
      String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
      if (msg.contains("not found") || msg.contains("no record")
          || msg.contains("invalid vat")) {
        logger.info("SARS returned not-found for VAT ending ...{}: {}",
            maskVatNumber(request.vatNumber()), e.getMessage());
        return VatVendorSearchResponse.notFound();
      }
      throw e;
    } catch (TransientException e) {
      throw e;
    } catch (Exception e) {
      logger.error("Unexpected error searching VAT vendor: {}", e.getMessage(), e);
      throw new PermanentException(
          "SARS VAT vendor search failed: " + e.getMessage(), e);
    }
  }

  private String maskVatNumber(String vatNumber) {
    if (vatNumber == null || vatNumber.length() <= 4) {
      return "****";
    }
    return vatNumber.substring(vatNumber.length() - 4);
  }
}
