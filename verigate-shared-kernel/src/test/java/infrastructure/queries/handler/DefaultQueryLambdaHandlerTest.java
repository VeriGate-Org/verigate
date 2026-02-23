/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.queries.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import domain.queries.handlers.QueryHandler;
import infrastructure.functions.lambda.serializers.http.JsonSerializer;
import infrastructure.queries.model.QueryStringParameterModel;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class DefaultQueryLambdaHandlerTest {

  private String pathParameterKey;
  private String queryStringParameterKey;

  @Mock private QueryHandler<String, String> queryHandler;
  @Mock private QueryHandler<QueryStringParameterModel, String> multiQueryHandler;

  @Mock private JsonSerializer jsonSerializer;

  @Mock private Context mockContext;

  private DefaultQueryLambdaHandler<String, String> handler;
  private DefaultQueryLambdaHandler<QueryStringParameterModel, String> multiHandler;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    handler =
        new DefaultQueryLambdaHandler<String, String>(
            event -> {
              if (event.getQueryStringParameters() != null
                  && event.getQueryStringParameters().containsKey(queryStringParameterKey)) {
                return event.getQueryStringParameters().get(queryStringParameterKey);
              }
              if (event.getPathParameters() != null
                  && event.getPathParameters().containsKey(pathParameterKey)) {
                return event.getPathParameters().get(pathParameterKey);
              }
              throw new IllegalArgumentException("Missing required parameter: testQuery");
            },
            null,
            queryHandler,
            jsonSerializer) {};

    multiHandler =
        new DefaultQueryLambdaHandler<QueryStringParameterModel, String>(
            null, QueryStringParameterModel::new, multiQueryHandler, jsonSerializer) {};
  }

  @Test
  void testHandleRequestWithPathParameterIsSuccessful() {
    pathParameterKey = "pathParamater";

    var requestEvent =
        new APIGatewayProxyRequestEvent()
            .withPathParameters(Map.of(pathParameterKey, "pathParamaterValue"));

    when(queryHandler.handle("pathParamaterValue")).thenReturn("pathParamaterValueHandled");
    when(jsonSerializer.serialize("pathParamaterValueHandled"))
        .thenReturn("pathParamaterValueHandled");

    var response = handler.handleRequest(requestEvent, mockContext);

    assertNotNull(response);
    assertEquals(200, response.getStatusCode());
    assertEquals("pathParamaterValueHandled", response.getBody());

    verify(queryHandler).handle("pathParamaterValue");
    verify(jsonSerializer).serialize("pathParamaterValueHandled");
  }

  @Test
  void testHandleRequestWithQueryStringParameterIsSuccessful() {
    queryStringParameterKey = "queryStringParameter";
    APIGatewayProxyRequestEvent requestEvent =
        new APIGatewayProxyRequestEvent()
            .withQueryStringParameters(Map.of(queryStringParameterKey, "testQueryStringParameter"));

    when(queryHandler.handle("testQueryStringParameter"))
        .thenReturn("processedTestQueryStringParameter");
    when(jsonSerializer.serialize("processedTestQueryStringParameter"))
        .thenReturn("processedTestQueryStringParameter");

    var response = handler.handleRequest(requestEvent, mockContext);

    assertNotNull(response);
    assertEquals(200, response.getStatusCode());
    assertEquals("processedTestQueryStringParameter", response.getBody());

    verify(queryHandler).handle("testQueryStringParameter");
    verify(jsonSerializer).serialize("processedTestQueryStringParameter");
  }

  @Test
  void testHandleRequestQueryExtractionFails() {

    var requestEvent = new APIGatewayProxyRequestEvent();

    var response = handler.handleRequest(requestEvent, mockContext);

    assertNotNull(response);
    assertEquals(400, response.getStatusCode());
    assertTrue(response.getBody().contains("Invalid request parameters"));

    verifyNoInteractions(queryHandler);
    verifyNoInteractions(jsonSerializer);
  }

  @Test
  void testHandleRequestQueryProcessingFails() {
    pathParameterKey = "queryParamater";
    var requestEvent =
        new APIGatewayProxyRequestEvent().withPathParameters(Map.of(pathParameterKey, "testQuery"));

    when(queryHandler.handle("testQuery"))
        .thenThrow(new RuntimeException("Query processing error"));

    var response = handler.handleRequest(requestEvent, mockContext);

    assertNotNull(response);
    assertEquals(500, response.getStatusCode());
    assertTrue(response.getBody().contains("Failed to handle query"));

    verify(queryHandler).handle("testQuery");
    verifyNoInteractions(jsonSerializer);
  }

  @Test
  void testHandleRequestSerializationFails() {
    pathParameterKey = "queryParamater";
    var requestEvent =
        new APIGatewayProxyRequestEvent()
            .withPathParameters(Map.of(pathParameterKey, "testQuery"))
            .withQueryStringParameters(null);

    when(queryHandler.handle("testQuery")).thenReturn("processedQuery");
    when(jsonSerializer.serialize("processedQuery"))
        .thenThrow(new RuntimeException("Serialization error"));

    var response = handler.handleRequest(requestEvent, mockContext);

    assertNotNull(response);
    assertEquals(500, response.getStatusCode());
    assertTrue(response.getBody().contains("Failed to serialize query"));

    verify(queryHandler).handle("testQuery");
    verify(jsonSerializer).serialize("processedQuery");
  }

  @Test
  void testHandleRequestWithMultipleQueryStrings() {
    pathParameterKey = "queryParamater";
    var requestEvent =
        new APIGatewayProxyRequestEvent()
            .withQueryStringParameters(
                Map.of(
                    "queryString", "queryStringValue",
                    "secondaryQueryString", "secondaryQueryStringValue"));

    var expectedQuery =
        new QueryStringParameterModel(
            Map.of(
                "queryString", "queryStringValue",
                "secondaryQueryString", "secondaryQueryStringValue"));

    var expectedJson =
        "{\"queryString\": \"queryStringValue\",\"secondaryQueryString\":"
            + " \"secondaryQueryStringValue\",}";

    when(multiQueryHandler.handle(
            argThat(
                query ->
                    expectedQuery.getQueryString().equals("queryStringValue")
                        && expectedQuery
                            .getSecondaryQueryString()
                            .equals("secondaryQueryStringValue"))))
        .thenReturn("processedQuery");

    when(jsonSerializer.serialize("processedQuery")).thenReturn(expectedJson);

    var response = multiHandler.handleRequest(requestEvent, mockContext);

    assertNotNull(response);
    assertEquals(200, response.getStatusCode());
    assertTrue(response.getBody().contains(expectedJson));

    verify(multiQueryHandler).handle(argThat(query -> query.equals(expectedQuery)));
    verify(jsonSerializer).serialize("processedQuery");
  }

  @Test
  void testHandleRequestWithPathAndQueryParameters() {
    pathParameterKey = "queryParamater";
    var requestEvent =
        new APIGatewayProxyRequestEvent()
            .withQueryStringParameters(
                Map.of(
                    "queryString", "queryStringValue",
                    "secondaryQueryString", "secondaryQueryStringValue"))
            .withPathParameters(Map.of("pathParameter", "pathParameterValue"));

    var expectedQuery =
        new QueryStringParameterModel(
            Map.of(
                "queryString",
                "queryStringValue",
                "secondaryQueryString",
                "secondaryQueryStringValue",
                "pathParameter",
                "pathParameterValue"));

    var expectedJson =
        "{\"queryString\": \"queryStringValue\",\"secondaryQueryString\":"
            + " \"secondaryQueryStringValue\",\"pathParameter\": \"pathParameterValue\"}";

    when(multiQueryHandler.handle(
            argThat(
                query ->
                    expectedQuery.getQueryString().equals("queryStringValue")
                        && expectedQuery
                            .getSecondaryQueryString()
                            .equals("secondaryQueryStringValue")
                        && expectedQuery.getPathParameter().equals("pathParameterValue"))))
        .thenReturn("processedQuery");

    when(jsonSerializer.serialize("processedQuery")).thenReturn(expectedJson);

    var response = multiHandler.handleRequest(requestEvent, mockContext);

    assertNotNull(response);
    assertEquals(200, response.getStatusCode());
    assertTrue(response.getBody().contains(expectedJson));

    verify(multiQueryHandler).handle(argThat(query -> query.equals(expectedQuery)));
    verify(jsonSerializer).serialize("processedQuery");
  }
}
