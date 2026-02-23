/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.s3;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class S3ObjectUriTest {

  @Test
  void validNoPath() {
    final S3ObjectUri s3ObjectUri = new S3ObjectUri("s3://bucket/object");
    assertEquals("bucket", s3ObjectUri.getBucket());
    assertEquals("object", s3ObjectUri.getKey());
    assertEquals("object", s3ObjectUri.getObjectName());
  }

  @Test
  void validWithPath() {
    final S3ObjectUri s3ObjectUri = new S3ObjectUri("s3://bucket/path-1/path-2/object.txt");
    assertEquals("bucket", s3ObjectUri.getBucket());
    assertEquals("path-1/path-2/object.txt", s3ObjectUri.getKey());
    assertEquals("object.txt", s3ObjectUri.getObjectName());
  }

  @Test
  void invalidScheme() {
    assertThrows(IllegalArgumentException.class, () -> new S3ObjectUri("https://bucket/object"));
    assertThrows(IllegalArgumentException.class, () -> new S3ObjectUri("random text"));
  }

  @Test
  void noBucket() {
    assertThrows(IllegalArgumentException.class, () -> new S3ObjectUri("s3://"));
  }

  @Test
  void noKey() {
    assertThrows(IllegalArgumentException.class, () -> new S3ObjectUri("s3://bucket"));
    assertThrows(IllegalArgumentException.class, () -> new S3ObjectUri("s3://bucket/"));
    assertThrows(IllegalArgumentException.class, () -> new S3ObjectUri("s3://bucket/path/"));
  }

}