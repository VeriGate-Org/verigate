/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.adapter.datanamix.infrastructure.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import verigate.adapter.datanamix.domain.models.CompanyProfile;
import verigate.adapter.datanamix.domain.models.CompanySearchMatch;
import verigate.adapter.datanamix.infrastructure.config.DatanamixApiConfiguration;
import verigate.adapter.datanamix.infrastructure.http.dto.CipcResultDto;
import verigate.adapter.datanamix.infrastructure.http.dto.CommercialBusinessInformationDto;
import verigate.adapter.datanamix.infrastructure.http.dto.CommercialDirectorInformationDto;
import verigate.adapter.datanamix.infrastructure.http.dto.CompanyMatchDto;
import verigate.adapter.datanamix.infrastructure.http.dto.CompanyResultResponseDto;
import verigate.adapter.datanamix.infrastructure.http.dto.CompanySearchResponseDto;

@ExtendWith(MockitoExtension.class)
class DatanamixCompanySearchClientTest {

  private static final String SEARCH_ENDPOINT = "/v1/cipc/company-search";
  private static final String RESULT_ENDPOINT = "/v1/cipc/company-result";

  @Mock
  private DatanamixHttpAdapter httpAdapter;

  @Mock
  private DatanamixApiConfiguration configuration;

  private DatanamixCompanySearchClient client;

  @BeforeEach
  void setUp() {
    client = new DatanamixCompanySearchClient(httpAdapter, configuration);
  }

  @Test
  void shouldReturnEmptyListWhenNameSearchHasNoResults() throws Exception {
    when(configuration.getEnvironmentType()).thenReturn("SANDBOX");
    when(httpAdapter.post(eq(SEARCH_ENDPOINT), any(), eq(CompanySearchResponseDto.class)))
        .thenReturn(new CompanySearchResponseDto(List.of(), true, List.of(), 0));

    List<CompanySearchMatch> matches = client.searchByName("Nonexistent Co");

    assertTrue(matches.isEmpty());
  }

  @Test
  void shouldReturnEmptyListWhenNameSearchReportsNotFoundResponseCode() throws Exception {
    when(configuration.getEnvironmentType()).thenReturn("SANDBOX");
    when(httpAdapter.post(eq(SEARCH_ENDPOINT), any(), eq(CompanySearchResponseDto.class)))
        .thenReturn(new CompanySearchResponseDto(List.of(), false, List.of("No Record Found"), 4));

    List<CompanySearchMatch> matches = client.searchByName("Nonexistent Co");

    assertTrue(matches.isEmpty());
  }

  @Test
  void shouldMapAllMatchesFromNameSearch() throws Exception {
    when(configuration.getEnvironmentType()).thenReturn("SANDBOX");
    CompanyMatchDto matchA = new CompanyMatchDto("2010/123456/07", "XYZ Shoes (Pty) Ltd", 234567L, 876543L);
    CompanyMatchDto matchB = new CompanyMatchDto("2010/234567/07", "XYZ Shoes", 123456L, 987654L);
    when(httpAdapter.post(eq(SEARCH_ENDPOINT), any(), eq(CompanySearchResponseDto.class)))
        .thenReturn(new CompanySearchResponseDto(List.of(matchA, matchB), true, List.of(), 0));

    List<CompanySearchMatch> matches = client.searchByName("XYZ Shoes");

    assertEquals(2, matches.size());
    assertEquals("2010/123456/07", matches.get(0).registrationNumber());
    assertEquals(234567L, matches.get(0).enquiryId());
    assertEquals(876543L, matches.get(0).enquiryResultId());
  }

  @Test
  void shouldThrowTransientExceptionWhenSearchReportsServiceUnavailable() throws Exception {
    when(configuration.getEnvironmentType()).thenReturn("SANDBOX");
    when(httpAdapter.post(eq(SEARCH_ENDPOINT), any(), eq(CompanySearchResponseDto.class)))
        .thenReturn(new CompanySearchResponseDto(List.of(), false, List.of("Service Unavailable"), 5));

    assertThrows(TransientException.class, () -> client.searchByName("XYZ Shoes"));
  }

  @Test
  void shouldThrowPermanentExceptionWhenSearchReportsValidationError() throws Exception {
    when(configuration.getEnvironmentType()).thenReturn("SANDBOX");
    when(httpAdapter.post(eq(SEARCH_ENDPOINT), any(), eq(CompanySearchResponseDto.class)))
        .thenReturn(new CompanySearchResponseDto(List.of(), false, List.of("Validation Error"), 6));

    assertThrows(PermanentException.class, () -> client.searchByName("XYZ Shoes"));
  }

