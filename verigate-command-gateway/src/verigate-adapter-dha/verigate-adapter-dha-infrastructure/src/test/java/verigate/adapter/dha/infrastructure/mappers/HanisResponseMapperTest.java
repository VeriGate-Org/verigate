/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.infrastructure.mappers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verigate.adapter.dha.domain.models.CitizenshipStatus;
import verigate.adapter.dha.domain.models.HanisPersonDetails;
import verigate.adapter.dha.domain.models.IdVerificationStatus;
import verigate.adapter.dha.domain.models.IdentityVerificationResponse;
import verigate.adapter.dha.domain.models.VitalStatus;

class HanisResponseMapperTest {

  private HanisResponseMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new HanisResponseMapper();
  }

  @Test
  void mapToVerificationResponse_successfulVerification() {
    HanisPersonDetails details = HanisPersonDetails.success(
        "TXN001", "THABO", "MOKWENA", true, "2018/05/10", "000",
        false, false, null, "Married", "2016/09/14",
        new byte[]{1, 2, 3}, true, true, "ZA");

    IdentityVerificationResponse response = mapper.mapToVerificationResponse(details);

    assertEquals(IdVerificationStatus.VERIFIED, response.status());
    assertEquals(CitizenshipStatus.CITIZEN, response.citizenshipStatus());
    assertEquals(VitalStatus.ALIVE, response.vitalStatus());
    assertTrue(response.matchDetails().contains("THABO"));
    assertTrue(response.matchDetails().contains("MOKWENA"));
    assertTrue(response.matchDetails().contains("Smart Card: Yes"));
    assertTrue(response.matchDetails().contains("Birth Country: ZA"));
  }

  @Test
  void mapToVerificationResponse_deceasedPerson() {
    HanisPersonDetails details = HanisPersonDetails.success(
        "TXN002", "JOHN", "DOE", true, "2015/01/15", "001",
        true, false, "2023/06/15", "Married", "2010/03/20",
        null, true, true, "ZA");

    IdentityVerificationResponse response = mapper.mapToVerificationResponse(details);

    assertEquals(IdVerificationStatus.DECEASED, response.status());
    assertEquals(VitalStatus.DECEASED, response.vitalStatus());
    assertTrue(response.matchDetails().contains("deceased"));
    assertTrue(response.matchDetails().contains("2023/06/15"));
  }

  @Test
  void mapToVerificationResponse_blockedId() {
    HanisPersonDetails details = HanisPersonDetails.success(
        "TXN003", "BLOCKED", "PERSON", false, null, "002",
        false, true, null, "Single", null,
        null, true, true, "ZA");

    IdentityVerificationResponse response = mapper.mapToVerificationResponse(details);

    assertEquals(IdVerificationStatus.BLOCKED, response.status());
    assertTrue(response.matchDetails().contains("blocked"));
  }

  @Test
  void mapToVerificationResponse_notFoundError() {
    HanisPersonDetails details = HanisPersonDetails.notFound();

    IdentityVerificationResponse response = mapper.mapToVerificationResponse(details);

    assertEquals(IdVerificationStatus.NOT_FOUND, response.status());
  }

  @Test
  void mapToVerificationResponse_nullDetails() {
    IdentityVerificationResponse response = mapper.mapToVerificationResponse(null);

    assertEquals(IdVerificationStatus.NOT_FOUND, response.status());
  }

  @Test
  void mapToVerificationResponse_nonSaCitizen() {
    HanisPersonDetails details = HanisPersonDetails.success(
        "TXN004", "FOREIGN", "NATIONAL", false, null, "003",
        false, false, null, "Single", null,
        null, true, true, "UK");

    IdentityVerificationResponse response = mapper.mapToVerificationResponse(details);

    assertEquals(IdVerificationStatus.VERIFIED, response.status());
    assertEquals(CitizenshipStatus.PERMANENT_RESIDENT, response.citizenshipStatus());
  }

  @Test
  void mapToVerificationResponse_unknownCitizenship() {
    HanisPersonDetails details = HanisPersonDetails.success(
        "TXN005", "UNKNOWN", "ORIGIN", false, null, "004",
        false, false, null, "Single", null,
        null, true, true, null);

    IdentityVerificationResponse response = mapper.mapToVerificationResponse(details);

    assertEquals(IdVerificationStatus.VERIFIED, response.status());
    assertEquals(CitizenshipStatus.UNKNOWN, response.citizenshipStatus());
  }
}
