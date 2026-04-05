/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.ai.common.domain.models;

/**
 * Constants for AI model configuration.
 */
public final class AiConstants {

  public static final String DEFAULT_MODEL_ID =
      "us.anthropic.claude-sonnet-4-5-20250929-v1:0";

  public static final int DEFAULT_MAX_TOKENS = 4096;

  public static final double DEFAULT_TEMPERATURE = 0.3;

  public static final String ANTHROPIC_VERSION = "bedrock-2023-05-31";

  private AiConstants() {
  }
}
