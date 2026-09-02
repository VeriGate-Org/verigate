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
import verigate.adapter.datanamix.domain.models.DirectorPortfolio;
import verigate.adapter.datanamix.infrastructure.config.DatanamixApiConfiguration;
import verigate.adapter.datanamix.infrastructure.http.dto.ConsumerDirectorshipLinkDto;
import verigate.adapter.datanamix.infrastructure.http.dto.DirectorResultResponseDto;
import verigate.adapter.datanamix.infrastructure.http.dto.DirectorResultResultDto;
import verigate.adapter.datanamix.infrastructure.http.dto.DirectorResultsDto;
import verigate.adapter.datanamix.infrastructure.http.dto.DirectorSearchResponseDto;
import verigate.adapter.datanamix.infrastructure.http.dto.DirectorSearchResultDto;

@ExtendWith(MockitoExtension.class)
class DatanamixDirectorSearchClientTest {

  @Mock
  private DatanamixHttpAdapter httpAdapter;

  @Mock
  private DatanamixApiConfiguration configuration;

  private DatanamixDirectorSearchClient client;

  @BeforeEach
  void setUp() {
    client = new DatanamixDirectorSearchClient(httpAdapter, configuration);
  }

  @Test
  void shouldReturnNotFoundWhenSearchHasNoResults() throws Exception {
    when(configuration.getEnvironmentType()).thenReturn("SANDBOX");
    when(httpAdapter.post(eq("/v1/cipc/director-search"), any(), eq(DirectorSearchResponseDto.class)))
        .thenReturn(new DirectorSearchResponseDto(List.of(), true, List.of()));

    DirectorPortfolio result = client.searchByIdNumber("0101010000081");

    assertFalse(result.found());
    assertTrue(result.directorships().isEmpty());
  }

  @Test
  void shouldReturnNotFoundWhenSearchReportsFailure() throws Exception {
    when(configuration.getEnvironmentType()).thenReturn("SANDBOX");
    DirectorSearchResultDto match = new DirectorSearchResultDto(
        "0101010000081", "John", "Doe", 1L, 1L);
    when(httpAdapter.post(eq("/v1/cipc/director-search"), any(), eq(DirectorSearchResponseDto.class)))
        .thenReturn(new DirectorSearchResponseDto(List.of(match), false, List.of("No match")));

    DirectorPortfolio result = client.searchByIdNumber("0101010000081");

    assertFalse(result.found());
  }

  @Test
  void shouldAggregateDirectorshipsFromASingleIdentityMatch() throws Exception {
    when(configuration.getEnvironmentType()).thenReturn("SANDBOX");
    DirectorSearchResultDto match = new DirectorSearchResultDto(
        "1234567890123", "John", "Doe", 123456L, 654321L);
    when(httpAdapter.post(eq("/v1/cipc/director-search"), any(), eq(DirectorSearchResponseDto.class)))
        .thenReturn(new DirectorSearchResponseDto(List.of(match), true, List.of("Success")));

    ConsumerDirectorshipLinkDto link = new ConsumerDirectorshipLinkDto(
        "", "2020-01-01", "Tech Solutions Ltd", "Active", "2020/123456/07", "Active");
    DirectorResultResponseDto resultResponse = new DirectorResultResponseDto(
        new DirectorResultResultDto(new DirectorResultsDto(List.of(link))), true, List.of("Success"));
    when(httpAdapter.post(eq("/v1/cipc/director-result"), any(), eq(DirectorResultResponseDto.class)))
        .thenReturn(resultResponse);

    DirectorPortfolio result = client.searchByIdNumber("1234567890123");

    assertTrue(result.found());
    assertEquals("John", result.firstName());
    assertEquals("Doe", result.surname());
    assertEquals(1, result.directorships().size());
    assertEquals("Tech Solutions Ltd", result.directorships().get(0).companyName());
    assertEquals("2020/123456/07", result.directorships().get(0).registrationNumber());
    assertEquals(LocalDate.of(2020, 1, 1), result.directorships().get(0).appointmentDate());
  }

  @Test
  void shouldAggregateDirectorshipsAcrossMultipleIdentityMatches() throws Exception {
    when(configuration.getEnvironmentType()).thenReturn("SANDBOX");
    DirectorSearchResultDto matchA = new DirectorSearchResultDto("id", "John", "Doe", 1L, 1L);
    DirectorSearchResultDto matchB = new DirectorSearchResultDto("id", "John", "Doe", 2L, 2L);
    when(httpAdapter.post(eq("/v1/cipc/director-search"), any(), eq(DirectorSearchResponseDto.class)))
        .thenReturn(new DirectorSearchResponseDto(List.of(matchA, matchB), true, List.of()));

    ConsumerDirectorshipLinkDto linkA = new ConsumerDirectorshipLinkDto(
        "Director", "2020-01-01", "Company A", "Active", "2020/111111/07", "Active");
    ConsumerDirectorshipLinkDto linkB = new ConsumerDirectorshipLinkDto(
        "Director", "2021-01-01", "Company B", "Active", "2021/222222/07", "Active");
    when(httpAdapter.post(eq("/v1/cipc/director-result"), any(), eq(DirectorResultResponseDto.class)))
        .thenReturn(
            new DirectorResultResponseDto(
                new DirectorResultResultDto(new DirectorResultsDto(List.of(linkA))), true, List.of()),
            new DirectorResultResponseDto(
                new DirectorResultResultDto(new DirectorResultsDto(List.of(linkB))), true, List.of()));

    DirectorPortfolio result = client.searchByIdNumber("id");

    assertEquals(2, result.directorships().size());
  }

  @Test
  void shouldPropagateTransientExceptionFromResultCall() throws Exception {
    when(configuration.getEnvironmentType()).thenReturn("SANDBOX");
    DirectorSearchResultDto match = new DirectorSearchResultDto("id", "John", "Doe", 1L, 1L);
    when(httpAdapter.post(eq("/v1/cipc/director-search"), any(), eq(DirectorSearchResponseDto.class)))
        .thenReturn(new DirectorSearchResponseDto(List.of(match), true, List.of()));
    when(httpAdapter.post(eq("/v1/cipc/director-result"), any(), eq(DirectorResultResponseDto.class)))
        .thenThrow(new TransientException("Datanamix unreachable"));

    assertThrows(TransientException.class, () -> client.searchByIdNumber("id"));
  }

  @Test
  void shouldThrowPermanentExceptionForBlankIdNumber() {
    assertThrows(PermanentException.class, () -> client.searchByIdNumber("  "));
  }
}
