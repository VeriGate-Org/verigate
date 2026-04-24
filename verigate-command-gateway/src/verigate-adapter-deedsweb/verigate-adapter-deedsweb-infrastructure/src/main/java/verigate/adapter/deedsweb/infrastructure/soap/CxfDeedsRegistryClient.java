/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.soap;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.soap.SOAPFaultException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.deedsweb.domain.models.DeedsPropertyType;
import verigate.adapter.deedsweb.domain.models.DeedsWebCredentials;
import verigate.adapter.deedsweb.domain.models.OfficeRegistry;
import verigate.adapter.deedsweb.domain.models.PersonFullProperty;
import verigate.adapter.deedsweb.domain.models.PropertyDetails;
import verigate.adapter.deedsweb.domain.services.DeedsRegistryClient;
import verigate.adapter.deedsweb.domain.services.DeedsWebCredentialsProvider;
import verigate.adapter.deedsweb.infrastructure.soap.generated.DeedsRegistrationEnquiryService;

/**
 * CXF-backed implementation of {@link DeedsRegistryClient}. Credentials are resolved once
 * per verification request (reused across any fan-out calls). If the caller supplies a
 * specific {@code officeCode}, the SOAP call is made directly; otherwise the call is
 * fanned out across every office via a shared executor and the results are merged.
 */
