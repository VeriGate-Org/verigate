package verigate.webbff.sanctions.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SanctionsProxyService {

  private static final Logger logger = LoggerFactory.getLogger(SanctionsProxyService.class);

  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;
  private final String apiBaseUrl;
  private final String apiKey;

  public SanctionsProxyService(
      ObjectMapper objectMapper,
      @Value("${verigate.opensanctions.api-url:https://api.opensanctions.org}") String apiBaseUrl,
      @Value("${verigate.opensanctions.api-key:}") String apiKey) {
    this.objectMapper = objectMapper;
    this.apiBaseUrl = apiBaseUrl;
    this.apiKey = apiKey;
    this.httpClient =
        HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();
  }

  public Map<String, Object> getEntity(String entityId) {
    try {
      String url = apiBaseUrl + "/entities/" + entityId;
      HttpRequest request = buildGetRequest(url);
      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      return objectMapper.readValue(response.body(), new TypeReference<>() {});
    } catch (Exception e) {
      logger.error("Failed to get entity {}", entityId, e);
      throw new RuntimeException("Failed to retrieve entity details", e);
    }
  }

  public Map<String, Object> getAdjacentEntities(String entityId) {
    try {
      String url = apiBaseUrl + "/entities/" + entityId + "/adjacent";
      HttpRequest request = buildGetRequest(url);
      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      return objectMapper.readValue(response.body(), new TypeReference<>() {});
    } catch (Exception e) {
      logger.error("Failed to get adjacent entities for {}", entityId, e);
      throw new RuntimeException("Failed to retrieve adjacent entities", e);
    }
  }

  public Map<String, Object> submitDisposition(Map<String, Object> disposition) {
    // Store disposition in-memory for now; DynamoDB persistence can be added later
    Map<String, Object> result = new HashMap<>();
    result.put("dispositionId", UUID.randomUUID().toString());
    result.put("entityId", disposition.get("entityId"));
    result.put("action", disposition.get("action"));
    result.put("createdAt", Instant.now().toString());
    return result;
  }

  public Map<String, Object> getScreeningHistory() {
    // Placeholder - returns empty history
    Map<String, Object> result = new HashMap<>();
    result.put("items", java.util.List.of());
    result.put("total", 0);
    return result;
  }

  private HttpRequest buildGetRequest(String url) {
    HttpRequest.Builder builder =
        HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(60))
            .GET();
    if (apiKey != null && !apiKey.isEmpty()) {
      builder.header("Authorization", "ApiKey " + apiKey);
    }
    return builder.build();
  }
}
