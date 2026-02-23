/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.s3;

import com.google.common.annotations.VisibleForTesting;
import crosscutting.util.ExceptionLoggingSupplier;
import domain.exceptions.PermanentException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper for S3 presigned URLs for S3 Objects with parsing and validation.
 */
public class S3PresignedUrl {

  private static final Logger LOGGER = LoggerFactory.getLogger(S3PresignedUrl.class);

  private static final Set<String> SIGNED_QUERY_PARAMS = Set.of("X-Amz-Credential",
      "X-Amz-Signature", "X-Amz-Security-Token", "X-Amz-Expires");

  private final URL url;

  /**
   * Construct a wrapper with a string URL.
   *
   * @throws IllegalArgumentException If the supplied URL is not a valid pre-signed S3 URL.
   */
  public S3PresignedUrl(String strUrl) throws IllegalArgumentException {
    this.url = createUrl(strUrl, true);
  }

  /**
   * Construct a wrapper with a string URL and optionally enable validation. Mainly used for
   * testing.
   *
   * @throws IllegalArgumentException If validateUrl is true and the supplied URL is not a valid
   *                                  pre-signed S3 URL
   */
  @VisibleForTesting
  public S3PresignedUrl(String strUrl, boolean validateUrl) throws IllegalArgumentException {
    this.url = createUrl(strUrl, validateUrl);
  }

  public URL getUrl() {
    return url;
  }

  /**
   * Create a supplier of an InputStream to the S3 object represented by this presigned URL.
   */
  public Supplier<InputStream> getS3ObjectInputStreamSupplier() {
    return new ExceptionLoggingSupplier<>(
        LOGGER,
        () -> getUrl().openStream(),
        PermanentException.wrapInPermanentException("Failed to open stream to S3 pre-signed URL"));
  }

  private static URL createUrl(String strUrl, boolean validateUrl) throws IllegalArgumentException {
    try {
      final URL url = new URI(strUrl).toURL();
      if (validateUrl) {
        validateUrl(url);
      }
      return url;
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Malformed S3 presigned URL", e);
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("Invalid S3 presigned URI syntax", e);
    }
  }

  private static void validateUrl(URL url) throws IllegalArgumentException {
    if (!isS3Url(url)) {
      throw new IllegalArgumentException("URL is not an S3 presigned URL");
    }
    if (!isSigned(url)) {
      throw new IllegalArgumentException("S3 URL is not properly signed");
    }
  }

  /**
   * Checks if a given URL is an Amazon S3 URL.
   *
   * @param url The URL to check.
   * @return true if the URL is a recognized format of an S3 URL, otherwise false.
   */
  private static boolean isS3Url(URL url) {
    final String host = url.getHost();
    final String path = url.getPath();

    // Check for virtual-hosted-style URL
    if (host.endsWith("amazonaws.com")) {
      if (host.startsWith("s3.") || host.matches(".*\\.s3[.-].*")) {
        return true;
      }
    }

    // Check for path-style URL
    return "s3.amazonaws.com".equals(host) && path.length() > 1;
  }

  /**
   * Determines whether a URL is a signed URL. Signed URLs contain specific query parameters that
   * authenticate access.
   *
   * @param url The URL to check.
   * @return true if the URL contains signature parameters, otherwise false.
   */
  private static boolean isSigned(URL url) {
    final String query = url.getQuery();
    if (StringUtils.isEmpty(query)) {
      return false;
    }
    for (String param : SIGNED_QUERY_PARAMS) {
      if (query.contains(param)) {
        return true;
      }
    }
    return false;
  }

}
