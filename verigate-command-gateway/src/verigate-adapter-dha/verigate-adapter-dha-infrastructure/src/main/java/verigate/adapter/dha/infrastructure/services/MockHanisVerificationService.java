/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.infrastructure.services;

import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.dha.domain.models.HanisPersonDetails;
import verigate.adapter.dha.domain.services.HanisVerificationService;

/**
 * Mock implementation of {@link HanisVerificationService} for testing and development.
 * Returns realistic NPR data for known test ID numbers.
 */
public class MockHanisVerificationService implements HanisVerificationService {

  private static final Logger logger =
      LoggerFactory.getLogger(MockHanisVerificationService.class);

  // 1x1 transparent PNG for mock photo
  private static final byte[] MOCK_PHOTO = Base64.getDecoder().decode(
      "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==");

  @Override
  public HanisPersonDetails verifyIdentity(String idNumber, String siteId, String workstationId) {
    logger.info("Mock HANIS verification for ID: ...{}",
        idNumber != null && idNumber.length() >= 4
            ? idNumber.substring(idNumber.length() - 4) : "***");

    if (idNumber == null || idNumber.length() != 13) {
      return HanisPersonDetails.error(10004); // INVALID_IDN
    }

    // Deceased scenario: IDs ending in 9
    if (idNumber.endsWith("9")) {
      return HanisPersonDetails.success(
          "TXN-MOCK-" + System.currentTimeMillis(),
          "JOHN", "DOE",
          true, "2015/01/15", "001",
          true, false,
          "2023/06/15", "Married", "2010/03/20",
          MOCK_PHOTO, true, true, "ZA");
    }

    // Blocked scenario: IDs ending in 8
    if (idNumber.endsWith("8")) {
      return HanisPersonDetails.success(
          "TXN-MOCK-" + System.currentTimeMillis(),
          "BLOCKED", "PERSON",
          false, null, "002",
          false, true,
          null, "Single", null,
          null, true, true, "ZA");
    }

    // Not found scenario: IDs ending in 7
    if (idNumber.endsWith("7")) {
      return HanisPersonDetails.notFound();
    }

    // Default: successful verification
    String gender = extractGender(idNumber);
    return HanisPersonDetails.success(
        "TXN-MOCK-" + System.currentTimeMillis(),
        gender.equals("Male") ? "THABO" : "NOMSA",
        "MOKWENA",
        true, "2018/05/10", "000",
        false, false,
        null, "Married", "2016/09/14",
        MOCK_PHOTO, true, true, "ZA");
  }

  private String extractGender(String idNumber) {
    try {
      int genderDigits = Integer.parseInt(idNumber.substring(6, 10));
      return genderDigits >= 5000 ? "Male" : "Female";
    } catch (Exception e) {
      return "Unknown";
    }
  }
}
