/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.infrastructure.integration;

import crosscutting.config.Config;
import crosscutting.environment.Environment;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import verigate.adapter.opensanctions.domain.constants.DomainConstants;
import verigate.adapter.opensanctions.domain.models.EntityExample;
import verigate.adapter.opensanctions.domain.models.EntityMatchRequest;
import verigate.adapter.opensanctions.domain.models.EntityMatchResponse;
import verigate.adapter.opensanctions.infrastructure.config.OpenSanctionsApiConfiguration;
import verigate.adapter.opensanctions.infrastructure.http.OpenSanctionsApiAdapter;
import verigate.adapter.opensanctions.infrastructure.services.DefaultOpenSanctionsMatchingService;

/**
 * Quick live test for OpenSanctions API connectivity.
 *
 * <p>This is a simple standalone test that can be run directly to quickly validate
 * OpenSanctions API connectivity and authentication.
 *
 * <p>To run this test:
 * <ol>
 * <li>Set your API key: export OPENSANCTIONS_API_KEY="your-key-here"</li>
 * <li>Run: java QuickLiveTest</li>
 * </ol>
 *
 * <p>Alternatively, you can set the API key in integration-test-local.properties
 * and run this as a regular test.
 */
public class QuickLiveTest {

  public static void main(String[] args) {
    System.out.println("🚀 OpenSanctions Quick Live Test");
    System.out.println("================================");

    try {
      // Get API key from environment or properties
      String apiKey = getApiKey();
      if (apiKey == null || apiKey.contains("your-")) {
        System.out.println("❌ No valid API key found!");
        System.out.println("Set OPENSANCTIONS_API_KEY environment variable or");
        System.out.println("configure integration-test-local.properties");
        return;
      }

      System.out.println("✅ API key configured");

      // Create service
      Properties configProperties = new Properties();
      configProperties.setProperty("opensanctions.api.key", apiKey);
      configProperties.setProperty("opensanctions.base.url", "https://api.opensanctions.org");
      configProperties.setProperty("opensanctions.connection.timeout.ms", "30000");
      configProperties.setProperty("opensanctions.read.timeout.ms", "30000");

      OpenSanctionsApiConfiguration config = new OpenSanctionsApiConfiguration(configProperties);

      // Create API adapter
      OpenSanctionsApiAdapter apiAdapter = new OpenSanctionsApiAdapter(config);

      DefaultOpenSanctionsMatchingService service =
          new DefaultOpenSanctionsMatchingService(apiAdapter);

      System.out.println("✅ Service initialized");

      // Test 1: Health check with clean person
      System.out.println("\n🏥 Testing API connectivity...");
      EntityExample cleanExample =
          new EntityExample.Builder()
              .id("clean-test")
              .schema(DomainConstants.PERSON_SCHEMA)
              .properties(
                  Map.of(
                      "name", List.of("John Smith", "John", "Smith"),
                      "birthDate", List.of("1990-01-01")))
              .build();

      Map<String, EntityExample> cleanQueries = new HashMap<>();
      cleanQueries.put("entity1", cleanExample);

      EntityMatchRequest cleanRequest =
          new EntityMatchRequest.Builder()
              .dataset("default")
              .queries(cleanQueries)
              .limit(5)
              .threshold(0.5)
              .build();

      EntityMatchResponse cleanResponse = service.matchEntities(cleanRequest);
      int cleanResults =
          cleanResponse.getResponses().values().stream()
              .mapToInt(entityMatches -> entityMatches.getResults().size())
              .sum();
      System.out.println("✅ API accessible - found " + cleanResults + " results");

      // Test 2: Test with known sanctioned person
      System.out.println("\n🎯 Testing sanctions matching...");
      EntityExample sanctionedExample =
          new EntityExample.Builder()
              .id("sanctioned-test")
              .schema(DomainConstants.PERSON_SCHEMA)
              .properties(
                  Map.of(
                      "name", List.of("Vladimir Putin", "Vladimir", "Putin"),
                      "birthDate", List.of("1952-10-07")))
              .build();

      Map<String, EntityExample> sanctionedQueries = new HashMap<>();
      sanctionedQueries.put("entity1", sanctionedExample);

      EntityMatchRequest sanctionsRequest =
          new EntityMatchRequest.Builder()
              .dataset("default")
              .queries(sanctionedQueries)
              .limit(10)
              .threshold(0.5)
              .topics(List.of(DomainConstants.SANCTIONS_TOPIC, DomainConstants.PEP_TOPIC))
              .build();

      EntityMatchResponse sanctionsResponse = service.matchEntities(sanctionsRequest);
      int sanctionsResults =
          sanctionsResponse.getResponses().values().stream()
              .mapToInt(entityMatches -> entityMatches.getResults().size())
              .sum();
      System.out.println("✅ Sanctions test completed - found " + sanctionsResults + " results");

      // Show high-confidence matches
      long highConfidenceMatches =
          sanctionsResponse.getResponses().values().stream()
              .flatMap(entityMatches -> entityMatches.getResults().stream())
              .filter(match -> match.getScore() > 0.8)
              .count();

      System.out.println("🎯 High-confidence matches: " + highConfidenceMatches);

      System.out.println("\n🎉 All tests passed! OpenSanctions integration is working.");

    } catch (Exception e) {
      System.out.println("❌ Test failed: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static String getApiKey() {
    // Try environment variable first
    String apiKey = System.getenv("OPENSANCTIONS_API_KEY");
    if (apiKey != null && !apiKey.trim().isEmpty()) {
      return apiKey;
    }

    // Try properties file
    try {
      Properties props = new Properties();
      try (InputStream is =
          QuickLiveTest.class.getResourceAsStream("/integration-test-local.properties")) {
        if (is != null) {
          props.load(is);
          return props.getProperty("opensanctions.api.key");
        }
      }

      // Fallback to base properties
      try (InputStream is =
          QuickLiveTest.class.getResourceAsStream("/integration-test.properties")) {
        if (is != null) {
          props.load(is);
          return props.getProperty("opensanctions.api.key");
        }
      }
    } catch (IOException e) {
      System.err.println("Warning: Could not load properties file");
    }

    return null;
  }
}
