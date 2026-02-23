/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.application.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import verigate.adapter.cipc.domain.models.CompanyProfile;
import verigate.adapter.cipc.domain.models.CompanyProfileRequest;
import verigate.adapter.cipc.domain.models.CompanyProfileResponse;
import verigate.adapter.cipc.domain.models.CompanyStatus;
import verigate.adapter.cipc.domain.models.Director;
import verigate.adapter.cipc.domain.models.DirectorStatus;
import verigate.adapter.cipc.domain.services.CipcCompanyService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;

class DefaultVerifyCompanyDetailsCommandHandlerTest {

    @Mock
    private CipcCompanyService companyService;

    private DefaultVerifyCompanyDetailsCommandHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new DefaultVerifyCompanyDetailsCommandHandler(companyService);
    }

    @Test
    void testHandleSuccessfulVerification() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(), 
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("enterpriseNumber", "2020/939681/07")
        );

        Director activeDirector = Director.builder()
            .firstNames("John")
            .surname("Doe")
            .directorStatus(DirectorStatus.ACTIVE)
            .build();

        CompanyProfile activeCompany = CompanyProfile.builder()
            .enterpriseNumber("2020/939681/07")
            .enterpriseName("Test Company (Pty) Ltd")
            .enterpriseStatus(CompanyStatus.ACTIVE)
            .directors(List.of(activeDirector))
            .secretaries(List.of())
            .auditors(List.of())
            .capital(List.of())
            .history(List.of())
            .build();

        CompanyProfileResponse successResponse = CompanyProfileResponse.success(activeCompany);

        when(companyService.getCompanyProfile(any(CompanyProfileRequest.class)))
            .thenReturn(successResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("Company verified successfully"));
        assertEquals("2020/939681/07", result.get("enterpriseNumber"));
        assertEquals("Test Company (Pty) Ltd", result.get("enterpriseName"));
        assertEquals("ACTIVE", result.get("enterpriseStatus"));
        assertEquals("true", result.get("isActive"));
        assertEquals("1", result.get("activeDirectorsCount"));
        assertEquals("1", result.get("totalDirectorsCount"));

        verify(companyService).getCompanyProfile(any(CompanyProfileRequest.class));
    }

    @Test
    void testHandleCompanyNotFound() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(), 
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("enterpriseNumber", "2020/999999/99")
        );

        CompanyProfileResponse notFoundResponse = CompanyProfileResponse.notFound();

        when(companyService.getCompanyProfile(any(CompanyProfileRequest.class)))
            .thenReturn(notFoundResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertEquals("Company not found in CIPC registry", result.get("details"));
        assertEquals("2020/999999/99", result.get("enterpriseNumber"));
        assertEquals("false", result.get("found"));

        verify(companyService).getCompanyProfile(any(CompanyProfileRequest.class));
    }

    @Test
    void testHandleInactiveCompany() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(), 
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("enterpriseNumber", "2019/123456/01")
        );

        CompanyProfile inactiveCompany = CompanyProfile.builder()
            .enterpriseNumber("2019/123456/01")
            .enterpriseName("Inactive Company (Pty) Ltd")
            .enterpriseStatus(CompanyStatus.DEREGISTERED)
            .directors(List.of())
            .secretaries(List.of())
            .auditors(List.of())
            .capital(List.of())
            .history(List.of())
            .build();

        CompanyProfileResponse successResponse = CompanyProfileResponse.success(inactiveCompany);

        when(companyService.getCompanyProfile(any(CompanyProfileRequest.class)))
            .thenReturn(successResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("Company is not active"));
        assertTrue(result.get("details").contains("DEREGISTERED"));
        assertEquals("DEREGISTERED", result.get("enterpriseStatus"));
        assertEquals("false", result.get("isActive"));

        verify(companyService).getCompanyProfile(any(CompanyProfileRequest.class));
    }

    @Test
    void testHandleCompanyWithNoActiveDirectors() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(), 
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("enterpriseNumber", "2020/939681/07")
        );

        Director resignedDirector = Director.builder()
            .firstNames("Jane")
            .surname("Smith")
            .directorStatus(DirectorStatus.RESIGNED)
            .resignationDate(LocalDate.of(2023, 1, 1))
            .build();

        CompanyProfile companyWithoutActiveDirectors = CompanyProfile.builder()
            .enterpriseNumber("2020/939681/07")
            .enterpriseName("Test Company (Pty) Ltd")
            .enterpriseStatus(CompanyStatus.ACTIVE)
            .directors(List.of(resignedDirector))
            .secretaries(List.of())
            .auditors(List.of())
            .capital(List.of())
            .history(List.of())
            .build();

        CompanyProfileResponse successResponse = CompanyProfileResponse.success(companyWithoutActiveDirectors);

        when(companyService.getCompanyProfile(any(CompanyProfileRequest.class)))
            .thenReturn(successResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertEquals("Company has no active directors", result.get("details"));
        assertEquals("0", result.get("activeDirectorsCount"));
        assertEquals("1", result.get("totalDirectorsCount"));

        verify(companyService).getCompanyProfile(any(CompanyProfileRequest.class));
    }

    @Test
    void testHandleMissingEnterpriseNumber() {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(), 
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("companyName", "Test Company")
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
        
        verifyNoInteractions(companyService);
    }

    @Test
    void testHandleServiceError() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(), 
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("enterpriseNumber", "2020/939681/07")
        );

        CompanyProfileResponse errorResponse = CompanyProfileResponse.error("API service unavailable");

        when(companyService.getCompanyProfile(any(CompanyProfileRequest.class)))
            .thenReturn(errorResponse);

        // Act & Assert
        assertThrows(PermanentException.class, () -> handler.handle(command));

        verify(companyService).getCompanyProfile(any(CompanyProfileRequest.class));
    }

    @Test
    void testHandleTransientException() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(), 
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("enterpriseNumber", "2020/939681/07")
        );

        when(companyService.getCompanyProfile(any(CompanyProfileRequest.class)))
            .thenThrow(new TransientException("Network timeout"));

        // Act & Assert
        assertThrows(TransientException.class, () -> handler.handle(command));

        verify(companyService).getCompanyProfile(any(CompanyProfileRequest.class));
    }

    @Test
    void testHandleAsyncSuccess() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(), 
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("enterpriseNumber", "2020/939681/07")
        );

        Director activeDirector = Director.builder()
            .firstNames("John")
            .surname("Doe")
            .directorStatus(DirectorStatus.ACTIVE)
            .build();

        CompanyProfile activeCompany = CompanyProfile.builder()
            .enterpriseNumber("2020/939681/07")
            .enterpriseName("Test Company (Pty) Ltd")
            .enterpriseStatus(CompanyStatus.ACTIVE)
            .directors(List.of(activeDirector))
            .secretaries(List.of())
            .auditors(List.of())
            .capital(List.of())
            .history(List.of())
            .build();

        CompanyProfileResponse successResponse = CompanyProfileResponse.success(activeCompany);

        when(companyService.getCompanyProfile(any(CompanyProfileRequest.class)))
            .thenReturn(successResponse);

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());
        assertTrue(result.failureReason().contains("Company verified successfully"));

        verify(companyService).getCompanyProfile(any(CompanyProfileRequest.class));
    }

    @Test
    void testHandleAsyncFailure() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(), 
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("enterpriseNumber", "2020/939681/07")
        );

        when(companyService.getCompanyProfile(any(CompanyProfileRequest.class)))
            .thenThrow(new PermanentException("Service error"));

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL, result.outcome());
        assertTrue(result.failureReason().contains("Company verification failed"));

        verify(companyService).getCompanyProfile(any(CompanyProfileRequest.class));
    }
}