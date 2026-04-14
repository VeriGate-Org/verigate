/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.adapter.document.infrastructure.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
  void shouldExtractFieldsWithConfidenceScores() {
    String aiResponseJson = """
        {
          "extractedFields": {
            "fullName": { "value": "John Doe", "confidence": 0.99 },
            "idNumber": { "value": "8501015009087", "confidence": 0.98 },
            "dateOfBirth": { "value": "1985-01-01", "confidence": 0.95 }
          },
          "crossValidation": {
            "fullName": "MATCH",
            "idNumber": "MATCH"
          },
          "authenticityScore": 0.92,
          "tamperingIndicators": {
            "fontConsistency": 95,
            "layoutAlignment": 90,
            "imageQuality": 88,
            "securityFeatures": 85,
            "metadataConsistency": 92,
            "overallTamperingScore": 90,
            "flags": []
          },
          "anomalies": []
        }
        """;

    when(visionService.analyzeDocument(any(byte[].class), anyString(), anyString()))
        .thenReturn(new AiResponse(aiResponseJson, "end_turn", 200, 100));

    AiDocumentAnalyzer.DocumentAnalysisResult result =
        analyzer.analyze(new byte[]{1, 2, 3}, "image/jpeg", "John Doe", "8501015009087",
            "IDENTITY_DOCUMENT");

    assertNotNull(result);
    assertEquals("John Doe", result.extractedFields().get("fullName").value());
    assertEquals(0.99, result.extractedFields().get("fullName").confidence(), 0.01);
    assertEquals("MATCH", result.crossValidation().get("fullName"));
    assertEquals(0.92, result.authenticityScore(), 0.01);
    assertFalse(result.hasAnomalies());
    assertTrue(result.overallConfidence() > 0.9);
    assertNotNull(result.tamperingIndicators());
    assertEquals(90, result.tamperingIndicators().overallTamperingScore());
  }

  @Test
  void shouldHandleLegacyFlatFieldFormat() {
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
    assertEquals("John Doe", result.extractedFields().get("name").value());
    assertEquals(0.0, result.extractedFields().get("name").confidence());
    assertEquals("John Doe", result.extractedFieldsFlat().get("name"));
    assertNull(result.tamperingIndicators());
  }

  @Test
  void shouldDetectAnomaliesAndTamperingFlags() {
    String aiResponseJson = """
        {
          "extractedFields": {
            "fullName": { "value": "Jane Smith", "confidence": 0.70 },
            "idNumber": { "value": "9001015009082", "confidence": 0.85 }
          },
          "crossValidation": {
            "fullName": "MISMATCH",
            "idNumber": "MATCH"
          },
          "authenticityScore": 0.45,
          "tamperingIndicators": {
            "fontConsistency": 40,
            "layoutAlignment": 55,
            "imageQuality": 60,
            "securityFeatures": 30,
            "metadataConsistency": 45,
            "overallTamperingScore": 46,
            "flags": [
              "Font inconsistency detected in name field",
              "Document layout differs from standard template"
            ]
          },
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
    assertEquals("MISMATCH", result.crossValidation().get("fullName"));
    assertEquals(0.45, result.authenticityScore(), 0.01);
    assertNotNull(result.tamperingIndicators());
    assertEquals(46, result.tamperingIndicators().overallTamperingScore());
    assertEquals(2, result.tamperingIndicators().flags().size());
  }

  @Test
  void shouldHandleMarkdownWrappedJson() {
    String aiResponseJson = """
        ```json
        {
          "extractedFields": {
            "fullName": { "value": "Test User", "confidence": 0.95 }
          },
          "crossValidation": {"fullName": "MATCH"},
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
    assertEquals("Test User", result.extractedFields().get("fullName").value());
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
    assertEquals(0.0, result.overallConfidence());
  }

  @Test
  void shouldHandleNullExpectedFields() {
    String aiResponseJson = """
        {
          "extractedFields": {
            "fullName": { "value": "Some Name", "confidence": 0.90 }
          },
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

  @Test
  void shouldComputeOverallConfidenceFromFieldScores() {
    String aiResponseJson = """
        {
          "extractedFields": {
            "fullName": { "value": "John Doe", "confidence": 0.99 },
            "idNumber": { "value": "8501015009087", "confidence": 0.97 },
            "dateOfBirth": { "value": null, "confidence": 0.0 }
          },
          "crossValidation": {},
          "authenticityScore": 0.90,
          "anomalies": []
        }
        """;

    when(visionService.analyzeDocument(any(byte[].class), anyString(), anyString()))
        .thenReturn(new AiResponse(aiResponseJson, "end_turn", 200, 100));

    AiDocumentAnalyzer.DocumentAnalysisResult result =
        analyzer.analyze(new byte[]{1, 2, 3}, "image/jpeg", "John Doe", "8501015009087", "ID");

    // Only non-null fields contribute to overall confidence: (0.99 + 0.97) / 2 = 0.98
    assertEquals(0.98, result.overallConfidence(), 0.01);
  }

  @Test
  void shouldSupportValidationChecksViaWithMethod() {
    var result = AiDocumentAnalyzer.DocumentAnalysisResult.error("test");
    var checks = java.util.List.of(
        new AiDocumentAnalyzer.ValidationCheck("LUHN_CHECK", "PASS", "Valid checksum"),
        new AiDocumentAnalyzer.ValidationCheck("FORMAT_CHECK", "PASS", "Valid SA ID format")
    );
    var withChecks = result.withValidationChecks(checks);

    assertEquals(2, withChecks.validationChecks().size());
    assertEquals("LUHN_CHECK", withChecks.validationChecks().get(0).name());
  }
}
