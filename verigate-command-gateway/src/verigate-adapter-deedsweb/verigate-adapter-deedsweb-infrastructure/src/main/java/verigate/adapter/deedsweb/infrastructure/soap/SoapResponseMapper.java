/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.soap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.deedsweb.domain.models.DeedsPropertyType;
import verigate.adapter.deedsweb.domain.models.OfficeRegistry;
import verigate.adapter.deedsweb.domain.models.PersonDetails;
import verigate.adapter.deedsweb.domain.models.PersonFullProperty;
import verigate.adapter.deedsweb.domain.models.PropertyDetails;
import verigate.adapter.deedsweb.domain.models.PropertyEndorsement;
import verigate.adapter.deedsweb.domain.models.PropertyHistoryEntry;
import verigate.adapter.deedsweb.infrastructure.soap.generated.AgriculturalHoldingAreaPropertyDetailResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.AgriculturalHoldingAreaPropertyInformationResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.AgriculturalHoldingDetailResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.AgriculturalHoldingPropertyInformationResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.CompanyDetailsResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.CompanyInformationResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.ErfPropertyDetailResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.ErfPropertyInformationResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.ErrorResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.ExclusiveUseAreaFullPropertyDetailResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.ExclusiveUseAreaPropertyInformationResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.FarmPropertyDetailsResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.FarmPropertyInformationResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.FullPersonPropertyInformationResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.OfficeRegistryInformationResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.PersonDetailsResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.PersonInformationResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.PropertyEndorsementDetailsResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.PropertyHistoryDetailsResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.PropertyOwnerDetailsResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.PropertyResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.PropertySummaryResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.PropertyTypeInformationResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.SchemeFullPropertyDetailResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.SchemePropertyInformationResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.TitleDeedDetailsResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.TownshipPropertyDetailResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.TownshipPropertyInformationResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pure functions that map CXF-generated SOAP response objects to domain models.
 *
 * <p>The DeedsWeb registry returns dates as free-form strings — most often
 * {@code yyyy-MM-dd} but occasionally {@code dd/MM/yyyy}. Numeric fields are
 * also strings; we attempt to parse them and silently degrade to {@code null}
 * on failure to keep partial data available.</p>
 */
public final class SoapResponseMapper {

  private static final Logger LOGGER = LoggerFactory.getLogger(SoapResponseMapper.class);

