package verigate.webbff.sanctions.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import verigate.webbff.verification.repository.model.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import verigate.webbff.verification.repository.CommandStatusRepository;
import verigate.webbff.verification.repository.model.VerificationCommandStoreItem;
import verigate.webbff.verification.model.CommandStatus;

class SanctionsProxyServiceTest {

  @Mock private CommandStatusRepository commandStatusRepository;
  @Mock private S3Presigner s3Presigner;
  @Mock private PresignedGetObjectRequest presignedGetObjectRequest;

  private SanctionsProxyService service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new SanctionsProxyService(
        new ObjectMapper(),
        "https://api.opensanctions.org",
        "test-api-key",
        commandStatusRepository,
        s3Presigner,
        "verigate-documents");
  }

  private static final String PARTNER_ID = "partner-abc";
  private static final String OTHER_PARTNER_ID = "partner-xyz";

  @Test
  void getReportPresignedUrl_returnsUrlAndMetadata() throws Exception {
    var commandId = UUID.randomUUID();
    var s3Key = "sanctions-screening/" + commandId + ".txt";
    var expectedUrl = new URL("https://s3.amazonaws.com/verigate-documents/" + s3Key + "?sig=abc");

    var item = buildItem(commandId, PARTNER_ID, Map.of("reportDocumentId", s3Key));
    when(commandStatusRepository.findById(commandId)).thenReturn(Optional.of(item));
    when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
        .thenReturn(presignedGetObjectRequest);
    when(presignedGetObjectRequest.url()).thenReturn(expectedUrl);

    Map<String, Object> result = service.getReportPresignedUrl(commandId, PARTNER_ID);

    assertEquals(expectedUrl.toString(), result.get("downloadUrl"));
    assertEquals(s3Key, result.get("documentId"));
    assertEquals(900, result.get("expiresIn"));
  }

  @Test
  void getReportPresignedUrl_presignRequestUsesCorrectBucketAndFilename() throws Exception {
    var commandId = UUID.randomUUID();
    var s3Key = "sanctions-screening/" + commandId + ".txt";
    var dummyUrl = new URL("https://s3.amazonaws.com/bucket/key?sig=x");

    when(commandStatusRepository.findById(commandId))
        .thenReturn(Optional.of(buildItem(commandId, PARTNER_ID, Map.of("reportDocumentId", s3Key))));
    when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
        .thenReturn(presignedGetObjectRequest);
    when(presignedGetObjectRequest.url()).thenReturn(dummyUrl);

    service.getReportPresignedUrl(commandId, PARTNER_ID);

    verify(s3Presigner, times(1)).presignGetObject(any(GetObjectPresignRequest.class));
  }

  @Test
  void getReportPresignedUrl_throwsWhenPartnerIdDoesNotMatch() {
    var commandId = UUID.randomUUID();
    when(commandStatusRepository.findById(commandId))
        .thenReturn(Optional.of(buildItem(commandId, OTHER_PARTNER_ID, Map.of("reportDocumentId", "some-key"))));

    var ex = assertThrows(RuntimeException.class,
        () -> service.getReportPresignedUrl(commandId, PARTNER_ID));
    assertTrue(ex.getMessage().contains(commandId.toString()));
    verify(s3Presigner, never()).presignGetObject(any(GetObjectPresignRequest.class));
  }

  @Test
  void getReportPresignedUrl_throwsWhenCommandNotFound() {
    var commandId = UUID.randomUUID();
    when(commandStatusRepository.findById(commandId)).thenReturn(Optional.empty());

    var ex = assertThrows(RuntimeException.class,
        () -> service.getReportPresignedUrl(commandId, PARTNER_ID));
    assertTrue(ex.getMessage().contains(commandId.toString()));
  }

  @Test
  void getReportPresignedUrl_throwsWhenAuxiliaryDataHasNoReportKey() {
    var commandId = UUID.randomUUID();
    when(commandStatusRepository.findById(commandId))
        .thenReturn(Optional.of(buildItem(commandId, PARTNER_ID, Map.of("outcome", "SUCCEEDED"))));

    var ex = assertThrows(RuntimeException.class,
        () -> service.getReportPresignedUrl(commandId, PARTNER_ID));
    assertTrue(ex.getMessage().contains(commandId.toString()));
  }

  @Test
  void getReportPresignedUrl_throwsWhenAuxiliaryDataIsNull() {
    var commandId = UUID.randomUUID();
    when(commandStatusRepository.findById(commandId))
        .thenReturn(Optional.of(buildItem(commandId, PARTNER_ID, null)));

    assertThrows(RuntimeException.class,
        () -> service.getReportPresignedUrl(commandId, PARTNER_ID));
  }

  @Test
  @SuppressWarnings("unchecked")
  void getScreeningHistory_returnsItemsMappedFromDynamoDb() {
    var commandId = UUID.randomUUID();
    var item = buildItem(commandId, PARTNER_ID, Map.of(
        "outcome", "HARD_FAIL",
        "subject_name", "Test Subject",
        "entity_type", "Person",
        "significant_matches_count", "1",
        "provider", "OpenSanctions"));
    item.setCommandName("SANCTIONS_SCREENING");
    item.setCreatedAt("2026-06-25T10:00:00Z");

    when(commandStatusRepository.findByPartnerId(
        eq(PARTNER_ID), eq(null), eq("SANCTIONS_SCREENING"), eq(50), eq(null)))
        .thenReturn(new PageResult<>(List.of(item), null));

    Map<String, Object> result = service.getScreeningHistory(PARTNER_ID);

    assertEquals(1, result.get("total"));
    var items = (List<Map<String, Object>>) result.get("items");
    assertEquals(1, items.size());
    assertEquals("HARD_FAIL", items.get(0).get("outcome"));
    assertEquals("Test Subject", items.get(0).get("subjectName"));
    assertEquals("OpenSanctions", items.get(0).get("provider"));
  }

  @Test
  @SuppressWarnings("unchecked")
  void getScreeningHistory_returnsEmptyListWhenNoResults() {
    when(commandStatusRepository.findByPartnerId(
        eq(PARTNER_ID), eq(null), eq("SANCTIONS_SCREENING"), eq(50), eq(null)))
        .thenReturn(new PageResult<>(List.of(), null));

    Map<String, Object> result = service.getScreeningHistory(PARTNER_ID);

    assertEquals(0, result.get("total"));
    assertEquals(0, ((List<?>) result.get("items")).size());
  }

  @Test
  void submitDisposition_persistsToRepositoryAndReturnsResponse() {
    var commandId = UUID.randomUUID();
    when(commandStatusRepository.findById(commandId))
        .thenReturn(Optional.of(buildItem(commandId, PARTNER_ID, new HashMap<>())));
    doNothing().when(commandStatusRepository).updateAuxiliaryData(any(UUID.class), any());

    Map<String, Object> disposition = Map.of(
        "entityId", "NK-123",
        "screeningId", commandId.toString(),
        "action", "FALSE_POSITIVE",
        "reason", "Not the same person");

    Map<String, Object> result = service.submitDisposition(disposition, PARTNER_ID);

    assertEquals("NK-123", result.get("entityId"));
    assertEquals("FALSE_POSITIVE", result.get("action"));
    assertNotNull(result.get("dispositionId"));
    assertNotNull(result.get("createdAt"));
    verify(commandStatusRepository).updateAuxiliaryData(eq(commandId), any());
  }

  @Test
  void submitDisposition_throwsWhenScreeningNotFound() {
    var commandId = UUID.randomUUID();
    when(commandStatusRepository.findById(commandId)).thenReturn(Optional.empty());

    Map<String, Object> disposition = Map.of(
        "entityId", "NK-123",
        "screeningId", commandId.toString(),
        "action", "FALSE_POSITIVE",
        "reason", "Test");

    assertThrows(RuntimeException.class,
        () -> service.submitDisposition(disposition, PARTNER_ID));
    verify(commandStatusRepository, never()).updateAuxiliaryData(any(), any());
  }

  @Test
  void submitDisposition_throwsWhenPartnerDoesNotOwnScreening() {
    var commandId = UUID.randomUUID();
    when(commandStatusRepository.findById(commandId))
        .thenReturn(Optional.of(buildItem(commandId, OTHER_PARTNER_ID, new HashMap<>())));

    Map<String, Object> disposition = Map.of(
        "entityId", "NK-123",
        "screeningId", commandId.toString(),
        "action", "FALSE_POSITIVE",
        "reason", "Test");

    assertThrows(RuntimeException.class,
        () -> service.submitDisposition(disposition, PARTNER_ID));
    verify(commandStatusRepository, never()).updateAuxiliaryData(any(), any());
  }

  // ── helpers ──

  private VerificationCommandStoreItem buildItem(
      UUID commandId, String partnerId, Map<String, String> auxiliaryData) {
    var item = new VerificationCommandStoreItem();
    item.setCommandId(commandId.toString());
    item.setCommandName("VerifyPartyCommand");
    item.setPartnerId(partnerId);
    item.setStatus(CommandStatus.COMPLETED);
    item.setAuxiliaryData(auxiliaryData != null ? new HashMap<>(auxiliaryData) : null);
    return item;
  }
}
