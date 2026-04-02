/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.infrastructure.soap;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import verigate.adapter.dha.domain.models.HanisPersonDetails;

/**
 * Builds SOAP 1.1 XML request envelopes and parses responses for the HANIS
 * PersonData_4/HANISNPRRequest.asmx service.
 */
public class HanisSoapXmlBuilder {

  private static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";
  private static final String HANIS_NS = "http://HANIS.org/";

  /**
   * Builds the SOAP XML envelope for a GetData request.
   *
   * @param idn             the 13-digit SA ID number
   * @param transactionDate the transaction date in dd/MM/yyyy format
   * @param siteId          the registered HANIS site ID
   * @param wrkStnId        the workstation identifier
   * @return the full SOAP envelope XML string
   */
  public String buildGetDataRequest(
      String idn, String transactionDate, String siteId, String wrkStnId) {
    return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
        + "<soap:Envelope xmlns:soap=\"" + SOAP_NS + "\" "
        + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
        + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
        + "<soap:Body>"
        + "<GetData xmlns=\"" + HANIS_NS + "\">"
        + "<IDN>" + escapeXml(idn) + "</IDN>"
        + "<TransactionDate>" + escapeXml(transactionDate) + "</TransactionDate>"
        + "<SiteId>" + escapeXml(siteId) + "</SiteId>"
        + "<WrkStnId>" + escapeXml(wrkStnId) + "</WrkStnId>"
        + "</GetData>"
        + "</soap:Body>"
        + "</soap:Envelope>";
  }

  /**
   * Parses a SOAP response XML string from a GetData call into a {@link HanisPersonDetails}.
   *
   * @param soapXml the raw SOAP response XML
   * @return the parsed person details
   * @throws RuntimeException if XML parsing fails or a SOAP fault is present
   */
  public HanisPersonDetails parseGetDataResponse(String soapXml) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      // Disable external entities for security
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(
          new ByteArrayInputStream(soapXml.getBytes(StandardCharsets.UTF_8)));

      // Check for SOAP Fault
      NodeList faults = doc.getElementsByTagNameNS(SOAP_NS, "Fault");
      if (faults.getLength() > 0) {
        Element fault = (Element) faults.item(0);
        String faultString = getElementText(fault, "faultstring");
        throw new RuntimeException("SOAP Fault: " + faultString);
      }

      // Parse GetDataResult
      NodeList results = doc.getElementsByTagNameNS(HANIS_NS, "GetDataResult");
      if (results.getLength() == 0) {
        // Try without namespace for some HANIS implementations
        results = doc.getElementsByTagName("GetDataResult");
      }
      if (results.getLength() == 0) {
        throw new RuntimeException("No GetDataResult element in SOAP response");
      }

      Element result = (Element) results.item(0);

      int error = parseInt(getElementTextNs(result, "Error"), 0);
      if (error != 0) {
        return HanisPersonDetails.error(error);
      }

      String tranNo = getElementTextNs(result, "TranNo");
      String name = getElementTextNs(result, "Name");
      String surname = getElementTextNs(result, "Surname");
      boolean smartCardIssued = parseBool(getElementTextNs(result, "SmartCardIssued"));
      String idIssueDate = getElementTextNs(result, "IDIssueDate");
      String idSequenceNo = getElementTextNs(result, "IDSequenceNo");
      boolean deadIndicator = parseBool(getElementTextNs(result, "DeadIndicator"));
      boolean idnBlocked = parseBool(getElementTextNs(result, "IDNBlocked"));
      String dateOfDeath = getElementTextNs(result, "DateOfDeath");
      String maritalStatus = getElementTextNs(result, "MaritalStatus");
      String dateOfMarriage = getElementTextNs(result, "DateOfMarriage");
      String photoBase64 = getElementTextNs(result, "Photo");
      byte[] photo = (photoBase64 != null && !photoBase64.isBlank())
          ? Base64.getDecoder().decode(photoBase64)
          : null;
      boolean onHanis = parseBool(getElementTextNs(result, "OnHanis"));
      boolean onNpr = parseBool(getElementTextNs(result, "OnNPR"));
      String birthPlaceCountryCode = getElementTextNs(result, "BirthPlaceCountryCode");

      return HanisPersonDetails.success(
          tranNo, name, surname, smartCardIssued, idIssueDate, idSequenceNo,
          deadIndicator, idnBlocked, dateOfDeath, maritalStatus, dateOfMarriage,
          photo, onHanis, onNpr, birthPlaceCountryCode);

    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse HANIS SOAP response", e);
    }
  }

  private String getElementTextNs(Element parent, String localName) {
    NodeList nodes = parent.getElementsByTagNameNS(HANIS_NS, localName);
    if (nodes.getLength() == 0) {
      nodes = parent.getElementsByTagName(localName);
    }
    if (nodes.getLength() == 0) {
      return null;
    }
    return nodes.item(0).getTextContent();
  }

  private String getElementText(Element parent, String tagName) {
    NodeList nodes = parent.getElementsByTagName(tagName);
    if (nodes.getLength() == 0) {
      return null;
    }
    return nodes.item(0).getTextContent();
  }

  private int parseInt(String value, int defaultValue) {
    if (value == null || value.isBlank()) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(value.trim());
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  private boolean parseBool(String value) {
    if (value == null || value.isBlank()) {
      return false;
    }
    String v = value.trim().toLowerCase();
    return "true".equals(v) || "1".equals(v) || "y".equals(v) || "yes".equals(v);
  }

  private String escapeXml(String input) {
    if (input == null) {
      return "";
    }
    return input
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;");
  }
}
