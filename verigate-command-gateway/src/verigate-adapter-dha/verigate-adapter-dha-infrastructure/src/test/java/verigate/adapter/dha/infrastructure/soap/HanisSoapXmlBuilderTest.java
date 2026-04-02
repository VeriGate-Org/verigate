/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.infrastructure.soap;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verigate.adapter.dha.domain.models.HanisPersonDetails;

class HanisSoapXmlBuilderTest {

  private HanisSoapXmlBuilder xmlBuilder;

  @BeforeEach
  void setUp() {
    xmlBuilder = new HanisSoapXmlBuilder();
  }

  @Test
  void buildGetDataRequest_generatesValidSoapEnvelope() {
    String xml = xmlBuilder.buildGetDataRequest("9001015009087", "01/01/2025", "SITE01", "WS001");

    assertTrue(xml.contains("soap:Envelope"));
    assertTrue(xml.contains("soap:Body"));
    assertTrue(xml.contains("<GetData xmlns=\"http://HANIS.org/\">"));
    assertTrue(xml.contains("<IDN>9001015009087</IDN>"));
    assertTrue(xml.contains("<TransactionDate>01/01/2025</TransactionDate>"));
    assertTrue(xml.contains("<SiteId>SITE01</SiteId>"));
    assertTrue(xml.contains("<WrkStnId>WS001</WrkStnId>"));
  }

  @Test
  void buildGetDataRequest_escapesXmlSpecialCharacters() {
    String xml = xmlBuilder.buildGetDataRequest("1234567890123", "01/01/2025", "SITE&01", "WS<01>");

    assertTrue(xml.contains("SITE&amp;01"));
    assertTrue(xml.contains("WS&lt;01&gt;"));
  }

  @Test
  void parseGetDataResponse_parsesSuccessResponse() {
    String soapXml = buildSuccessResponse(
        "0", "TXN001", "THABO", "MOKWENA",
        "true", "2018/05/10", "000",
        "false", "false", "", "Married", "2016/09/14",
        "", "true", "true", "ZA");

    HanisPersonDetails details = xmlBuilder.parseGetDataResponse(soapXml);

    assertTrue(details.isSuccess());
    assertEquals(0, details.error());
    assertEquals("TXN001", details.tranNo());
    assertEquals("THABO", details.name());
    assertEquals("MOKWENA", details.surname());
    assertTrue(details.smartCardIssued());
    assertEquals("2018/05/10", details.idIssueDate());
    assertFalse(details.deadIndicator());
    assertFalse(details.idnBlocked());
    assertEquals("Married", details.maritalStatus());
    assertEquals("2016/09/14", details.dateOfMarriage());
    assertTrue(details.onHanis());
    assertTrue(details.onNpr());
    assertEquals("ZA", details.birthPlaceCountryCode());
  }

  @Test
  void parseGetDataResponse_parsesErrorResponse() {
    String soapXml = buildErrorResponse("800");

    HanisPersonDetails details = xmlBuilder.parseGetDataResponse(soapXml);

    assertFalse(details.isSuccess());
    assertEquals(800, details.error());
    assertNull(details.name());
  }

  @Test
  void parseGetDataResponse_parsesDeceasedResponse() {
    String soapXml = buildSuccessResponse(
        "0", "TXN002", "JOHN", "DOE",
        "true", "2015/01/15", "001",
        "true", "false", "2023/06/15", "Married", "2010/03/20",
        "", "true", "true", "ZA");

    HanisPersonDetails details = xmlBuilder.parseGetDataResponse(soapXml);

    assertTrue(details.isSuccess());
    assertTrue(details.deadIndicator());
    assertEquals("2023/06/15", details.dateOfDeath());
  }

  @Test
  void parseGetDataResponse_throwsOnSoapFault() {
    String soapXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
        + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
        + "<soap:Body>"
        + "<soap:Fault>"
        + "<faultcode>soap:Server</faultcode>"
        + "<faultstring>Internal server error</faultstring>"
        + "</soap:Fault>"
        + "</soap:Body>"
        + "</soap:Envelope>";

    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> xmlBuilder.parseGetDataResponse(soapXml));
    assertTrue(ex.getMessage().contains("SOAP Fault"));
  }

  private String buildSuccessResponse(
      String error, String tranNo, String name, String surname,
      String smartCard, String idIssueDate, String idSeqNo,
      String dead, String blocked, String deathDate,
      String marital, String marriageDate,
      String photo, String onHanis, String onNpr, String birthCountry) {
    return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
        + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
        + "<soap:Body>"
        + "<GetDataResponse xmlns=\"http://HANIS.org/\">"
        + "<GetDataResult>"
        + "<Error>" + error + "</Error>"
        + "<TranNo>" + tranNo + "</TranNo>"
        + "<Name>" + name + "</Name>"
        + "<Surname>" + surname + "</Surname>"
        + "<SmartCardIssued>" + smartCard + "</SmartCardIssued>"
        + "<IDIssueDate>" + idIssueDate + "</IDIssueDate>"
        + "<IDSequenceNo>" + idSeqNo + "</IDSequenceNo>"
        + "<DeadIndicator>" + dead + "</DeadIndicator>"
        + "<IDNBlocked>" + blocked + "</IDNBlocked>"
        + "<DateOfDeath>" + deathDate + "</DateOfDeath>"
        + "<MaritalStatus>" + marital + "</MaritalStatus>"
        + "<DateOfMarriage>" + marriageDate + "</DateOfMarriage>"
        + "<Photo>" + photo + "</Photo>"
        + "<OnHanis>" + onHanis + "</OnHanis>"
        + "<OnNPR>" + onNpr + "</OnNPR>"
        + "<BirthPlaceCountryCode>" + birthCountry + "</BirthPlaceCountryCode>"
        + "</GetDataResult>"
        + "</GetDataResponse>"
        + "</soap:Body>"
        + "</soap:Envelope>";
  }

  private String buildErrorResponse(String error) {
    return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
        + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
        + "<soap:Body>"
        + "<GetDataResponse xmlns=\"http://HANIS.org/\">"
        + "<GetDataResult>"
        + "<Error>" + error + "</Error>"
        + "</GetDataResult>"
        + "</GetDataResponse>"
        + "</soap:Body>"
        + "</soap:Envelope>";
  }
}
