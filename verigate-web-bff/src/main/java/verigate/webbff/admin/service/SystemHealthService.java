package verigate.webbff.admin.service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import verigate.webbff.admin.model.health.ExternalServiceHealth;
import verigate.webbff.admin.model.health.InfrastructureHealth;
import verigate.webbff.admin.model.health.SystemHealthResponse;
import verigate.webbff.admin.service.health.AwsInfrastructureProber;
import verigate.webbff.admin.service.health.BedrockProber;
import verigate.webbff.admin.service.health.ExternalServiceProber;
import verigate.webbff.config.properties.HealthCheckProperties;

@Service
public class SystemHealthService {

  private static final Logger logger = LoggerFactory.getLogger(SystemHealthService.class);

  private final ExternalServiceProber externalServiceProber;
  private final AwsInfrastructureProber awsInfrastructureProber;
  private final BedrockProber bedrockProber;
  private final int overallTimeoutSeconds;

  public SystemHealthService(
      ExternalServiceProber externalServiceProber,
      AwsInfrastructureProber awsInfrastructureProber,
      BedrockProber bedrockProber,
      HealthCheckProperties properties) {
    this.externalServiceProber = externalServiceProber;
    this.awsInfrastructureProber = awsInfrastructureProber;
    this.bedrockProber = bedrockProber;
    this.overallTimeoutSeconds = properties.getOverallTimeoutSeconds();
  }

  @Cacheable("system-health")
  public SystemHealthResponse checkHealth() {
    logger.info("Running system health check");
    long start = System.currentTimeMillis();

    try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
      CompletableFuture<List<ExternalServiceHealth>> externalFuture =
          CompletableFuture.supplyAsync(externalServiceProber::probeAll, executor);

      CompletableFuture<List<InfrastructureHealth.DynamoDbTableHealth>> dynamoFuture =
          CompletableFuture.supplyAsync(awsInfrastructureProber::probeDynamoDb, executor);

      CompletableFuture<List<InfrastructureHealth.SqsQueueHealth>> sqsFuture =
          CompletableFuture.supplyAsync(awsInfrastructureProber::probeSqs, executor);

      CompletableFuture<InfrastructureHealth.KinesisStreamHealth> kinesisFuture =
          CompletableFuture.supplyAsync(awsInfrastructureProber::probeKinesis, executor);

      CompletableFuture<InfrastructureHealth.BedrockHealth> bedrockFuture =
          CompletableFuture.supplyAsync(bedrockProber::probe, executor);

      CompletableFuture.allOf(externalFuture, dynamoFuture, sqsFuture, kinesisFuture, bedrockFuture)
          .orTimeout(overallTimeoutSeconds, TimeUnit.SECONDS)
          .join();

      List<ExternalServiceHealth> externalResults = externalFuture.getNow(List.of());
      List<InfrastructureHealth.DynamoDbTableHealth> dynamoResults = dynamoFuture.getNow(List.of());
      List<InfrastructureHealth.SqsQueueHealth> sqsResults = sqsFuture.getNow(List.of());
      InfrastructureHealth.KinesisStreamHealth kinesisResult = kinesisFuture.getNow(null);
      InfrastructureHealth.BedrockHealth bedrockResult = bedrockFuture.getNow(null);

      InfrastructureHealth infrastructure = new InfrastructureHealth(
          dynamoResults, sqsResults, kinesisResult, bedrockResult
      );

      String overallStatus = computeOverallStatus(externalResults, infrastructure);
      SystemHealthResponse.Summary summary = computeSummary(externalResults, infrastructure);

      long elapsed = System.currentTimeMillis() - start;
      logger.info("System health check completed in {}ms: status={}", elapsed, overallStatus);

      return new SystemHealthResponse(
          overallStatus, summary, externalResults, infrastructure, Instant.now()
      );
    } catch (Exception e) {
      logger.error("System health check failed: {}", e.getMessage(), e);
      long elapsed = System.currentTimeMillis() - start;
      logger.info("System health check failed after {}ms", elapsed);
      return new SystemHealthResponse(
          "DOWN",
          new SystemHealthResponse.Summary(0, 0, 0, 0, 0),
          List.of(),
          new InfrastructureHealth(List.of(), List.of(), null, null),
          Instant.now()
      );
    }
  }

  private String computeOverallStatus(
      List<ExternalServiceHealth> external,
      InfrastructureHealth infrastructure) {

    boolean anyDown = external.stream().anyMatch(s -> "DOWN".equals(s.overallStatus()));
    boolean anyDegraded = external.stream().anyMatch(s -> "DEGRADED".equals(s.overallStatus()));

    if (infrastructure.dynamoDbTables() != null) {
      anyDown = anyDown || infrastructure.dynamoDbTables().stream()
          .anyMatch(t -> "ERROR".equals(t.status()));
    }
    if (infrastructure.sqsQueues() != null) {
      anyDown = anyDown || infrastructure.sqsQueues().stream()
          .anyMatch(q -> "ERROR".equals(q.status()));
    }
    if (infrastructure.kinesisStream() != null && "ERROR".equals(infrastructure.kinesisStream().status())) {
      anyDown = true;
    }
    if (infrastructure.bedrock() != null && "ERROR".equals(infrastructure.bedrock().status())) {
      anyDegraded = true;
    }

    boolean allDown = external.stream().noneMatch(s -> "HEALTHY".equals(s.overallStatus()))
        && !external.isEmpty();

    if (allDown) return "DOWN";
    if (anyDown || anyDegraded) return "DEGRADED";
    return "HEALTHY";
  }

  private SystemHealthResponse.Summary computeSummary(
      List<ExternalServiceHealth> external,
      InfrastructureHealth infrastructure) {

    int healthy = 0;
    int degraded = 0;
    int down = 0;
    int unconfigured = 0;

    for (ExternalServiceHealth service : external) {
      switch (service.overallStatus()) {
        case "HEALTHY" -> healthy++;
        case "DEGRADED" -> degraded++;
        case "DOWN" -> down++;
        case "UNCONFIGURED" -> unconfigured++;
        default -> down++;
      }
    }

    // Count infrastructure
    if (infrastructure.dynamoDbTables() != null) {
      for (var table : infrastructure.dynamoDbTables()) {
        if ("ERROR".equals(table.status())) down++;
        else healthy++;
      }
    }
    if (infrastructure.sqsQueues() != null) {
      for (var queue : infrastructure.sqsQueues()) {
        if ("ERROR".equals(queue.status())) down++;
        else healthy++;
      }
    }
    if (infrastructure.kinesisStream() != null && !"UNCONFIGURED".equals(infrastructure.kinesisStream().status())) {
      if ("ERROR".equals(infrastructure.kinesisStream().status())) down++;
      else healthy++;
    }
    if (infrastructure.bedrock() != null) {
      if ("ERROR".equals(infrastructure.bedrock().status())) degraded++;
      else healthy++;
    }

    int total = healthy + degraded + down + unconfigured;
    return new SystemHealthResponse.Summary(total, healthy, degraded, down, unconfigured);
  }
}
