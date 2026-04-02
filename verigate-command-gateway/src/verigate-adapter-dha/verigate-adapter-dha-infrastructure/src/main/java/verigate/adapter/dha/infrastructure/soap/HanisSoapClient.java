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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.dha.domain.models.HanisPersonDetails;

/**
 * HTTP client for HANIS SOAP web service operations.
 * Uses Java built-in {@link HttpClient} to POST SOAP XML envelopes.
 */
public class HanisSoapClient {

  private static final Logger logger = LoggerFactory.getLogger(HanisSoapClient.class);

  private static final String SOAP_ACTION_GET_DATA = "http://HANIS.org/GetData";
  private static final String CONTENT_TYPE = "text/xml; charset=utf-8";
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  private final HttpClient httpClient;
  private final HanisSoapXmlBuilder xmlBuilder;
  private final String primaryUrl;
  private final String failoverUrl;
  private final Duration timeout;

  public HanisSoapClient(
      String primaryUrl, String failoverUrl, Duration timeout) {
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(timeout)
        .build();
    this.xmlBuilder = new HanisSoapXmlBuilder();
    this.primaryUrl = primaryUrl;
    this.failoverUrl = failoverUrl;
    this.timeout = timeout;
  }

  /**
   * Calls the HANIS GetData SOAP operation to retrieve person details from the NPR.
   *
   * @param idn       the 13-digit SA ID number
   * @param siteId    the registered HANIS site ID
   * @param wrkStnId  the workstation identifier
   * @return parsed person details from the NPR
   * @throws TransientException if HANIS is temporarily unavailable
   * @throws PermanentException if the request is permanently invalid
   */
  public HanisPersonDetails getData(String idn, String siteId, String wrkStnId) {
    String transactionDate = LocalDate.now().format(DATE_FORMAT);
    String soapXml = xmlBuilder.buildGetDataRequest(idn, transactionDate, siteId, wrkStnId);

    try {
      return postSoap(primaryUrl, SOAP_ACTION_GET_DATA, soapXml);
    } catch (TransientException e) {
      if (failoverUrl != null && !failoverUrl.isBlank()) {
        logger.warn("Primary HANIS endpoint failed, trying failover: {}", e.getMessage());
        try {
          return postSoap(failoverUrl, SOAP_ACTION_GET_DATA, soapXml);
        } catch (Exception failoverEx) {
          logger.error("Failover HANIS endpoint also failed: {}", failoverEx.getMessage());
          throw new TransientException(
              "Both HANIS endpoints unavailable: " + e.getMessage(), failoverEx);
        }
      }
      throw e;
    }
  }

  private HanisPersonDetails postSoap(String url, String soapAction, String soapXml) {
    logger.debug("Posting SOAP request to {} with action {}", url, soapAction);

    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .header("Content-Type", CONTENT_TYPE)
          .header("SOAPAction", "\"" + soapAction + "\"")
          .POST(HttpRequest.BodyPublishers.ofString(soapXml))
          .timeout(timeout)
          .build();

      HttpResponse<String> response = httpClient.send(request,
          HttpResponse.BodyHandlers.ofString());

      int statusCode = response.statusCode();
      String body = response.body();

      if (statusCode == 200) {
        return xmlBuilder.parseGetDataResponse(body);
      }

      if (statusCode == 500 && body != null && body.contains("Fault")) {
        // SOAP fault in 500 response
        try {
          return xmlBuilder.parseGetDataResponse(body);
        } catch (RuntimeException ex) {
          throw new PermanentException("HANIS SOAP fault: " + ex.getMessage(), ex);
        }
      }

      if (statusCode >= 500 || statusCode == 429) {
        throw new TransientException(
            String.format("HANIS service returned HTTP %d", statusCode));
      }

      throw new PermanentException(
          String.format("HANIS service returned HTTP %d", statusCode));

    } catch (TransientException | PermanentException e) {
      throw e;
    } catch (java.net.http.HttpTimeoutException e) {
      throw new TransientException("HANIS request timed out", e);
    } catch (java.io.IOException e) {
      throw new TransientException("HANIS connection error: " + e.getMessage(), e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new TransientException("HANIS request interrupted", e);
    } catch (Exception e) {
      throw new PermanentException("HANIS request failed: " + e.getMessage(), e);
    }
  }
}
