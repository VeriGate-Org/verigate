/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import domain.exceptions.PermanentException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.deedsweb.domain.models.EntityMatchRequest;
import verigate.adapter.deedsweb.domain.models.EntityMatchResponse;
import verigate.adapter.deedsweb.domain.models.EntityExample;
import verigate.adapter.deedsweb.infrastructure.config.DeedsWebApiConfiguration;
import verigate.adapter.deedsweb.domain.constants.DomainConstants;
import java.util.List;
import java.util.HashMap;
import verigate.adapter.deedsweb.infrastructure.services.DefaultDeedsWebMatchingService;
import verigate.adapter.deedsweb.infrastructure.http.DeedsWebApiAdapter;

/**
 * Live integration test for OpenSanctions API.
 * 
 * <p>This test validates the OpenSanctions adapter against the real OpenSanctions API.
 * It requires a valid API key and internet connectivity.
 * 
 * <p>To run this test:
 * <ol>
 * <li>Copy integration-test.properties to integration-test-local.properties</li>
 * <li>Add your real OpenSanctions API key to integration-test-local.properties</li>
 * <li>Set integration.test.enabled=true in the local properties file</li>
 * <li>Run: mvn test -Dtest=DeedsWebLiveIntegrationTest</li>
 * </ol>
 * 
 * <p><strong>Security Note:</strong> Never commit real API keys to version control.
 * The integration-test-local.properties file is gitignored for security.
 * 
 * @author VeriGate Development Team
 * @version 1.0
 * @since 2025-07-22
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DeedsWebLiveIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeedsWebLiveIntegrationTest.class);
    
    private static Properties testConfig;
    private static DefaultDeedsWebMatchingService matchingService;
    
    @BeforeAll
    static void setupLiveTest() throws IOException {
        LOGGER.info("🚀 Setting up OpenSanctions live integration test...");
        
        testConfig = loadTestConfiguration();
        
        // Skip test if not enabled
        assumeTrue(Boolean.parseBoolean(testConfig.getProperty("integration.test.enabled", "false")),
                "Integration tests are disabled. Set integration.test.enabled=true to run live tests.");
        
        // Validate API key is configured
        String apiKey = testConfig.getProperty("OPENSANCTIONS_API_KEY");
        assumeTrue(apiKey != null && !apiKey.contains("your-") && !apiKey.trim().isEmpty(),
                "Please set your actual OpenSanctions API key in integration-test-local.properties");
        
        LOGGER.info("✅ Configuration validated - API key present");
        
        // Initialize service with test configuration
        Properties configProperties = new Properties();
        configProperties.putAll(testConfig);
        DeedsWebApiConfiguration config = new DeedsWebApiConfiguration(configProperties);
        
        // Create API adapter
        DeedsWebApiAdapter apiAdapter = new DeedsWebApiAdapter(config);
        
        matchingService = new DefaultDeedsWebMatchingService(apiAdapter);
        
        LOGGER.info("✅ OpenSanctions matching service initialized");
    }
    
    private static Properties loadTestConfiguration() throws IOException {
        Properties config = new Properties();
        
        // Load base configuration
        try (InputStream baseConfig = DeedsWebLiveIntegrationTest.class
                .getResourceAsStream("/integration-test.properties")) {
            if (baseConfig != null) {
                config.load(baseConfig);
                LOGGER.info("📄 Loaded base integration test configuration");
            }
        }
        
        // Override with local configuration if it exists
        try (InputStream localConfig = DeedsWebLiveIntegrationTest.class
                .getResourceAsStream("/integration-test-local.properties")) {
            if (localConfig != null) {
                config.load(localConfig);
                LOGGER.info("🔒 Loaded local integration test configuration (with credentials)");
            } else {
                LOGGER.info("ℹ️  No local configuration found - using base configuration only");
            }
        }
        
        return config;
    }
    
    
    @Test
    @Order(1)
    void test01_HealthCheck() throws Exception {
        LOGGER.info("🏥 Testing OpenSanctions API health check...");
        
        // Create a simple test request to verify connectivity
        EntityExample entityExample = new EntityExample.Builder()
                .id("health-check")
                .schema(DomainConstants.PERSON_SCHEMA)
                .properties(Map.of(
                        "name", List.of("Test Health Check"),
                        "birthDate", List.of("2000-01-01")
                ))
                .build();
        
        Map<String, EntityExample> queries = new HashMap<>();
        queries.put("entity1", entityExample);
        
        EntityMatchRequest healthCheckRequest = new EntityMatchRequest.Builder()
                .dataset(testConfig.getProperty("integration.test.dataset", "default"))
                .queries(queries)
                .limit(5)
                .threshold(0.5)
                .build();
        
        // This should not throw an exception if the API is accessible
        assertDoesNotThrow(() -> {
            EntityMatchResponse response = matchingService.matchEntities(healthCheckRequest);
            assertNotNull(response, "Health check response should not be null");
            LOGGER.info("✅ Health check passed - API is accessible");
        });
    }
    
    @Test
    @Order(2) 
    void test02_MatchKnownSanctionedPerson() throws Exception {
        LOGGER.info("🎯 Testing match against known sanctioned person...");
        
        String firstName = testConfig.getProperty("test.person.first.name", "Vladimir");
        String lastName = testConfig.getProperty("test.person.last.name", "Putin");
        String fullName = firstName + " " + lastName;
        
        EntityExample entityExample = new EntityExample.Builder()
                .id("test-person")
                .schema(DomainConstants.PERSON_SCHEMA)
                .properties(Map.of(
                        "name", List.of(fullName, firstName, lastName),
                        "birthDate", List.of(testConfig.getProperty("test.person.birth.date", "1952-10-07")),
                        "nationality", List.of(testConfig.getProperty("test.person.nationality", "Russian"))
                ))
                .build();
        
        Map<String, EntityExample> queries = new HashMap<>();
        queries.put("entity1", entityExample);
        
        EntityMatchRequest request = new EntityMatchRequest.Builder()
                .dataset(testConfig.getProperty("integration.test.dataset", "default"))
                .queries(queries)
                .limit(10)
                .threshold(0.5)
                .topics(List.of(DomainConstants.SANCTIONS_TOPIC, DomainConstants.PEP_TOPIC))
                .build();
        
        EntityMatchResponse response = matchingService.matchEntities(request);
        
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getResponses(), "Responses should not be null");
        
        // Should find matches for a known sanctioned person
        assertFalse(response.getResponses().isEmpty(), 
                "Should find matches for known sanctioned person");
        
        // Check for high-confidence matches
        boolean hasHighConfidenceMatch = response.getResponses().values().stream()
                .flatMap(entityMatches -> entityMatches.getResults().stream())
                .anyMatch(scoredEntity -> scoredEntity.getScore() > 0.8);
        
        assertTrue(hasHighConfidenceMatch, 
                "Should have at least one high-confidence match (score > 0.8)");
        
        int totalResults = response.getResponses().values().stream()
                .mapToInt(entityMatches -> entityMatches.getResults().size())
                .sum();
        
        LOGGER.info("✅ Successfully matched sanctioned person - found {} results", totalResults);
        
        // Log details of top matches
        response.getResponses().values().stream()
                .flatMap(entityMatches -> entityMatches.getResults().stream())
                .filter(scoredEntity -> scoredEntity.getScore() > 0.7)
                .forEach(match -> LOGGER.info("🎯 High match: {} (score: {})", 
                        match.getCaption(), match.getScore()));
    }
    
    @Test
    @Order(3)
    void test03_MatchCleanPerson() throws Exception {
        LOGGER.info("🧹 Testing match against clean (non-sanctioned) person...");
        
        String cleanFirstName = testConfig.getProperty("clean.person.first.name", "John");
        String cleanLastName = testConfig.getProperty("clean.person.last.name", "Smith");
        String cleanFullName = cleanFirstName + " " + cleanLastName;
        
        EntityExample cleanEntityExample = new EntityExample.Builder()
                .id("clean-person")
                .schema(DomainConstants.PERSON_SCHEMA)
                .properties(Map.of(
                        "name", List.of(cleanFullName, cleanFirstName, cleanLastName),
                        "birthDate", List.of(testConfig.getProperty("clean.person.birth.date", "1990-01-01")),
                        "nationality", List.of(testConfig.getProperty("clean.person.nationality", "US"))
                ))
                .build();
        
        Map<String, EntityExample> queries = new HashMap<>();
        queries.put("entity1", cleanEntityExample);
        
        EntityMatchRequest request = new EntityMatchRequest.Builder()
                .dataset(testConfig.getProperty("integration.test.dataset", "default"))
                .queries(queries)
                .limit(10)
                .threshold(0.5)
                .topics(List.of(DomainConstants.SANCTIONS_TOPIC, DomainConstants.PEP_TOPIC))
                .build();
        
        EntityMatchResponse response = matchingService.matchEntities(request);
        
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getResponses(), "Responses should not be null");
        
        // Should have few or no high-confidence matches for clean person
        long highConfidenceMatches = response.getResponses().values().stream()
                .flatMap(entityMatches -> entityMatches.getResults().stream())
                .filter(scoredEntity -> scoredEntity.getScore() > 0.8)
                .count();
        
        assertTrue(highConfidenceMatches == 0, 
                "Clean person should not have high-confidence matches");
        
        LOGGER.info("✅ Clean person test passed - {} high-confidence matches found", 
                highConfidenceMatches);
    }
    
    @Test
    @Order(4)
    void test04_MatchKnownSanctionedCompany() throws Exception {
        LOGGER.info("🏢 Testing match against known sanctioned company...");
        
        String companyName = testConfig.getProperty("test.company.name", "ROSNEFT");
        
        EntityExample companyExample = new EntityExample.Builder()
                .id("test-company")
                .schema(DomainConstants.ORGANIZATION_SCHEMA)
                .properties(Map.of(
                        "name", List.of(companyName),
                        "jurisdiction", List.of(testConfig.getProperty("test.company.jurisdiction", "RU"))
                ))
                .build();
        
        Map<String, EntityExample> queries = new HashMap<>();
        queries.put("entity1", companyExample);
        
        EntityMatchRequest request = new EntityMatchRequest.Builder()
                .dataset(testConfig.getProperty("integration.test.dataset", "default"))
                .queries(queries)
                .limit(10)
                .threshold(0.5)
                .topics(List.of(DomainConstants.SANCTIONS_TOPIC))
                .build();
        
        EntityMatchResponse response = matchingService.matchEntities(request);
        
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getResponses(), "Responses should not be null");
        
        // Should find matches for known sanctioned company
        boolean hasMatches = !response.getResponses().isEmpty();
        
        if (hasMatches) {
            int totalResults = response.getResponses().values().stream()
                    .mapToInt(entityMatches -> entityMatches.getResults().size())
                    .sum();
            
            LOGGER.info("✅ Found {} matches for sanctioned company", totalResults);
            
            // Log company matches
            response.getResponses().values().stream()
                    .flatMap(entityMatches -> entityMatches.getResults().stream())
                    .filter(scoredEntity -> scoredEntity.getScore() > 0.5)
                    .forEach(match -> LOGGER.info("🏢 Company match: {} (score: {})", 
                            match.getCaption(), match.getScore()));
        } else {
            LOGGER.info("ℹ️  No matches found for test company - this may be expected depending on dataset");
        }
    }
    
    @Test
    @Order(5)
    void test05_HandleInvalidApiKey() throws Exception {
        LOGGER.info("🔐 Testing invalid API key handling...");
        
        // Create service with invalid API key
        Properties badConfigProperties = new Properties();
        badConfigProperties.putAll(testConfig);
        badConfigProperties.setProperty("opensanctions.api.key", "invalid-api-key-12345");
        
        DeedsWebApiConfiguration badConfig = new DeedsWebApiConfiguration(badConfigProperties);
        
        DeedsWebApiAdapter badApiAdapter = new DeedsWebApiAdapter(badConfig);
        DefaultDeedsWebMatchingService badService = new DefaultDeedsWebMatchingService(badApiAdapter);
        
        EntityExample testExample = new EntityExample.Builder()
                .id("invalid-test")
                .schema(DomainConstants.PERSON_SCHEMA)
                .properties(Map.of("name", List.of("Test Person")))
                .build();
        
        Map<String, EntityExample> queries = new HashMap<>();
        queries.put("entity1", testExample);
        
        EntityMatchRequest request = new EntityMatchRequest.Builder()
                .dataset("default")
                .queries(queries)
                .limit(5)
                .build();
        
        // Should throw PermanentException for authentication failure
        assertThrows(PermanentException.class, () -> {
            badService.matchEntities(request);
        }, "Invalid API key should result in PermanentException");
        
        LOGGER.info("✅ Invalid API key properly handled with PermanentException");
    }
    
    @Test
    @Order(6)
    void test06_HandleMalformedRequest() throws Exception {
        LOGGER.info("🚫 Testing malformed request handling...");
        
        // Create request with invalid properties
        EntityExample invalidExample = new EntityExample.Builder()
                .id("malformed-test")
                .schema("InvalidSchema")
                .properties(Map.of("invalidField", List.of("test")))
                .build();
        
        Map<String, EntityExample> queries = new HashMap<>();
        queries.put("entity1", invalidExample);
        
        EntityMatchRequest request = new EntityMatchRequest.Builder()
                .dataset("default")
                .queries(queries)
                .limit(5)
                .build();
        
        // Should handle gracefully (may return empty results or throw exception)
        assertDoesNotThrow(() -> {
            EntityMatchResponse response = matchingService.matchEntities(request);
            // Response may be empty but should not crash
            assertNotNull(response, "Response should not be null even for invalid requests");
            LOGGER.info("✅ Malformed request handled gracefully");
        });
    }
    
    @Test
    @Order(7)
    void test07_PerformanceTest() throws Exception {
        LOGGER.info("⚡ Testing API performance...");
        
        EntityExample perfExample = new EntityExample.Builder()
                .id("perf-test")
                .schema(DomainConstants.PERSON_SCHEMA)
                .properties(Map.of(
                        "name", List.of("John Doe", "John", "Doe"),
                        "birthDate", List.of("1985-05-15")
                ))
                .build();
        
        Map<String, EntityExample> queries = new HashMap<>();
        queries.put("entity1", perfExample);
        
        EntityMatchRequest request = new EntityMatchRequest.Builder()
                .dataset(testConfig.getProperty("integration.test.dataset", "default"))
                .queries(queries)
                .limit(10)
                .threshold(0.5)
                .build();
        
        long startTime = System.currentTimeMillis();
        EntityMatchResponse response = matchingService.matchEntities(request);
        long endTime = System.currentTimeMillis();
        
        long responseTime = endTime - startTime;
        
        assertNotNull(response, "Response should not be null");
        
        // API should respond within reasonable time (30 seconds as configured)
        int timeoutMs = Integer.parseInt(testConfig.getProperty("integration.test.timeout.seconds", "30")) * 1000;
        assertTrue(responseTime < timeoutMs, 
                String.format("API should respond within %d ms, but took %d ms", timeoutMs, responseTime));
        
        LOGGER.info("✅ Performance test passed - API responded in {} ms", responseTime);
    }
}