/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.adapter.document.infrastructure.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import verigate.ai.common.domain.models.AiResponse;
import verigate.ai.common.infrastructure.bedrock.BedrockVisionService;

@ExtendWith(MockitoExtension.class)
class AiDocumentAnalyzerTest {

  @Mock
  private BedrockVisionService visionService;

  private AiDocumentAnalyzer analyzer;

  @BeforeEach
  void setUp() {
    analyzer = new AiDocumentAnalyzer(visionService);
  }

  @Test
  void shouldExtractFieldsAndCrossValidate() {
    String aiResponseJson = """
        {
          "extractedFields": {
            "name": "John Doe",
            "idNumber": "8501015009087",
            "dateOfBirth": "1985-01-01"
          },
          "crossValidation": {
            "name": "MATCH",
            "idNumber": "MATCH"
          },
          "authenticityScore": 0.92,
          "anomalies": []
        }
        """;

    when(visionService.analyzeDocument(any(byte[].class), anyString(), anyString()))
        .thenReturn(new AiResponse(aiResponseJson, "end_turn", 200, 100));

    AiDocumentAnalyzer.DocumentAnalysisResult result =
        analyzer.analyze(new byte[]{1, 2, 3}, "image/jpeg", "John Doe", "8501015009087", "ID");

    assertNotNull(result);
    assertEquals("John Doe", result.extractedFields().get("name"));
    assertEquals("MATCH", result.crossValidation().get("name"));
    assertEquals(0.92, result.authenticityScore(), 0.01);
    assertFalse(result.hasAnomalies());
  }

  @Test
  void shouldDetectAnomalies() {
    String aiResponseJson = """
        {
          "extractedFields": {
            "name": "Jane Smith",
            "idNumber": "9001015009082"
          },
          "crossValidation": {
            "name": "MISMATCH",
            "idNumber": "MATCH"
          },
          "authenticityScore": 0.45,
          "anomalies": [
            "Font inconsistency detected in name field",
            "Document layout differs from standard template"
          ]
        }
        """;

    when(visionService.analyzeDocument(any(byte[].class), anyString(), anyString()))
        .thenReturn(new AiResponse(aiResponseJson, "end_turn", 200, 100));

    AiDocumentAnalyzer.DocumentAnalysisResult result =
        analyzer.analyze(new byte[]{1, 2, 3}, "image/png", "John Doe", "9001015009082", "ID");

    assertNotNull(result);
    assertTrue(result.hasAnomalies());
    assertEquals(2, result.anomalies().size());
    assertEquals("MISMATCH", result.crossValidation().get("name"));
    assertEquals(0.45, result.authenticityScore(), 0.01);
  }

  @Test
  void shouldHandleMarkdownWrappedJson() {
    String aiResponseJson = """
        ```json
        {
          "extractedFields": {"name": "Test User"},
          "crossValidation": {"name": "MATCH"},
          "authenticityScore": 0.88,
          "anomalies": []
        }
        ```
        """;

    when(visionService.analyzeDocument(any(byte[].class), anyString(), anyString()))
        .thenReturn(new AiResponse(aiResponseJson, "end_turn", 200, 100));

    AiDocumentAnalyzer.DocumentAnalysisResult result =
        analyzer.analyze(new byte[]{1, 2, 3}, "image/jpeg", "Test User", "123", "PASSPORT");

    assertNotNull(result);
    assertEquals("Test User", result.extractedFields().get("name"));
    assertEquals(0.88, result.authenticityScore(), 0.01);
  }

  @Test
  void shouldReturnErrorResultOnVisionServiceFailure() {
    when(visionService.analyzeDocument(any(byte[].class), anyString(), anyString()))
        .thenThrow(new RuntimeException("Bedrock unavailable"));

    AiDocumentAnalyzer.DocumentAnalysisResult result =
        analyzer.analyze(new byte[]{1, 2, 3}, "image/jpeg", "John", "123", "ID");

    assertNotNull(result);
    assertTrue(result.hasAnomalies());
    assertTrue(result.anomalies().get(0).contains("AI analysis failed"));
    assertEquals(0.0, result.authenticityScore());
  }

  @Test
  void shouldHandleNullExpectedFields() {
    String aiResponseJson = """
        {
          "extractedFields": {"name": "Some Name"},
          "crossValidation": {},
          "authenticityScore": 0.75,
          "anomalies": []
        }
        """;

    when(visionService.analyzeDocument(any(byte[].class), anyString(), anyString()))
        .thenReturn(new AiResponse(aiResponseJson, "end_turn", 200, 100));

    AiDocumentAnalyzer.DocumentAnalysisResult result =
        analyzer.analyze(new byte[]{1, 2, 3}, "image/jpeg", null, null, null);

    assertNotNull(result);
    assertEquals(0.75, result.authenticityScore(), 0.01);
  }
}
