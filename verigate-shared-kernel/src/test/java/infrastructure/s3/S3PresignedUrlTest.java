/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.s3;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import org.junit.jupiter.api.Test;

class S3PresignedUrlTest {

  @Test
  void validUrl() {
    final S3PresignedUrl s3PresignedUrl = new S3PresignedUrl("https://bucket-name.s3.eu-west-1.amazonaws.com/object.name?response-content-disposition=inline&X-Amz-Security-Token=security%2Ftoken&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240807T104027Z&X-Amz-SignedHeaders=host&X-Amz-Expires=10800&X-Amz-Credential=CREDENTIAL&X-Amz-Signature=SIGNATURE");
    assertEquals("bucket-name.s3.eu-west-1.amazonaws.com", s3PresignedUrl.getUrl().getHost());
    assertEquals("object.name", new File(s3PresignedUrl.getUrl().getPath()).getName());
  }

  @Test
  void invalidSyntax() {
    assertThrows(IllegalArgumentException.class, () -> new S3PresignedUrl("random text"));
  }

  @Test
  void nonAmazonHost() {
    assertThrows(IllegalArgumentException.class, () -> new S3PresignedUrl("https://my.host.com/object.name?response-content-disposition=inline&X-Amz-Security-Token=security%2Ftoken&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240807T104027Z&X-Amz-SignedHeaders=host&X-Amz-Expires=10800&X-Amz-Credential=CREDENTIAL&X-Amz-Signature=SIGNATURE"));
  }

  @Test
  void unSigned() {
    assertThrows(IllegalArgumentException.class, () -> new S3PresignedUrl("https://bucket-name.s3.eu-west-1.amazonaws.com/object.name"));
  }

  @Test
  void validationDisabled() {
    final S3PresignedUrl s3PresignedUrl = new S3PresignedUrl("https://not.a.valid.s3.presigned/url", false);
    assertEquals("not.a.valid.s3.presigned", s3PresignedUrl.getUrl().getHost());
    assertEquals("url", new File(s3PresignedUrl.getUrl().getPath()).getName());
  }

}