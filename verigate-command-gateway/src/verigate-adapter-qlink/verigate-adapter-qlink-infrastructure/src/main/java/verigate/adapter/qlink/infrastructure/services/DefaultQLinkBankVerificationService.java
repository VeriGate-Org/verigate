/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.qlink.infrastructure.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.qlink.domain.models.BankVerificationRequest;
import verigate.adapter.qlink.domain.models.BankVerificationResponse;
import verigate.adapter.qlink.domain.services.QLinkBankVerificationService;
import verigate.adapter.qlink.infrastructure.http.QLinkBankApiAdapter;
import verigate.adapter.qlink.infrastructure.http.dto.QLinkBankVerificationResponseDto;
import verigate.adapter.qlink.infrastructure.mappers.QLinkDtoMapper;

/**
 * Default implementation of the {@link QLinkBankVerificationService} using the infrastructure HTTP
 * adapter.
 */
public class DefaultQLinkBankVerificationService implements QLinkBankVerificationService {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultQLinkBankVerificationService.class);

  private final QLinkBankApiAdapter apiAdapter;
  private final QLinkDtoMapper dtoMapper;

  public DefaultQLinkBankVerificationService(
      QLinkBankApiAdapter apiAdapter, QLinkDtoMapper dtoMapper) {
    this.apiAdapter = apiAdapter;
    this.dtoMapper = dtoMapper;
  }

  @Override
  public BankVerificationResponse verifyBankAccount(BankVerificationRequest request) {
    String maskedAccount = maskAccountNumber(request.accountNumber());
    logger.info("Verifying bank account ending: {}", maskedAccount);

    try {
      QLinkBankVerificationResponseDto responseDto = apiAdapter.verifyBankAccount(
          request.accountNumber(),
          request.branchCode(),
          request.accountHolderName(),
          request.idNumber());

      if (responseDto == null) {
        logger.info("No verification result for account ending: {}", maskedAccount);
        return BankVerificationResponse.notFound();
      }

      BankVerificationResponse response = dtoMapper.mapToBankVerificationResponse(responseDto);

      logger.info(
          "Successfully verified bank account ending {} - status: {}",
          maskedAccount,
          response.status());
      return response;

    } catch (PermanentException e) {
      if (e.getMessage() != null && e.getMessage().contains("not found")) {
        logger.info("Bank account not found for account ending: {}", maskedAccount);
        return BankVerificationResponse.notFound();
      }

      logger.error(
          "Permanent error verifying bank account ending {}: {}",
          maskedAccount,
          e.getMessage());
      throw e;

    } catch (TransientException e) {
      logger.warn(
          "Transient error verifying bank account ending {}: {}",
          maskedAccount,
          e.getMessage());
      throw e;

    } catch (Exception e) {
      logger.error(
          "Unexpected error verifying bank account ending {}: {}",
          maskedAccount,
          e.getMessage(),
          e);
      throw new PermanentException("Unexpected error: " + e.getMessage(), e);
    }
  }

  private String maskAccountNumber(String accountNumber) {
    if (accountNumber == null || accountNumber.length() < 4) {
      return "****";
    }
    return "****" + accountNumber.substring(accountNumber.length() - 4);
  }
}
