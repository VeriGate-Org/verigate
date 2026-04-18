package verigate.webbff.admin.service.health;

import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import verigate.webbff.admin.model.health.ExternalServiceHealth;
import verigate.webbff.admin.model.health.ExternalServiceHealth.ProbeResult;
import verigate.webbff.config.properties.HealthCheckProperties;

@Component
public class ExternalServiceProber {

  private static final Logger logger = LoggerFactory.getLogger(ExternalServiceProber.class);

  private final HealthCheckProperties properties;
  private final HttpClient httpClient;

  public ExternalServiceProber(HealthCheckProperties properties) {
    this.properties = properties;
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofMillis(properties.getConnectTimeoutMs()))
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();
  }

  public List<ExternalServiceHealth> probeAll() {
    List<ExternalServiceHealth> results = new ArrayList<>();
    for (Map.Entry<String, HealthCheckProperties.ServiceEntry> entry : properties.getServices().entrySet()) {
      String id = entry.getKey();
      HealthCheckProperties.ServiceEntry service = entry.getValue();
      results.add(probe(id, service));
    }
    return results;
  }

  ExternalServiceHealth probe(String id, HealthCheckProperties.ServiceEntry service) {
    String url = service.getUrl();
    if (url == null || url.isBlank()) {
      return new ExternalServiceHealth(
          id, service.getName(), service.getProtocol(), "",
          new ProbeResult(false, 0, "Not configured"),
          new ProbeResult(false, 0, "Not configured"),
          new ProbeResult(false, 0, "Not configured"),
          "UNCONFIGURED", 0
      );
    }

    String hostname;
    int port = service.getPort();
    try {
      URI uri = URI.create(url);
      hostname = uri.getHost();
      if (hostname == null || hostname.isBlank()) {
        throw new IllegalArgumentException("No hostname in URL");
      }
      if (uri.getPort() > 0) {
        port = uri.getPort();
      } else if ("http".equalsIgnoreCase(uri.getScheme())) {
        port = 80;
      }
    } catch (Exception e) {
      logger.warn("Invalid URL for service {}: {}", id, url, e);
      return new ExternalServiceHealth(
          id, service.getName(), service.getProtocol(), url,
          new ProbeResult(false, 0, "Invalid URL: " + e.getMessage()),
          new ProbeResult(false, 0, "Skipped"),
          new ProbeResult(false, 0, "Skipped"),
          "DOWN", 0
      );
    }

    long totalStart = System.currentTimeMillis();

    ProbeResult dnsResult = probeDns(hostname);
    ProbeResult tcpResult = dnsResult.success() ? probeTcp(hostname, port) : new ProbeResult(false, 0, "Skipped (DNS failed)");
    ProbeResult httpResult = tcpResult.success() ? probeHttp(url) : new ProbeResult(false, 0, "Skipped (TCP failed)");

    long totalLatency = System.currentTimeMillis() - totalStart;

    String overallStatus;
    if (dnsResult.success() && tcpResult.success() && httpResult.success()) {
      overallStatus = "HEALTHY";
    } else if (dnsResult.success()) {
      overallStatus = "DEGRADED";
    } else {
      overallStatus = "DOWN";
    }

    return new ExternalServiceHealth(
        id, service.getName(), service.getProtocol(), url,
        dnsResult, tcpResult, httpResult,
        overallStatus, totalLatency
    );
  }

  ProbeResult probeDns(String hostname) {
    long start = System.currentTimeMillis();
    try {
      InetAddress address = InetAddress.getByName(hostname);
      long latency = System.currentTimeMillis() - start;
      return new ProbeResult(true, latency, address.getHostAddress());
    } catch (Exception e) {
      long latency = System.currentTimeMillis() - start;
      logger.debug("DNS probe failed for {}: {}", hostname, e.getMessage());
      return new ProbeResult(false, latency, e.getMessage());
    }
  }

  ProbeResult probeTcp(String hostname, int port) {
    long start = System.currentTimeMillis();
    try (Socket socket = new Socket()) {
      socket.connect(new java.net.InetSocketAddress(hostname, port), properties.getConnectTimeoutMs());
      long latency = System.currentTimeMillis() - start;
      return new ProbeResult(true, latency, "Connected to " + hostname + ":" + port);
    } catch (Exception e) {
      long latency = System.currentTimeMillis() - start;
      logger.debug("TCP probe failed for {}:{}: {}", hostname, port, e.getMessage());
      return new ProbeResult(false, latency, e.getMessage());
    }
  }

  ProbeResult probeHttp(String url) {
    long start = System.currentTimeMillis();
    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .method("HEAD", HttpRequest.BodyPublishers.noBody())
          .timeout(Duration.ofMillis(properties.getReadTimeoutMs()))
          .build();
      HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
      long latency = System.currentTimeMillis() - start;
      int statusCode = response.statusCode();
      boolean success = statusCode < 500;
      return new ProbeResult(success, latency, "HTTP " + statusCode);
    } catch (Exception e) {
      long latency = System.currentTimeMillis() - start;
      logger.debug("HTTP probe failed for {}: {}", url, e.getMessage());
      return new ProbeResult(false, latency, e.getMessage());
    }
  }
}
