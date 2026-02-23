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
import verigate.adapter.sars.domain.enums.TaxComplianceStatus;
import verigate.adapter.sars.domain.handlers.VerifyTaxComplianceCommandHandler;
import verigate.adapter.sars.domain.mappers.TaxComplianceMapper;
import verigate.adapter.sars.domain.models.TaxClearanceCertificate;
import verigate.adapter.sars.domain.models.TaxComplianceRequest;
import verigate.adapter.sars.domain.models.TaxComplianceResponse;
import verigate.adapter.sars.domain.services.SarsTaxComplianceService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;
import verigate.verification.cg.domain.models.VerificationResult;

/**
 * Default implementation for processing SARS tax compliance verification commands.
 */
public class DefaultVerifyTaxComplianceCommandHandler
    implements VerifyTaxComplianceCommandHandler {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultVerifyTaxComplianceCommandHandler.class);

  private final SarsTaxComplianceService sarsTaxComplianceService;

  /**
   * Constructor for default tax compliance verification handler.
   *
   * @param sarsTaxComplianceService the SARS tax compliance verification service
   */
  public DefaultVerifyTaxComplianceCommandHandler(
      SarsTaxComplianceService sarsTaxComplianceService) {
    this.sarsTaxComplianceService = sarsTaxComplianceService;
  }

  /**
   * Handles tax compliance verification using the SARS eFiling API.
   *
   * @param command the command to handle
   * @return map containing verification results (for compatibility with existing interface)
   */
  @Override
  public Map<String, String> handle(VerifyPartyCommand command)
      throws TransientException, PermanentException, InvariantViolationException {

    logger.info("Starting SARS tax compliance verification for command: {}", command.getId());

    try {
      TaxComplianceRequest verificationRequest =
          TaxComplianceMapper.mapToTaxComplianceRequestDefault(command);

      logger.info(
          "Mapped command to tax compliance request for tax ref: {}",
          maskTaxReference(verificationRequest.taxReferenceNumber()));

      TaxComplianceResponse verificationResponse =
          sarsTaxComplianceService.verifyTaxCompliance(verificationRequest);

      logger.info(
          "SARS tax compliance verification completed with status: {} for tax ref: {}",
          verificationResponse.status(),
          maskTaxReference(verificationRequest.taxReferenceNumber()));

      return buildResultMap(verificationResponse, verificationRequest);

    } catch (TransientException | PermanentException e) {
      logger.error(
          "SARS tax compliance verification failed for command {}: {}",
          command.getId(),
          e.getMessage(),
          e);
      throw e;
    } catch (IllegalArgumentException e) {
      logger.error(
          "Invalid tax compliance verification request for command {}: {}",
          command.getId(),
          e.getMessage());
      throw e;
    } catch (Exception e) {
      logger.error(
          "Unexpected error during SARS tax compliance verification for command {}: {}",
          command.getId(),
          e.getMessage(),
          e);
      throw new PermanentException("Unexpected SARS tax compliance verification error", e);
    }
  }

  /**
   * Builds the result map based on the verification response.
   *
   * @param response the tax compliance verification response
   * @param request the original request
   * @return the result map with outcome and details
   */
  private Map<String, String> buildResultMap(
      TaxComplianceResponse response,
      TaxComplianceRequest request) {

    VerificationOutcome outcome = analyzeTaxComplianceStatus(response.status());
    String details = buildDetailsMessage(response);

    Map<String, String> result = new HashMap<>();
    result.put(DomainConstants.RESULT_KEY_OUTCOME, outcome.toString());
    result.put(DomainConstants.RESULT_KEY_DETAILS, details);
    result.put(DomainConstants.RESULT_KEY_TAX_COMPLIANCE_STATUS, response.status().toString());
    result.put(DomainConstants.RESULT_KEY_REASON,
        response.reason() != null ? response.reason() : "");

    TaxClearanceCertificate certificate = response.certificate();
    if (certificate != null) {
      result.put(DomainConstants.RESULT_KEY_CERTIFICATE_NUMBER,
          certificate.certificateNumber() != null ? certificate.certificateNumber() : "");
      result.put(DomainConstants.RESULT_KEY_CERTIFICATE_ISSUE_DATE,
          certificate.issueDate() != null ? certificate.issueDate().toString() : "");
      result.put(DomainConstants.RESULT_KEY_CERTIFICATE_EXPIRY_DATE,
          certificate.expiryDate() != null ? certificate.expiryDate().toString() : "");
      result.put(DomainConstants.RESULT_KEY_CLEARANCE_TYPE,
          certificate.type() != null ? certificate.type().toString() : "");
      result.put(DomainConstants.RESULT_KEY_CERTIFICATE_VALID,
          String.valueOf(certificate.valid()));
    } else {
      result.put(DomainConstants.RESULT_KEY_CERTIFICATE_NUMBER, "");
      result.put(DomainConstants.RESULT_KEY_CERTIFICATE_ISSUE_DATE, "");
      result.put(DomainConstants.RESULT_KEY_CERTIFICATE_EXPIRY_DATE, "");
      result.put(DomainConstants.RESULT_KEY_CLEARANCE_TYPE, "");
      result.put(DomainConstants.RESULT_KEY_CERTIFICATE_VALID, "false");
    }

    logger.info(
        "SARS tax compliance verification result - outcome: {}, status: {}",
        outcome,
        response.status());

    return result;
  }

  /**
   * Analyzes the tax compliance status to determine the verification outcome.
   *
   * <p>Analysis logic:
   * <ul>
   *   <li>COMPLIANT, TCC_VALID -> SUCCEEDED</li>
   *   <li>NON_COMPLIANT, TCC_EXPIRED -> HARD_FAIL</li>
   *   <li>NOT_FOUND -> SOFT_FAIL</li>
   *   <li>ERROR -> SYSTEM_OUTAGE</li>
   * </ul>
   *
   * @param status the tax compliance status
   * @return the corresponding verification outcome
   */
  private VerificationOutcome analyzeTaxComplianceStatus(TaxComplianceStatus status) {
    return switch (status) {
      case COMPLIANT -> VerificationOutcome.SUCCEEDED;
      case TCC_VALID -> VerificationOutcome.SUCCEEDED;
      case NON_COMPLIANT -> VerificationOutcome.HARD_FAIL;
      case TCC_EXPIRED -> VerificationOutcome.HARD_FAIL;
      case NOT_FOUND -> VerificationOutcome.SOFT_FAIL;
      case ERROR -> VerificationOutcome.SYSTEM_OUTAGE;
    };
  }

  /**
   * Builds a human-readable details message based on the verification response.
   *
   * @param response the tax compliance verification response
   * @return a human-readable details message
   */
  private String buildDetailsMessage(TaxComplianceResponse response) {
    return switch (response.status()) {
      case COMPLIANT -> {
        TaxClearanceCertificate cert = response.certificate();
        if (cert != null) {
          yield String.format(
              "Tax compliance verified successfully. Certificate: %s, Type: %s, Expiry: %s",
              cert.certificateNumber() != null ? cert.certificateNumber() : "N/A",
              cert.type() != null ? cert.type() : "N/A",
              cert.expiryDate() != null ? cert.expiryDate() : "N/A");
        }
        yield "Tax compliance verified successfully. Taxpayer is in good standing with SARS";
      }
      case TCC_VALID -> {
        TaxClearanceCertificate cert = response.certificate();
        if (cert != null) {
          yield String.format(
              "Tax Clearance Certificate is valid. Certificate: %s, Expiry: %s",
              cert.certificateNumber() != null ? cert.certificateNumber() : "N/A",
              cert.expiryDate() != null ? cert.expiryDate() : "N/A");
        }
        yield "Tax Clearance Certificate is valid";
      }
      case NON_COMPLIANT -> String.format(
          "Taxpayer is non-compliant with SARS. Reason: %s",
          response.reason() != null ? response.reason() : "N/A");
      case TCC_EXPIRED -> {
        TaxClearanceCertificate cert = response.certificate();
        if (cert != null) {
          yield String.format(
              "Tax Clearance Certificate has expired. Certificate: %s, Expired: %s",
              cert.certificateNumber() != null ? cert.certificateNumber() : "N/A",
              cert.expiryDate() != null ? cert.expiryDate() : "N/A");
        }
        yield "Tax Clearance Certificate has expired";
      }
      case NOT_FOUND -> "No taxpayer record found for the provided details";
      case ERROR -> String.format(
          "SARS tax compliance verification error: %s",
          response.reason() != null ? response.reason() : "Unknown error");
    };
  }

  /**
   * Alternative async method for non-blocking verification.
   */
  @Override
  public CompletableFuture<VerificationResult> handleAsync(VerifyPartyCommand command) {
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            Map<String, String> result = handle(command);
            VerificationOutcome outcome = VerificationOutcome.valueOf(result.get("outcome"));
            String details = result.get("details");
            return new VerificationResult(outcome, details);
          } catch (Exception e) {
            logger.error(
                "Async SARS tax compliance verification failed for command {}: {}",
                command.getId(),
                e.getMessage(),
                e);
            return new VerificationResult(
                VerificationOutcome.SYSTEM_OUTAGE,
                "SARS tax compliance verification failed: " + e.getMessage());
          }
        });
  }

  /**
   * Masks a tax reference number for safe logging, showing only the last 4 digits.
   *
   * @param taxReference the tax reference number to mask
   * @return the masked tax reference number
   */
  private String maskTaxReference(String taxReference) {
    if (taxReference == null || taxReference.length() <= 4) {
      return "****";
    }
    return "****" + taxReference.substring(taxReference.length() - 4);
  }
}
