/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.domain.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;

/**
 * Port for retrieving the raw bytes of an uploaded document image ahead of AI analysis. Kept as
 * a domain-level interface so the application layer does not depend on any specific storage
 * technology (S3 in production; the infrastructure layer provides the real implementation).
 */
public interface DocumentImageFetcher {

  /**
   * Fetches the raw bytes of a stored document image.
   *
   * @param bucketName the storage bucket/container name
   * @param objectKey the object key/path within the bucket
   * @return the raw image bytes
   * @throws TransientException if a temporary error occurs that may be retried
   * @throws PermanentException if the object cannot be retrieved (missing, access denied, etc.)
   */
  byte[] fetch(String bucketName, String objectKey)
      throws TransientException, PermanentException;
}
