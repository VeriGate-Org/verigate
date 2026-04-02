/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.infrastructure.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.dha.domain.models.HanisPersonDetails;
import verigate.adapter.dha.domain.models.IdentityVerificationRequest;
import verigate.adapter.dha.domain.models.IdentityVerificationResponse;
import verigate.adapter.dha.domain.services.DhaIdentityVerificationService;
import verigate.adapter.dha.domain.services.HanisVerificationService;
import verigate.adapter.dha.infrastructure.config.HanisConfiguration;
import verigate.adapter.dha.infrastructure.mappers.HanisResponseMapper;

/**
 * DHA identity verification service implementation that uses the HANIS SOAP interface
 * instead of the generic REST API.
 * Selected when {@code HANIS_INTEGRATION_ENABLED=true}.
 */
public class HanisDhaIdentityVerificationService implements DhaIdentityVerificationService {

  private static final Logger logger =
      LoggerFactory.getLogger(HanisDhaIdentityVerificationService.class);

  private final HanisVerificationService hanisService;
  private final HanisResponseMapper responseMapper;
  private final HanisConfiguration configuration;
  private HanisPersonDetails lastHanisDetails;

  public HanisDhaIdentityVerificationService(
      HanisVerificationService hanisService,
      HanisResponseMapper responseMapper,
      HanisConfiguration configuration) {
    this.hanisService = hanisService;
    this.responseMapper = responseMapper;
    this.configuration = configuration;
  }

  @Override
  public IdentityVerificationResponse verifyIdentity(IdentityVerificationRequest request) {
    logger.info("Verifying identity via HANIS SOAP for ID ending: ...{}",
        maskIdNumber(request.idNumber()));

    HanisPersonDetails details = hanisService.verifyIdentity(
        request.idNumber(),
        configuration.getSiteId(),
        configuration.getWorkstationId());

    // Store last details for enrichment by the command handler
    this.lastHanisDetails = details;

    return responseMapper.mapToVerificationResponse(details);
  }

  /**
   * Returns the raw HANIS person details from the most recent verification call.
   * Used by the command handler to enrich the result map with HANIS-specific fields.
   */
  public HanisPersonDetails getLastHanisDetails() {
    return lastHanisDetails;
  }

  private String maskIdNumber(String idNumber) {
    if (idNumber == null || idNumber.length() < 4) {
      return "***";
    }
    return idNumber.substring(idNumber.length() - 4);
  }
}
