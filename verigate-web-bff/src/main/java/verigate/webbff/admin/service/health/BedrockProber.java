package verigate.webbff.admin.service.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.bedrock.BedrockClient;
import software.amazon.awssdk.services.bedrock.model.ListFoundationModelsRequest;
import verigate.webbff.admin.model.health.InfrastructureHealth;

@Component
public class BedrockProber {

  private static final Logger logger = LoggerFactory.getLogger(BedrockProber.class);

  private final BedrockClient bedrockClient;

  public BedrockProber(BedrockClient bedrockClient) {
    this.bedrockClient = bedrockClient;
  }

  public InfrastructureHealth.BedrockHealth probe() {
    long start = System.currentTimeMillis();
    try {
      bedrockClient.listFoundationModels(ListFoundationModelsRequest.builder().build());
      long latency = System.currentTimeMillis() - start;
      return new InfrastructureHealth.BedrockHealth("us-east-1", "HEALTHY", latency, null);
    } catch (Exception e) {
      long latency = System.currentTimeMillis() - start;
      logger.warn("Bedrock probe failed: {}", e.getMessage());
      return new InfrastructureHealth.BedrockHealth("us-east-1", "ERROR", latency, e.getMessage());
    }
  }
}
