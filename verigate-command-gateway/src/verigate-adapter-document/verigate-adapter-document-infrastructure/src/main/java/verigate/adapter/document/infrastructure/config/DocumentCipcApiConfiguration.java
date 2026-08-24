/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.infrastructure.config;

import crosscutting.config.Config;
import crosscutting.environment.Environment;
import verigate.adapter.document.infrastructure.constants.EnvironmentConstants;

/**
 * Configuration for the document adapter's own CIPC lookup client (used only for cross-validating
 * CIPC registration document uploads). Deliberately separate from
 * {@code verigate-adapter-cipc}'s {@code CipcApiConfiguration} and its {@code CIPC_*} env vars —
 * see story 2.1 design notes.
 */
public class DocumentCipcApiConfiguration {

  private final Environment environment;
  private final Config config;

  public DocumentCipcApiConfiguration(Environment environment, Config config) {
    this.environment = environment;
    this.config = config;
  }

  /**
   * Returns the API key used to authenticate against the CIPC API.
   */
  public String getApiKey() {
    return environment.get(EnvironmentConstants.DOCUMENT_CIPC_API_KEY);
  }

  /**
   * Returns the base URL for the CIPC API.
   */
  public String getBaseUrl() {
    String baseUrl = environment.get(EnvironmentConstants.DOCUMENT_CIPC_BASE_URL);
    if (baseUrl == null || baseUrl.trim().isEmpty()) {
      baseUrl = config.get("document.cipc.api.base-url");
    }
    return baseUrl != null && !baseUrl.trim().isEmpty()
        ? baseUrl
        : EnvironmentConstants.DEFAULT_DOCUMENT_CIPC_BASE_URL;
  }
}
