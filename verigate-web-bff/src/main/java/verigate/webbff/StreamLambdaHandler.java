/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.webbff;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.model.HttpApiV2ProxyRequest;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Lambda entry point that bridges API Gateway HTTP API v2 proxy requests to the Spring Boot
 * application.
 *
 * <p>Uses aws-serverless-java-container to initialize the Spring Boot context once during cold
 * start and route all subsequent API Gateway requests through the existing Spring MVC controllers.
 *
 * <p>Also handles EventBridge Schedule events by converting them to HTTP API v2 format before
 * forwarding to the Spring Boot handler.
 */
public class StreamLambdaHandler implements RequestStreamHandler {

  private static final SpringBootLambdaContainerHandler<HttpApiV2ProxyRequest, AwsProxyResponse> handler;

  static {
    try {
      handler = SpringBootLambdaContainerHandler.getHttpApiV2ProxyHandler(
          VerigateWebBffApplication.class);
    } catch (ContainerInitializationException e) {
      throw new RuntimeException("Could not initialize Spring Boot application", e);
    }
  }

  @Override
  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
      throws IOException {
    byte[] inputBytes = inputStream.readAllBytes();
    String inputJson = new String(inputBytes, StandardCharsets.UTF_8);

    // Detect EventBridge Schedule events (contain httpMethod but not version 2.0)
    if (inputJson.contains("\"httpMethod\"") && !inputJson.contains("\"version\"")) {
      // Convert schedule event to HTTP API v2 format
      String httpApiV2Event = """
          {
            "version": "2.0",
            "routeKey": "POST /api/admin/system-health/snapshot",
            "rawPath": "/api/admin/system-health/snapshot",
            "rawQueryString": "",
            "headers": {"content-type": "application/json"},
            "requestContext": {
              "accountId": "scheduled",
              "apiId": "scheduled",
              "http": {
                "method": "POST",
                "path": "/api/admin/system-health/snapshot",
                "protocol": "HTTP/1.1",
                "sourceIp": "127.0.0.1",
                "userAgent": "EventBridge-Scheduler"
              },
              "requestId": "scheduled-health-snapshot",
              "routeKey": "POST /api/admin/system-health/snapshot",
              "stage": "$default",
              "time": "01/Jan/2024:00:00:00 +0000",
              "timeEpoch": 0
            },
            "isBase64Encoded": false
          }
          """;
      inputStream = new ByteArrayInputStream(httpApiV2Event.getBytes(StandardCharsets.UTF_8));
    } else {
      inputStream = new ByteArrayInputStream(inputBytes);
    }

    handler.proxyStream(inputStream, outputStream, context);
  }
}
