/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.domain.mappers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import verigate.adapter.opensanctions.domain.models.EntityMatchResponse;
import verigate.adapter.opensanctions.domain.models.EntityMatches;
import verigate.adapter.opensanctions.domain.models.ScoredEntity;
import verigate.verification.cg.domain.models.VerificationOutcome;
import verigate.verification.cg.domain.models.VerificationResult;

class VerificationResultMapperTest {

    // ---- Outcome determination tests ----

    @Test
    void mapToVerificationResult_highScore_returnsHardFail() {
        // Arrange - score >= 0.9
        EntityMatchResponse response = createResponseWithScore(0.95);

        // Act
        VerificationResult result =
            VerificationResultMapper.mapToVerificationResult(response, "req-1");

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL, result.outcome());
        assertNotNull(result.failureReason());
        assertTrue(result.failureReason().contains("OpenSanctions match found"));
    }

    @Test
    void mapToVerificationResult_exactHighThreshold_returnsHardFail() {
        // Arrange - score exactly 0.9
        EntityMatchResponse response = createResponseWithScore(0.9);

        // Act
        VerificationResult result =
            VerificationResultMapper.mapToVerificationResult(response, "req-2");

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL, result.outcome());
    }

    @Test
    void mapToVerificationResult_mediumScore_returnsSoftFail() {
        // Arrange - score >= 0.7 and < 0.9
        EntityMatchResponse response = createResponseWithScore(0.78);

        // Act
        VerificationResult result =
            VerificationResultMapper.mapToVerificationResult(response, "req-3");

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL, result.outcome());
        assertNotNull(result.failureReason());
    }

    @Test
    void mapToVerificationResult_exactMediumThreshold_returnsSoftFail() {
        // Arrange - score exactly 0.7
        EntityMatchResponse response = createResponseWithScore(0.7);

        // Act
        VerificationResult result =
            VerificationResultMapper.mapToVerificationResult(response, "req-4");

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL, result.outcome());
    }

    @Test
    void mapToVerificationResult_lowScore_returnsSucceeded() {
        // Arrange - score < 0.7
        EntityMatchResponse response = createResponseWithScore(0.65);

        // Act
        VerificationResult result =
            VerificationResultMapper.mapToVerificationResult(response, "req-5");

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());
        assertNull(result.failureReason());
    }

    @Test
    void mapToVerificationResult_emptyResponses_returnsSucceeded() {
        // Arrange
        EntityMatchResponse response = new EntityMatchResponse(Map.of(), Map.of(), 5);

        // Act
        VerificationResult result =
            VerificationResultMapper.mapToVerificationResult(response, "req-6");

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());
        assertNull(result.failureReason());
    }

    @Test
    void mapToVerificationResult_nullResponses_returnsSucceeded() {
        // Arrange
        EntityMatchResponse response = new EntityMatchResponse(null, null, null);

        // Act
        VerificationResult result =
            VerificationResultMapper.mapToVerificationResult(response, "req-7");

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());
        assertNull(result.failureReason());
    }

    @Test
    void mapToVerificationResult_nullResults_returnsSucceeded() {
        // Arrange - EntityMatches with null results list
        EntityMatches matches = new EntityMatches(200, null, null, null);
        EntityMatchResponse response =
            new EntityMatchResponse(Map.of("entity1", matches), Map.of(), 5);

        // Act
        VerificationResult result =
            VerificationResultMapper.mapToVerificationResult(response, "req-8");

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());
    }

    @Test
    void mapToVerificationResult_emptyResults_returnsSucceeded() {
        // Arrange
        EntityMatches matches = new EntityMatches(200, List.of(), null, null);
        EntityMatchResponse response =
            new EntityMatchResponse(Map.of("entity1", matches), Map.of(), 5);

        // Act
        VerificationResult result =
            VerificationResultMapper.mapToVerificationResult(response, "req-9");

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());
    }

    @Test
    void mapToVerificationResult_nullScoreEntity_treatedAsZero() {
        // Arrange - entity with null score
        ScoredEntity entity = new ScoredEntity.Builder()
            .id("ent-1")
            .caption("Test Entity")
            .datasets(List.of("us_ofac_sdn"))
            .score(null)
            .build();

        EntityMatches matches = new EntityMatches(200, List.of(entity), null, null);
        EntityMatchResponse response =
            new EntityMatchResponse(Map.of("entity1", matches), Map.of(), 5);

        // Act
        VerificationResult result =
            VerificationResultMapper.mapToVerificationResult(response, "req-10");

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());
    }

    @Test
    void mapToVerificationResult_multipleQueries_highestScoreWins() {
        // Arrange - two queries, one low score, one high
        ScoredEntity lowEntity = new ScoredEntity.Builder()
            .id("ent-low")
            .caption("Low Match")
            .datasets(List.of("us_ofac_sdn"))
            .score(0.5)
            .build();

        ScoredEntity highEntity = new ScoredEntity.Builder()
            .id("ent-high")
            .caption("High Match")
            .datasets(List.of("eu_sanctions"))
            .score(0.92)
            .build();

        EntityMatches lowMatches = new EntityMatches(200, List.of(lowEntity), null, null);
        EntityMatches highMatches = new EntityMatches(200, List.of(highEntity), null, null);

        EntityMatchResponse response = new EntityMatchResponse(
            Map.of("query1", lowMatches, "query2", highMatches),
            Map.of(), 5);

        // Act
        VerificationResult result =
            VerificationResultMapper.mapToVerificationResult(response, "req-11");

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL, result.outcome());
    }

    // ---- Failure reason tests ----

    @Test
    void mapToVerificationResult_allSignificantMatchesInFailureReason() {
        // Arrange - multiple matches above MEDIUM_MATCH_THRESHOLD
        ScoredEntity entity1 = new ScoredEntity.Builder()
            .id("ent-1")
            .caption("Entity A")
            .datasets(List.of("us_ofac_sdn"))
            .score(0.92)
            .build();

        ScoredEntity entity2 = new ScoredEntity.Builder()
            .id("ent-2")
            .caption("Entity B")
            .datasets(List.of("eu_sanctions"))
            .score(0.75)
            .build();

        ScoredEntity belowThreshold = new ScoredEntity.Builder()
            .id("ent-3")
            .caption("Low Entity")
            .datasets(List.of("some_list"))
            .score(0.55)
            .build();

        EntityMatches matches =
            new EntityMatches(200, List.of(entity1, entity2, belowThreshold), null, null);
        EntityMatchResponse response =
            new EntityMatchResponse(Map.of("entity1", matches), Map.of(), 5);

        // Act
        VerificationResult result =
            VerificationResultMapper.mapToVerificationResult(response, "req-12");

        // Assert
        assertNotNull(result.failureReason());
        assertTrue(result.failureReason().contains("Entity A"));
        assertTrue(result.failureReason().contains("Entity B"));
        assertFalse(result.failureReason().contains("Low Entity"));
    }

    // ---- PEP vs Sanctions classification tests ----

    @Test
    void mapToVerificationResult_pepDataset_classifiedAsPep() {
        // Arrange
        ScoredEntity pepEntity = new ScoredEntity.Builder()
            .id("ent-pep")
            .caption("PEP Person")
            .datasets(List.of("ru_pep_registry", "us_ofac_sdn"))
            .score(0.85)
            .build();

        EntityMatches matches = new EntityMatches(200, List.of(pepEntity), null, null);
        EntityMatchResponse response =
            new EntityMatchResponse(Map.of("entity1", matches), Map.of(), 5);

        // Act
        VerificationResult result =
            VerificationResultMapper.mapToVerificationResult(response, "req-13");

        // Assert
        assertTrue(result.failureReason().contains("PEP"));
    }

    @Test
    void mapToVerificationResult_sanctionsDataset_classifiedAsSanctions() {
        // Arrange
        ScoredEntity sanctionsEntity = new ScoredEntity.Builder()
            .id("ent-sanctions")
            .caption("Sanctioned Person")
            .datasets(List.of("us_ofac_sdn", "eu_sanctions"))
            .score(0.85)
            .build();

        EntityMatches matches = new EntityMatches(200, List.of(sanctionsEntity), null, null);
        EntityMatchResponse response =
            new EntityMatchResponse(Map.of("entity1", matches), Map.of(), 5);

        // Act
        VerificationResult result =
            VerificationResultMapper.mapToVerificationResult(response, "req-14");

        // Assert
        assertTrue(result.failureReason().contains("SANCTIONS"));
    }

    // ---- createResultDetails tests ----

    @Test
    void createResultDetails_withMatches_populatesDetails() {
        // Arrange
        ScoredEntity entity = new ScoredEntity.Builder()
            .id("ent-1")
            .caption("Sanctioned Entity")
            .datasets(List.of("us_ofac_sdn"))
            .score(0.88)
            .target(true)
            .build();

        EntityMatches matches = new EntityMatches(200, List.of(entity), null, null);
        EntityMatchResponse response =
            new EntityMatchResponse(Map.of("entity1", matches), Map.of(), 5);

        // Act
        Map<String, String> details = VerificationResultMapper.createResultDetails(response);

        // Assert
        assertEquals("OpenSanctions", details.get("provider"));
        assertEquals("entity-matching", details.get("algorithm"));
        assertEquals("1", details.get("total_matches"));
        assertEquals("1", details.get("significant_matches_count"));

        // Check individual match details
        assertEquals("ent-1", details.get("match_0_id"));
        assertEquals("Sanctioned Entity", details.get("match_0_caption"));
        assertEquals("0.88", details.get("match_0_score"));
        assertEquals("us_ofac_sdn", details.get("match_0_datasets"));
        assertEquals("SANCTIONS", details.get("match_0_type"));
        assertEquals("true", details.get("match_0_target"));
    }

    @Test
    void createResultDetails_pepMatch_setsTypeToPep() {
        // Arrange
        ScoredEntity pepEntity = new ScoredEntity.Builder()
            .id("ent-pep")
            .caption("PEP Person")
            .datasets(List.of("za_pep_list"))
            .score(0.75)
            .build();

        EntityMatches matches = new EntityMatches(200, List.of(pepEntity), null, null);
        EntityMatchResponse response =
            new EntityMatchResponse(Map.of("entity1", matches), Map.of(), 5);

        // Act
        Map<String, String> details = VerificationResultMapper.createResultDetails(response);

        // Assert
        assertEquals("PEP", details.get("match_0_type"));
    }

    @Test
    void createResultDetails_noMatches_zeroSignificantCount() {
        // Arrange
        EntityMatchResponse response = new EntityMatchResponse(Map.of(), Map.of(), 5);

        // Act
        Map<String, String> details = VerificationResultMapper.createResultDetails(response);

        // Assert
        assertEquals("OpenSanctions", details.get("provider"));
        assertEquals("0", details.get("total_matches"));
    }

    @Test
    void createResultDetails_nullResponses_returnsProviderAndAlgorithm() {
        // Arrange
        EntityMatchResponse response = new EntityMatchResponse(null, null, null);

        // Act
        Map<String, String> details = VerificationResultMapper.createResultDetails(response);

        // Assert
        assertEquals("OpenSanctions", details.get("provider"));
        assertEquals("entity-matching", details.get("algorithm"));
        assertNull(details.get("total_matches"));
    }

    @Test
    void createResultDetails_mixedScores_onlySignificantMatchesIncluded() {
        // Arrange
        ScoredEntity highEntity = new ScoredEntity.Builder()
            .id("ent-1")
            .caption("High Match")
            .datasets(List.of("us_ofac_sdn"))
            .score(0.9)
            .build();

        ScoredEntity lowEntity = new ScoredEntity.Builder()
            .id("ent-2")
            .caption("Low Match")
            .datasets(List.of("eu_sanctions"))
            .score(0.55)
            .build();

        EntityMatches matches =
            new EntityMatches(200, List.of(highEntity, lowEntity), null, null);
        EntityMatchResponse response =
            new EntityMatchResponse(Map.of("entity1", matches), Map.of(), 5);

        // Act
        Map<String, String> details = VerificationResultMapper.createResultDetails(response);

        // Assert
        assertEquals("2", details.get("total_matches"));
        assertEquals("1", details.get("significant_matches_count"));
        assertEquals("ent-1", details.get("match_0_id"));
        assertNull(details.get("match_1_id"));
    }

    @Test
    void createResultDetails_multipleSignificantMatches_indexedCorrectly() {
        // Arrange
        ScoredEntity entity1 = new ScoredEntity.Builder()
            .id("ent-1")
            .caption("Entity A")
            .datasets(List.of("us_ofac_sdn"))
            .score(0.92)
            .build();

        ScoredEntity entity2 = new ScoredEntity.Builder()
            .id("ent-2")
            .caption("Entity B")
            .datasets(List.of("eu_pep_registry"))
            .score(0.78)
            .build();

        EntityMatches matches = new EntityMatches(200, List.of(entity1, entity2), null, null);
        EntityMatchResponse response =
            new EntityMatchResponse(Map.of("entity1", matches), Map.of(), 5);

        // Act
        Map<String, String> details = VerificationResultMapper.createResultDetails(response);

        // Assert
        assertEquals("2", details.get("significant_matches_count"));
        assertEquals("ent-1", details.get("match_0_id"));
        assertEquals("SANCTIONS", details.get("match_0_type"));
        assertEquals("ent-2", details.get("match_1_id"));
        assertEquals("PEP", details.get("match_1_type"));
    }

    // ---- Helper methods ----

    private EntityMatchResponse createResponseWithScore(double score) {
        ScoredEntity entity = new ScoredEntity.Builder()
            .id("ent-test")
            .caption("Test Entity")
            .datasets(List.of("us_ofac_sdn"))
            .score(score)
            .build();

        EntityMatches matches = new EntityMatches(200, List.of(entity), null, null);
        return new EntityMatchResponse(Map.of("entity1", matches), Map.of(), 5);
    }
}
