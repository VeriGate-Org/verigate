/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.infrastructure.bulk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import verigate.adapter.dha.domain.models.BillingGroupSelection;

/**
 * Builds zipped CSV files for HANIS bulk verification uploads.
 * The CSV format follows the HANIS Bulk Non-Bio Service Specification.
 */
public class BulkCsvBuilder {

  private static final String CSV_FILENAME = "bulk_request.csv";

  /**
   * Builds a zipped CSV file containing the billing group header and ID numbers.
   *
   * @param groups    the billing group selection
   * @param idNumbers the list of 13-digit SA ID numbers
   * @return the zipped CSV as a byte array
   */
  public byte[] buildZippedCsv(BillingGroupSelection groups, List<String> idNumbers) {
    StringBuilder csv = new StringBuilder();

    // Header line: billing group selection
    csv.append(groups.toHeaderString()).append("\n");

    // One ID number per line
    for (String idNumber : idNumbers) {
      csv.append(idNumber.trim()).append("\n");
    }

    return zipContent(csv.toString());
  }

  private byte[] zipContent(String csvContent) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ZipOutputStream zos = new ZipOutputStream(baos)) {

      ZipEntry entry = new ZipEntry(CSV_FILENAME);
      zos.putNextEntry(entry);
      zos.write(csvContent.getBytes(StandardCharsets.UTF_8));
      zos.closeEntry();
      zos.finish();

      return baos.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException("Failed to create zipped CSV", e);
    }
  }
}
