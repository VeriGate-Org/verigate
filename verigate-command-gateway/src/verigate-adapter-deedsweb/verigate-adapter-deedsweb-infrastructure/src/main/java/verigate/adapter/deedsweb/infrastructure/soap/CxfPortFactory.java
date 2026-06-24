/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.soap;

import jakarta.xml.ws.BindingProvider;
import java.security.Security;
import java.util.Map;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.deedsweb.infrastructure.config.DeedsWebApiConfiguration;
import verigate.adapter.deedsweb.infrastructure.soap.generated.DeedsRegistrationEnquiryService;

/**
 * Builds a CXF JAX-WS proxy for {@link DeedsRegistrationEnquiryService} using the endpoint
 * and timeouts resolved from {@link DeedsWebApiConfiguration}. CXF's
 * {@code java.util.logging} output is bridged to SLF4J by the shared kernel.
 */
public final class CxfPortFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(CxfPortFactory.class);

  static {
    // deedssoap.deeds.gov.za:443 negotiates TLS using a 1024-bit DHE key.
    // JDK 17+ raised the minimum accepted DHE key size to 2048 bits via
    // jdk.tls.disabledAlgorithms, causing the handshake to fail. Relax it to
    // 1024 bits for this JVM. Certificate chain validation is unaffected.
    String algProp = "jdk.tls.disabledAlgorithms";
    String current = Security.getProperty(algProp);
    if (current != null) {
      String updated = current
          .replace("DH keySize < 2048", "DH keySize < 1024")
          .replace("DHE keySize < 2048", "DHE keySize < 1024");
      if (!updated.equals(current)) {
        Security.setProperty(algProp, updated);
        LOGGER.info("Relaxed jdk.tls.disabledAlgorithms DHE minimum to 1024 bits for DeedsWeb TLS compatibility");
      }
    }
  }

  private CxfPortFactory() {
    // utility
  }

  /** Builds a fresh JAX-WS proxy bound to the configured SOAP endpoint. */
  public static DeedsRegistrationEnquiryService create(DeedsWebApiConfiguration config) {
    String endpoint = config.getBaseUrl();

    JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
    factory.setServiceClass(DeedsRegistrationEnquiryService.class);
    factory.setAddress(endpoint);

    // Register the operation-URL interceptor so CXF appends the SOAP operation
    // name to the endpoint path. DeedsWeb's CXF server uses URL-based dispatch
    // (one path per operation); without this, requests hit the base URL and get
    // the HTML service-listing page instead of a SOAP response.
    factory.getOutInterceptors().add(new OperationUrlInterceptor());

    DeedsRegistrationEnquiryService port =
        (DeedsRegistrationEnquiryService) factory.create();

    // Override endpoint on the BindingProvider in case an environment prefers it.
    BindingProvider bindingProvider = (BindingProvider) port;
    Map<String, Object> requestContext = bindingProvider.getRequestContext();
    requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

    // Configure HTTP timeouts and TLS.
    HTTPClientPolicy policy = new HTTPClientPolicy();
    policy.setConnectionTimeout(config.getConnectionTimeoutMs());
    policy.setReceiveTimeout(config.getReadTimeoutMs());
    policy.setAllowChunking(false);
    // Force HTTP/1.1 — CXF 4.x's HttpClient-based conduit otherwise tries HTTP/2
    // first which causes RST_STREAM errors against servers that don't support h2.
    policy.setVersion("1.1");

    final HTTPConduit conduit =
        (HTTPConduit) ClientProxy.getClient(port).getConduit();
    // Do not follow HTTP 302 redirects. The DeedsWeb BigIP currently redirects
    // SOAP operation POSTs to the base path; following would yield HTML (permanent
    // dispatch error) instead of a transient failure that the gateway can retry.
    policy.setAutoRedirect(false);
    conduit.setClient(policy);

    // Configure TLS for HTTPS endpoints. CXF's X509TrustManagerWrapper does its
    // own hostname verification that fails to match wildcard certificates (e.g.
    // *.deeds.gov.za vs deedssoap.deeds.gov.za). Disabling the CN check lets CXF
    // build its own SSLContext from the JVM's default trust store while skipping
    // the broken hostname check in the trust manager wrapper. The JVM's SSL engine
    // still performs standard certificate chain validation.
    if (endpoint.toLowerCase().startsWith("https")) {
      TLSClientParameters tls = new TLSClientParameters();
      tls.setDisableCNCheck(true);
      conduit.setTlsClientParameters(tls);
    }

    LOGGER.info(
        "Built DeedsWeb SOAP proxy: endpoint={}, connectTimeoutMs={}, readTimeoutMs={}",
        endpoint,
        config.getConnectionTimeoutMs(),
        config.getReadTimeoutMs());
    return port;
  }
}
