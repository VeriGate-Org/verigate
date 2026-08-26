/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.infrastructure.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import verigate.adapter.document.domain.services.DocumentImageFetcher;

/**
 * Fetches raw document image bytes from S3, for AI analysis.
 */
public class S3DocumentFetcher implements DocumentImageFetcher {

  private static final Logger logger = LoggerFactory.getLogger(S3DocumentFetcher.class);

  private final S3Client s3Client;

  public S3DocumentFetcher(S3Client s3Client) {
    this.s3Client = s3Client;
  }

  @Override
  public byte[] fetch(String bucketName, String objectKey)
      throws TransientException, PermanentException {

    if (bucketName == null || bucketName.trim().isEmpty()
        || objectKey == null || objectKey.trim().isEmpty()) {
      throw new PermanentException("S3 bucket name and object key are required");
    }

    logger.debug("Fetching document image from S3: bucket={}, key={}", bucketName, objectKey);

    try (var response = s3Client.getObject(
        GetObjectRequest.builder()
            .bucket(bucketName)
            .key(objectKey)
            .build())) {
      return response.readAllBytes();
    } catch (NoSuchKeyException e) {
      logger.error("Document image not found in S3: bucket={}, key={}", bucketName, objectKey);
      throw new PermanentException("Document image not found in S3: " + objectKey, e);
    } catch (SdkServiceException e) {
      logger.warn("Transient S3 error fetching document image: {}", e.getMessage());
      throw new TransientException("Failed to fetch document image from S3", e);
    } catch (SdkClientException e) {
      // Connection failure, timeout, DNS issue, etc. reaching S3 -- a sibling of
      // SdkServiceException (not a subtype), so it needs its own catch to be treated as
      // retriable rather than falling into the generic PermanentException case below.
      logger.warn("Transient S3 connectivity error fetching document image: {}", e.getMessage());
      throw new TransientException("Failed to fetch document image from S3", e);
    } catch (Exception e) {
      logger.error("Unexpected error fetching document image from S3: {}", e.getMessage(), e);
      throw new PermanentException("Failed to fetch document image from S3", e);
    }
  }
}
