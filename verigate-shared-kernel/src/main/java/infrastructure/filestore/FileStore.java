/*
 * VeriGate (c) 2024 - 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.filestore;

import domain.exceptions.PermanentException;
import infrastructure.documents.ResourceUri;
import java.time.Duration;

/**
 * The FileStore interface provides methods for storing and retrieving files.
 */
public interface FileStore {

  public ResourceUri storeFile(String bucketName, String keyName, byte[] fileStream)
      throws PermanentException;

  public byte[] getFile(String bucketName, String keyName) throws PermanentException;

  public FileDownloadLink getDownloadLink(
      String bucketName, String keyName, Duration validityDuration) throws PermanentException;

  public FileDownloadLink getDownloadLink(
      String bucketName, String keyName, Duration validityDuration, String fileName)
      throws PermanentException;

  public boolean deleteFile(String bucketName, String keyName) throws PermanentException;

  public boolean archiveFile(String bucketName, String keyName) throws PermanentException;
}
