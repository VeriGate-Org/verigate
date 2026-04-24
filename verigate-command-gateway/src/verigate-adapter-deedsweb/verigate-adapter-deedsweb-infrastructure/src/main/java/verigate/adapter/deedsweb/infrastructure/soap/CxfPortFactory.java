/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.soap;

import jakarta.xml.ws.BindingProvider;
import java.util.Map;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.ext.logging.LoggingFeature;
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
 * and timeouts resolved from {@link DeedsWebApiConfiguration}. CXF request/response logging
 * is wired through a {@link LoggingFeature} (truncates large payloads) and CXF's
 * {@code java.util.logging} output is bridged to SLF4J by the shared kernel.
 */
public final class CxfPortFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(CxfPortFactory.class);

  private static final int LOG_PAYLOAD_LIMIT_BYTES = 8_192;

  private CxfPortFactory() {
    // utility
  }

  /** Builds a fresh JAX-WS proxy bound to the configured SOAP endpoint. */
  public static DeedsRegistrationEnquiryService create(DeedsWebApiConfiguration config) {
    String endpoint = config.getBaseUrl();

    JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
    factory.setServiceClass(DeedsRegistrationEnquiryService.class);
    factory.setAddress(endpoint);

    LoggingFeature logging = new LoggingFeature();
    logging.setPrettyLogging(true);
    logging.setLimit(LOG_PAYLOAD_LIMIT_BYTES);
    factory.getFeatures().add(logging);

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
    HTTPConduit conduit = (HTTPConduit) ClientProxy.getClient(port).getConduit();
    HTTPClientPolicy policy = new HTTPClientPolicy();
    policy.setConnectionTimeout(config.getConnectionTimeoutMs());
    policy.setReceiveTimeout(config.getReadTimeoutMs());
    policy.setAllowChunking(false);
    // Force HTTP/1.1 — CXF 4.x's HttpClient-based conduit otherwise tries HTTP/2
    // first which causes RST_STREAM errors against servers that don't support h2.
    policy.setVersion("1.1");
    conduit.setClient(policy);

    // Explicitly configure TLS for HTTPS endpoints so CXF uses the JVM's default
    // trust store rather than falling back to conduit-level defaults.
    if (endpoint.toLowerCase().startsWith("https")) {
      TLSClientParameters tls = new TLSClientParameters();
      tls.setUseHttpsURLConnectionDefaultSslSocketFactory(true);
      tls.setUseHttpsURLConnectionDefaultHostnameVerifier(true);
      // Disable CXF's own CN check in its X509TrustManagerWrapper. CXF wraps the
      // JVM trust manager and adds a redundant hostname verification pass that
      // fails to match wildcard certificates (e.g. *.deeds.gov.za). The JVM's
      // built-in hostname verifier handles wildcards correctly and is already
      // active via setUseHttpsURLConnectionDefaultHostnameVerifier above.
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