public class CxfDeedsRegistryClient implements DeedsRegistryClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(CxfDeedsRegistryClient.class);

  private final DeedsRegistrationEnquiryService port;
  private final DeedsWebCredentialsProvider credentialsProvider;
  private final CachingOfficeRegistry officeCache;
  private final ExecutorService fanoutExecutor;

  public CxfDeedsRegistryClient(
      DeedsRegistrationEnquiryService port,
      DeedsWebCredentialsProvider credentialsProvider,
      CachingOfficeRegistry officeCache,
      ExecutorService fanoutExecutor) {
    this.port = port;
    this.credentialsProvider = credentialsProvider;
    this.officeCache = officeCache;
    this.fanoutExecutor = fanoutExecutor;
  }

  // --------------------------------------------------------------------------------------
  // Summary by ID number
  // --------------------------------------------------------------------------------------

  @Override
  public List<PropertyDetails> findPropertiesByIdNumber(String idNumber, String officeCode)
      throws TransientException, PermanentException {
    DeedsWebCredentials creds = credentialsProvider.get();
    return fanOutOrSingle(
        officeCode,
        office ->
            SoapResponseMapper.mapPersonSummaries(
                invokeList(
                    () ->
                        port.getPropertySummaryInformationByIDNumber(
                            idNumber, office, creds.username(), creds.password()))));
  }

  @Override
  public PersonFullProperty findFullPropertyByIdNumber(String idNumber, String officeCode)
      throws TransientException, PermanentException {
    DeedsWebCredentials creds = credentialsProvider.get();
    List<PersonFullProperty> aggregated =
        fanOutOrSingle(
            officeCode,
            office ->
                SoapResponseMapper.mapFullPersonResponses(
                    invokeList(
                        () ->
                            port.getPersonFullPropertyInformationByIdentityNumber(
                                idNumber, office, creds.username(), creds.password()))));
    if (aggregated.isEmpty()) {
      return null;
    }
    if (aggregated.size() == 1) {
      return aggregated.get(0);
    }
    return SoapResponseMapper.combineFullPersonRecords(aggregated);
  }

  @Override
  public List<PersonFullProperty> findFullPropertiesByIdNumberList(
      List<String> idNumbers, String officeCode) throws TransientException, PermanentException {
    DeedsWebCredentials creds = credentialsProvider.get();
    return fanOutOrSingle(
        officeCode,
        office ->
            SoapResponseMapper.mapFullPersonResponses(
                invokeList(
                    () ->
                        port.getPersonFullPropertyInformationByIdentityNumberList(
                            idNumbers, office, creds.username(), creds.password()))));
  }

  @Override
  public List<PropertyDetails> findPropertiesByCompany(
      String companyName, String companyNumber, String officeCode)
      throws TransientException, PermanentException {
    DeedsWebCredentials creds = credentialsProvider.get();
    return fanOutOrSingle(
        officeCode,
        office ->
            SoapResponseMapper.mapCompanySummaries(
                invokeList(
                    () ->
                        port.getPropertySummaryInformationByCompanyNameAndRegistrationNumber(
                            companyName,
                            companyNumber,
                            office,
                            creds.username(),
                            creds.password()))));
  }

  // --------------------------------------------------------------------------------------
  // Erf / farm / township / agricultural / scheme / exclusive-use
  // --------------------------------------------------------------------------------------

  @Override
  public List<PropertyDetails> findErf(
      String erf, String township, String officeCode, String portion, String propertyTypeCode)
      throws TransientException, PermanentException {
    DeedsWebCredentials creds = credentialsProvider.get();
    return fanOutOrSingle(
        officeCode,
        office ->
            SoapResponseMapper.mapErf(
                invokeList(
                    () ->
                        port.getErfPropertyInformation(
                            erf,
                            township,
                            office,
                            portion,
                            propertyTypeCode,
                            creds.username(),
                            creds.password()))));
  }

  @Override
  public List<PropertyDetails> findErfPortions(
      String erf,
      String township,
      String officeCode,
      List<String> portions,
      String propertyTypeCode)
      throws TransientException, PermanentException {
    DeedsWebCredentials creds = credentialsProvider.get();
    List<String> portionsSafe = portions == null ? Collections.emptyList() : portions;
    return fanOutOrSingle(
        officeCode,
        office ->
            SoapResponseMapper.mapErf(
                invokeList(
                    () ->
                        port.getErfPropertyInformationByPortionList(
                            erf,
                            township,
                            office,
                            portionsSafe,
                            propertyTypeCode,
                            creds.username(),
                            creds.password()))));
  }

  @Override
  public List<PropertyDetails> findFarm(
      String farmNumber,
      List<String> portions,
      String regDivision,
      String officeCode,
      String propertyTypeCode)
      throws TransientException, PermanentException {
    DeedsWebCredentials creds = credentialsProvider.get();
    List<String> portionsSafe = portions == null ? Collections.emptyList() : portions;
    return fanOutOrSingle(
        officeCode,
        office ->
            SoapResponseMapper.mapFarm(
                invokeList(
                    () ->
                        port.getFarmPropertyInformationByNumberAndPortionList(
                            farmNumber,
                            portionsSafe,
                            regDivision,
                            office,
                            propertyTypeCode,
                            creds.username(),
                            creds.password()))));
  }

  @Override
  public List<PropertyDetails> findTownshipProperties(
      String township, String officeCode, String propertyTypeCode)
      throws TransientException, PermanentException {
    DeedsWebCredentials creds = credentialsProvider.get();
    return fanOutOrSingle(
        officeCode,
        office ->
            SoapResponseMapper.mapTownship(
                invokeList(
                    () ->
                        port.getTownshipPropertyInformation(
                            township,
                            office,
                            propertyTypeCode,
                            creds.username(),
                            creds.password()))));
  }

  @Override
  public List<PropertyDetails> findAgriculturalHolding(
      String agriculturalHoldingNumber,
      String agriculturalHoldingName,
      String officeCode,
      String portion,
      String propertyTypeCode)
      throws TransientException, PermanentException {
    DeedsWebCredentials creds = credentialsProvider.get();
    return fanOutOrSingle(
        officeCode,
        office ->
            SoapResponseMapper.mapAgricultural(
                invokeList(
                    () ->
                        port.getAgriculturalHoldingPropertyInformation(
                            agriculturalHoldingNumber,
                            agriculturalHoldingName,
                            office,
                            portion,
                            propertyTypeCode,
                            creds.username(),
                            creds.password()))));
  }

  @Override
  public List<PropertyDetails> findAgriculturalHoldingByArea(
      String agriculturalHoldingAreaName, String officeCode, String propertyTypeCode)
      throws TransientException, PermanentException {
    DeedsWebCredentials creds = credentialsProvider.get();
    return fanOutOrSingle(
        officeCode,
        office ->
            SoapResponseMapper.mapAgriculturalArea(
                invokeList(
                    () ->
                        port.getAgriculturalHoldingAreaNamePropertyInformation(
                            agriculturalHoldingAreaName,
                            office,
                            propertyTypeCode,
                            creds.username(),
                            creds.password()))));
  }

  @Override
  public List<PropertyDetails> findScheme(
      String schemeName,
      List<String> schemeNumbers,
      String officeCode,
      String propertyTypeCode)
      throws TransientException, PermanentException {
    DeedsWebCredentials creds = credentialsProvider.get();
    List<String> numbersSafe = schemeNumbers == null ? Collections.emptyList() : schemeNumbers;
    return fanOutOrSingle(
        officeCode,
        office ->
            SoapResponseMapper.mapScheme(
                invokeList(
                    () ->
                        port.getSchemePropertyInformationByNameAndSchemeNumberList(
                            schemeName,
                            numbersSafe,
                            office,
                            propertyTypeCode,
                            creds.username(),
                            creds.password()))));
  }

  @Override
  public List<PropertyDetails> findExclusiveUseArea(
      String schemeNumber,
      String schemeName,
      List<String> exclusiveUseNumbers,
      String officeCode,
      String propertyTypeCode)
      throws TransientException, PermanentException {
    DeedsWebCredentials creds = credentialsProvider.get();
    List<String> exclusiveSafe =
        exclusiveUseNumbers == null ? Collections.emptyList() : exclusiveUseNumbers;
    return fanOutOrSingle(
        officeCode,
        office ->
            SoapResponseMapper.mapExclusiveUse(
                invokeList(
                    () ->
                        port.getExclusiveUseAreaPropertyInformation(
                            schemeNumber,
                            schemeName,
                            exclusiveSafe,
                            office,
                            propertyTypeCode,
                            creds.username(),
                            creds.password()))));
  }

  // --------------------------------------------------------------------------------------
  // No-credential lookups
  // --------------------------------------------------------------------------------------

  @Override
  public List<OfficeRegistry> getOfficeRegistryList() throws TransientException, PermanentException {
    return officeCache.getAll();
  }

  @Override
  public List<DeedsPropertyType> getPropertyTypeList()
      throws TransientException, PermanentException {
    try {
      return SoapResponseMapper.mapPropertyTypes(port.getDeedsPropertyTypeList());
    } catch (SOAPFaultException e) {
      throw SoapErrorClassifier.classifyFault(e);
    } catch (WebServiceException e) {
      throw new TransientException("Transport error calling getDeedsPropertyTypeList", e);
    }
  }

  @Override
  public boolean isServiceHealthy() throws TransientException {
    try {
      officeCache.getAll();
      return true;
    } catch (PermanentException e) {
      LOGGER.warn("DeedsWeb service returned permanent error during health check", e);
      return false;
    }
  }

  // --------------------------------------------------------------------------------------
  // Fan-out core
  // --------------------------------------------------------------------------------------

  @FunctionalInterface
  private interface OfficeCall<T> {
    List<T> call(String officeCode) throws TransientException, PermanentException;
  }

  private <T> List<T> fanOutOrSingle(String officeCode, OfficeCall<T> call)
      throws TransientException, PermanentException {
    if (targetsSingleOffice(officeCode)) {
      return call.call(officeCode);
    }
    return fanOut(call);
  }

  private static boolean targetsSingleOffice(String officeCode) {
    if (officeCode == null) {
      return false;
    }
    String trimmed = officeCode.trim();
    if (trimmed.isEmpty()) {
      return false;
    }
    return !trimmed.equalsIgnoreCase("all");
  }

  private <T> List<T> fanOut(OfficeCall<T> call) throws TransientException, PermanentException {
    List<OfficeRegistry> offices = officeCache.getAll();
    if (offices.isEmpty()) {
      LOGGER.warn("Office registry is empty; cannot fan out");
      return Collections.emptyList();
    }

    List<CompletableFuture<List<T>>> futures = new ArrayList<>(offices.size());
    for (OfficeRegistry office : offices) {
      String code = office.officeCode();
      futures.add(
          CompletableFuture.supplyAsync(
              () -> {
                try {
                  return call.call(code);
                } catch (TransientException | PermanentException e) {
                  throw new CompletionWrap(e);
                }
              },
              fanoutExecutor));
    }

    List<T> merged = new ArrayList<>();
    TransientException firstTransient = null;
    for (int i = 0; i < futures.size(); i++) {
      String code = offices.get(i).officeCode();
      try {
        List<T> result = futures.get(i).get();
        if (result != null) {
          merged.addAll(result);
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new TransientException("Interrupted while fanning out DeedsWeb call", e);
      } catch (ExecutionException e) {
        Throwable cause = e.getCause();
        if (cause instanceof CompletionWrap wrap) {
          Throwable inner = wrap.getCause();
          if (inner instanceof PermanentException pe) {
            throw pe;
          }
          if (inner instanceof TransientException te) {
            LOGGER.warn("Transient failure for office {}: {}", code, te.getMessage());
            if (firstTransient == null) {
              firstTransient = te;
            }
            continue;
          }
        }
        LOGGER.warn("Unexpected failure for office {}: {}", code, cause == null ? e.getMessage() : cause.getMessage());
        if (firstTransient == null) {
          firstTransient = new TransientException("Fan-out office " + code + " failed", cause);
        }
      }
    }

    if (merged.isEmpty() && firstTransient != null) {
      throw firstTransient;
    }
    return merged;
  }

  private static final class CompletionWrap extends RuntimeException {
    CompletionWrap(Throwable cause) {
      super(cause);
    }
  }

  /**
   * Invokes a raw SOAP call, returning an empty list when the service returns {@code null}
   * and translating transport / fault exceptions into domain exceptions.
   */
  private static <T> List<T> invokeList(SoapCall<List<T>> call)
      throws TransientException, PermanentException {
    try {
      List<T> result = call.call();
      return result == null ? Collections.emptyList() : result;
    } catch (SOAPFaultException e) {
      RuntimeException mapped = SoapErrorClassifier.classifyFault(e);
      if (mapped instanceof PermanentException pe) {
        throw pe;
      }
      throw (TransientException) mapped;
    } catch (WebServiceException e) {
      RuntimeException classified = classifyTransport(e);
      if (classified instanceof PermanentException pe) {
        throw pe;
      }
      throw (TransientException) classified;
    }
  }

  private static RuntimeException classifyTransport(WebServiceException e) {
    String message = e.getMessage() == null ? "" : e.getMessage();
    String lower = message.toLowerCase(Locale.ROOT);
    if (lower.contains("401") || lower.contains("unauthor")) {
      // Surface as permanent — auth issues should not be retried.
      return new PermanentException("DeedsWeb rejected the request: " + message, e);
    }
    if (SoapErrorClassifier.isHtmlResponseError(message)) {
      // The server returned HTML instead of SOAP XML. This indicates a dispatch
      // misconfiguration (e.g. request hit the base URL instead of an operation-
      // specific path). Retrying will not help.
      return new PermanentException(
          "DeedsWeb returned HTML instead of SOAP XML (dispatch error): " + message, e);
    }
    return new TransientException("DeedsWeb transport error: " + message, e);
  }

  @FunctionalInterface
  private interface SoapCall<T> {
    T call();
  }
}
