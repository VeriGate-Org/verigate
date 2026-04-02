/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.infrastructure.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.dha.domain.models.HanisErrorCode;
import verigate.adapter.dha.domain.models.HanisPersonDetails;
import verigate.adapter.dha.domain.services.HanisVerificationService;
import verigate.adapter.dha.infrastructure.soap.HanisSoapClient;

/**
 * Default implementation of {@link HanisVerificationService} using the HANIS SOAP client.
 * Maps HANIS error codes to domain exceptions.
 */
public class DefaultHanisVerificationService implements HanisVerificationService {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultHanisVerificationService.class);

  private final HanisSoapClient soapClient;

  public DefaultHanisVerificationService(HanisSoapClient soapClient) {
    this.soapClient = soapClient;
  }

  @Override
  public HanisPersonDetails verifyIdentity(String idNumber, String siteId, String workstationId) {
    String maskedId = maskIdNumber(idNumber);
    logger.info("HANIS verification for ID ending: ...{}", maskedId);

    try {
      HanisPersonDetails details = soapClient.getData(idNumber, siteId, workstationId);

      if (details.isSuccess()) {
        logger.info("HANIS verification succeeded for ID ending: ...{}", maskedId);
        return details;
      }

      HanisErrorCode errorCode = HanisErrorCode.fromCode(details.error());
      if (errorCode == null) {
        logger.warn("Unknown HANIS error code {} for ID ending: ...{}", details.error(), maskedId);
        return details;
      }

      logger.info("HANIS returned error code {} ({}) for ID ending: ...{}",
          errorCode.code(), errorCode.description(), maskedId);

      if (errorCode.isRetriable()) {
        throw new TransientException(
            "HANIS temporarily unavailable: " + errorCode.description());
      }

      switch (errorCode) {
        case INVALID_IDN, SITE_ID_NOT_SUPPLIED, WRKSTN_ID_NOT_SUPPLIED,
            WRKSTN_ID_TOO_LONG, SITE_ID_TOO_LONG, NOT_REGISTERED, SERVICE_NOT_ALLOWED:
          throw new PermanentException("HANIS configuration error: " + errorCode.description());
        case IDN_NOT_FOUND_NPR:
          return HanisPersonDetails.notFound();
        default:
          return details;
      }

    } catch (TransientException | PermanentException e) {
      throw e;
    } catch (Exception e) {
      logger.error("Unexpected HANIS error for ID ending ...{}: {}", maskedId, e.getMessage(), e);
      throw new PermanentException("HANIS verification failed: " + e.getMessage(), e);
    }
  }

  private String maskIdNumber(String idNumber) {
    if (idNumber == null || idNumber.length() < 4) {
      return "***";
    }
    return idNumber.substring(idNumber.length() - 4);
  }
}
