/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.infrastructure.bulk;

import java.io.ByteArrayInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import verigate.adapter.dha.domain.models.BillingGroupSelection;
import verigate.adapter.dha.domain.models.BulkVerificationResult;

/**
 * Parses zipped CSV result files from HANIS bulk verification responses.
 */
public class BulkResultParser {

  /**
   * Parses a zipped CSV result file into a list of bulk verification results.
   *
   * @param zipData the zipped CSV data
   * @param groups  the billing groups that were requested (determines CSV column layout)
   * @return the list of per-ID verification results
   */
  public List<BulkVerificationResult> parseZippedCsv(byte[] zipData, BillingGroupSelection groups) {
    List<String> csvLines = unzipToLines(zipData);
    List<BulkVerificationResult> results = new ArrayList<>();

    for (String line : csvLines) {
      if (line.isBlank()) continue;

      String[] fields = line.split(",", -1);
      if (fields.length < 2) continue;

      // First field is always the ID number
      String idNumber = fields[0].trim();
      if (idNumber.length() != 13) continue; // Skip header or invalid lines

      results.add(parseResultLine(fields));
    }

    return results;
  }

  private BulkVerificationResult parseResultLine(String[] fields) {
    // Map CSV columns to result fields based on the HANIS bulk response spec
    // The exact column layout depends on which billing groups were selected
    return new BulkVerificationResult(
        safeGet(fields, 0),   // idNumber
        safeGet(fields, 1),   // names
        safeGet(fields, 2),   // surname
        safeGet(fields, 3),   // gender
        safeGet(fields, 4),   // dateOfBirth
        safeGet(fields, 5),   // birthCountry
        safeGet(fields, 6),   // citizenStatus
        safeGet(fields, 7),   // nationality
        safeBool(fields, 8),  // smartCardIssued
        safeGet(fields, 9),   // idCardIssueDate
        safeBool(fields, 10), // idBookIssued
        safeGet(fields, 11),  // idBookIssueDate
        safeBool(fields, 12), // idBlocked
        safeGet(fields, 13),  // maritalStatus
        safeGet(fields, 14),  // maidenName
        safeGet(fields, 15),  // marriageDate
        safeGet(fields, 16),  // divorceDate
        safeGet(fields, 17),  // dateOfDeath
        safeGet(fields, 18),  // deathPlace
        safeGet(fields, 19),  // causeOfDeath
        safeInt(fields, 20)   // errorCode
    );
  }

  private List<String> unzipToLines(byte[] zipData) {
    List<String> lines = new ArrayList<>();

    try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipData))) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        if (!entry.isDirectory()) {
          BufferedReader reader = new BufferedReader(
              new InputStreamReader(zis, StandardCharsets.UTF_8));
          String line;
          while ((line = reader.readLine()) != null) {
            lines.add(line);
          }
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to unzip bulk result data", e);
    }

    return lines;
  }

  private String safeGet(String[] fields, int index) {
    return index < fields.length ? fields[index].trim() : "";
  }

  private boolean safeBool(String[] fields, int index) {
    String val = safeGet(fields, index);
    return "Y".equalsIgnoreCase(val) || "true".equalsIgnoreCase(val) || "1".equals(val);
  }

  private int safeInt(String[] fields, int index) {
    String val = safeGet(fields, index);
    if (val.isBlank()) return 0;
    try { return Integer.parseInt(val); } catch (NumberFormatException e) { return 0; }
  }
}
