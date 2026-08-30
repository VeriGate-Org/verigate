/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.adapter.datanamix.infrastructure.http.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

/**
 * Deserialization test using the exact live curl example response Datanamix's formal
 * API reference gives for {@code POST /v1/cipc/company-search}, to catch field-name
 * mismatches immediately.
 */
class CompanySearchResponseDtoTest {

  private static final String EXAMPLE_RESPONSE = """
      {
        "CommercialDetails": [
          {
            "RegistrationNumber": "2010/123456/07",
            "BusinessName": "XYZ Shoes (Pty) Ltd",
            "EnquiryID": 234567,
            "EnquiryResultID": 876543
          },
          {
            "RegistrationNumber": "2010/234567/07",
            "BusinessName": "XYZ Shoes",
            "EnquiryID": 123456,
            "EnquiryResultID": 987654
          },
          {
            "RegistrationNumber": "2010/323456/07",
            "BusinessName": "XYZ Shoe",
            "EnquiryID": 923456,
            "EnquiryResultID": 187654
          }
        ],
        "Header": {
          "SearchDate": "2026-04-30T14:32:11.2852681+00:00",
          "CreatedUserId": 1,
          "ReportName": "XYZ Shoes",
          "ReportReference": "DX-1-1",
          "ClientReference": "Client Reference",
          "ReportType": "COMMERCIAL"
        },
        "Success": true,
        "Messages": ["Sandbox data"],
        "ResponseCode": 0
      }
      """;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void shouldDeserializeTheDocumentedExampleResponse() throws Exception {
    CompanySearchResponseDto response =
        objectMapper.readValue(EXAMPLE_RESPONSE, CompanySearchResponseDto.class);

    assertTrue(response.success());
    assertEquals(0, response.responseCode());
    assertEquals(3, response.commercialDetails().size());

    CompanyMatchDto first = response.commercialDetails().get(0);
    assertEquals("2010/123456/07", first.registrationNumber());
    assertEquals("XYZ Shoes (Pty) Ltd", first.businessName());
    assertEquals(234567L, first.enquiryId());
    assertEquals(876543L, first.enquiryResultId());
  }

  @Test
  void shouldDeserializeTheAlternateFieldNamingFromTheOlderSandboxDocPage() throws Exception {
    // Datanamix's narrative "Sandbox" pages show RegistrationNo/Businessname instead of
    // RegistrationNumber/BusinessName -- confirming the @JsonAlias hedge actually works,
    // in case sandbox (or a future version) really does return this shape.
    String alternateShape = """
        {
          "CommercialDetails": [
            {
              "CommercialID": "1",
              "RegistrationNo": "2010/123456/07",
              "Businessname": "XYZ Shoes (Pty) Ltd",
              "EnquiryID": 234567,
              "EnquiryResultID": 876543
            }
          ],
          "Success": true,
          "Messages": ["Sandbox data"],
          "ResponseCode": 0
        }
        """;

    CompanySearchResponseDto response =
        objectMapper.readValue(alternateShape, CompanySearchResponseDto.class);

    CompanyMatchDto match = response.commercialDetails().get(0);
    assertEquals("2010/123456/07", match.registrationNumber());
    assertEquals("XYZ Shoes (Pty) Ltd", match.businessName());
  }
}
