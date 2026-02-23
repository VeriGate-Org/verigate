/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.infrastructure.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import crosscutting.config.Config;
import crosscutting.environment.Environment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CipcApiConfigurationTest {

    @Mock
    private Environment environment;

    @Mock
    private Config config;

    private CipcApiConfiguration configuration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        configuration = new CipcApiConfiguration(environment, config);
    }

    @Test
    void testGetApiKey() {
        when(environment.get("CIPC_API_KEY")).thenReturn("test-api-key");
        
        assertEquals("test-api-key", configuration.getApiKey());
    }

    @Test
    void testGetBaseUrlFromEnvironment() {
        when(environment.get("CIPC_BASE_URL")).thenReturn("https://test.cipc.api/v1");
        
        assertEquals("https://test.cipc.api/v1", configuration.getBaseUrl());
    }

    @Test
    void testGetBaseUrlFromConfig() {
        when(environment.get("CIPC_BASE_URL")).thenReturn(null);
        when(config.get("cipc.api.base-url")).thenReturn("https://config.cipc.api/v1");
        
        assertEquals("https://config.cipc.api/v1", configuration.getBaseUrl());
    }

    @Test
    void testGetBaseUrlDefault() {
        when(environment.get("CIPC_BASE_URL")).thenReturn(null);
        when(config.get("cipc.api.base-url")).thenReturn(null);
        
        assertEquals("https://cipc-apm-rs-dev.azure-api.net/enterprise/v1", configuration.getBaseUrl());
    }

    @Test
    void testGetHttpTimeoutSecondsFromEnvironment() {
        when(environment.get("CIPC_HTTP_TIMEOUT_SECONDS")).thenReturn("60");
        
        assertEquals(60, configuration.getHttpTimeoutSeconds());
    }

    @Test
    void testGetHttpTimeoutSecondsInvalidValue() {
        when(environment.get("CIPC_HTTP_TIMEOUT_SECONDS")).thenReturn("invalid");
        when(config.get("cipc.http.timeout-seconds")).thenReturn(null);
        
        assertEquals(30, configuration.getHttpTimeoutSeconds()); // Default value
    }

    @Test
    void testValidateSuccess() {
        when(environment.get("CIPC_API_KEY")).thenReturn("valid-api-key");
        when(environment.get("CIPC_BASE_URL")).thenReturn("https://valid.url.com");
        
        assertDoesNotThrow(() -> configuration.validate());
    }

    @Test
    void testValidateFailsWithoutApiKey() {
        when(environment.get("CIPC_API_KEY")).thenReturn(null);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> configuration.validate());
        
        assertTrue(exception.getMessage().contains("API key is required"));
    }

    @Test
    void testValidateFailsWithEmptyApiKey() {
        when(environment.get("CIPC_API_KEY")).thenReturn("  ");
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> configuration.validate());
        
        assertTrue(exception.getMessage().contains("API key is required"));
    }

    @Test
    void testValidateFailsWithInvalidUrl() {
        when(environment.get("CIPC_API_KEY")).thenReturn("valid-api-key");
        when(environment.get("CIPC_BASE_URL")).thenReturn("invalid-url");
        when(config.get("cipc.api.base-url")).thenReturn(null);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> configuration.validate());
        
        assertTrue(exception.getMessage().contains("must start with http"));
    }

    @Test
    void testGetMaskedApiKey() {
        when(environment.get("CIPC_API_KEY")).thenReturn("1234567890abcdef");
        
        assertEquals("1234***cdef", configuration.getMaskedApiKey());
    }

    @Test
    void testGetMaskedApiKeyShort() {
        when(environment.get("CIPC_API_KEY")).thenReturn("short");
        
        assertEquals("***", configuration.getMaskedApiKey());
    }

    @Test
    void testGetMaskedBaseUrl() {
        when(environment.get("CIPC_BASE_URL")).thenReturn("https://api.example.com/v1?key=secret");
        
        assertEquals("https://api.example.com/v1?***", configuration.getMaskedBaseUrl());
    }

    @Test
    void testGetMaskedBaseUrlNoQuery() {
        when(environment.get("CIPC_BASE_URL")).thenReturn("https://api.example.com/v1");
        
        assertEquals("https://api.example.com/v1", configuration.getMaskedBaseUrl());
    }
}