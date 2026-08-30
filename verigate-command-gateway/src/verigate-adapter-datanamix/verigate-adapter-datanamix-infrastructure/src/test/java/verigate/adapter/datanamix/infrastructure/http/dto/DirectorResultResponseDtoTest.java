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
 * {@code POST /v1/cipc/director-result}, including the full rich consumer-bureau
 * payload (fraud indicators, property, employment history, etc.) that this integration
 * deliberately does NOT model — proves {@code @JsonIgnoreProperties(ignoreUnknown = true)}
 * actually lets deserialization succeed despite all that extra data, and that the one
 * array we do care about ({@code ConsumerDirectorshipLink}) maps correctly.
 */
class DirectorResultResponseDtoTest {

  private static final String EXAMPLE_RESPONSE = """
      {
        "Header": {
          "SearchDate": "2025-06-26T07:10:33.8481987Z",
          "CreatedUserId": 1,
          "ReportName": "1213161514082",
          "ReportReference": "DX-12345-67890",
          "ClientReference": "TEST-API",
          "ReportType": "COMMERCIAL"
        },
        "Result": {
          "DirectorResults": {
            "ConsumerDetail": {
              "FirstName": "John",
              "Surname": "Doe",
              "IDNumber": "1234567890123"
            },
            "ConsumerFraudIndicatorsSummary": {
              "HomeAffairsVerificationYN": "Y",
              "HomeAffairsDeceasedStatus": "",
              "EmployerFraudVerificationYN": "N"
            },
            "ConsumerDirectorSummary": {
              "NumberOfCompanyDirector": "1"
            },
            "ConsumerEnquiryHistory": [
              {
                "EnquiryDate": "2023-10-19",
                "SubscriberName": "Retail Bank"
              }
            ],
            "ConsumerDirectorshipLink": [
              {
                "DirectorDesignationDescription": "",
                "AppointmentDate": "2020-01-01",
                "CommercialName": "Tech Solutions Ltd",
                "CommercialStatus": "Active",
                "RegistrationNumber": "2020/123456/07",
                "TelephoneNumber": "0111234567",
                "SICDescription": "Unknown data",
                "DirectorStatus": "Active",
                "PhysicalAddress": "123 Main St, Bryanston, 2191",
                "PostalAddress": "PO Box 123, Bryanston, 2191"
              }
            ],
            "ConsumerPropertyInformation": [
              {
                "AuthorityName": "Deeds Office",
                "BuyerName": "John Doe"
              }
            ],
            "ConsumerEmploymentHistory": [
              {
                "EmployerDetail": "Tech Solutions Ltd",
                "Designation": "Software Engineer"
              }
            ]
          }
        },
        "PDFReport": "Base64",
        "Success": true,
        "Messages": ["Success"],
        "ResponseCode": 200
      }
      """;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void shouldDeserializeTheDocumentedExampleResponseIgnoringUnmappedFields() throws Exception {
    DirectorResultResponseDto response =
        objectMapper.readValue(EXAMPLE_RESPONSE, DirectorResultResponseDto.class);

    assertTrue(response.success());

    var links = response.result().directorResults().consumerDirectorshipLink();
    assertEquals(1, links.size());

    ConsumerDirectorshipLinkDto link = links.get(0);
    assertEquals("Tech Solutions Ltd", link.commercialName());
    assertEquals("2020/123456/07", link.registrationNumber());
    assertEquals("Active", link.commercialStatus());
    assertEquals("Active", link.directorStatus());
    assertEquals("2020-01-01", link.appointmentDate());
  }
}
