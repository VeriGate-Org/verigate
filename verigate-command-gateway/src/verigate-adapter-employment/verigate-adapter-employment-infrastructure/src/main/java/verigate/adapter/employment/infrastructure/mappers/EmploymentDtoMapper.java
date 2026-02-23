/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.employment.infrastructure.mappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.employment.domain.models.EmploymentStatus;
import verigate.adapter.employment.domain.models.EmploymentType;
import verigate.adapter.employment.domain.models.EmploymentVerificationRequest;
import verigate.adapter.employment.domain.models.EmploymentVerificationResponse;
import verigate.adapter.employment.infrastructure.http.dto.EmploymentVerificationRequestDto;
import verigate.adapter.employment.infrastructure.http.dto.EmploymentVerificationResponseDto;

/**
 * Mapper for converting Employment Verification DTOs to domain models and vice versa.
 */
public class EmploymentDtoMapper {

  private static final Logger logger = LoggerFactory.getLogger(EmploymentDtoMapper.class);

  /**
   * Maps a domain request to an infrastructure DTO for the API call.
   */
  public EmploymentVerificationRequestDto mapToRequestDto(
      EmploymentVerificationRequest domainRequest) {
    if (domainRequest == null) {
      return null;
    }

    return new EmploymentVerificationRequestDto(
        domainRequest.idNumber(),
        domainRequest.employerName(),
        domainRequest.employeeNumber(),
        domainRequest.startDate());
  }

  /**
   * Maps an infrastructure response DTO to a domain response model.
   */
  public EmploymentVerificationResponse mapToResponse(EmploymentVerificationResponseDto dto) {
    if (dto == null) {
      logger.warn("Received null employment verification response DTO");
      return EmploymentVerificationResponse.notFound();
    }

    EmploymentStatus status = EmploymentStatus.fromDescription(dto.status());
    EmploymentType employmentType = EmploymentType.fromDescription(dto.employmentType());

    return new EmploymentVerificationResponse(
        status,
        dto.employerName(),
        employmentType,
        dto.jobTitle(),
        dto.startDate(),
        dto.endDate(),
        dto.department());
  }
}
