/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.infrastructure.soap;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import verigate.adapter.sars.domain.models.VatVendorDetails;

/**
 * Builds SOAP 1.1 XML request envelopes and parses responses for the SARS
 * VAT Vendor Search service at secure.sarsefiling.co.za.
 */
public class SarsVatSoapXmlBuilder {

  private static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";
  private static final String SARS_NS = "http://www.sars.gov.za";

  /**
   * Builds the SOAP XML envelope for a VAT vendor Search request.
   *
   * @param vatNumber   the VAT registration number
   * @param description an optional vendor description filter
   * @param loginName   the eFiling login username
   * @param password    the eFiling login password
   * @return the full SOAP envelope XML string
   */
  public String buildSearchRequest(
      String vatNumber, String description, String loginName, String password) {
    return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
        + "<soap:Envelope xmlns:soap=\"" + SOAP_NS + "\" "
        + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
        + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
        + "<soap:Body>"
        + "<Search xmlns=\"" + SARS_NS + "\">"
        + "<sVatNumber>" + escapeXml(vatNumber) + "</sVatNumber>"
        + "<sDescription>" + escapeXml(description != null ? description : "") + "</sDescription>"
        + "<sLoginName>" + escapeXml(loginName) + "</sLoginName>"
        + "<sUserPassword>" + escapeXml(password) + "</sUserPassword>"
        + "</Search>"
        + "</soap:Body>"
        + "</soap:Envelope>";
  }

  /**
   * Parses a SOAP response XML string from a Search call.
   *
   * <p>The SARS VAT Vendor Search returns an ADO.NET DataSet wrapped inside
   * {@code <SearchResult>}. The actual vendor data rows are inside a
   * {@code <diffgr:diffgram>} element. This method navigates to the first
   * vendor row and extracts vendor details.</p>
   *
   * @param soapXml the raw SOAP response XML
   * @return the parsed vendor details, or null if no vendor was found
   * @throws RuntimeException if XML parsing fails or a SOAP fault is present
   */
  public VatVendorDetails parseSearchResponse(String soapXml) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
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

      // Parse SearchResult - the wrapper element
      NodeList results = doc.getElementsByTagNameNS(SARS_NS, "SearchResult");
      if (results.getLength() == 0) {
        results = doc.getElementsByTagName("SearchResult");
      }
      if (results.getLength() == 0) {
        return null;
      }

      // Navigate to diffgram - the actual data container
      NodeList diffgrams = doc.getElementsByTagName("diffgr:diffgram");
      if (diffgrams.getLength() == 0) {
        // Try without prefix
        diffgrams = doc.getElementsByTagNameNS(
            "urn:schemas-microsoft-com:xml-diffgram-v1", "diffgram");
      }
      if (diffgrams.getLength() == 0) {
        return null;
      }

      Element diffgram = (Element) diffgrams.item(0);

      // Look for the first data row - commonly named "Table" in ADO.NET DataSets
      NodeList tableRows = diffgram.getElementsByTagName("Table");
      if (tableRows.getLength() == 0) {
        // Some implementations may use "Table1" or "VendorSearch"
        tableRows = diffgram.getElementsByTagName("Table1");
      }
      if (tableRows.getLength() == 0) {
        tableRows = diffgram.getElementsByTagName("VendorSearch");
      }
      if (tableRows.getLength() == 0) {
        return null;
      }

      Element row = (Element) tableRows.item(0);
      return extractVendorDetails(row);

    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse SARS VAT Vendor SOAP response", e);
    }
  }

  private VatVendorDetails extractVendorDetails(Element row) {
    return VatVendorDetails.builder()
        .vatNumber(getElementText(row, "Reg_Num"))
        .vendorName(getElementText(row, "Name"))
        .tradingName(getElementText(row, "Trade_Name"))
        .registrationDate(getElementText(row, "Reg_Dt"))
        .vendorStatus(getElementText(row, "Status"))
        .activityCode(getElementText(row, "Activity_Cd"))
        .physicalAddress(buildAddress(row))
        .build();
  }

  private String buildAddress(Element row) {
    StringBuilder sb = new StringBuilder();
    appendIfPresent(sb, getElementText(row, "Addr_Line1"));
    appendIfPresent(sb, getElementText(row, "Addr_Line2"));
    appendIfPresent(sb, getElementText(row, "Addr_Line3"));
    appendIfPresent(sb, getElementText(row, "Addr_Line4"));
    appendIfPresent(sb, getElementText(row, "Post_Code"));
    return sb.toString();
  }

  private void appendIfPresent(StringBuilder sb, String value) {
    if (value != null && !value.isBlank()) {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      sb.append(value.trim());
    }
  }

  private String getElementText(Element parent, String tagName) {
    NodeList nodes = parent.getElementsByTagName(tagName);
    if (nodes.getLength() == 0) {
      return null;
    }
    String text = nodes.item(0).getTextContent();
    return (text != null && !text.isBlank()) ? text.trim() : null;
  }

  String escapeXml(String input) {
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
