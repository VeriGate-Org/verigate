/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.ai.common.infrastructure.bedrock;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

/**
 * Factory for creating singleton {@link BedrockRuntimeClient} instances.
 */
public final class BedrockClientFactory {

  private static volatile BedrockRuntimeClient instance;

  private BedrockClientFactory() {
  }

  /**
   * Returns a singleton BedrockRuntimeClient configured for the specified region.
   *
   * @param region the AWS region (e.g., "us-east-1")
   * @return the configured BedrockRuntimeClient
   */
  public static BedrockRuntimeClient create(String region) {
    if (instance == null) {
      synchronized (BedrockClientFactory.class) {
        if (instance == null) {
          instance = BedrockRuntimeClient.builder()
              .region(Region.of(region))
              .build();
        }
      }
    }
    return instance;
  }

  /**
   * Returns a singleton BedrockRuntimeClient using the default region (us-east-1).
   *
   * @return the configured BedrockRuntimeClient
   */
  public static BedrockRuntimeClient create() {
    return create("us-east-1");
  }
}
