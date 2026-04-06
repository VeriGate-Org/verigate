/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.infrastructure.soap;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verigate.adapter.sars.domain.models.VatVendorDetails;

class SarsVatSoapXmlBuilderTest {

  private SarsVatSoapXmlBuilder xmlBuilder;

  @BeforeEach
  void setUp() {
    xmlBuilder = new SarsVatSoapXmlBuilder();
  }

  @Test
  void buildSearchRequest_generatesValidSoapEnvelope() {
    String xml = xmlBuilder.buildSearchRequest("4123456789", "Acme", "user@efiling", "secret");

    assertTrue(xml.contains("soap:Envelope"));
    assertTrue(xml.contains("soap:Body"));
    assertTrue(xml.contains("<Search xmlns=\"http://www.sars.gov.za\">"));
    assertTrue(xml.contains("<sVatNumber>4123456789</sVatNumber>"));
    assertTrue(xml.contains("<sDescription>Acme</sDescription>"));
    assertTrue(xml.contains("<sLoginName>user@efiling</sLoginName>"));
    assertTrue(xml.contains("<sUserPassword>secret</sUserPassword>"));
  }

  @Test
  void buildSearchRequest_escapesXmlSpecialCharacters() {
    String xml = xmlBuilder.buildSearchRequest(
        "4123456789", "Acme & Co <PTY>", "user@efiling", "p&ss<word>");

    assertTrue(xml.contains("Acme &amp; Co &lt;PTY&gt;"));
    assertTrue(xml.contains("p&amp;ss&lt;word&gt;"));
  }

  @Test
  void buildSearchRequest_handlesNullDescription() {
    String xml = xmlBuilder.buildSearchRequest("4123456789", null, "user", "pass");

    assertTrue(xml.contains("<sDescription></sDescription>"));
  }

  @Test
  void parseSearchResponse_parsesSuccessfulVendor() {
    String soapXml = buildVendorResponse(
        "4123456789", "Acme Trading (Pty) Ltd", "Acme Trading",
        "2015/03/01", "Active", "47110",
        "123 Main Rd", "Sandton", "", "", "2196");

    VatVendorDetails details = xmlBuilder.parseSearchResponse(soapXml);

    assertNotNull(details);
    assertEquals("4123456789", details.vatNumber());
    assertEquals("Acme Trading (Pty) Ltd", details.vendorName());
    assertEquals("Acme Trading", details.tradingName());
    assertEquals("2015/03/01", details.registrationDate());
    assertEquals("Active", details.vendorStatus());
    assertEquals("47110", details.activityCode());
    assertTrue(details.physicalAddress().contains("123 Main Rd"));
    assertTrue(details.physicalAddress().contains("Sandton"));
  }

  @Test
  void parseSearchResponse_returnsNullWhenNoVendorFound() {
    String soapXml = buildEmptyResponse();

    VatVendorDetails details = xmlBuilder.parseSearchResponse(soapXml);

    assertNull(details);
  }

  @Test
  void parseSearchResponse_throwsOnSoapFault() {
    String soapXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
        + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
        + "<soap:Body>"
        + "<soap:Fault>"
        + "<faultcode>soap:Server</faultcode>"
        + "<faultstring>Authentication failed</faultstring>"
        + "</soap:Fault>"
        + "</soap:Body>"
        + "</soap:Envelope>";

    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> xmlBuilder.parseSearchResponse(soapXml));
    assertTrue(ex.getMessage().contains("SOAP Fault"));
    assertTrue(ex.getMessage().contains("Authentication failed"));
  }

  @Test
  void parseSearchResponse_handlesTable1RowName() {
    String soapXml = buildVendorResponseWithRowName("Table1",
        "4999999999", "Delta Corp", "Delta", "2020/01/15", "Inactive", "62020",
        "45 Long St", "Cape Town", "", "", "8001");

    VatVendorDetails details = xmlBuilder.parseSearchResponse(soapXml);

    assertNotNull(details);
    assertEquals("4999999999", details.vatNumber());
    assertEquals("Delta Corp", details.vendorName());
    assertEquals("Inactive", details.vendorStatus());
  }

  @Test
  void escapeXml_handlesAllSpecialCharacters() {
    assertEquals("&amp;&lt;&gt;&quot;&apos;", xmlBuilder.escapeXml("&<>\"'"));
  }

  @Test
  void escapeXml_handlesNullInput() {
    assertEquals("", xmlBuilder.escapeXml(null));
  }

  private String buildVendorResponse(
      String regNum, String name, String tradeName,
      String regDt, String status, String activityCd,
      String addr1, String addr2, String addr3, String addr4, String postCode) {
    return buildVendorResponseWithRowName("Table",
        regNum, name, tradeName, regDt, status, activityCd,
        addr1, addr2, addr3, addr4, postCode);
  }

  private String buildVendorResponseWithRowName(
      String rowName,
      String regNum, String name, String tradeName,
      String regDt, String status, String activityCd,
      String addr1, String addr2, String addr3, String addr4, String postCode) {
    return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
        + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
        + "<soap:Body>"
        + "<SearchResponse xmlns=\"http://www.sars.gov.za\">"
        + "<SearchResult>"
        + "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">"
        + "</xs:schema>"
        + "<diffgr:diffgram xmlns:diffgr=\"urn:schemas-microsoft-com:xml-diffgram-v1\">"
        + "<NewDataSet>"
        + "<" + rowName + ">"
        + "<Reg_Num>" + regNum + "</Reg_Num>"
        + "<Name>" + name + "</Name>"
        + "<Trade_Name>" + tradeName + "</Trade_Name>"
        + "<Reg_Dt>" + regDt + "</Reg_Dt>"
        + "<Status>" + status + "</Status>"
        + "<Activity_Cd>" + activityCd + "</Activity_Cd>"
        + "<Addr_Line1>" + addr1 + "</Addr_Line1>"
        + "<Addr_Line2>" + addr2 + "</Addr_Line2>"
        + "<Addr_Line3>" + addr3 + "</Addr_Line3>"
        + "<Addr_Line4>" + addr4 + "</Addr_Line4>"
        + "<Post_Code>" + postCode + "</Post_Code>"
        + "</" + rowName + ">"
        + "</NewDataSet>"
        + "</diffgr:diffgram>"
        + "</SearchResult>"
        + "</SearchResponse>"
        + "</soap:Body>"
        + "</soap:Envelope>";
  }

  private String buildEmptyResponse() {
    return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
        + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
        + "<soap:Body>"
        + "<SearchResponse xmlns=\"http://www.sars.gov.za\">"
        + "<SearchResult>"
        + "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">"
        + "</xs:schema>"
        + "<diffgr:diffgram xmlns:diffgr=\"urn:schemas-microsoft-com:xml-diffgram-v1\">"
        + "<NewDataSet />"
        + "</diffgr:diffgram>"
        + "</SearchResult>"
        + "</SearchResponse>"
        + "</soap:Body>"
        + "</soap:Envelope>";
  }
}
