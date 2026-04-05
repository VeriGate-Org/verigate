/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.ai.common.domain.services;

import verigate.ai.common.domain.models.AiRequest;
import verigate.ai.common.domain.models.AiResponse;

/**
 * Service interface for AI model invocations.
 */
public interface AiService {

  /**
   * Invokes the AI model synchronously and returns the complete response.
   *
   * @param request the AI request containing prompt and configuration
   * @return the AI response with generated content and token usage
   */
  AiResponse invoke(AiRequest request);
}
