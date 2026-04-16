/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.domain.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import verigate.adapter.deedsweb.domain.models.DeedsWebCredentials;

/**
 * Resolves the SOAP credentials used to call the DeedsWeb registry. Implementations
 * typically fetch from AWS Secrets Manager. Resolution happens per request — there
 * is no caching at this layer.
 */
public interface DeedsWebCredentialsProvider {

  /**
   * @return the current DeedsWeb credentials
   * @throws TransientException if the secret store is unreachable
   * @throws PermanentException if the secret is missing or malformed
   */
  DeedsWebCredentials get() throws TransientException, PermanentException;
}
