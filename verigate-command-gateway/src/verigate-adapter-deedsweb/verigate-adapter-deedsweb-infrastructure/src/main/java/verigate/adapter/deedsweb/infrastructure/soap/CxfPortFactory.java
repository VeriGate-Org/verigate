/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.soap;

import jakarta.xml.ws.BindingProvider;
import java.util.Map;
import org.apache.cxf.ext.logging.LoggingFeature;
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

    DeedsRegistrationEnquiryService port =
        (DeedsRegistrationEnquiryService) factory.create();

    // Override endpoint on the BindingProvider in case an environment prefers it.
    BindingProvider bindingProvider = (BindingProvider) port;
    Map<String, Object> requestContext = bindingProvider.getRequestContext();
    requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

    // Configure HTTP timeouts.
    HTTPConduit conduit =
        (HTTPConduit) org.apache.cxf.frontend.ClientProxy.getClient(port).getConduit();
    HTTPClientPolicy policy = new HTTPClientPolicy();
    policy.setConnectionTimeout(config.getConnectionTimeoutMs());
    policy.setReceiveTimeout(config.getReadTimeoutMs());
    policy.setAllowChunking(false);
    conduit.setClient(policy);

    LOGGER.info(
        "Built DeedsWeb SOAP proxy: endpoint={}, connectTimeoutMs={}, readTimeoutMs={}",
        endpoint,
        config.getConnectionTimeoutMs(),
        config.getReadTimeoutMs());
    return port;
  }
}
