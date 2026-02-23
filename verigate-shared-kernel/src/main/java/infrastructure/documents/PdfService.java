/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.documents;

import infrastructure.filestore.FileDownloadLink;
import java.time.Duration;

/**
 * The PdfService interface provides methods for loading templates, generating documents,
 * storing documents, and retrieving document download links.
 */
public interface PdfService {

  public byte[] generatePdf(
      DocumentType documentType,
      TemplateId templateIdentifier,
      String documentContentJsonString,
      String password);

  public byte[] generatePdf(
      DocumentType documentType, TemplateId templateIdentifier, String documentContentJsonString);

  public DocumentStoredResponse storePdf(
      DocumentType documentType, DocumentRef documentRef, byte[] document);

  public FileDownloadLink createPdfDownloadLink(DocumentId documentId, Duration validityDuration);
}