  @Test
  void shouldThrowPermanentExceptionForBlankBusinessName() {
    assertThrows(PermanentException.class, () -> client.searchByName("  "));
  }

  @Test
  void shouldThrowPermanentExceptionForBlankRegistrationNumber() {
    assertThrows(PermanentException.class, () -> client.searchByRegistrationNumber(" "));
  }

  @Test
  void shouldReturnNotFoundWhenRegistrationNumberSearchHasNoMatch() throws Exception {
    when(configuration.getEnvironmentType()).thenReturn("SANDBOX");
    when(httpAdapter.post(eq(SEARCH_ENDPOINT), any(), eq(CompanySearchResponseDto.class)))
        .thenReturn(new CompanySearchResponseDto(List.of(), true, List.of(), 0));

    CompanyProfile profile = client.searchByRegistrationNumber("2099/999999/07");

    assertFalse(profile.found());
  }

  @Test
  void shouldResolveFullProfileForRegistrationNumberSearch() throws Exception {
    when(configuration.getEnvironmentType()).thenReturn("SANDBOX");
    CompanyMatchDto match = new CompanyMatchDto("2010/123456/07", "XYZ Shoes (Pty) Ltd", 234567L, 876543L);
    when(httpAdapter.post(eq(SEARCH_ENDPOINT), any(), eq(CompanySearchResponseDto.class)))
        .thenReturn(new CompanySearchResponseDto(List.of(match), true, List.of(), 0));

    CommercialBusinessInformationDto business = new CommercialBusinessInformationDto(
        "XYZ Shoes (Pty) Ltd", "XYZ Shoes", "K2010/123456/07", "In Business",
        "Private Company", "2010-01-01");
    CommercialDirectorInformationDto director = new CommercialDirectorInformationDto(
        "1234567890123", "John Doe", "Active", "2010-01-01");
    CipcResultDto cipcResult = new CipcResultDto(business, List.of(director), true, List.of(), 0);
    when(httpAdapter.post(eq(RESULT_ENDPOINT), any(), eq(CompanyResultResponseDto.class)))
        .thenReturn(new CompanyResultResponseDto(cipcResult));

    CompanyProfile profile = client.searchByRegistrationNumber("2010/123456/07");

    assertTrue(profile.found());
    assertEquals("XYZ Shoes (Pty) Ltd", profile.businessName());
    assertEquals("XYZ Shoes", profile.tradeName());
    assertEquals("K2010/123456/07", profile.registrationNumber());
    assertEquals("In Business", profile.businessStatus());
    assertEquals(LocalDate.of(2010, 1, 1), profile.registrationDate());
    assertEquals(1, profile.directors().size());
    assertEquals("John Doe", profile.directors().get(0).fullName());
    assertEquals(LocalDate.of(2010, 1, 1), profile.directors().get(0).appointmentDate());
  }

  @Test
  void shouldReturnNotFoundWhenResultHasNoBusinessData() throws Exception {
    CipcResultDto cipcResult = new CipcResultDto(null, List.of(), true, List.of(), 0);
    when(httpAdapter.post(eq(RESULT_ENDPOINT), any(), eq(CompanyResultResponseDto.class)))
        .thenReturn(new CompanyResultResponseDto(cipcResult));

    CompanyProfile profile = client.getProfile(234567L, 876543L);

    assertFalse(profile.found());
  }

  @Test
  void shouldPropagateTransientExceptionFromResultCall() throws Exception {
    when(configuration.getEnvironmentType()).thenReturn("SANDBOX");
    CompanyMatchDto match = new CompanyMatchDto("2010/123456/07", "XYZ Shoes (Pty) Ltd", 234567L, 876543L);
    when(httpAdapter.post(eq(SEARCH_ENDPOINT), any(), eq(CompanySearchResponseDto.class)))
        .thenReturn(new CompanySearchResponseDto(List.of(match), true, List.of(), 0));
    when(httpAdapter.post(eq(RESULT_ENDPOINT), any(), eq(CompanyResultResponseDto.class)))
        .thenThrow(new TransientException("Datanamix unreachable"));

    assertThrows(TransientException.class, () -> client.searchByRegistrationNumber("2010/123456/07"));
  }
}
