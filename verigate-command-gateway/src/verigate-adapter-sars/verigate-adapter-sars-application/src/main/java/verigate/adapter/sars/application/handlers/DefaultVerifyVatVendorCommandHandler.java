/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.application.handlers;

import domain.exceptions.InvariantViolationException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.sars.domain.constants.DomainConstants;
import verigate.adapter.sars.domain.enums.VatVendorStatus;
import verigate.adapter.sars.domain.handlers.VerifyVatVendorCommandHandler;
import verigate.adapter.sars.domain.mappers.VatVendorMapper;
import verigate.adapter.sars.domain.models.VatVendorDetails;
import verigate.adapter.sars.domain.models.VatVendorSearchRequest;
import verigate.adapter.sars.domain.models.VatVendorSearchResponse;
import verigate.adapter.sars.domain.services.SarsVatVendorService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;
import verigate.verification.cg.domain.models.VerificationResult;

/**
 * Default implementation for processing SARS VAT vendor verification commands.
 */
public class DefaultVerifyVatVendorCommandHandler
    implements VerifyVatVendorCommandHandler {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultVerifyVatVendorCommandHandler.class);

  private final SarsVatVendorService sarsVatVendorService;

  public DefaultVerifyVatVendorCommandHandler(
      SarsVatVendorService sarsVatVendorService) {
    this.sarsVatVendorService = sarsVatVendorService;
  }

  @Override
  public Map<String, String> handle(VerifyPartyCommand command)
      throws TransientException, PermanentException, InvariantViolationException {

    logger.info("Starting SARS VAT vendor verification for command: {}", command.getId());

    try {
      VatVendorSearchRequest request =
          VatVendorMapper.mapToVatVendorSearchRequest(command);

      logger.info("Mapped command to VAT vendor search request for VAT: {}",
          maskVatNumber(request.vatNumber()));

      VatVendorSearchResponse response =
          sarsVatVendorService.searchVatVendor(request);

      logger.info("SARS VAT vendor search completed with status: {} for VAT: {}",
          response.status(), maskVatNumber(request.vatNumber()));

      return buildResultMap(response);

    } catch (TransientException | PermanentException e) {
      logger.error("SARS VAT vendor verification failed for command {}: {}",
          command.getId(), e.getMessage(), e);
      throw e;
    } catch (IllegalArgumentException e) {
      logger.error("Invalid VAT vendor verification request for command {}: {}",
          command.getId(), e.getMessage());
      throw e;
    } catch (Exception e) {
      logger.error("Unexpected error during SARS VAT vendor verification for command {}: {}",
          command.getId(), e.getMessage(), e);
      throw new PermanentException("Unexpected SARS VAT vendor verification error", e);
    }
  }

  private Map<String, String> buildResultMap(VatVendorSearchResponse response) {
    VerificationOutcome outcome = analyzeVatVendorStatus(response.status());
    String details = buildDetailsMessage(response);

    Map<String, String> result = new HashMap<>();
    result.put(DomainConstants.RESULT_KEY_OUTCOME, outcome.toString());
    result.put(DomainConstants.RESULT_KEY_DETAILS, details);
    result.put(DomainConstants.RESULT_KEY_VAT_VENDOR_STATUS, response.status().toString());
    result.put(DomainConstants.RESULT_KEY_REASON,
        response.reason() != null ? response.reason() : "");

    VatVendorDetails vendor = response.vendorDetails();
    if (vendor != null) {
      result.put(DomainConstants.RESULT_KEY_VAT_NUMBER,
          vendor.vatNumber() != null ? vendor.vatNumber() : "");
      result.put(DomainConstants.RESULT_KEY_VENDOR_NAME,
          vendor.vendorName() != null ? vendor.vendorName() : "");
      result.put(DomainConstants.RESULT_KEY_TRADING_NAME,
          vendor.tradingName() != null ? vendor.tradingName() : "");
      result.put(DomainConstants.RESULT_KEY_REGISTRATION_DATE,
          vendor.registrationDate() != null ? vendor.registrationDate() : "");
      result.put(DomainConstants.RESULT_KEY_ACTIVITY_CODE,
          vendor.activityCode() != null ? vendor.activityCode() : "");
      result.put(DomainConstants.RESULT_KEY_PHYSICAL_ADDRESS,
          vendor.physicalAddress() != null ? vendor.physicalAddress() : "");
    } else {
      result.put(DomainConstants.RESULT_KEY_VAT_NUMBER, "");
      result.put(DomainConstants.RESULT_KEY_VENDOR_NAME, "");
      result.put(DomainConstants.RESULT_KEY_TRADING_NAME, "");
      result.put(DomainConstants.RESULT_KEY_REGISTRATION_DATE, "");
      result.put(DomainConstants.RESULT_KEY_ACTIVITY_CODE, "");
      result.put(DomainConstants.RESULT_KEY_PHYSICAL_ADDRESS, "");
    }

    logger.info("SARS VAT vendor verification result - outcome: {}, status: {}",
        outcome, response.status());

    return result;
  }

  /**
   * Maps VAT vendor status to verification outcome.
   *
   * <ul>
   *   <li>ACTIVE -> SUCCEEDED</li>
   *   <li>INACTIVE, DEREGISTERED -> HARD_FAIL</li>
   *   <li>NOT_FOUND -> SOFT_FAIL</li>
   *   <li>ERROR -> SYSTEM_OUTAGE</li>
   * </ul>
   */
  private VerificationOutcome analyzeVatVendorStatus(VatVendorStatus status) {
    return switch (status) {
      case ACTIVE -> VerificationOutcome.SUCCEEDED;
      case INACTIVE -> VerificationOutcome.HARD_FAIL;
      case DEREGISTERED -> VerificationOutcome.HARD_FAIL;
      case NOT_FOUND -> VerificationOutcome.SOFT_FAIL;
      case ERROR -> VerificationOutcome.SYSTEM_OUTAGE;
    };
  }

  private String buildDetailsMessage(VatVendorSearchResponse response) {
    return switch (response.status()) {
      case ACTIVE -> {
        VatVendorDetails v = response.vendorDetails();
        if (v != null) {
          yield String.format(
              "VAT vendor is active. Name: %s, Trading Name: %s, Registered: %s",
              v.vendorName() != null ? v.vendorName() : "N/A",
              v.tradingName() != null ? v.tradingName() : "N/A",
              v.registrationDate() != null ? v.registrationDate() : "N/A");
        }
        yield "VAT vendor is registered and active";
      }
      case INACTIVE -> String.format("VAT vendor is inactive. Reason: %s",
          response.reason() != null ? response.reason() : "N/A");
      case DEREGISTERED -> String.format("VAT vendor has been deregistered. Reason: %s",
          response.reason() != null ? response.reason() : "N/A");
      case NOT_FOUND -> "No VAT vendor record found for the provided VAT number";
      case ERROR -> String.format("SARS VAT vendor search error: %s",
          response.reason() != null ? response.reason() : "Unknown error");
    };
  }

  @Override
  public CompletableFuture<VerificationResult> handleAsync(VerifyPartyCommand command) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        Map<String, String> result = handle(command);
        VerificationOutcome outcome =
            VerificationOutcome.valueOf(result.get(DomainConstants.RESULT_KEY_OUTCOME));
        String details = result.get(DomainConstants.RESULT_KEY_DETAILS);
        return new VerificationResult(outcome, details);
      } catch (Exception e) {
        logger.error("Async SARS VAT vendor verification failed for command {}: {}",
            command.getId(), e.getMessage(), e);
        return new VerificationResult(
            VerificationOutcome.SYSTEM_OUTAGE,
            "SARS VAT vendor verification failed: " + e.getMessage());
      }
    });
  }

  private String maskVatNumber(String vatNumber) {
    if (vatNumber == null || vatNumber.length() <= 4) {
      return "****";
    }
    return "****" + vatNumber.substring(vatNumber.length() - 4);
  }
}
