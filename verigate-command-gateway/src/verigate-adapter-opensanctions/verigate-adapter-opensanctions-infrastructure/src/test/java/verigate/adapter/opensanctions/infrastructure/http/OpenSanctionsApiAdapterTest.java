/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.infrastructure.http;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import crosscutting.config.Config;
import crosscutting.environment.Environment;
import verigate.adapter.opensanctions.domain.models.EntityExample;
import verigate.adapter.opensanctions.domain.models.EntityMatchRequest;
import verigate.adapter.opensanctions.infrastructure.config.OpenSanctionsApiConfiguration;

class OpenSanctionsApiAdapterTest {

    @Mock
    private Environment mockEnvironment;

    @Mock
    private Config mockConfig;

    private OpenSanctionsApiAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Provide required config values for HttpAdapter instantiation
        when(mockEnvironment.get("OPENSANCTIONS_API_KEY")).thenReturn("test-api-key");
        when(mockEnvironment.get(anyString())).thenReturn(null);
        when(mockConfig.get(anyString())).thenReturn(null);

        // Specific overrides for required values
        when(mockEnvironment.get("OPENSANCTIONS_API_KEY")).thenReturn("test-api-key");

        OpenSanctionsApiConfiguration config =
            new OpenSanctionsApiConfiguration(mockEnvironment, mockConfig);
        adapter = new OpenSanctionsApiAdapter(config);
    }

    // ---- buildMatchEndpoint tests ----

    @Test
    void buildMatchEndpoint_allParams_constructsCorrectUrl() throws Exception {
        // Arrange
        EntityMatchRequest request = new EntityMatchRequest.Builder()
            .dataset("sanctions")
            .queries(Map.of("e1", new EntityExample.Builder().id("e1").schema("Person").build()))
            .limit(10)
            .threshold(0.7)
            .cutoff(0.5)
            .algorithm("logic-v2")
            .topics(List.of("sanction", "role.pep"))
            .build();

        // Act
        String endpoint = invokeBuildMatchEndpoint(request);

        // Assert
        assertTrue(endpoint.startsWith("/match/sanctions?"));
        assertTrue(endpoint.contains("limit=10"));
        assertTrue(endpoint.contains("threshold=0.7"));
        assertTrue(endpoint.contains("cutoff=0.5"));
        assertTrue(endpoint.contains("algorithm=logic-v2"));
        assertTrue(endpoint.contains("topics=sanction"));
        assertTrue(endpoint.contains("topics=role.pep"));
    }

    @Test
    void buildMatchEndpoint_datasetOnly_minimalUrl() throws Exception {
        // Arrange
        EntityMatchRequest request = new EntityMatchRequest.Builder()
            .dataset("default")
            .build();

        // Act
        String endpoint = invokeBuildMatchEndpoint(request);

        // Assert
        assertEquals("/match/default", endpoint);
    }

    @Test
    void buildMatchEndpoint_withTopics_eachTopicAsParam() throws Exception {
        // Arrange
        EntityMatchRequest request = new EntityMatchRequest.Builder()
            .dataset("sanctions")
            .topics(List.of("sanction", "role.pep", "crime.fin"))
            .build();

        // Act
        String endpoint = invokeBuildMatchEndpoint(request);

        // Assert
        assertTrue(endpoint.contains("topics=sanction"));
        assertTrue(endpoint.contains("topics=role.pep"));
        assertTrue(endpoint.contains("topics=crime.fin"));
        // Count occurrences of "topics=" should be 3
        assertEquals(3, endpoint.split("topics=").length - 1);
    }

    @Test
    void buildMatchEndpoint_withIncludeDatasets_eachAsParam() throws Exception {
        // Arrange
        EntityMatchRequest request = new EntityMatchRequest.Builder()
            .dataset("default")
            .includeDatasets(List.of("us_ofac_sdn", "eu_sanctions"))
            .build();

        // Act
        String endpoint = invokeBuildMatchEndpoint(request);

        // Assert
        assertTrue(endpoint.contains("include_dataset=us_ofac_sdn"));
        assertTrue(endpoint.contains("include_dataset=eu_sanctions"));
    }

    @Test
    void buildMatchEndpoint_withExcludeDatasets_eachAsParam() throws Exception {
        // Arrange
        EntityMatchRequest request = new EntityMatchRequest.Builder()
            .dataset("default")
            .excludeDatasets(List.of("ru_pep_registry"))
            .build();

        // Act
        String endpoint = invokeBuildMatchEndpoint(request);

        // Assert
        assertTrue(endpoint.contains("exclude_dataset=ru_pep_registry"));
    }

    @Test
    void buildMatchEndpoint_withExcludeSchemas_eachAsParam() throws Exception {
        // Arrange
        EntityMatchRequest request = new EntityMatchRequest.Builder()
            .dataset("default")
            .excludeSchemas(List.of("Vessel", "Aircraft"))
            .build();

        // Act
        String endpoint = invokeBuildMatchEndpoint(request);

        // Assert
        assertTrue(endpoint.contains("exclude_schema=Vessel"));
        assertTrue(endpoint.contains("exclude_schema=Aircraft"));
    }

    @Test
    void buildMatchEndpoint_emptyTopics_noTopicsParam() throws Exception {
        // Arrange
        EntityMatchRequest request = new EntityMatchRequest.Builder()
            .dataset("sanctions")
            .topics(List.of())
            .build();

        // Act
        String endpoint = invokeBuildMatchEndpoint(request);

        // Assert
        assertFalse(endpoint.contains("topics="));
    }

    @Test
    void buildMatchEndpoint_nullTopics_noTopicsParam() throws Exception {
        // Arrange
        EntityMatchRequest request = new EntityMatchRequest.Builder()
            .dataset("sanctions")
            .topics(null)
            .build();

        // Act
        String endpoint = invokeBuildMatchEndpoint(request);

        // Assert
        assertFalse(endpoint.contains("topics="));
    }

    @Test
    void buildMatchEndpoint_paramDelimiters_firstUsesQuestionMark() throws Exception {
        // Arrange
        EntityMatchRequest request = new EntityMatchRequest.Builder()
            .dataset("sanctions")
            .limit(5)
            .threshold(0.8)
            .build();

        // Act
        String endpoint = invokeBuildMatchEndpoint(request);

        // Assert
        int qIndex = endpoint.indexOf('?');
        assertTrue(qIndex > 0);
        assertTrue(endpoint.indexOf('&') > qIndex);
        // Should NOT have multiple ?
        assertEquals(qIndex, endpoint.lastIndexOf('?'));
    }

    // ---- buildSearchEndpoint tests ----

    @Test
    void buildSearchEndpoint_withQueryAndLimit_constructsCorrectUrl() throws Exception {
        // Act
        String endpoint = invokeBuildSearchEndpoint("sanctions", "John Doe", 5);

        // Assert
        assertTrue(endpoint.startsWith("/search/sanctions?"));
        assertTrue(endpoint.contains("q=John"));
        assertTrue(endpoint.contains("limit=5"));
    }

    @Test
    void buildSearchEndpoint_specialCharacters_urlEncoded() throws Exception {
        // Act
        String endpoint = invokeBuildSearchEndpoint("sanctions", "O'Brien & Sons", 10);

        // Assert
        // Should be URL-encoded (no raw apostrophe or ampersand)
        assertTrue(endpoint.contains("q="));
        assertFalse(endpoint.contains("q=O'Brien"));
    }

    @Test
    void buildSearchEndpoint_nullQuery_noQueryParam() throws Exception {
        // Act
        String endpoint = invokeBuildSearchEndpoint("sanctions", null, 5);

        // Assert
        assertFalse(endpoint.contains("q="));
        assertTrue(endpoint.contains("limit=5"));
    }

    @Test
    void buildSearchEndpoint_emptyQuery_noQueryParam() throws Exception {
        // Act
        String endpoint = invokeBuildSearchEndpoint("sanctions", "  ", 5);

        // Assert
        assertFalse(endpoint.contains("q="));
    }

    @Test
    void buildSearchEndpoint_nullLimit_noLimitParam() throws Exception {
        // Act
        String endpoint = invokeBuildSearchEndpoint("sanctions", "test", null);

        // Assert
        assertTrue(endpoint.contains("q=test"));
        assertFalse(endpoint.contains("limit="));
    }

    // ---- Helper: invoke private methods via reflection ----

    private String invokeBuildMatchEndpoint(EntityMatchRequest request) throws Exception {
        Method method = OpenSanctionsApiAdapter.class.getDeclaredMethod(
            "buildMatchEndpoint", EntityMatchRequest.class);
        method.setAccessible(true);
        return (String) method.invoke(adapter, request);
    }

    private String invokeBuildSearchEndpoint(String dataset, String query, Integer limit)
        throws Exception {
        Method method = OpenSanctionsApiAdapter.class.getDeclaredMethod(
            "buildSearchEndpoint", String.class, String.class, Integer.class);
        method.setAccessible(true);
        return (String) method.invoke(adapter, dataset, query, limit);
    }
}
