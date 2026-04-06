/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.infrastructure.soap;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.sars.domain.constants.DomainConstants;
import verigate.adapter.sars.domain.models.VatVendorDetails;
import verigate.adapter.sars.infrastructure.config.SarsEfilingCredentials;

/**
 * HTTP client for the SARS VAT Vendor Search SOAP web service.
 * Uses Java 21 {@link HttpClient} to POST SOAP XML envelopes.
 */
public class SarsVatSoapClient {

  private static final Logger logger = LoggerFactory.getLogger(SarsVatSoapClient.class);

  private static final String CONTENT_TYPE = "text/xml; charset=utf-8";

  private final HttpClient httpClient;
  private final SarsVatSoapXmlBuilder xmlBuilder;
  private final String endpointUrl;
  private final SarsEfilingCredentials credentials;
  private final Duration timeout;

  public SarsVatSoapClient(
      String endpointUrl, SarsEfilingCredentials credentials, Duration timeout) {
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(timeout)
        .build();
    this.xmlBuilder = new SarsVatSoapXmlBuilder();
    this.endpointUrl = endpointUrl;
    this.credentials = credentials;
    this.timeout = timeout;
  }

  /**
   * Calls the SARS VAT Vendor Search SOAP operation.
   *
   * @param vatNumber   the VAT registration number to search
   * @param description optional vendor description filter
   * @return parsed vendor details, or null if not found
   * @throws TransientException if SARS is temporarily unavailable
   * @throws PermanentException if the request is permanently invalid
   */
  public VatVendorDetails search(String vatNumber, String description) {
    String soapXml = xmlBuilder.buildSearchRequest(
        vatNumber, description, credentials.getLoginName(), credentials.getPassword());

    logger.debug("Posting SARS VAT vendor search for VAT ending ...{}",
        vatNumber != null && vatNumber.length() > 4
            ? vatNumber.substring(vatNumber.length() - 4)
            : "****");

    return postSoap(endpointUrl, DomainConstants.SARS_VAT_SOAP_ACTION, soapXml);
  }

  private VatVendorDetails postSoap(String url, String soapAction, String soapXml) {
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
        return xmlBuilder.parseSearchResponse(body);
      }

      if (statusCode == 500 && body != null && body.contains("Fault")) {
        try {
          return xmlBuilder.parseSearchResponse(body);
        } catch (RuntimeException ex) {
          throw new PermanentException(
              "SARS VAT Vendor SOAP fault: " + ex.getMessage(), ex);
        }
      }

      if (statusCode >= 500 || statusCode == 429) {
        throw new TransientException(
            String.format("SARS VAT Vendor service returned HTTP %d", statusCode));
      }

      throw new PermanentException(
          String.format("SARS VAT Vendor service returned HTTP %d", statusCode));

    } catch (TransientException | PermanentException e) {
      throw e;
    } catch (java.net.http.HttpTimeoutException e) {
      throw new TransientException("SARS VAT Vendor request timed out", e);
    } catch (java.io.IOException e) {
      throw new TransientException(
          "SARS VAT Vendor connection error: " + e.getMessage(), e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new TransientException("SARS VAT Vendor request interrupted", e);
    } catch (Exception e) {
      throw new PermanentException(
          "SARS VAT Vendor request failed: " + e.getMessage(), e);
    }
  }
}
