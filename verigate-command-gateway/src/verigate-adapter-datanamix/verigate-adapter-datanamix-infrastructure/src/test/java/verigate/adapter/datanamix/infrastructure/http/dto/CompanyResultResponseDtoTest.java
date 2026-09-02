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
 * {@code POST /v1/cipc/company-result} ("CIPC Result Sandbox"), including several rich
 * blocks (auditor info, active/inactive summaries, addresses, principal information)
 * this integration deliberately does NOT model — proves
 * {@code @JsonIgnoreProperties(ignoreUnknown = true)} lets deserialization succeed
 * despite all that extra data, and that {@code Success}/{@code Messages}/
 * {@code ResponseCode} really do nest inside {@code CIPCResult} (not at the top level,
 * unlike the director-result response) as the doc example shows.
 */
class CompanyResultResponseDtoTest {

  private static final String EXAMPLE_RESPONSE = """
      {
        "Header": {
          "SearchDate": "2025-03-20T14:20:21.019713+02:00",
          "CreatedUserId": 123,
          "ReportName": "XYZ Shoes (Pty) Ltd - (K2010/123456/07)",
          "ReportReference": "DX-1234-3216549",
          "ClientReference": "TEST-API-CALL",
          "ReportType": "COMMERCIAL"
        },
        "CIPCResult": {
          "CommercialDirectorInformation": [
            {
              "IDNumber": "1234567890123",
              "Initials": "J",
              "FirstName": "John",
              "Surname": "Doe",
              "FullName": "John Doe",
              "BirthDate": "1980-01-01",
              "DirectorStatusCode": "Active",
              "AppointmentDate": "2010-01-01",
              "DirectorStatusDate": "2010-01-01",
              "MemberSize": "1",
              "PhysicalAddress": "123 Main Street, Sandton, 2196"
            },
            {
              "IDNumber": "1234567890123",
              "Initials": "T",
              "FirstName": "Tom",
              "Surname": "Doe",
              "FullName": "Tom Doe",
              "BirthDate": "1980-01-01",
              "DirectorStatusCode": "Resigned",
              "AppointmentDate": "2010-01-01",
              "DirectorStatusDate": "2010-01-01",
              "MemberSize": "1",
              "PhysicalAddress": "123 Main Street, Sandton, 2196"
            }
          ],
          "ActiveDirectorSummary": [
            {
              "IDNumber": "1234567890123",
              "Initials": "J",
              "FirstName": "John",
              "Surname": "Doe",
              "DirectorStatus": "Active"
            }
          ],
          "InactiveDirectorSummary": [],
          "CommercialAddressInformation": [
            {
              "AddressType": "Physical",
              "Address1": "123 Main Street",
              "PostalCode": "2196",
              "LastUpdatedDate": "2021-01-01"
            }
          ],
          "CommercialBusinessInformation": {
            "BusinessName": "XYZ Shoes (Pty) Ltd",
            "TradeName": "XYZ Shoes",
            "PreviousBussinessName": "ABC Shoes",
            "RegistrationNumber": "K2010/123456/07",
            "BusinessStartDate": "2010-01-01",
            "RegistrationNumberOld": "K2010/123456/07",
            "BusinessStatus": "In Business",
            "BusinessType": "Private Company",
            "TaxNumber": "9836001223",
            "RegistrationDate": "2010-01-01",
            "BusinessDescription": "Shoe Manufacturer",
            "DirectorCount": "2",
            "PhysicalAddress": "123 Main Street, Sandton, 2196",
            "PostalAddress": "PO Box 123, Sandton, 2196"
          },
          "CommercialActivePrincipalInformation": [
            {
              "IDNumber": "1234567890123",
              "FirstName": "John",
              "Surname": "Doe"
            }
          ],
          "CommercialAuditorInformation": [
            {
              "AuditorName": "Super auditors",
              "ProfessionNumber": "123456"
            }
          ],
          "Header": {
            "SearchDate": "2025-03-20T14:20:21.0192556+02:00",
            "CreatedUserId": 1,
            "ReportName": "XYZ Shoes - K2010/123456/07",
            "ReportReference": "DX-1-1",
            "ClientReference": "Client Reference",
            "ReportType": "COMMERCIAL"
          },
          "Success": true,
          "Messages": ["Sandbox data"],
          "ResponseCode": 0
        }
      }
      """;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void shouldDeserializeTheDocumentedExampleResponseIgnoringUnmappedFields() throws Exception {
    CompanyResultResponseDto response =
        objectMapper.readValue(EXAMPLE_RESPONSE, CompanyResultResponseDto.class);

    assertTrue(response.cipcResult().success());
    assertEquals(0, response.cipcResult().responseCode());

    var business = response.cipcResult().commercialBusinessInformation();
    assertEquals("XYZ Shoes (Pty) Ltd", business.businessName());
    assertEquals("XYZ Shoes", business.tradeName());
    assertEquals("K2010/123456/07", business.registrationNumber());
    assertEquals("In Business", business.businessStatus());
    assertEquals("Private Company", business.businessType());
    assertEquals("2010-01-01", business.registrationDate());

    var directors = response.cipcResult().commercialDirectorInformation();
    assertEquals(2, directors.size());
    assertEquals("John Doe", directors.get(0).fullName());
    assertEquals("Active", directors.get(0).directorStatusCode());
    assertEquals("Tom Doe", directors.get(1).fullName());
    assertEquals("Resigned", directors.get(1).directorStatusCode());
  }
}
