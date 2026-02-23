/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.s3;

import crosscutting.util.ExceptionLoggingSupplier;
import domain.exceptions.PermanentException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Supplier;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

/**
 * Wrapper for S3 object URIs (s3://*) with parsing and validation features.
 */
public class S3ObjectUri {

  private static final Logger LOGGER = LoggerFactory.getLogger(S3ObjectUri.class);

  private static final String S3_URI_SCHEME = "s3";
  private static final String PATH_SEPARATOR = "/";

  private final S3ObjectMetaData s3ObjectMetaData;

  /**
   * Construct a wrapper from a string URI with validation.
   *
   * @param strUri The S3 URI in string format.
   */
  public S3ObjectUri(String strUri) throws IllegalArgumentException {
    this.s3ObjectMetaData = createS3ObjectMetaData(strUri);
  }

  private S3ObjectMetaData createS3ObjectMetaData(String strUri) throws IllegalArgumentException {
    if (StringUtils.isBlank(strUri)) {
      throw new IllegalArgumentException("S3 Object Uri is empty");
    }
    try {
      final URI uri = new URI(strUri);
      if (!S3_URI_SCHEME.equals(uri.getScheme())) {
        throw new IllegalArgumentException("S3 Object Uri is invalid");
      }
      final String bucket = parseBucket(uri);
      final String key = parseKey(uri);
      final String objectName = parseObjectName(uri);
      return new S3ObjectMetaData(bucket, key, objectName);
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("S3 Object Uri syntax is invalid", e);
    }
  }

  private static String parseBucket(URI uri) {
    final String bucket = uri.getHost();
    if (StringUtils.isBlank(bucket)) {
      throw new IllegalArgumentException("S3 Object Uri does not contain a bucket");
    }
    return bucket;
  }

  private static String parseKey(URI uri) {
    final String path = uri.getPath();
    if (StringUtils.isBlank(path) || !path.startsWith(PATH_SEPARATOR) || path.endsWith(
        PATH_SEPARATOR)) {
      throw new IllegalArgumentException("S3 Object Uri does not contain a key");
    }
    final String key = path.substring(1);
    if (StringUtils.isBlank(key)) {
      throw new IllegalArgumentException("S3 Object Uri does not contain a key");
    }
    return key;
  }

  private String parseObjectName(URI uri) {
    final String[] pathSegments = uri.getPath().split(PATH_SEPARATOR);
    if (pathSegments.length == 0) {
      throw new IllegalArgumentException("S3 Object Uri does not contain a path");
    }
    return pathSegments[pathSegments.length - 1];
  }

  public String getBucket() {
    return s3ObjectMetaData.bucket();
  }

  public String getKey() {
    return s3ObjectMetaData.key();
  }

  public String getObjectName() {
    return s3ObjectMetaData.objectName();
  }

  /**
   * Create a supplier of an InputStream to the S3 object represented by this S3 URI.
   *
   * @param s3Client The client to use for creating an InputStream.
   */
  public Supplier<InputStream> getS3ObjectInputStreamSupplier(S3Client s3Client) {
    return new ExceptionLoggingSupplier<>(
        LOGGER,
        () -> s3Client.getObject(
            GetObjectRequest.builder()
                .bucket(getBucket())
                .key(getKey())
                .build()),
        PermanentException.wrapInPermanentException("Failed to get S3 object"));
  }

  private record S3ObjectMetaData(String bucket, String key, String objectName) {}

}
