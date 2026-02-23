/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.documents;

import com.google.inject.Inject;
import crosscutting.environment.Environment;
import domain.exceptions.PermanentException;
import infrastructure.filestore.FileDownloadLink;
import infrastructure.filestore.FileStore;
import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.util.logging.Logger;

/**
 * This class represents a document service implementation for Digisure.
 * It provides methods for loading templates, generating documents, storing documents,
 * and retrieving document download links.
 */
public final class DigisurePdfService implements PdfService {
  private final PdfGenerator pdfGenerator;
  private final FileStore fileStore;
  private final String templateBucketName;
  private final String outputBucketName;

  private static final Logger logger = Logger.getLogger(DigisurePdfService.class.getName());

  /**
   * Constructs a new DigisurePdfService instance with the specified PdfGenerator.
   *
   * @param pdfGenerator The PdfGenerator instance to use for generating documents.
   */
  @Inject
  public DigisurePdfService(
      PdfGenerator pdfGenerator, FileStore fileStore, Environment environment) {
    this.pdfGenerator = pdfGenerator;
    this.fileStore = fileStore;

    this.templateBucketName = environment.get("DOCUMENTS_BUCKET");
    this.outputBucketName = environment.get("DOCUMENTS_BUCKET");
  }

  private byte[] loadTemplate(TemplateId templateIdentifier) {
    var file = fileStore.getFile(templateBucketName, templateIdentifier.value());
    return file;
  }

  @Override
  public byte[] generatePdf(
      DocumentType documentType, TemplateId templateIdentifier, String documentContentJsonString) {
    try {
      var documentTemplate = loadTemplate(templateIdentifier);

      var stream =
          pdfGenerator.generateDocument(
              new ByteArrayInputStream(documentTemplate), documentContentJsonString);

      var bytes = stream.readAllBytes();

      return bytes;
    } catch (Exception e) {
      throw new PermanentException("Error generating PDF");
    }
  }

  @Override
  public byte[] generatePdf(
      DocumentType documentType,
      TemplateId templateIdentifier,
      String documentContentJsonString,
      String password) {
    try {
      var documentTemplate = loadTemplate(templateIdentifier);
      var stream =
          pdfGenerator.generateDocument(
              new ByteArrayInputStream(documentTemplate), documentContentJsonString, password);
      var bytes = stream.readAllBytes();

      return bytes;
    } catch (Exception e) {
      logger.warning(e.getMessage());
      throw new PermanentException("Error generating PDF");
    }
  }

  @Override
  public DocumentStoredResponse storePdf(
      DocumentType documentType, DocumentRef documentRef, byte[] document) {

    var key = documentType.toString() + "/" + documentRef.value() + ".pdf";

    var resourceUri = fileStore.storeFile(outputBucketName, key, document);

    return new DocumentStoredResponse(new DocumentId(key), resourceUri);
  }

  @Override
  public FileDownloadLink createPdfDownloadLink(DocumentId documentId, Duration validityDuration) {
    return fileStore.getDownloadLink(outputBucketName, documentId.value(), validityDuration);
  }
}
