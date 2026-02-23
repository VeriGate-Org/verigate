/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.infrastructure.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import verigate.adapter.cipc.domain.models.CompanyProfile;
import verigate.adapter.cipc.domain.models.CompanyProfileRequest;
import verigate.adapter.cipc.domain.models.CompanyProfileResponse;
import verigate.adapter.cipc.domain.models.CompanyStatus;
import verigate.adapter.cipc.infrastructure.http.CipcCompanyApiAdapter;
import verigate.adapter.cipc.infrastructure.http.dto.CipcCompanyDto;
import verigate.adapter.cipc.infrastructure.http.dto.CipcCompanyProfileResponseDto;
import verigate.adapter.cipc.infrastructure.mappers.CipcDtoMapper;

class DefaultCipcCompanyServiceTest {

    @Mock
    private CipcCompanyApiAdapter apiAdapter;

    @Mock
    private CipcDtoMapper dtoMapper;

    private DefaultCipcCompanyService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new DefaultCipcCompanyService(apiAdapter, dtoMapper);
    }

    @Test
    void testGetCompanyProfileSuccess() throws Exception {
        // Arrange
        CompanyProfileRequest request = CompanyProfileRequest.builder()
            .enterpriseNumber("2020/939681/07")
            .build();

        CipcCompanyDto mockCompanyDto = new CipcCompanyDto(
            "2020/939681/07", "Test Company", null, null, null, 
            "Private Company", null, null, null, "ACTIVE", 
            null, null, null, null, null, 
            null, null, null, null, null
        );

        CipcCompanyProfileResponseDto mockResponseDto = new CipcCompanyProfileResponseDto(
            List.of(mockCompanyDto)
        );

        CompanyProfile mockCompanyProfile = CompanyProfile.builder()
            .enterpriseNumber("2020/939681/07")
            .enterpriseName("Test Company")
            .enterpriseStatus(CompanyStatus.ACTIVE)
            .directors(List.of())
            .secretaries(List.of())
            .auditors(List.of())
            .capital(List.of())
            .history(List.of())
            .build();

        when(apiAdapter.getCompanyProfile("2020/939681/07")).thenReturn(mockResponseDto);
        when(dtoMapper.mapToCompanyProfile(mockCompanyDto)).thenReturn(mockCompanyProfile);

        // Act
        CompanyProfileResponse response = service.getCompanyProfile(request);

        // Assert
        assertTrue(response.isSuccess());
        assertNotNull(response.company());
        assertEquals("2020/939681/07", response.company().enterpriseNumber());
        assertEquals("Test Company", response.company().enterpriseName());
        assertEquals(CompanyStatus.ACTIVE, response.company().enterpriseStatus());

        verify(apiAdapter).getCompanyProfile("2020/939681/07");
        verify(dtoMapper).mapToCompanyProfile(mockCompanyDto);
    }

    @Test
    void testGetCompanyProfileNotFound() throws Exception {
        // Arrange
        CompanyProfileRequest request = CompanyProfileRequest.builder()
            .enterpriseNumber("2020/999999/99")
            .build();

        CipcCompanyProfileResponseDto emptyResponseDto = new CipcCompanyProfileResponseDto(
            List.of()
        );

        when(apiAdapter.getCompanyProfile("2020/999999/99")).thenReturn(emptyResponseDto);

        // Act
        CompanyProfileResponse response = service.getCompanyProfile(request);

        // Assert
        assertFalse(response.found());
        assertNull(response.company());
        assertEquals("Company not found", response.errorMessage());

        verify(apiAdapter).getCompanyProfile("2020/999999/99");
        verifyNoInteractions(dtoMapper);
    }

    @Test
    void testGetCompanyProfilePermanentException() throws Exception {
        // Arrange
        CompanyProfileRequest request = CompanyProfileRequest.builder()
            .enterpriseNumber("2020/939681/07")
            .build();

        when(apiAdapter.getCompanyProfile("2020/939681/07"))
            .thenThrow(new PermanentException("API error"));

        // Act
        CompanyProfileResponse response = service.getCompanyProfile(request);

        // Assert
        assertFalse(response.isSuccess());
        assertTrue(response.isError());
        assertTrue(response.errorMessage().contains("API error"));

        verify(apiAdapter).getCompanyProfile("2020/939681/07");
        verifyNoInteractions(dtoMapper);
    }

    @Test
    void testGetCompanyProfileTransientException() throws Exception {
        // Arrange
        CompanyProfileRequest request = CompanyProfileRequest.builder()
            .enterpriseNumber("2020/939681/07")
            .build();

        when(apiAdapter.getCompanyProfile("2020/939681/07"))
            .thenThrow(new TransientException("Network error"));

        // Act & Assert
        assertThrows(TransientException.class, () -> service.getCompanyProfile(request));

        verify(apiAdapter).getCompanyProfile("2020/939681/07");
        verifyNoInteractions(dtoMapper);
    }

    @Test
    void testGetCompanyProfileNotFoundPermanentException() throws Exception {
        // Arrange
        CompanyProfileRequest request = CompanyProfileRequest.builder()
            .enterpriseNumber("2020/999999/99")
            .build();

        when(apiAdapter.getCompanyProfile("2020/999999/99"))
            .thenThrow(new PermanentException("Resource not found"));

        // Act
        CompanyProfileResponse response = service.getCompanyProfile(request);

        // Assert
        assertFalse(response.found());
        assertNull(response.company());
        assertEquals("Company not found", response.errorMessage());

        verify(apiAdapter).getCompanyProfile("2020/999999/99");
        verifyNoInteractions(dtoMapper);
    }

    @Test
    void testGetCompanyProfileUnexpectedException() throws Exception {
        // Arrange
        CompanyProfileRequest request = CompanyProfileRequest.builder()
            .enterpriseNumber("2020/939681/07")
            .build();

        when(apiAdapter.getCompanyProfile("2020/939681/07"))
            .thenThrow(new RuntimeException("Unexpected error"));

        // Act
        CompanyProfileResponse response = service.getCompanyProfile(request);

        // Assert
        assertFalse(response.isSuccess());
        assertTrue(response.isError());
        assertTrue(response.errorMessage().contains("Unexpected error"));

        verify(apiAdapter).getCompanyProfile("2020/939681/07");
        verifyNoInteractions(dtoMapper);
    }

    @Test
    void testGetCompanyInformation() throws Exception {
        // Arrange
        CompanyProfileRequest request = CompanyProfileRequest.builder()
            .enterpriseNumber("2020/939681/07")
            .build();

        CipcCompanyDto mockCompanyDto = new CipcCompanyDto(
            "2020/939681/07", "Test Company", null, null, null, 
            "Private Company", null, null, null, "ACTIVE", 
            null, null, null, null, null, 
            null, null, null, null, null
        );

        CipcCompanyProfileResponseDto mockResponseDto = new CipcCompanyProfileResponseDto(
            List.of(mockCompanyDto)
        );

        CompanyProfile mockCompanyProfile = CompanyProfile.builder()
            .enterpriseNumber("2020/939681/07")
            .enterpriseName("Test Company")
            .enterpriseStatus(CompanyStatus.ACTIVE)
            .directors(List.of())
            .secretaries(List.of())
            .auditors(List.of())
            .capital(List.of())
            .history(List.of())
            .build();

        when(apiAdapter.getCompanyInformation("2020/939681/07")).thenReturn(mockResponseDto);
        when(dtoMapper.mapToCompanyProfile(mockCompanyDto)).thenReturn(mockCompanyProfile);

        // Act
        CompanyProfileResponse response = service.getCompanyInformation(request);

        // Assert
        assertTrue(response.isSuccess());
        assertNotNull(response.company());
        assertEquals("2020/939681/07", response.company().enterpriseNumber());

        verify(apiAdapter).getCompanyInformation("2020/939681/07");
        verify(dtoMapper).mapToCompanyProfile(mockCompanyDto);
    }
}