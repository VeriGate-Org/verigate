/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.infrastructure.mocks;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.time.LocalDate;
import java.util.List;
import verigate.adapter.cipc.domain.models.Address;
import verigate.adapter.cipc.domain.models.CompanyProfile;
import verigate.adapter.cipc.domain.models.CompanyProfileRequest;
import verigate.adapter.cipc.domain.models.CompanyProfileResponse;
import verigate.adapter.cipc.domain.models.CompanyStatus;
import verigate.adapter.cipc.domain.models.Director;
import verigate.adapter.cipc.domain.models.DirectorStatus;
import verigate.adapter.cipc.domain.models.DirectorType;
import verigate.adapter.cipc.domain.services.CipcCompanyService;

/**
 * Mock implementation of CIPC Company Service for testing.
 */
public class MockCipcCompanyService implements CipcCompanyService {

    private static final String MOCK_ACTIVE_ENTERPRISE = "2020/939681/07";
    private static final String MOCK_INACTIVE_ENTERPRISE = "2019/123456/01";
    private static final String MOCK_NOT_FOUND_ENTERPRISE = "2021/999999/99";

    @Override
    public CompanyProfileResponse getCompanyProfile(CompanyProfileRequest request) 
        throws TransientException, PermanentException {
        
        return switch (request.enterpriseNumber()) {
            case MOCK_ACTIVE_ENTERPRISE -> createMockActiveCompanyResponse();
            case MOCK_INACTIVE_ENTERPRISE -> createMockInactiveCompanyResponse();
            case MOCK_NOT_FOUND_ENTERPRISE -> CompanyProfileResponse.notFound();
            default -> {
                if (request.enterpriseNumber().startsWith("ERROR")) {
                    yield CompanyProfileResponse.error("Mock error for testing");
                }
                yield createMockActiveCompanyResponse();
            }
        };
    }

    @Override
    public CompanyProfileResponse getCompanyInformation(CompanyProfileRequest request) 
        throws TransientException, PermanentException {
        
        // For basic information, return same as profile but with limited data
        return getCompanyProfile(request);
    }

    private CompanyProfileResponse createMockActiveCompanyResponse() {
        Address mockAddress = Address.builder()
            .addressLine1("123 Mock Street")
            .city("Cape Town")
            .region("Western Cape")
            .postalCode("8001")
            .country("South Africa")
            .build();

        Director mockDirector = Director.builder()
            .firstNames("John")
            .surname("Doe")
            .initials("J")
            .directorStatus(DirectorStatus.ACTIVE)
            .directorType(DirectorType.EXECUTIVE_DIRECTOR)
            .appointmentDate(LocalDate.of(2020, 1, 1))
            .residentialAddress(mockAddress)
            .build();

        CompanyProfile mockCompany = CompanyProfile.builder()
            .enterpriseNumber(MOCK_ACTIVE_ENTERPRISE)
            .enterpriseName("Mock Test Company (Pty) Ltd")
            .enterpriseShortName("Mock Test Co")
            .tradingName("Mock Test Trading")
            .enterpriseTypeDescription("Private Company")
            .businessActivity("Software Development")
            .registrationDate(LocalDate.of(2020, 6, 15))
            .businessStartDate(LocalDate.of(2020, 7, 1))
            .enterpriseStatus(CompanyStatus.ACTIVE)
            .financialYearEnd("28-02")
            .taxNumber("9000000000")
            .officeAddress(mockAddress)
            .postalAddress(mockAddress)
            .directors(List.of(mockDirector))
            .secretaries(List.of())
            .auditors(List.of())
            .capital(List.of())
            .history(List.of())
            .build();

        return CompanyProfileResponse.success(mockCompany);
    }

    private CompanyProfileResponse createMockInactiveCompanyResponse() {
        CompanyProfile mockCompany = CompanyProfile.builder()
            .enterpriseNumber(MOCK_INACTIVE_ENTERPRISE)
            .enterpriseName("Inactive Mock Company (Pty) Ltd")
            .enterpriseStatus(CompanyStatus.DEREGISTERED)
            .registrationDate(LocalDate.of(2019, 1, 1))
            .directors(List.of())
            .secretaries(List.of())
            .auditors(List.of())
            .capital(List.of())
            .history(List.of())
            .build();

        return CompanyProfileResponse.success(mockCompany);
    }

    /**
     * Creates mock responses for specific test scenarios.
     */
    public static MockCipcCompanyService withScenario(String scenario) {
        return new MockCipcCompanyService() {
            @Override
            public CompanyProfileResponse getCompanyProfile(CompanyProfileRequest request) {
                return switch (scenario) {
                    case "TRANSIENT_ERROR" -> {
                        throw new TransientException("Mock transient error");
                    }
                    case "PERMANENT_ERROR" -> {
                        throw new PermanentException("Mock permanent error");
                    }
                    case "NOT_FOUND" -> CompanyProfileResponse.notFound();
                    default -> super.getCompanyProfile(request);
                };
            }
        };
    }
}