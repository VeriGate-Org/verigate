/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.soap;

import jakarta.xml.ws.BindingProvider;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
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

    // Configure TLS for HTTPS endpoints.
    //
    // 1. CN check disabled: CXF's X509TrustManagerWrapper does its own hostname
    //    verification that fails to match wildcard certificates (e.g.
    //    *.deeds.gov.za vs deedssoap.deeds.gov.za). Disabling it lets the JVM's
    //    SSL engine handle standard certificate chain validation instead.
    //
    // 2. Relaxed DH key size: The DeedsWeb server uses a Diffie-Hellman key
    //    smaller than 2048 bits. Java 21's default security policy rejects this
    //    with "DH ServerKeyExchange does not comply to algorithm constraints".
    //    We lower the JVM's constraint from "DH keySize < 2048" to
    //    "DH keySize < 1024" so the handshake succeeds. This is scoped to the
    //    JVM-level security property because SSLContext does not expose per-context
    //    algorithm constraints, but since each Lambda instance runs a single
    //    adapter, the blast radius is limited to this process.
    if (endpoint.toLowerCase().startsWith("https")) {
      relaxDhKeyConstraints();
      TLSClientParameters tls = new TLSClientParameters();
      tls.setDisableCNCheck(true);
      tls.setSSLSocketFactory(createRelaxedSslContext().getSocketFactory());
      conduit.setTlsClientParameters(tls);
    }

    LOGGER.info(
        "Built DeedsWeb SOAP proxy: endpoint={}, connectTimeoutMs={}, readTimeoutMs={}",
        endpoint,
        config.getConnectionTimeoutMs(),
        config.getReadTimeoutMs());
    return port;
  }

  /**
   * Relaxes the JVM's {@code jdk.tls.disabledAlgorithms} security property to allow DH keys
   * down to 1024 bits. The DeedsWeb server presents a DH key smaller than the Java 21 default
   * minimum of 2048 bits.
   */
  private static void relaxDhKeyConstraints() {
    String current = Security.getProperty("jdk.tls.disabledAlgorithms");
    if (current != null && current.contains("DH keySize < 2048")) {
      String relaxed = current.replace("DH keySize < 2048", "DH keySize < 1024");
      Security.setProperty("jdk.tls.disabledAlgorithms", relaxed);
      LOGGER.info("Relaxed jdk.tls.disabledAlgorithms: DH keySize minimum lowered to 1024");
    }
  }

  /**
   * Creates an {@link SSLContext} that uses the JVM's default trust store (for CA chain
   * validation) but inherits the relaxed algorithm constraints set by
   * {@link #relaxDhKeyConstraints()}.
   */
  private static SSLContext createRelaxedSslContext() {
    try {
      TrustManagerFactory tmf =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmf.init((java.security.KeyStore) null);
      TrustManager[] trustManagers = tmf.getTrustManagers();

      SSLContext ctx = SSLContext.getInstance("TLS");
      ctx.init(null, trustManagers, null);
      return ctx;
    } catch (NoSuchAlgorithmException | KeyManagementException
        | java.security.KeyStoreException e) {
      LOGGER.warn("Failed to create relaxed SSLContext, falling back to JVM default", e);
      try {
        return SSLContext.getDefault();
      } catch (NoSuchAlgorithmException ex) {
        throw new IllegalStateException("No default SSLContext available", ex);
      }
    }
  }
}
