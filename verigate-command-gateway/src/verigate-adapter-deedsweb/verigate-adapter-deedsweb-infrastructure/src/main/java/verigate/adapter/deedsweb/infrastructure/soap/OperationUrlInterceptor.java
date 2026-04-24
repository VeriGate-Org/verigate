/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.soap;

import java.util.Map;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CXF out-interceptor that rewrites the endpoint URL to include the SOAP operation name.
 *
 * <p>The DeedsWeb CXF server uses <strong>URL-based dispatch</strong>: each operation has its
 * own path (e.g. {@code /deeds-registration-soap/getOfficeRegistryList}). By default, CXF sends
 * every request to the base service URL ({@code /deeds-registration-soap/}), which causes the
 * server to return its CXF service-listing HTML page instead of processing the SOAP request.
 *
 * <p>This interceptor runs in the {@link Phase#PREPARE_SEND} phase (before the HTTP request is
 * dispatched), reads the {@code SOAPAction} header to determine the operation name, and appends
 * it to the endpoint address.
 */
public class OperationUrlInterceptor extends AbstractSoapInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(OperationUrlInterceptor.class);

  public OperationUrlInterceptor() {
    super(Phase.PREPARE_SEND);
  }

  @Override
  public void handleMessage(SoapMessage message) throws Fault {
    String soapAction = extractSoapAction(message);
    if (soapAction == null || soapAction.isBlank()) {
      return;
    }

    String currentAddress = (String) message.get(Message.ENDPOINT_ADDRESS);
    if (currentAddress == null) {
      return;
    }

    // Strip any surrounding quotes from SOAPAction (JAX-WS may quote it).
    String operationName = soapAction.replace("\"", "").trim();
    if (operationName.isEmpty()) {
      return;
    }

    // Only rewrite if the address doesn't already end with the operation name.
    String normalized = currentAddress.endsWith("/")
        ? currentAddress.substring(0, currentAddress.length() - 1)
        : currentAddress;
    if (normalized.endsWith("/" + operationName)) {
      return;
    }

    String rewritten = normalized + "/" + operationName;
    message.put(Message.ENDPOINT_ADDRESS, rewritten);
    LOGGER.debug("Rewrote SOAP endpoint: {} -> {}", currentAddress, rewritten);
  }

  @SuppressWarnings("unchecked")
  private static String extractSoapAction(SoapMessage message) {
    // CXF stores outbound HTTP headers in a Map<String, List<String>> under
    // Message.PROTOCOL_HEADERS. The SOAPAction value comes from the @WebMethod annotation.
    Map<String, java.util.List<String>> headers =
        (Map<String, java.util.List<String>>) message.get(Message.PROTOCOL_HEADERS);
    if (headers != null) {
      java.util.List<String> actions = headers.get("SOAPAction");
      if (actions != null && !actions.isEmpty()) {
        return actions.get(0);
      }
    }
    return null;
  }
}
