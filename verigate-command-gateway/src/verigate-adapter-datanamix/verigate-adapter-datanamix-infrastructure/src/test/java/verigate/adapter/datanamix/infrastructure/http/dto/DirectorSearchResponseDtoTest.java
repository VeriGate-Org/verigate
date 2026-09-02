/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.adapter.datanamix.infrastructure.http.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

/**
 * Deserialization test using the exact example response Datanamix's docs provide for
 * {@code POST /v1/cipc/director-search}, to catch field-name mismatches immediately.
 */
class DirectorSearchResponseDtoTest {

  private static final String EXAMPLE_RESPONSE = """
      {
        "DirectorSearchResults": [
          {
            "IDNumber": "1234567890123",
            "PassportNumber": "",
            "FirstInitial": "J",
            "FirstName": "John",
            "Surname": "Doe",
            "BirthDate": "1986-05-16T00:00:00",
            "Gender": "M",
            "EnquiryID": 123456,
            "EnquiryResultID": 654321
          }
        ],
        "Header": {
          "SearchDate": "2025-06-25T14:34:44.1836442Z",
          "CreatedUserId": 1,
          "ReportName": "XYZ Shoes",
          "ReportReference": "DX-1-1",
          "ClientReference": "TEST-API-CALL",
          "ReportType": "COMMERCIAL"
        },
        "Success": true,
        "Messages": ["Success"],
        "ResponseCode": 200
      }
      """;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void shouldDeserializeTheDocumentedExampleResponse() throws Exception {
    DirectorSearchResponseDto response =
        objectMapper.readValue(EXAMPLE_RESPONSE, DirectorSearchResponseDto.class);

    assertTrue(response.success());
    assertEquals(1, response.directorSearchResults().size());

    DirectorSearchResultDto match = response.directorSearchResults().get(0);
    assertEquals("1234567890123", match.idNumber());
    assertEquals("John", match.firstName());
    assertEquals("Doe", match.surname());
    assertEquals(123456L, match.enquiryId());
    assertEquals(654321L, match.enquiryResultId());
  }
}
