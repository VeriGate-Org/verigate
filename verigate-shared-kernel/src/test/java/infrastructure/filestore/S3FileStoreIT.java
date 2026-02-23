/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.filestore;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import domain.exceptions.PermanentException;
import infrastructure.constants.TestConstants;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Testcontainers
final class S3FileStoreIT {

  @Container
  LocalStackContainer localstack =
      new LocalStackContainer(TestConstants.getLocalstackDefaultDockerImage())
          .withServices(LocalStackContainer.Service.SQS, LocalStackContainer.Service.S3);

  S3Client s3Client;

  // class under test
  S3FileStore fileStore;

  @BeforeEach
  void beforeEach() {

    // Credentials used for everything - use what is known to localstack
    final StaticCredentialsProvider credentialsProvider =
        StaticCredentialsProvider.create(
            AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey()));

    // Create S3 client
    s3Client =
        S3Client.builder()
            .region(Region.of(localstack.getRegion()))
            .endpointOverride(localstack.getEndpoint())
            .credentialsProvider(credentialsProvider)
            .build();

    final S3Presigner s3Presigner =
        S3Presigner.builder()
            .region(Region.of(localstack.getRegion()))
            .credentialsProvider(credentialsProvider)
            .build();

    fileStore = new S3FileStore(s3Client, s3Presigner);
  }

  @Test
  void fileExists() {

    assertFalse(fileStore.fileExists("bucket", "key"));

    // create the file
    s3Client.createBucket(CreateBucketRequest.builder().bucket("bucket").build());
    s3Client.putObject(
        PutObjectRequest.builder().bucket("bucket").key("key").build(), RequestBody.empty());

    // test that file-store says the file now exists
    assertTrue(fileStore.fileExists("bucket", "key"));
  }

  @Test
  void storeFile_when_no_bucket_then_throws_PermanentException() {

    final byte[] bytes = "test".getBytes();
    assertThrows(PermanentException.class, () -> fileStore.storeFile("bucket", "key", bytes));
  }

  @Test
  void storeFile() throws IOException {
    s3Client.createBucket(CreateBucketRequest.builder().bucket("bucket").build());

    final byte[] bytes = "test".getBytes();
    fileStore.storeFile("bucket", "key", bytes);

    assertArrayEquals(bytes, s3Client.getObject(b -> b.bucket("bucket").key("key")).readAllBytes());
  }
}
