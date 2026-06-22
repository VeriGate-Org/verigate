/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.infrastructure.mappers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import verigate.adapter.opensanctions.domain.models.EntityExample;
import verigate.adapter.opensanctions.domain.models.EntityMatchRequest;
import verigate.adapter.opensanctions.domain.models.EntityMatchResponse;
import verigate.adapter.opensanctions.domain.models.EntityMatches;
import verigate.adapter.opensanctions.domain.models.ScoredEntity;
import verigate.adapter.opensanctions.infrastructure.http.dto.EntityExampleDto;
import verigate.adapter.opensanctions.infrastructure.http.dto.EntityMatchRequestDto;
import verigate.adapter.opensanctions.infrastructure.http.dto.EntityMatchResponseDto;
import verigate.adapter.opensanctions.infrastructure.http.dto.EntityMatchesDto;
import verigate.adapter.opensanctions.infrastructure.http.dto.ScoredEntityDto;

class OpenSanctionsDtoMapperTest {

    // ---- mapToDomain(ScoredEntityDto) ----

    @Test
    void mapToDomain_scoredEntity_coreFieldsMapped() {
        ScoredEntityDto dto = new ScoredEntityDto();
        dto.setId("Q12345");
        dto.setCaption("Test Person");
        dto.setSchema("Person");
        dto.setScore(0.92);
        dto.setDatasets(List.of("us_ofac_sdn"));
        dto.setTarget(true);

        ScoredEntity result = OpenSanctionsDtoMapper.mapToDomain(dto);

        assertEquals("Q12345", result.getId());
        assertEquals("Test Person", result.getCaption());
        assertEquals("Person", result.getSchema());
        assertEquals(0.92, result.getScore());
        assertEquals(List.of("us_ofac_sdn"), result.getDatasets());
        assertTrue(result.getTarget());
    }

    @Test
    void mapToDomain_scoredEntity_pepTopicMapped() {
        ScoredEntityDto dto = new ScoredEntityDto();
        dto.setId("pep-001");
        dto.setCaption("PEP Person");
        dto.setDatasets(List.of("za_gov_gazette"));
        dto.setTopics(List.of("role.pep"));
        dto.setScore(0.85);

        ScoredEntity result = OpenSanctionsDtoMapper.mapToDomain(dto);

        assertNotNull(result.getTopics());
        assertEquals(1, result.getTopics().size());
        assertEquals("role.pep", result.getTopics().get(0));
    }

    @Test
    void mapToDomain_scoredEntity_multipleTopicsMapped() {
        ScoredEntityDto dto = new ScoredEntityDto();
        dto.setId("ent-001");
        dto.setTopics(List.of("sanction", "role.pep"));
        dto.setScore(0.91);

        ScoredEntity result = OpenSanctionsDtoMapper.mapToDomain(dto);

        assertEquals(2, result.getTopics().size());
        assertTrue(result.getTopics().contains("sanction"));
        assertTrue(result.getTopics().contains("role.pep"));
    }

    @Test
    void mapToDomain_scoredEntity_nullTopics_mappedAsNull() {
        ScoredEntityDto dto = new ScoredEntityDto();
        dto.setId("ent-002");
        dto.setTopics(null);
        dto.setScore(0.88);

        ScoredEntity result = OpenSanctionsDtoMapper.mapToDomain(dto);

        assertNull(result.getTopics());
    }

    @Test
    void mapToDomain_scoredEntity_nullDatasets_mappedAsNull() {
        ScoredEntityDto dto = new ScoredEntityDto();
        dto.setId("ent-003");
        dto.setDatasets(null);
        dto.setScore(0.75);

        ScoredEntity result = OpenSanctionsDtoMapper.mapToDomain(dto);

        assertNull(result.getDatasets());
    }

    @Test
    void mapToDomain_scoredEntity_featuresMapped() {
        ScoredEntityDto dto = new ScoredEntityDto();
        dto.setId("ent-004");
        dto.setFeatures(Map.of("name_match", 0.9, "dob_match", 0.8));
        dto.setScore(0.87);

        ScoredEntity result = OpenSanctionsDtoMapper.mapToDomain(dto);

        assertNotNull(result.getFeatures());
        assertEquals(0.9, result.getFeatures().get("name_match"));
        assertEquals(0.8, result.getFeatures().get("dob_match"));
    }

    @Test
    void mapToDomain_scoredEntity_matchAndTokenMapped() {
        ScoredEntityDto dto = new ScoredEntityDto();
        dto.setId("ent-005");
        dto.setMatch(true);
        dto.setToken("tok-abc123");
        dto.setScore(0.91);

        ScoredEntity result = OpenSanctionsDtoMapper.mapToDomain(dto);

        assertTrue(result.getMatch());
        assertEquals("tok-abc123", result.getToken());
    }

