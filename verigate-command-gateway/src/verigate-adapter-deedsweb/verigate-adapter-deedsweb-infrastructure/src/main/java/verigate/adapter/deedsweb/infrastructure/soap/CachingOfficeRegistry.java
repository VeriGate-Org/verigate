/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.soap;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.deedsweb.domain.models.OfficeRegistry;
import verigate.adapter.deedsweb.infrastructure.soap.generated.DeedsRegistrationEnquiryService;
import verigate.adapter.deedsweb.infrastructure.soap.generated.OfficeRegistryInformationResponse;

/**
 * Caches the deeds office list for the Lambda's warm lifetime. The registry is virtually
 * static, so refreshing once per cold start is sufficient. Used by the fan-out path in
 * {@link CxfDeedsRegistryClient}.
 */
public class CachingOfficeRegistry {

  private static final Logger LOGGER = LoggerFactory.getLogger(CachingOfficeRegistry.class);

  private final DeedsRegistrationEnquiryService port;
  private final AtomicReference<List<OfficeRegistry>> cache = new AtomicReference<>();

  public CachingOfficeRegistry(DeedsRegistrationEnquiryService port) {
    this.port = port;
  }

  /**
   * Returns the cached office list, fetching from DeedsWeb on first call or after
   * explicit {@link #invalidate()}. Failures propagate as {@link TransientException}
   * or {@link PermanentException}.
   */
  public List<OfficeRegistry> getAll() throws TransientException, PermanentException {
    List<OfficeRegistry> current = cache.get();
    if (current != null) {
      return current;
    }

    synchronized (this) {
      current = cache.get();
      if (current != null) {
        return current;
      }
      List<OfficeRegistry> fresh = fetch();
      cache.set(fresh);
      return fresh;
    }
  }

  /** Clears the cache so the next call re-fetches. */
  public void invalidate() {
    cache.set(null);
  }

  private List<OfficeRegistry> fetch() throws TransientException, PermanentException {
    List<OfficeRegistryInformationResponse> raw;
    try {
      raw = port.getOfficeRegistryList();
    } catch (jakarta.xml.ws.soap.SOAPFaultException e) {
      throw SoapErrorClassifier.classifyFault(e);
    } catch (jakarta.xml.ws.WebServiceException e) {
      throw new TransientException("Transport error calling getOfficeRegistryList", e);
    }
    List<OfficeRegistry> offices = SoapResponseMapper.mapOffices(raw);
    LOGGER.info("Loaded {} deeds offices from registry", offices.size());
    return offices;
  }
}
