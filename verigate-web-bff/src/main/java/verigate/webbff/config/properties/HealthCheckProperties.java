package verigate.webbff.config.properties;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "verigate.healthcheck")
public class HealthCheckProperties {

  private int overallTimeoutSeconds = 25;
  private int connectTimeoutMs = 3000;
  private int readTimeoutMs = 5000;
  private String snapshotTableName = "health-snapshots";
  private Map<String, ServiceEntry> services = new LinkedHashMap<>();

  public int getOverallTimeoutSeconds() {
    return overallTimeoutSeconds;
  }

  public void setOverallTimeoutSeconds(int overallTimeoutSeconds) {
    this.overallTimeoutSeconds = overallTimeoutSeconds;
  }

  public int getConnectTimeoutMs() {
    return connectTimeoutMs;
  }

  public void setConnectTimeoutMs(int connectTimeoutMs) {
    this.connectTimeoutMs = connectTimeoutMs;
  }

  public int getReadTimeoutMs() {
    return readTimeoutMs;
  }

  public void setReadTimeoutMs(int readTimeoutMs) {
    this.readTimeoutMs = readTimeoutMs;
  }

  public String getSnapshotTableName() {
    return snapshotTableName;
  }

  public void setSnapshotTableName(String snapshotTableName) {
    this.snapshotTableName = snapshotTableName;
  }

  public Map<String, ServiceEntry> getServices() {
    return services;
  }

  public void setServices(Map<String, ServiceEntry> services) {
    this.services = services;
  }

  public static class ServiceEntry {
    private String name;
    private String url = "";
    private String protocol = "REST";
    private int port = 443;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public String getProtocol() {
      return protocol;
    }

    public void setProtocol(String protocol) {
      this.protocol = protocol;
    }

    public int getPort() {
      return port;
    }

    public void setPort(int port) {
      this.port = port;
    }
  }
}
