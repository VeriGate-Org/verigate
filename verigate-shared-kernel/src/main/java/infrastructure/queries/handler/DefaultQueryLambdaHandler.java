/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.queries.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import domain.queries.handlers.QueryHandler;
import infrastructure.apigateway.ApiGatewayResponse;
import infrastructure.functions.lambda.serializers.Serializer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A default implementation of a Lambda handler for query processing. It extracts a query from an
 * APIGatewayProxyRequestEvent, processes the query, and returns an ApiGatewayResponse.
 *
 * @param <QueryT> The type of query to be processed.
 * @param <OutputT> The type of output to be returned.
 */
public class DefaultQueryLambdaHandler<QueryT, OutputT>
    implements QueryLambdaHandler<APIGatewayProxyRequestEvent, ApiGatewayResponse> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultQueryLambdaHandler.class);

  private final Function<APIGatewayProxyRequestEvent, QueryT> extractSingleQuery;
  private final Function<Map<String, String>, QueryT> queryMapper;
  private final QueryHandler<QueryT, OutputT> queryHandler;
  private final Serializer jsonSerializer;

  /**
   * Constructs a new DefaultQueryLambdaHandler.
   *
   * @param extractSingleQuery The function to extract a query from an APIGatewayProxyRequestEvent.
   * @param queryHandler The handler for the query.
   * @param jsonSerializer The serializer for the output.
   */
  public DefaultQueryLambdaHandler(
      Function<APIGatewayProxyRequestEvent, QueryT> extractSingleQuery,
      Function<Map<String, String>, QueryT> queryMapper,
      QueryHandler<QueryT, OutputT> queryHandler,
      Serializer jsonSerializer) {
    this.extractSingleQuery = extractSingleQuery;
    this.queryMapper = queryMapper;
    this.queryHandler = Objects.requireNonNull(queryHandler);
    this.jsonSerializer = Objects.requireNonNull(jsonSerializer);
  }

  /**
   * Handles the incoming API Gateway request.
   *
   * @param event The API Gateway request event.
   * @param context The Lambda execution context.
   * @return The ApiGatewayResponse.
   */
  @Override
  public ApiGatewayResponse handleRequest(APIGatewayProxyRequestEvent event, Context context) {
    LOGGER.info("Received API Gateway request: {}", event.getPathParameters());
    try {
      QueryT query = extractQuery(event);
      OutputT output = handleQuery(query);
      String json = serialize(output);
      return buildResponse(200, json);
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Client error: {}", e.getMessage(), e);
      return buildResponse(400, e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Error processing request: {}", e.getMessage(), e);
      return buildResponse(500, e.getMessage());
    }
  }

  /**
   * Extracts a query from the given APIGatewayProxyRequestEvent.
   *
   * @param event The APIGatewayProxyRequestEvent.
   */
  public QueryT extractQuery(APIGatewayProxyRequestEvent event) {
    try {
      if (queryMapper != null) {
        Map<String, String> allParams = extractAllParameters(event);
        QueryT query = queryMapper.apply(allParams);
        LOGGER.info("Successfully extracted query: {}", query);
        return query;
      } else {
        QueryT query = extractSingleQuery.apply(event);
        LOGGER.info("Successfully extracted query: {}", query);
        return query;
      }
    } catch (Exception e) {
      LOGGER.error("Error extracting query from request: {}", e.getMessage(), e);
      throw new IllegalArgumentException("Invalid request parameters", e);
    }
  }

  /**
   * Handles the given query with the provided queryhandler.
   *
   * @param query The incoming query to handle.
   */
  public OutputT handleQuery(QueryT query) {
    try {
      LOGGER.info("Handling query: {}", query);
      var output = queryHandler.handle(query);
      return output;
    } catch (Exception e) {
      LOGGER.error("Error handling query: {}", query, e);
      throw new RuntimeException("Failed to handle query", e);
    }
  }

  /**
   * Serializes the given output to be used in the response.
   *
   * @param output The output to serialize.
   */
  public String serialize(OutputT output) {
    try {
      LOGGER.info("Serializing output: {}", output);
      var json = jsonSerializer.serialize(output);
      LOGGER.info("Successfully serialized output: {}", json);
      return json;
    } catch (Exception e) {
      LOGGER.error("Error serializing output: {}", output);
      throw new RuntimeException("Failed to serialize query", e);
    }
  }

  /**
   * Handles the extraction of all query parameters from the APIGatewayProxyRequestEvent.
   *
   * @param event The APIGatewayProxyRequestEvent.
   *
   */
  protected static Map<String, String> extractAllQueryParameters(
      APIGatewayProxyRequestEvent event) {
    return event.getQueryStringParameters() != null ? event.getQueryStringParameters() : Map.of();
  }

  /**
   * Extracts all parameters (query and path) from the APIGatewayProxyRequestEvent.
   *
   * @param event The APIGatewayProxyRequestEvent.
   * @return A map containing all parameters.
   */
  protected static Map<String, String> extractAllParameters(APIGatewayProxyRequestEvent event) {
    var allParams = new HashMap<String, String>();
    if (event.getQueryStringParameters() != null) {
      var queryStringMap = event.getQueryStringParameters();
      allParams.putAll(queryStringMap);
    }

    if (event.getPathParameters() != null) {
      var pathMap = event.getPathParameters();
      allParams.putAll(pathMap);
    }

    return allParams;
  }

  /**
   * Builds an ApiGatewayResponse with the given status and JSON body.
   *
   * @param status The HTTP status code.
   * @param json The JSON body.
   * @return The ApiGatewayResponse.
   *
   */
  public ApiGatewayResponse buildResponse(int status, String json) {
    return new ApiGatewayResponse.Builder()
        .setStatusCode(status)
        .addJsonHeader()
        .setBody(json)
        .build();
  }
}
