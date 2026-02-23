/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.filestore;

import domain.exceptions.PermanentException;
import infrastructure.documents.ResourceUri;
import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.time.Instant;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.MetadataDirective;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.StorageClass;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

/**
 * A class that implements the DocumentStore interface and provides
 * methods to store and retrieve documents from Amazon S3.
 */
public class S3FileStore implements FileStore {

  private final S3Client s3;
  private final S3Presigner presigner;

  /**
   * Constructor that initializes the S3 client and presigner.
   */
  public S3FileStore() {
    this(S3Client.builder().region(Region.EU_WEST_1).build(), S3Presigner.create());
  }

  /**
   * Constructs an instance with a supplied S3 client.
   */
  public S3FileStore(S3Client s3) {
    this(s3, S3Presigner.create());
  }

  /**
   * Constructs an instance with the specified S3 client and presigner.
   */
  public S3FileStore(S3Client s3, S3Presigner presigner) {
    this.s3 = s3;
    this.presigner = presigner;
  }

  /**
   * Stores a document in the specified Amazon S3 bucket.
   *
   * @param bucketName     The name of the bucket where the document will be stored.
   * @param keyName        The key name under which the document will be stored.
   * @param fileStream   The input stream of the document to be stored.
   */
  public ResourceUri storeFile(String bucketName, String keyName, byte[] fileStream) {
    try {
      var stream = new ByteArrayInputStream(fileStream);

      var requestBuilder =
          PutObjectRequest.builder()
              .bucket(bucketName)
              .key(keyName)
              .bucket(bucketName)
              .key(keyName);

      if (keyName.endsWith(".pdf")) {
        requestBuilder.contentType("application/pdf");
      } else if (keyName.endsWith(".html")) {
        requestBuilder.contentType("text/html");
      } else if (keyName.endsWith(".jpg")) {
        requestBuilder.contentType("image/jpeg");
      } else if (keyName.endsWith(".svg")) {
        requestBuilder.contentType("image/svg+xml");
      }

      RequestBody requestBody = RequestBody.fromInputStream(stream, fileStream.length);

      s3.putObject(requestBuilder.build(), requestBody);

      // get the URI of the stored file

      String s3Uri = String.format("s3://%s/%s", bucketName, keyName);

      return new ResourceUri(s3Uri);

    } catch (Exception e) {
      throw new PermanentException("Error storing file", e);
    }
  }

  /**
   * Retrieves a document from the specified Amazon S3 bucket.
   *
   * @param bucketName     The name of the bucket where the document is stored.
   * @param keyName        The key name under which the document is stored.
   * @return The byte array of the document.
   */
  public byte[] getFile(String bucketName, String keyName) {
    try {

      GetObjectRequest requestBody =
          GetObjectRequest.builder().bucket(bucketName).key(keyName).build();

      var res = s3.getObject(requestBody);

      return res.readAllBytes();
    } catch (Exception e) {
      throw new PermanentException("Error getting file", e);
    }
  }

  /**
   * Check if the file exists.
   */
  public boolean fileExists(String bucketName, String keyName) {
    try {
      HeadObjectRequest objectRequest =
          HeadObjectRequest.builder().bucket(bucketName).key(keyName).build();
      s3.headObject(objectRequest);
      return true;
    } catch (NoSuchKeyException | NoSuchBucketException e) {
      return false;
    } catch (Exception e) {
      throw new PermanentException("Error checking if file exists", e);
    }
  }

  @Override
  public FileDownloadLink getDownloadLink(
      String bucketName, String keyName, Duration validityDuration) throws PermanentException {
    return createDownloadLink(bucketName, keyName, validityDuration, null);
  }

  @Override
  public FileDownloadLink getDownloadLink(
      String bucketName, String keyName, Duration validityDuration, String fileName)
      throws PermanentException {
    return createDownloadLink(bucketName, keyName, validityDuration, fileName);
  }

  /**
   * Creates a download link for the specified file.
   *
   * @param bucketName The name of the bucket where the file is stored.
   * @param keyName The key of the file in the bucket.
   * @param validityDuration The duration for which the link will be valid.
   * @param fileName The name of the file.
   * @return The download link.
   */
  private FileDownloadLink createDownloadLink(
      String bucketName, String keyName, Duration validityDuration, String fileName) {
    var objectRequest = GetObjectRequest.builder().bucket(bucketName).key(keyName);

    if (fileName != null) {
      objectRequest = objectRequest.responseContentDisposition("attachment; filename=" + fileName);
    }

    var objectRequestBuilt = objectRequest.build();

    GetObjectPresignRequest presignRequest =
        GetObjectPresignRequest.builder()
            .signatureDuration(validityDuration) // The URL will expire in 10 minutes.
            .getObjectRequest(objectRequestBuilt)
            .build();

    PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);

    var linkDetail =
        new FileDownloadLink(
            presignedRequest.url().toExternalForm(), Instant.now().plus(validityDuration));

    return linkDetail;
  }

  /**
   * Deletes a specified file from S3.
   *
   * @param bucketName The name of the bucket where the file is stored.
   * @param keyName The key of the file in the bucket.
   *
   */
  public boolean deleteFile(String bucketName, String keyName) {

    try {
      var objectRequest = DeleteObjectRequest.builder().bucket(bucketName).key(keyName);
      var objectRequestBuilt = objectRequest.build();
      var response = s3.deleteObject(objectRequestBuilt);
      if (response.deleteMarker()) {
        return true;
      }
      return false;

    } catch (Exception e) {
      throw new PermanentException("Error deleting file:", e);
    }
  }

  /**
   * Archives a specified file from S3 by copying the original to a deeper storage option
   * and then deletes the original.
   *
   * @param bucketName The name of the bucket where the file is stored.
   * @param keyName The key of the file in the bucket.
   *
   */
  public boolean archiveFile(String bucketName, String keyName) throws PermanentException {
    try {
      // add check to see if object exists
      // 1. Copy the object to itself with Glacier storage class
      CopyObjectRequest copyRequest =
          CopyObjectRequest.builder()
              .sourceBucket(bucketName)
              .sourceKey(keyName)
              .destinationBucket(bucketName)
              .destinationKey(keyName)
              .storageClass(StorageClass.GLACIER) // Change to StorageClass.DEEP_ARCHIVE if needed
              .metadataDirective(MetadataDirective.COPY) // Preserve metadata
              .build();

      s3.copyObject(copyRequest);

      // 2. Delete the original object
      DeleteObjectRequest deleteRequest =
          DeleteObjectRequest.builder().bucket(bucketName).key(keyName).build();

      s3.deleteObject(deleteRequest);

      return true; // Successfully archived the file

    } catch (Exception e) {
      throw new PermanentException("Error archiving file:", e);
    }
  }
}
