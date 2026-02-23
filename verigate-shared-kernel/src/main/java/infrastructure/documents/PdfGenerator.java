/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.documents;

import java.io.InputStream;

/**
 * The PdfGenerator interface provides methods for
 * generating documents based on document templates and content.
 */
public interface PdfGenerator {
  /**
   * Generates a document based on the provided document template,
   * document content JSON, and password.
   *
   * @param documentTemplate The input stream of the document template.
   * @param documentContentJson The JSON string representing the document content.
   * @param password The password for the document (optional).
   * @return The input stream of the generated document.
   */
  public InputStream generateDocument(
      InputStream documentTemplate, String documentContentJson, String password);

  /**
   * Generates a document based on the provided document template and document content JSON.
   *
   * @param documentTemplate The input stream of the document template.
   * @param documentContentJson The JSON string representing the document content.
   * @return The input stream of the generated document.
   */
  public InputStream generateDocument(InputStream documentTemplate, String documentContentJson);
}
