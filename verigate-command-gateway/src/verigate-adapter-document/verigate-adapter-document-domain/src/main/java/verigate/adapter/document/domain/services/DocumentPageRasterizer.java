/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.domain.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;

/**
 * Port for converting a PDF document's first page into a raster image, so it can be sent
 * through the same AI vision analysis pipeline as an image upload. Claude's vision API (see
 * {@code BedrockVisionService}) only accepts raster image media types (jpeg/png/gif/webp), not
 * raw PDF bytes.
 *
 * <p>Only the first page is rasterized — CIPC registration certificates are effectively
 * single-page documents, and the fields this adapter extracts (company name, registration
 * number, status, directors) are on that page. Genuinely multi-page analysis is a known
 * limitation, not handled here.
 */
public interface DocumentPageRasterizer {

  /**
   * Rasterizes the first page of a PDF document to PNG image bytes.
   *
   * @param pdfBytes the raw PDF file bytes
   * @return PNG-encoded image bytes for the first page
   * @throws TransientException if a temporary error occurs that may be retried
   * @throws PermanentException if the PDF is empty, corrupt, or otherwise unreadable
   */
  byte[] rasterizeFirstPage(byte[] pdfBytes) throws TransientException, PermanentException;
}
