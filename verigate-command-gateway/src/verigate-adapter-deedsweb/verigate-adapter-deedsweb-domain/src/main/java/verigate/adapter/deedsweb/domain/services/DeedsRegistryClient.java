/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.domain.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import verigate.adapter.deedsweb.domain.models.DeedsPropertyType;
import verigate.adapter.deedsweb.domain.models.OfficeRegistry;
import verigate.adapter.deedsweb.domain.models.PersonFullProperty;
import verigate.adapter.deedsweb.domain.models.PropertyDetails;

import java.util.List;

/**
 * Domain-facing facade for the DeedsWeb SOAP registry. One method per WSDL
 * operation, each accepting an {@code officeCode} that, if {@code null} or
 * blank, indicates a fan-out across every office. Implementations are
 * responsible for credential resolution, fan-out parallelism, and translating
 * SOAP faults to {@link TransientException} / {@link PermanentException}.
 */
public interface DeedsRegistryClient {

  /**
   * Get property summary records owned by a given South African ID number.
   *
   * @param idNumber South African ID number
   * @param officeCode deeds office code; {@code null}/blank/{@code "all"} fans out
   */
  List<PropertyDetails> findPropertiesByIdNumber(String idNumber, String officeCode)
      throws TransientException, PermanentException;

  /**
   * Get the full person + property payload for a single ID number.
   */
  PersonFullProperty findFullPropertyByIdNumber(String idNumber, String officeCode)
      throws TransientException, PermanentException;

  /**
   * Get full person + property payloads for multiple ID numbers.
   */
  List<PersonFullProperty> findFullPropertiesByIdNumberList(List<String> idNumbers, String officeCode)
      throws TransientException, PermanentException;

  /**
   * Get property summary records owned by a company.
   */
  List<PropertyDetails> findPropertiesByCompany(
      String companyName, String companyNumber, String officeCode)
      throws TransientException, PermanentException;

  /**
   * Look up a single erf.
   */
  List<PropertyDetails> findErf(
      String erf,
      String township,
      String officeCode,
      String portion,
      String propertyTypeCode)
      throws TransientException, PermanentException;

  /**
   * Look up an erf across multiple portions.
   */
  List<PropertyDetails> findErfPortions(
      String erf,
      String township,
      String officeCode,
      List<String> portions,
      String propertyTypeCode)
      throws TransientException, PermanentException;

  /**
   * Look up a farm by number / portions / registration division.
   */
  List<PropertyDetails> findFarm(
      String farmNumber,
      List<String> portions,
      String regDivision,
      String officeCode,
      String propertyTypeCode)
      throws TransientException, PermanentException;

  /**
   * List properties registered in a given township.
   */
  List<PropertyDetails> findTownshipProperties(
      String township, String officeCode, String propertyTypeCode)
      throws TransientException, PermanentException;

  /**
   * Look up an agricultural holding by number/name and portion.
   */
  List<PropertyDetails> findAgriculturalHolding(
      String agriculturalHoldingNumber,
      String agriculturalHoldingName,
      String officeCode,
      String portion,
      String propertyTypeCode)
      throws TransientException, PermanentException;

  /**
   * Look up an agricultural holding by area name.
   */
  List<PropertyDetails> findAgriculturalHoldingByArea(
      String agriculturalHoldingAreaName,
      String officeCode,
      String propertyTypeCode)
      throws TransientException, PermanentException;

  /**
   * Look up properties under a sectional title scheme by name + scheme numbers.
   */
  List<PropertyDetails> findScheme(
      String schemeName,
      List<String> schemeNumbers,
      String officeCode,
      String propertyTypeCode)
      throws TransientException, PermanentException;

  /**
   * Look up exclusive use areas attached to a scheme.
   */
  List<PropertyDetails> findExclusiveUseArea(
      String schemeNumber,
      String schemeName,
      List<String> exclusiveUseNumbers,
      String officeCode,
      String propertyTypeCode)
      throws TransientException, PermanentException;

  /**
   * @return every deeds office known to the registry. Used both by the BFF UI
   *     to populate the office picker and by the fan-out path.
   */
  List<OfficeRegistry> getOfficeRegistryList() throws TransientException, PermanentException;

  /**
   * @return every property type code recognised by the registry.
   */
  List<DeedsPropertyType> getPropertyTypeList() throws TransientException, PermanentException;

  /**
   * @return true if the registry is reachable. Implemented via
   *     {@code getOfficeRegistryList} (does not require credentials).
   */
  boolean isServiceHealthy() throws TransientException;
}
