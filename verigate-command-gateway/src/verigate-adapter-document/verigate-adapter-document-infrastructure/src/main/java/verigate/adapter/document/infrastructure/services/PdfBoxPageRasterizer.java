/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.infrastructure.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.document.domain.services.DocumentPageRasterizer;

/**
 * Renders a PDF document's first page to a PNG image using Apache PDFBox, so it can be sent
 * through the same Claude vision analysis pipeline as an image upload.
 */
public class PdfBoxPageRasterizer implements DocumentPageRasterizer {

  private static final Logger logger = LoggerFactory.getLogger(PdfBoxPageRasterizer.class);

  // 150 DPI balances OCR legibility against payload size for a Bedrock vision call.
  private static final float RENDER_DPI = 150f;
  private static final int FIRST_PAGE_INDEX = 0;
  private static final String IMAGE_FORMAT = "png";

  @Override
  public byte[] rasterizeFirstPage(byte[] pdfBytes) throws TransientException, PermanentException {
    if (pdfBytes == null || pdfBytes.length == 0) {
      throw new PermanentException("PDF bytes are empty; nothing to rasterize");
    }

    try (PDDocument document = Loader.loadPDF(pdfBytes)) {
      if (document.getNumberOfPages() == 0) {
        throw new PermanentException("PDF has no pages");
      }

      PDFRenderer renderer = new PDFRenderer(document);
      BufferedImage image = renderer.renderImageWithDPI(FIRST_PAGE_INDEX, RENDER_DPI);

      ByteArrayOutputStream output = new ByteArrayOutputStream();
      boolean written = ImageIO.write(image, IMAGE_FORMAT, output);
      if (!written) {
        throw new PermanentException("No suitable image writer found for PNG output");
      }

      byte[] pngBytes = output.toByteArray();
      logger.info(
          "Rasterized PDF first page to PNG: pages={}, pngSizeBytes={}",
          document.getNumberOfPages(), pngBytes.length);
      return pngBytes;

    } catch (PermanentException e) {
      throw e;
    } catch (IOException e) {
      // PDFBox throws IOException for both "not a valid PDF" (permanent) and genuine I/O
      // failures reading the byte source (which, since pdfBytes is already fully in memory,
      // in practice only happens for corrupt/malformed PDF content) -- treat as permanent.
      logger.error("Failed to rasterize PDF: {}", e.getMessage(), e);
      throw new PermanentException("Failed to rasterize PDF document: " + e.getMessage(), e);
    } catch (Exception e) {
      logger.error("Unexpected error rasterizing PDF: {}", e.getMessage(), e);
      throw new PermanentException("Unexpected error rasterizing PDF document", e);
    }
  }
}
