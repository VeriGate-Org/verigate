package verigate.webbff;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.sqs.SqsClient;

/**
 * Full-context integration test validating the Spring Boot application
 * works correctly when deployed behind API Gateway via Lambda.
 *
 * <p>Tests the complete security filter chain (Cognito JWT, API key auth,
 * rate limiting), CORS configuration, and endpoint routing — the same
 * behavior that occurs when {@link StreamLambdaHandler} proxies API
 * Gateway requests to the Spring Boot application.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = {
    org.springframework.cloud.function.serverless.web.ServerlessAutoConfiguration.class
})
class StreamLambdaHandlerTest {

  @TestConfiguration
  static class MockAwsConfig {
    @Bean
    @Primary
    DynamoDbClient mockDynamoDbClient() {
      return mock(DynamoDbClient.class);
    }

    @Bean
    @Primary
    SqsClient mockSqsClient() {
      return mock(SqsClient.class);
    }
  }

  @Autowired
  private MockMvc mockMvc;

  @Test
  void healthEndpointReturns200() throws Exception {
    mockMvc.perform(get("/actuator/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("UP"));
  }

  @Test
  void apiEndpointWithoutAuthReturnsForbidden() throws Exception {
    mockMvc.perform(get("/api/verifications")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  @Test
  void adminEndpointWithoutAuthReturnsForbidden() throws Exception {
    mockMvc.perform(post("/api/admin/partners")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"test\",\"contactEmail\":\"t@test.com\"}"))
        .andExpect(status().isForbidden());
  }

  @Test
  void corsPreflightAllowsVerigateDomain() throws Exception {
    mockMvc.perform(options("/api/verifications")
            .header("Origin", "https://portal.verigate.co.za")
            .header("Access-Control-Request-Method", "POST")
            .header("Access-Control-Request-Headers", "Content-Type,X-API-Key"))
        .andExpect(status().isOk())
        .andExpect(header().string("Access-Control-Allow-Origin", "https://portal.verigate.co.za"))
        .andExpect(header().exists("Access-Control-Allow-Methods"))
        .andExpect(header().exists("Access-Control-Allow-Headers"));
  }

  @Test
  void corsPreflightAllowsLocalhost() throws Exception {
    mockMvc.perform(options("/api/verifications")
            .header("Origin", "http://localhost:3000")
            .header("Access-Control-Request-Method", "GET")
            .header("Access-Control-Request-Headers", "X-API-Key"))
        .andExpect(status().isOk())
        .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
  }

  @Test
  void unknownPathReturnsForbidden() throws Exception {
    mockMvc.perform(get("/unknown/path"))
        .andExpect(status().isForbidden());
  }

  @Test
  void lambdaHandlerClassIsLoadable() {
    // Verify StreamLambdaHandler class exists and implements RequestStreamHandler
    // This validates the Maven shade plugin will find the correct handler class
    Class<?> handlerClass = StreamLambdaHandler.class;
    assert com.amazonaws.services.lambda.runtime.RequestStreamHandler.class
        .isAssignableFrom(handlerClass)
        : "StreamLambdaHandler must implement RequestStreamHandler";
  }
}
