/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.infrastructure.report;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

class SanctionsReportGeneratorTest {

  @Mock
  private S3Client mockS3Client;

  private SanctionsReportGenerator generator;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(mockS3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
        .thenReturn(PutObjectResponse.builder().build());
    generator = new SanctionsReportGenerator(mockS3Client, "verigate-documents");
  }

  @Test
  void generateReport_uploadsToCorrectS3Key() {
    String commandId = "abc-123";
    Map<String, String> details = baseDetails("SUCCEEDED");

    String key = generator.generateReport(commandId, "partner-1", details);

    assertEquals("sanctions-screening/abc-123.txt", key);
    ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
    verify(mockS3Client).putObject(captor.capture(), any(RequestBody.class));
    assertEquals("verigate-documents", captor.getValue().bucket());
    assertEquals("sanctions-screening/abc-123.txt", captor.getValue().key());
  }

  @Test
  void generateReport_clearedOutcome_containsClearClassification() {
    Map<String, String> details = baseDetails("SUCCEEDED");
    details.put("subject_name", "John Doe");
    details.put("significant_matches_count", "0");

    generator.generateReport("cmd-1", "partner-1", details);

    String content = captureUploadedContent();
    assertTrue(content.contains("John Doe"));
    assertTrue(content.contains("Clear"));
    assertTrue(content.contains("No matches found"));
  }

  @Test
  void generateReport_hardFail_containsHitClassification() {
    Map<String, String> details = baseDetails("HARD_FAIL");
    details.put("subject_name", "Vladimir Putin");
    details.put("entity_type", "Person");
    details.put("significant_matches_count", "1");
    details.put("match_0_id", "NK-12345");
    details.put("match_0_caption", "Vladimir Putin");
    details.put("match_0_score", "0.97");
    details.put("match_0_type", "SANCTIONS");
    details.put("match_0_datasets", "us_ofac_sdn,eu_sanctions");
    details.put("match_0_target", "true");

    generator.generateReport("cmd-2", "partner-1", details);

    String content = captureUploadedContent();
    assertTrue(content.contains("Vladimir Putin"));
    assertTrue(content.contains("Hit"));
    assertTrue(content.contains("97/100"));
    assertTrue(content.contains("High confidence"));
    assertTrue(content.contains("NK-12345"));
    assertTrue(content.contains("us_ofac_sdn,eu_sanctions"));
    assertTrue(content.contains("Yes")); // Risk Target
  }

  @Test
  void generateReport_softFail_containsPossibleMatchClassification() {
    Map<String, String> details = baseDetails("SOFT_FAIL");
    details.put("significant_matches_count", "1");
    details.put("match_0_id", "NK-99");
    details.put("match_0_caption", "J. Smith");
    details.put("match_0_score", "0.75");
    details.put("match_0_type", "PEP");
    details.put("match_0_datasets", "za_pep_register");
    details.put("match_0_target", "false");

    generator.generateReport("cmd-3", "partner-1", details);

    String content = captureUploadedContent();
    assertTrue(content.contains("Possible match"));
    assertTrue(content.contains("PEP"));
    assertTrue(content.contains("75/100"));
    assertTrue(content.contains("Medium confidence"));
  }

  @Test
  void generateReport_multipleMatches_allRendered() {
    Map<String, String> details = baseDetails("HARD_FAIL");
    details.put("significant_matches_count", "2");
    details.put("match_0_id", "id-1");
    details.put("match_0_caption", "Entity A");
    details.put("match_0_score", "0.92");
    details.put("match_0_type", "SANCTIONS");
    details.put("match_0_datasets", "us_ofac_sdn");
    details.put("match_1_id", "id-2");
    details.put("match_1_caption", "Entity B");
    details.put("match_1_score", "0.74");
    details.put("match_1_type", "PEP");
    details.put("match_1_datasets", "za_pep_register");

    generator.generateReport("cmd-4", "partner-1", details);

    String content = captureUploadedContent();
    assertTrue(content.contains("Match 1 of 2"));
    assertTrue(content.contains("Match 2 of 2"));
    assertTrue(content.contains("Entity A"));
    assertTrue(content.contains("Entity B"));
  }

  @Test
  void generateReport_alwaysIncludesImportantNotice() {
    generator.generateReport("cmd-5", "partner-1", baseDetails("SUCCEEDED"));

    String content = captureUploadedContent();
    assertTrue(content.contains("IMPORTANT NOTICE"));
    assertTrue(content.contains("VeriGate"));
    assertTrue(content.contains("OpenSanctions"));
  }

  @Test
  void generateReport_s3Failure_throwsRuntimeException() {
    when(mockS3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
        .thenThrow(new RuntimeException("S3 unavailable"));

    assertThrows(RuntimeException.class,
        () -> generator.generateReport("cmd-6", "partner-1", baseDetails("SUCCEEDED")));
  }

  // ── helpers ──

  private Map<String, String> baseDetails(String outcome) {
    Map<String, String> d = new HashMap<>();
    d.put("outcome", outcome);
    d.put("provider", "OpenSanctions");
    d.put("algorithm", "logic-v2");
    d.put("total_matches", "0");
    d.put("significant_matches_count", "0");
    d.put("subject_name", "Test Subject");
    d.put("entity_type", "Person");
    return d;
  }

  private String captureUploadedContent() {
    ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);
    verify(mockS3Client).putObject(any(PutObjectRequest.class), bodyCaptor.capture());
    try {
      return new String(bodyCaptor.getValue().contentStreamProvider().newStream().readAllBytes(),
          java.nio.charset.StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new RuntimeException("Failed to read captured body", e);
    }
  }
}
