/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.qlink.infrastructure.mappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.qlink.domain.models.BankAccountStatus;
import verigate.adapter.qlink.domain.models.BankAccountType;
import verigate.adapter.qlink.domain.models.BankVerificationResponse;
import verigate.adapter.qlink.infrastructure.http.dto.QLinkBankVerificationResponseDto;

/**
 * Mapper for converting QLink DTOs to domain models.
 */
public class QLinkDtoMapper {

  private static final Logger logger = LoggerFactory.getLogger(QLinkDtoMapper.class);

  /**
   * Maps QLink bank verification response DTO to domain model.
   */
  public BankVerificationResponse mapToBankVerificationResponse(
      QLinkBankVerificationResponseDto dto) {
    if (dto == null) {
      logger.warn("Received null QLink bank verification response DTO");
      return BankVerificationResponse.notFound();
    }

    BankAccountStatus status = BankAccountStatus.fromDescription(dto.status());
    BankAccountType accountType = BankAccountType.fromDescription(dto.accountType());

    logger.debug(
        "Mapped QLink response - status: {}, accountType: {}, matchScore: {}",
        status,
        accountType,
        dto.matchScore());

    return BankVerificationResponse.success(
        status,
        dto.accountHolderName(),
        dto.branchName(),
        accountType,
        dto.matchScore());
  }
}