  private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
      DateTimeFormatter.ISO_LOCAL_DATE,
      DateTimeFormatter.ofPattern("dd/MM/yyyy"),
      DateTimeFormatter.ofPattern("dd-MM-yyyy"),
      DateTimeFormatter.ofPattern("yyyy/MM/dd"));

  private SoapResponseMapper() {}

  // ----- Office / property type -----

  public static List<OfficeRegistry> mapOffices(List<OfficeRegistryInformationResponse> raw) {
    if (raw == null) {
      return Collections.emptyList();
    }
    List<OfficeRegistry> out = new ArrayList<>(raw.size());
    for (OfficeRegistryInformationResponse r : raw) {
      if (r == null) {
        continue;
      }
      out.add(new OfficeRegistry(r.getOfficeCode(), r.getFullDescription()));
    }
    return List.copyOf(out);
  }

  public static List<DeedsPropertyType> mapPropertyTypes(
      List<PropertyTypeInformationResponse> raw) {
    if (raw == null) {
      return Collections.emptyList();
    }
    List<DeedsPropertyType> out = new ArrayList<>(raw.size());
    for (PropertyTypeInformationResponse r : raw) {
      if (r == null) {
        continue;
      }
      out.add(new DeedsPropertyType(r.getPropertyCode(), r.getPropertyDescription()));
    }
    return List.copyOf(out);
  }

  // ----- Person / property summary (id-number / company endpoints) -----

  /**
   * Flattens a list of {@code personInformationResponse} envelopes into a flat list of
   * {@link PropertyDetails}. Owner/person info is propagated onto each summary.
   */
  public static List<PropertyDetails> mapPersonSummaries(List<PersonInformationResponse> raw) {
    if (raw == null) {
      return Collections.emptyList();
    }
    List<PropertyDetails> out = new ArrayList<>();
    for (PersonInformationResponse env : raw) {
      if (env == null) {
        continue;
      }
      logIfError(env.getErrorResponse(), "personInformationResponse");
      PersonDetailsResponse person = env.getPersonFullDetailsResponse();
      String ownerName = person == null ? null : person.getFullName();
      String ownerId = person == null ? null : person.getIdNumber();
      for (PropertySummaryResponse summary : env.getPropertySummaryResponseList()) {
        PropertyDetails mapped = mapSummary(summary, ownerName, ownerId);
        if (mapped != null) {
          out.add(mapped);
        }
      }
    }
    return out;
  }

  /**
   * Flattens a list of {@code companyInformationResponse} envelopes into a flat list of
   * {@link PropertyDetails}, propagating company name/registration as the registered owner.
   */
  public static List<PropertyDetails> mapCompanySummaries(List<CompanyInformationResponse> raw) {
    if (raw == null) {
      return Collections.emptyList();
    }
    List<PropertyDetails> out = new ArrayList<>();
    for (CompanyInformationResponse env : raw) {
      if (env == null) {
        continue;
      }
      logIfError(env.getErrorResponse(), "companyInformationResponse");
      CompanyDetailsResponse company = env.getCompanyDetailsResponse();
      String ownerName = company == null ? null : company.getCompanyName();
      String ownerId = company == null ? null : company.getCompanyRegistrationNumber();
      for (PropertySummaryResponse summary : env.getPropertySummaryResponseList()) {
        PropertyDetails mapped = mapSummary(summary, ownerName, ownerId);
        if (mapped != null) {
          out.add(mapped);
        }
      }
    }
    return out;
  }

  private static PropertyDetails mapSummary(
      PropertySummaryResponse summary, String ownerName, String ownerId) {
    if (summary == null) {
      return null;
    }
    logIfError(summary.getErrorResponse(), "propertySummaryResponse");
    String propertyDescription = composeSummaryDescription(summary);
    return new PropertyDetails.Builder()
        .deedNumber(summary.getTitleDeedNumber())
        .titleDeedReference(summary.getTitleDeedNumber())
        .propertyDescription(propertyDescription)
        .registrationDivision(summary.getTownship())
        .registeredOwnerName(ownerName)
        .registeredOwnerIdNumber(ownerId)
        .registrationDate(parseDate(summary.getRegistrationDate()))
        .transferDate(parseDate(summary.getPurchaseDate()))
        .purchasePrice(parseDouble(summary.getPrice()))
        .build();
  }

  private static String composeSummaryDescription(PropertySummaryResponse summary) {
    StringBuilder sb = new StringBuilder();
    appendIfPresent(sb, summary.getPropertyTypeDescription());
    appendIfPresent(sb, summary.getErf());
    appendIfPresent(sb, summary.getTownship());
    String composed = sb.toString().trim();
    return composed.isEmpty() ? null : composed;
  }

  private static void appendIfPresent(StringBuilder sb, String value) {
    if (value != null && !value.isBlank()) {
      if (sb.length() > 0) {
        sb.append(' ');
      }
      sb.append(value.trim());
    }
  }

  // ----- Full person + property aggregates -----

  public static List<PersonFullProperty> mapFullPersonResponses(
      List<FullPersonPropertyInformationResponse> raw) {
    if (raw == null) {
      return Collections.emptyList();
    }
    List<PersonFullProperty> out = new ArrayList<>(raw.size());
    for (FullPersonPropertyInformationResponse env : raw) {
      PersonFullProperty mapped = mapFullPerson(env);
      if (mapped != null) {
        out.add(mapped);
      }
    }
    return out;
  }

  public static PersonFullProperty mapFullPerson(FullPersonPropertyInformationResponse env) {
    if (env == null) {
      return null;
    }
    logIfError(env.getErrorResponse(), "fullPersonPropertyInformationResponse");
    PersonDetails person = mapPersonDetails(env.getPersonFullDetailsResponse());
    List<PropertyDetails> properties =
        mapFullProperties(
            env.getPropertyDetailsResponseList(),
            env.getPropertyOwnerInfoResponseList(),
            env.getTitleDeedInfoResponseList());
    List<PropertyEndorsement> endorsements = mapEndorsements(env.getPropertyEndorsementList());
    List<PropertyHistoryEntry> history = mapHistory(env.getHistoryInfoResponseList());
    return PersonFullProperty.builder()
        .person(person)
        .properties(properties)
        .endorsements(endorsements)
        .history(history)
        .build();
  }

  /**
   * Merges multiple per-office {@link PersonFullProperty} records (collected via fan-out)
   * into a single aggregate. The first non-null person record wins (the same person, just
   * fetched from different offices); property/endorsement/history lists are concatenated.
   */
  public static PersonFullProperty combineFullPersonRecords(List<PersonFullProperty> records) {
    if (records == null || records.isEmpty()) {
      return null;
    }
    PersonDetails person = null;
    List<PropertyDetails> properties = new ArrayList<>();
    List<PropertyEndorsement> endorsements = new ArrayList<>();
    List<PropertyHistoryEntry> history = new ArrayList<>();
    for (PersonFullProperty record : records) {
      if (record == null) {
        continue;
      }
      if (person == null && record.getPerson() != null) {
        person = record.getPerson();
      }
      if (record.getProperties() != null) {
        properties.addAll(record.getProperties());
      }
      if (record.getEndorsements() != null) {
        endorsements.addAll(record.getEndorsements());
      }
      if (record.getHistory() != null) {
        history.addAll(record.getHistory());
      }
    }
    return PersonFullProperty.builder()
        .person(person)
        .properties(properties)
        .endorsements(endorsements)
        .history(history)
        .build();
  }

  private static PersonDetails mapPersonDetails(PersonDetailsResponse src) {
    if (src == null) {
      return null;
    }
    logIfError(src.getErrorResponse(), "personDetailsResponse");
    return PersonDetails.builder()
        .fullName(src.getFullName())
        .formerName(src.getFormerName())
        .idNumber(src.getIdNumber())
        .maritalStatus(src.getMaritalStatus())
        .personType(src.getPersonType())
        .personTypeCode(src.getPersonTypeCode())
        .build();
  }

  /**
   * Combines {@code propertyResponse} (location), {@code propertyOwnerDetailsResponse} (owner),
   * and {@code titleDeedDetailsResponse} (commercial details) into one {@link PropertyDetails}
   * per property. Lists are zipped positionally — DeedsWeb returns them in matching order.
   */
  private static List<PropertyDetails> mapFullProperties(
      List<PropertyResponse> properties,
      List<PropertyOwnerDetailsResponse> owners,
      List<TitleDeedDetailsResponse> titleDeeds) {
    if (properties == null || properties.isEmpty()) {
      return Collections.emptyList();
    }
    List<PropertyDetails> out = new ArrayList<>(properties.size());
    for (int i = 0; i < properties.size(); i++) {
      PropertyResponse prop = properties.get(i);
      PropertyOwnerDetailsResponse owner = pick(owners, i);
      TitleDeedDetailsResponse title = pick(titleDeeds, i);
      logIfError(prop == null ? null : prop.getErrorResponse(), "propertyResponse");
      logIfError(owner == null ? null : owner.getErrorResponse(), "propertyOwnerDetailsResponse");
      logIfError(title == null ? null : title.getErrorResponse(), "titleDeedDetailsResponse");
      PropertyDetails.Builder builder = new PropertyDetails.Builder();
      if (prop != null) {
        builder
            .registrationDivision(prop.getRegistrationDivision())
            .province(prop.getProvince())
            .propertyDescription(composePropertyDescription(prop));
      }
      if (owner != null) {
        builder
            .registeredOwnerName(owner.getFullName())
            .registeredOwnerIdNumber(owner.getIdNumber())
            .titleDeedReference(owner.getTitleDeed());
      }
      if (title != null) {
        builder
            .deedNumber(title.getTitleDeed())
            .registrationDate(parseDate(title.getRegistrationDate()))
            .transferDate(parseDate(title.getPurchaseDate()))
            .purchasePrice(parseDouble(title.getPurchasePrice()));
      }
      out.add(builder.build());
    }
    return out;
  }

  private static String composePropertyDescription(PropertyResponse prop) {
    StringBuilder sb = new StringBuilder();
    appendIfPresent(sb, prop.getPropertyTypeDescription());
    appendIfPresent(sb, prop.getPortion());
    appendIfPresent(sb, prop.getRegistrationDivision());
    appendIfPresent(sb, prop.getLocalAuthority());
    String composed = sb.toString().trim();
    return composed.isEmpty() ? null : composed;
  }

  private static <T> T pick(List<T> list, int index) {
    return (list != null && index < list.size()) ? list.get(index) : null;
  }

  // ----- Detail-shaped responses (erf / farm / scheme / township / ag / exclusive use) -----

  public static List<PropertyDetails> mapErf(List<ErfPropertyInformationResponse> raw) {
    if (raw == null) {
      return Collections.emptyList();
    }
    List<PropertyDetails> out = new ArrayList<>();
    for (ErfPropertyInformationResponse env : raw) {
      if (env == null) {
        continue;
      }
      logIfError(env.getErrorResponse(), "erfPropertyInformationResponse");
      ErfPropertyDetailResponse detail = env.getErfPropertyDetailResponse();
      String description = detail == null ? null : composeErfDescription(detail);
      String extent = detail == null ? null : detail.getExtentValue();
      out.addAll(
          combineDetailWith(
              description,
              extent,
              env.getPropertyOwnerInfoResponseList(),
              env.getTitleDeedInfoResponseList(),
              env.getPropertyEndorsementList()));
    }
    return out;
  }

  private static String composeErfDescription(ErfPropertyDetailResponse detail) {
    StringBuilder sb = new StringBuilder("Erf");
    appendIfPresent(sb, detail.getErfNumber());
    appendIfPresent(sb, detail.getTownshipName());
    return sb.toString();
  }

  public static List<PropertyDetails> mapFarm(List<FarmPropertyInformationResponse> raw) {
    if (raw == null) {
      return Collections.emptyList();
    }
    List<PropertyDetails> out = new ArrayList<>();
    for (FarmPropertyInformationResponse env : raw) {
      if (env == null) {
        continue;
      }
      logIfError(env.getErrorResponse(), "farmPropertyInformationResponse");
      FarmPropertyDetailsResponse detail = env.getFarmPropertyDetailsResponse();
      String description = detail == null ? null : composeFarmDescription(detail);
      String extent = detail == null ? null : detail.getExtentValue();
      String division = detail == null ? null : detail.getRegistrationDivision();
      String province = detail == null ? null : detail.getProvince();
      out.addAll(
          combineDetailWithLocation(
              description,
              extent,
              division,
              province,
              env.getPropertyOwnerInfoResponseList(),
              env.getTitleDeedInfoResponseList(),
              env.getPropertyEndorsementList()));
    }
    return out;
  }

  private static String composeFarmDescription(FarmPropertyDetailsResponse detail) {
    StringBuilder sb = new StringBuilder("Farm");
    appendIfPresent(sb, detail.getFarmName());
    appendIfPresent(sb, detail.getFarmNumber());
    if (detail.getPortion() != null && !detail.getPortion().isBlank()) {
      sb.append(" Portion ").append(detail.getPortion().trim());
    }
    return sb.toString();
  }

  public static List<PropertyDetails> mapTownship(List<TownshipPropertyInformationResponse> raw) {
    if (raw == null) {
      return Collections.emptyList();
    }
    List<PropertyDetails> out = new ArrayList<>();
    for (TownshipPropertyInformationResponse env : raw) {
      if (env == null) {
        continue;
      }
      logIfError(env.getErrorResponse(), "townshipPropertyInformationResponse");
      TownshipPropertyDetailResponse detail = env.getTownshipPropertyDetailResponse();
      String description = detail == null ? null : "Township " + nullToEmpty(safeTownshipName(detail));
      out.addAll(
          combineDetailWith(
              description == null ? null : description.trim(),
              null,
              env.getPropertyOwnerInfoResponseList(),
              env.getTitleDeedInfoResponseList(),
              env.getPropertyEndorsementList()));
    }
    return out;
  }

  /**
   * Reflectively pulls a township name regardless of how the WSDL spelled the field
   * (CXF generates fields lazily based on the schema, so we tolerate either casing).
   */
  private static String safeTownshipName(TownshipPropertyDetailResponse detail) {
    try {
      return (String) detail.getClass().getMethod("getTownshipName").invoke(detail);
    } catch (ReflectiveOperationException ignored) {
      return "";
    }
  }

  public static List<PropertyDetails> mapAgricultural(
      List<AgriculturalHoldingPropertyInformationResponse> raw) {
    if (raw == null) {
      return Collections.emptyList();
    }
    List<PropertyDetails> out = new ArrayList<>();
    for (AgriculturalHoldingPropertyInformationResponse env : raw) {
      if (env == null) {
        continue;
      }
      logIfError(env.getErrorResponse(), "agriculturalHoldingPropertyInformationResponse");
      AgriculturalHoldingDetailResponse detail = env.getAgriculturalHoldingPropertyDetailResponse();
      String description =
          detail == null ? "Agricultural Holding" : "Agricultural Holding " + describeAg(detail);
      out.addAll(
          combineDetailWith(
              description,
              null,
              env.getPropertyOwnerInfoResponseList(),
              env.getTitleDeedInfoList(),
              env.getPropertyEndorsementResponseList()));
    }
    return out;
  }

  private static String describeAg(AgriculturalHoldingDetailResponse detail) {
    try {
      Object name = detail.getClass().getMethod("getAgriculturalHoldingName").invoke(detail);
      Object number = detail.getClass().getMethod("getAgriculturalHoldingNumber").invoke(detail);
      return (name == null ? "" : name) + " " + (number == null ? "" : number);
    } catch (ReflectiveOperationException ignored) {
      return "";
    }
  }

  public static List<PropertyDetails> mapAgriculturalArea(
      List<AgriculturalHoldingAreaPropertyInformationResponse> raw) {
    if (raw == null) {
      return Collections.emptyList();
    }
    List<PropertyDetails> out = new ArrayList<>();
    for (AgriculturalHoldingAreaPropertyInformationResponse env : raw) {
      if (env == null) {
        continue;
      }
      // Field names vary by WSDL revision; use reflection defensively.
      logIfError(extractError(env), "agriculturalHoldingAreaPropertyInformationResponse");
      AgriculturalHoldingAreaPropertyDetailResponse detail = extractAreaDetail(env);
      String description =
          detail == null ? "Agricultural Holding Area" : "Agricultural Holding Area";
      out.addAll(
          combineDetailWith(
              description,
              null,
              extractOwners(env),
              extractTitleDeeds(env),
              extractEndorsements(env)));
    }
    return out;
  }

  public static List<PropertyDetails> mapScheme(List<SchemePropertyInformationResponse> raw) {
    if (raw == null) {
      return Collections.emptyList();
    }
    List<PropertyDetails> out = new ArrayList<>();
    for (SchemePropertyInformationResponse env : raw) {
      if (env == null) {
        continue;
      }
      logIfError(env.getErrorResponse(), "schemePropertyInformationResponse");
      SchemeFullPropertyDetailResponse detail = env.getSchemePropertyDetailResponse();
      String description = detail == null ? "Scheme" : "Scheme";
      out.addAll(
          combineDetailWith(
              description,
              null,
              env.getPropertyOwnerDetailsResponseList(),
              env.getTitleDeedInfoList(),
              env.getPropertyEndorsementResponseList()));
    }
    return out;
  }

  public static List<PropertyDetails> mapExclusiveUse(
      List<ExclusiveUseAreaPropertyInformationResponse> raw) {
    if (raw == null) {
      return Collections.emptyList();
    }
    List<PropertyDetails> out = new ArrayList<>();
    for (ExclusiveUseAreaPropertyInformationResponse env : raw) {
      if (env == null) {
        continue;
      }
      logIfError(env.getErrorResponse(), "exclusiveUseAreaPropertyInformationResponse");
      ExclusiveUseAreaFullPropertyDetailResponse detail =
          env.getExclusiveUseAreaFullPropertyDetailResponse();
      String description = detail == null ? "Exclusive Use Area" : "Exclusive Use Area";
      out.addAll(
          combineDetailWith(
              description,
              null,
              env.getPropertyOwnerInfoResponseList(),
              env.getTitleDeedInfoList(),
              env.getPropertyEndorsementList()));
    }
    return out;
  }

  // ----- Combiners shared by detail-shaped responses -----

  private static List<PropertyDetails> combineDetailWith(
      String description,
      String extent,
      List<PropertyOwnerDetailsResponse> owners,
      List<TitleDeedDetailsResponse> titleDeeds,
      List<PropertyEndorsementDetailsResponse> endorsements) {
    return combineDetailWithLocation(
        description, extent, null, null, owners, titleDeeds, endorsements);
  }

  private static List<PropertyDetails> combineDetailWithLocation(
      String description,
      String extent,
      String registrationDivision,
      String province,
      List<PropertyOwnerDetailsResponse> owners,
      List<TitleDeedDetailsResponse> titleDeeds,
      List<PropertyEndorsementDetailsResponse> endorsements) {
    if ((owners == null || owners.isEmpty()) && (titleDeeds == null || titleDeeds.isEmpty())) {
      // Detail with no owner/title rows — emit a single record with whatever description we have.
      PropertyDetails.Builder builder = new PropertyDetails.Builder()
          .propertyDescription(description)
          .extent(extent)
          .registrationDivision(registrationDivision)
          .province(province);
      applyFirstEndorsement(builder, endorsements);
      return List.of(builder.build());
    }
    int count = Math.max(owners == null ? 0 : owners.size(), titleDeeds == null ? 0 : titleDeeds.size());
    List<PropertyDetails> out = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      PropertyOwnerDetailsResponse owner = pick(owners, i);
      TitleDeedDetailsResponse title = pick(titleDeeds, i);
      logIfError(owner == null ? null : owner.getErrorResponse(), "propertyOwnerDetailsResponse");
      logIfError(title == null ? null : title.getErrorResponse(), "titleDeedDetailsResponse");
      PropertyDetails.Builder builder = new PropertyDetails.Builder()
          .propertyDescription(description)
          .extent(extent)
          .registrationDivision(registrationDivision)
          .province(province);
      if (owner != null) {
        builder
            .registeredOwnerName(owner.getFullName())
            .registeredOwnerIdNumber(owner.getIdNumber())
            .titleDeedReference(owner.getTitleDeed());
      }
      if (title != null) {
        builder
            .deedNumber(title.getTitleDeed())
            .registrationDate(parseDate(title.getRegistrationDate()))
            .transferDate(parseDate(title.getPurchaseDate()))
            .purchasePrice(parseDouble(title.getPurchasePrice()));
      }
      // First-seen endorsement gets attached to the first record; this matches what most
      // partners care about (the active bond on the property).
      if (i == 0) {
        applyFirstEndorsement(builder, endorsements);
      }
      out.add(builder.build());
    }
    return out;
  }

  private static void applyFirstEndorsement(
      PropertyDetails.Builder builder, List<PropertyEndorsementDetailsResponse> endorsements) {
    if (endorsements == null || endorsements.isEmpty()) {
      return;
    }
    for (PropertyEndorsementDetailsResponse e : endorsements) {
      if (e == null) {
        continue;
      }
      logIfError(e.getErrorResponse(), "propertyEndorsementDetailsResponse");
      Double amount = parseDouble(e.getAmount());
      if (e.getHolder() != null && !e.getHolder().isBlank()) {
        builder.bondHolder(e.getHolder()).bondAmount(amount);
        return;
      }
    }
  }

  // ----- Endorsement / history mapping -----

  public static List<PropertyEndorsement> mapEndorsements(
      List<PropertyEndorsementDetailsResponse> raw) {
    if (raw == null) {
      return Collections.emptyList();
    }
    List<PropertyEndorsement> out = new ArrayList<>(raw.size());
    for (PropertyEndorsementDetailsResponse e : raw) {
      if (e == null) {
        continue;
      }
      logIfError(e.getErrorResponse(), "propertyEndorsementDetailsResponse");
      out.add(PropertyEndorsement.builder()
          .amount(e.getAmount())
          .document(e.getDocument())
          .holder(e.getHolder())
          .microfilmReference(e.getMicrofilmReference())
          .build());
    }
    return out;
  }

  public static List<PropertyHistoryEntry> mapHistory(List<PropertyHistoryDetailsResponse> raw) {
    if (raw == null) {
      return Collections.emptyList();
    }
    List<PropertyHistoryEntry> out = new ArrayList<>(raw.size());
    for (PropertyHistoryDetailsResponse e : raw) {
      if (e == null) {
        continue;
      }
      logIfError(e.getErrorResponse(), "propertyHistoryDetailsResponse");
      out.add(PropertyHistoryEntry.builder()
          .amount(e.getAmount())
          .document(e.getDocument())
          .holder(e.getHolder())
          .microfilmReference(e.getMicrofilmReference())
          .build());
    }
    return out;
  }

  // ----- Reflection helpers (defend against minor WSDL field-name drift) -----

  private static ErrorResponse extractError(Object env) {
    return invokeOrNull(env, "getErrorResponse", ErrorResponse.class);
  }

  private static AgriculturalHoldingAreaPropertyDetailResponse extractAreaDetail(Object env) {
    return invokeOrNull(
        env,
        "getAgriculturalHoldingAreaPropertyDetailResponse",
        AgriculturalHoldingAreaPropertyDetailResponse.class);
  }

  @SuppressWarnings("unchecked")
  private static List<PropertyOwnerDetailsResponse> extractOwners(Object env) {
    Object value = invokeOrNull(env, "getPropertyOwnerInfoResponseList", Object.class);
    if (value == null) {
      value = invokeOrNull(env, "getPropertyOwnerDetailsResponseList", Object.class);
    }
    return value instanceof List ? (List<PropertyOwnerDetailsResponse>) value : Collections.emptyList();
  }

  @SuppressWarnings("unchecked")
  private static List<TitleDeedDetailsResponse> extractTitleDeeds(Object env) {
    Object value = invokeOrNull(env, "getTitleDeedInfoResponseList", Object.class);
    if (value == null) {
      value = invokeOrNull(env, "getTitleDeedInfoList", Object.class);
    }
    return value instanceof List ? (List<TitleDeedDetailsResponse>) value : Collections.emptyList();
  }

  @SuppressWarnings("unchecked")
  private static List<PropertyEndorsementDetailsResponse> extractEndorsements(Object env) {
    Object value = invokeOrNull(env, "getPropertyEndorsementList", Object.class);
    if (value == null) {
      value = invokeOrNull(env, "getPropertyEndorsementResponseList", Object.class);
    }
    return value instanceof List
        ? (List<PropertyEndorsementDetailsResponse>) value
        : Collections.emptyList();
  }

  @SuppressWarnings("unchecked")
  private static <T> T invokeOrNull(Object target, String method, Class<T> expected) {
    if (target == null) {
      return null;
    }
    try {
      Object value = target.getClass().getMethod(method).invoke(target);
      if (value == null) {
        return null;
      }
      return expected.isInstance(value) ? (T) value : null;
    } catch (ReflectiveOperationException ignored) {
      return null;
    }
  }

  // ----- Primitive parsing -----

  private static String nullToEmpty(String s) {
    return s == null ? "" : s;
  }

  private static LocalDate parseDate(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    String trimmed = value.trim();
    for (DateTimeFormatter formatter : DATE_FORMATS) {
      try {
        return LocalDate.parse(trimmed, formatter);
      } catch (DateTimeParseException ignored) {
        // try next
      }
    }
    LOGGER.debug("Unable to parse DeedsWeb date value '{}'", value);
    return null;
  }

  private static Double parseDouble(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    String cleaned = value.trim().replace(",", "").replace("R", "").trim();
    try {
      return Double.parseDouble(cleaned);
    } catch (NumberFormatException e) {
      LOGGER.debug("Unable to parse DeedsWeb numeric value '{}'", value);
      return null;
    }
  }

  private static void logIfError(ErrorResponse error, String context) {
    if (error == null) {
      return;
    }
    if (error.getErrorCode() == null
        && error.getErrorDescription() == null
        && error.getErrorMessage() == null) {
      return;
    }
    LOGGER.debug(
        "DeedsWeb {} returned errorResponse code={} desc={} message={}",
        context,
        error.getErrorCode(),
        error.getErrorDescription(),
        error.getErrorMessage());
  }
}
