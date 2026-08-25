/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.adapter.document.infrastructure.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import domain.exceptions.PermanentException;
import java.io.ByteArrayOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;

class PdfBoxPageRasterizerTest {

  private final PdfBoxPageRasterizer rasterizer = new PdfBoxPageRasterizer();

  @Test
  void shouldRasterizeFirstPageOfValidPdfToPng() throws Exception {
    byte[] pdfBytes = buildOnePagePdf("CIPC Registration Certificate");

    byte[] pngBytes = rasterizer.rasterizeFirstPage(pdfBytes);

    assertTrue(pngBytes.length > 0, "Expected non-empty PNG output");
    // PNG files start with an 8-byte magic header: 89 50 4E 47 0D 0A 1A 0A
    assertEquals((byte) 0x89, pngBytes[0]);
    assertEquals('P', pngBytes[1]);
    assertEquals('N', pngBytes[2]);
    assertEquals('G', pngBytes[3]);
  }

  @Test
  void shouldRasterizeOnlyFirstPageOfMultiPagePdf() throws Exception {
    byte[] pdfBytes = buildTwoPagePdf();

    byte[] pngBytes = rasterizer.rasterizeFirstPage(pdfBytes);

    assertTrue(pngBytes.length > 0, "Expected non-empty PNG output for first page");
  }

  @Test
  void shouldThrowPermanentExceptionForNullBytes() {
    assertThrows(PermanentException.class, () -> rasterizer.rasterizeFirstPage(null));
  }

  @Test
  void shouldThrowPermanentExceptionForEmptyBytes() {
    assertThrows(PermanentException.class, () -> rasterizer.rasterizeFirstPage(new byte[0]));
  }

  @Test
  void shouldThrowPermanentExceptionForCorruptPdfBytes() {
    byte[] garbage = "not a real pdf file".getBytes();
    assertThrows(PermanentException.class, () -> rasterizer.rasterizeFirstPage(garbage));
  }

  private byte[] buildOnePagePdf(String text) throws Exception {
    try (PDDocument document = new PDDocument()) {
      PDPage page = new PDPage();
      document.addPage(page);
      try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
        stream.beginText();
        stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        stream.newLineAtOffset(50, 700);
        stream.showText(text);
        stream.endText();
      }
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      document.save(output);
      return output.toByteArray();
    }
  }

  private byte[] buildTwoPagePdf() throws Exception {
    try (PDDocument document = new PDDocument()) {
      document.addPage(new PDPage());
      document.addPage(new PDPage());
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      document.save(output);
      return output.toByteArray();
    }
  }
}
