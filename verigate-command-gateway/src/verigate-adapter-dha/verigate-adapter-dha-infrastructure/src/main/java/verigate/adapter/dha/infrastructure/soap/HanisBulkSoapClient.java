/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.infrastructure.soap;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SOAP client for HANIS bulk verification operations (UpLoadFile and RequestStatus).
 */
public class HanisBulkSoapClient {

  private static final Logger logger = LoggerFactory.getLogger(HanisBulkSoapClient.class);

  private static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";
  private static final String HANIS_NS = "http://HANIS.org/";
  private static final String CONTENT_TYPE = "text/xml; charset=utf-8";

  private final HttpClient httpClient;
  private final String serviceUrl;
  private final Duration timeout;

  public HanisBulkSoapClient(String serviceUrl, Duration timeout) {
    this.httpClient = HttpClient.newBuilder().connectTimeout(timeout).build();
    this.serviceUrl = serviceUrl;
    this.timeout = timeout;
  }

  /**
   * Result of an UpLoadFile SOAP call.
   */
  public record UploadResult(String requestId, int errorCode, String errorDescription) {
    public boolean isSuccess() { return errorCode == 0 && requestId != null; }
  }

  /**
   * Result of a RequestStatus SOAP call.
   */
  public record RequestStatusResult(
      int statusCode, String statusDescription,
      byte[] resultData, int errorCode, String errorDescription) {
    public boolean isReady() { return statusCode == 0 && resultData != null; }
    public boolean isProcessing() { return statusCode == 1; }
  }

  /**
   * Uploads a zipped CSV file to HANIS for bulk processing.
   */
  public UploadResult uploadFile(String siteId, String wrkStnId, byte[] zipData) {
    String base64Data = Base64.getEncoder().encodeToString(zipData);

    String soapXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
        + "<soap:Envelope xmlns:soap=\"" + SOAP_NS + "\">"
        + "<soap:Body>"
        + "<UpLoadFile xmlns=\"" + HANIS_NS + "\">"
        + "<SiteId>" + escapeXml(siteId) + "</SiteId>"
        + "<WrkStnId>" + escapeXml(wrkStnId) + "</WrkStnId>"
        + "<FileData>" + base64Data + "</FileData>"
        + "</UpLoadFile>"
        + "</soap:Body>"
        + "</soap:Envelope>";

    String response = postSoap(soapXml, "http://HANIS.org/UpLoadFile");

    // Parse response for RequestId and Error
    try {
      var factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      var doc = factory.newDocumentBuilder().parse(
          new java.io.ByteArrayInputStream(response.getBytes(java.nio.charset.StandardCharsets.UTF_8)));

      String requestId = getElementText(doc, "RequestId");
      int errorCode = parseInt(getElementText(doc, "Error"), -1);
      String errorDesc = getElementText(doc, "ErrorDescription");

      return new UploadResult(requestId, errorCode, errorDesc);
    } catch (Exception e) {
      throw new PermanentException("Failed to parse HANIS upload response", e);
    }
  }

  /**
   * Checks the status of a previously uploaded bulk verification request.
   */
  public RequestStatusResult requestStatus(String siteId, String wrkStnId, String requestId) {
    String soapXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
        + "<soap:Envelope xmlns:soap=\"" + SOAP_NS + "\">"
        + "<soap:Body>"
        + "<RequestStatus xmlns=\"" + HANIS_NS + "\">"
        + "<SiteId>" + escapeXml(siteId) + "</SiteId>"
        + "<WrkStnId>" + escapeXml(wrkStnId) + "</WrkStnId>"
        + "<RequestId>" + escapeXml(requestId) + "</RequestId>"
        + "</RequestStatus>"
        + "</soap:Body>"
        + "</soap:Envelope>";

    String response = postSoap(soapXml, "http://HANIS.org/RequestStatus");

    try {
      var factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      var doc = factory.newDocumentBuilder().parse(
          new java.io.ByteArrayInputStream(response.getBytes(java.nio.charset.StandardCharsets.UTF_8)));

      int statusCode = parseInt(getElementText(doc, "StatusCode"), -1);
      String statusDesc = getElementText(doc, "StatusDescription");
      String fileDataBase64 = getElementText(doc, "FileData");
      int errorCode = parseInt(getElementText(doc, "Error"), 0);
      String errorDesc = getElementText(doc, "ErrorDescription");

      byte[] resultData = (fileDataBase64 != null && !fileDataBase64.isBlank())
          ? Base64.getDecoder().decode(fileDataBase64) : null;

      return new RequestStatusResult(statusCode, statusDesc, resultData, errorCode, errorDesc);
    } catch (Exception e) {
      throw new PermanentException("Failed to parse HANIS status response", e);
    }
  }

  private String postSoap(String soapXml, String soapAction) {
    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(serviceUrl))
          .header("Content-Type", CONTENT_TYPE)
          .header("SOAPAction", "\"" + soapAction + "\"")
          .POST(HttpRequest.BodyPublishers.ofString(soapXml))
          .timeout(timeout)
          .build();

      HttpResponse<String> response = httpClient.send(request,
          HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        return response.body();
      }

      if (response.statusCode() >= 500) {
        throw new TransientException("HANIS bulk service returned HTTP " + response.statusCode());
      }

      throw new PermanentException("HANIS bulk service returned HTTP " + response.statusCode());

    } catch (TransientException | PermanentException e) {
      throw e;
    } catch (java.net.http.HttpTimeoutException e) {
      throw new TransientException("HANIS bulk request timed out", e);
    } catch (Exception e) {
      throw new PermanentException("HANIS bulk request failed: " + e.getMessage(), e);
    }
  }

  private String getElementText(org.w3c.dom.Document doc, String tagName) {
    var nodes = doc.getElementsByTagName(tagName);
    if (nodes.getLength() == 0) {
      // Try with namespace
      nodes = doc.getElementsByTagNameNS(HANIS_NS, tagName);
    }
    return nodes.getLength() > 0 ? nodes.item(0).getTextContent() : null;
  }

  private int parseInt(String value, int defaultValue) {
    if (value == null || value.isBlank()) return defaultValue;
    try { return Integer.parseInt(value.trim()); } catch (NumberFormatException e) { return defaultValue; }
  }

  private String escapeXml(String input) {
    if (input == null) return "";
    return input.replace("&", "&amp;").replace("<", "&lt;")
        .replace(">", "&gt;").replace("\"", "&quot;");
  }
}
