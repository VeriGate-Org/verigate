/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.mocks;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import verigate.adapter.deedsweb.domain.models.*;
import verigate.adapter.deedsweb.domain.services.DeedsWebMatchingService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/** Mock implementation of the DeedsWeb matching service for testing. */
public class MockDeedsWebMatchingService implements DeedsWebMatchingService {

    private static final Logger LOGGER = Logger.getLogger(MockDeedsWebMatchingService.class.getName());

    private boolean serviceHealthy = true;
    private boolean simulateTransientFailure = false;
    private boolean simulatePermanentFailure = false;

    @Override
    public EntityMatchResponse matchEntities(EntityMatchRequest request) 
            throws TransientException, PermanentException {
        
        LOGGER.info("Mock: Processing entity match request for dataset: " + request.getDataset());

        if (simulateTransientFailure) {
            throw new TransientException("Mock transient failure");
        }

        if (simulatePermanentFailure) {
            throw new PermanentException("Mock permanent failure");
        }

        // Create mock response based on request
        return createMockMatchResponse(request);
    }

    @Override
    public EntityMatchResponse searchEntities(String dataset, String query, Integer limit) 
            throws TransientException, PermanentException {
        
        LOGGER.info("Mock: Processing search request for dataset: " + dataset + ", query: " + query);

        if (simulateTransientFailure) {
            throw new TransientException("Mock transient failure");
        }

        if (simulatePermanentFailure) {
            throw new PermanentException("Mock permanent failure");
        }

        // Create mock response based on search parameters
        return createMockSearchResponse(dataset, query, limit);
    }

    @Override
    public boolean isServiceHealthy() throws TransientException {
        LOGGER.fine("Mock: Checking service health");

        if (simulateTransientFailure) {
            throw new TransientException("Mock health check failure");
        }

        return serviceHealthy;
    }

    // Configuration methods for testing scenarios

    public void setServiceHealthy(boolean healthy) {
        this.serviceHealthy = healthy;
    }

    public void setSimulateTransientFailure(boolean simulate) {
        this.simulateTransientFailure = simulate;
    }

    public void setSimulatePermanentFailure(boolean simulate) {
        this.simulatePermanentFailure = simulate;
    }

    public void reset() {
        this.serviceHealthy = true;
        this.simulateTransientFailure = false;
        this.simulatePermanentFailure = false;
    }

    private EntityMatchResponse createMockMatchResponse(EntityMatchRequest request) {
        // Create mock scored entity
        ScoredEntity mockEntity = new ScoredEntity.Builder()
            .id("mock-entity-123")
            .caption("John Doe (Mock)")
            .schema("Person")
            .properties(Map.of("name", List.of("John Doe")))
            .datasets(List.of("us_ofac_sdn"))
            .referents(List.of("ofac-123"))
            .target(true)
            .firstSeen(LocalDateTime.now().minusDays(30))
            .lastSeen(LocalDateTime.now().minusDays(1))
            .lastChange(LocalDateTime.now().minusDays(1))
            .score(0.85)
            .features(Map.of("name_literal", 0.9, "date_match", 0.8))
            .match(true)
            .token("mock-token")
            .build();

        // Create mock entity matches
        EntityMatches mockMatches = new EntityMatches(
            200,
            List.of(mockEntity),
            new TotalSpec(1, "eq"),
            request.getQueries().values().iterator().next()
        );

        // Create mock response
        Map<String, EntityMatches> responses = Map.of("entity1", mockMatches);
        Map<String, FeatureDoc> matcher = Map.of(
            "name_literal", new FeatureDoc("Exact name match", 0.9, "https://example.com/docs/name_literal"),
            "date_match", new FeatureDoc("Date of birth match", 0.8, "https://example.com/docs/date_match")
        );

        return new EntityMatchResponse(responses, matcher, request.getLimit());
    }

    private EntityMatchResponse createMockSearchResponse(String dataset, String query, Integer limit) {
        ScoredEntity primaryEntity = new ScoredEntity.Builder()
            .id("deed-" + Math.abs(query.hashCode()))
            .caption(query + " Property")
            .schema("RealEstate")
            .properties(Map.ofEntries(
                Map.entry("deedNumber", List.of("T12345/2024")),
                Map.entry("titleDeedReference", List.of("T12345/2024")),
                Map.entry("propertyDescription", List.of("Erf 101 Portion 2, Newcastle Central")),
                Map.entry("registrationDivision", List.of("Newcastle Central")),
                Map.entry("province", List.of("KwaZulu-Natal")),
                Map.entry("extent", List.of("850")),
                Map.entry("registeredOwnerName", List.of(query.matches("\\d+") ? "Owner 1" : query)),
                Map.entry("registeredOwnerIdNumber", List.of(query.matches("\\d+") ? query : "8001015009087")),
                Map.entry("registrationDate", List.of("2024-03-11")),
                Map.entry("transferDate", List.of("2024-03-11")),
                Map.entry("purchasePrice", List.of(1250000)),
                Map.entry("bondHolder", List.of("ABSA")),
                Map.entry("bondAmount", List.of(900000))
            ))
            .datasets(List.of(dataset))
            .score(0.97)
            .features(Map.of("query_match", 0.97))
            .build();

        ScoredEntity secondaryEntity = new ScoredEntity.Builder()
            .id("deed-secondary-" + Math.abs(query.hashCode()))
            .caption(query + " Property 2")
            .schema("RealEstate")
            .properties(Map.ofEntries(
                Map.entry("deedNumber", List.of("T54321/2022")),
                Map.entry("titleDeedReference", List.of("T54321/2022")),
                Map.entry("propertyDescription", List.of("Erf 202 Portion 0, Newcastle Industrial")),
                Map.entry("registrationDivision", List.of("Newcastle Industrial")),
                Map.entry("province", List.of("KwaZulu-Natal")),
                Map.entry("extent", List.of("1200")),
                Map.entry("registeredOwnerName", List.of(query.matches("\\d+") ? "Owner 2" : query)),
                Map.entry("registeredOwnerIdNumber", List.of(query.matches("\\d+") ? query : "8001015009087")),
                Map.entry("registrationDate", List.of("2022-08-05")),
                Map.entry("transferDate", List.of("2022-08-05")),
                Map.entry("purchasePrice", List.of(1750000))
            ))
            .datasets(List.of(dataset))
            .score(0.88)
            .features(Map.of("query_match", 0.88))
            .build();

        EntityExample mockQuery = new EntityExample.Builder()
            .id("search-query")
            .schema("RealEstate")
            .properties(Map.of("query", List.of(query)))
            .build();

        EntityMatches mockMatches = new EntityMatches(
            200,
            List.of(primaryEntity, secondaryEntity),
            new TotalSpec(2, "eq"),
            mockQuery
        );

        Map<String, EntityMatches> responses = Map.of("search", mockMatches);
        Map<String, FeatureDoc> matcher = Map.of(
            "query_match", new FeatureDoc("Query match", 1.0, "https://example.com/docs/query_match")
        );

        return new EntityMatchResponse(responses, matcher, limit);
    }
}