    // ---- mapToDomain(EntityMatchesDto) ----

    @Test
    void mapToDomain_entityMatches_resultsMapped() {
        ScoredEntityDto entityDto = new ScoredEntityDto();
        entityDto.setId("ent-001");
        entityDto.setTopics(List.of("sanction"));
        entityDto.setScore(0.92);

        EntityMatchesDto matchesDto = new EntityMatchesDto();
        matchesDto.setStatus(200);
        matchesDto.setResults(List.of(entityDto));

        EntityMatches result = OpenSanctionsDtoMapper.mapToDomain(matchesDto);

        assertEquals(200, result.getStatus());
        assertNotNull(result.getResults());
        assertEquals(1, result.getResults().size());
        assertEquals("ent-001", result.getResults().get(0).getId());
        assertEquals(List.of("sanction"), result.getResults().get(0).getTopics());
    }

    @Test
    void mapToDomain_entityMatches_nullResults_mappedAsNull() {
        EntityMatchesDto matchesDto = new EntityMatchesDto();
        matchesDto.setStatus(200);
        matchesDto.setResults(null);

        EntityMatches result = OpenSanctionsDtoMapper.mapToDomain(matchesDto);

        assertNull(result.getResults());
    }

    // ---- mapToDomain(EntityMatchResponseDto) ----

    @Test
    void mapToDomain_entityMatchResponse_responsesMapped() {
        ScoredEntityDto entityDto = new ScoredEntityDto();
        entityDto.setId("ent-001");
        entityDto.setTopics(List.of("role.pep"));
        entityDto.setScore(0.85);

        EntityMatchesDto matchesDto = new EntityMatchesDto();
        matchesDto.setStatus(200);
        matchesDto.setResults(List.of(entityDto));

        EntityMatchResponseDto responseDto = new EntityMatchResponseDto();
        responseDto.setResponses(Map.of("query1", matchesDto));
        responseDto.setLimit(10);

        EntityMatchResponse result = OpenSanctionsDtoMapper.mapToDomain(responseDto);

        assertNotNull(result.getResponses());
        assertTrue(result.getResponses().containsKey("query1"));
        assertEquals(1, result.getResponses().get("query1").getResults().size());

        ScoredEntity entity = result.getResponses().get("query1").getResults().get(0);
        assertEquals("ent-001", entity.getId());
        assertEquals(List.of("role.pep"), entity.getTopics());
        assertEquals(10, result.getLimit());
    }

    @Test
    void mapToDomain_entityMatchResponse_nullResponses_mappedAsNull() {
        EntityMatchResponseDto responseDto = new EntityMatchResponseDto();
        responseDto.setResponses(null);
        responseDto.setMatcher(null);

        EntityMatchResponse result = OpenSanctionsDtoMapper.mapToDomain(responseDto);

        assertNull(result.getResponses());
        assertNull(result.getMatcher());
    }

    // ---- mapToDto(EntityMatchRequest) ----

    @Test
    void mapToDto_entityMatchRequest_queriesMapped() {
        EntityExample example = new EntityExample.Builder()
            .id("e1")
            .schema("Person")
            .build();

        EntityMatchRequest request = new EntityMatchRequest.Builder()
            .dataset("default")
            .queries(Map.of("e1", example))
            .build();

        EntityMatchRequestDto dto = OpenSanctionsDtoMapper.mapToDto(request);

        assertNotNull(dto.getQueries());
        assertTrue(dto.getQueries().containsKey("e1"));
        assertEquals("e1", dto.getQueries().get("e1").getId());
        assertEquals("Person", dto.getQueries().get("e1").getSchema());
    }

    @Test
    void mapToDto_entityMatchRequest_nullQueries_mappedAsNull() {
        EntityMatchRequest request = new EntityMatchRequest.Builder()
            .dataset("default")
            .queries(null)
            .build();

        EntityMatchRequestDto dto = OpenSanctionsDtoMapper.mapToDto(request);

        assertNull(dto.getQueries());
    }

    // ---- mapToDto(EntityExample) ----

    @Test
    void mapToDto_entityExample_fieldsMapped() {
        EntityExample example = new EntityExample.Builder()
            .id("e1")
            .schema("Person")
            .properties(Map.of("name", List.of("John Doe")))
            .build();

        EntityExampleDto dto = OpenSanctionsDtoMapper.mapToDto(example);

        assertEquals("e1", dto.getId());
        assertEquals("Person", dto.getSchema());
        assertNotNull(dto.getProperties());
        assertEquals(List.of("John Doe"), dto.getProperties().get("name"));
    }
}
