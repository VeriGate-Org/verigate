/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.soap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import verigate.adapter.deedsweb.domain.models.DeedsPropertyType;
import verigate.adapter.deedsweb.domain.models.OfficeRegistry;
import verigate.adapter.deedsweb.domain.models.PersonDetails;
import verigate.adapter.deedsweb.domain.models.PersonFullProperty;
import verigate.adapter.deedsweb.domain.models.PropertyDetails;
import verigate.adapter.deedsweb.infrastructure.soap.generated.OfficeRegistryInformationResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.PersonDetailsResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.PersonInformationResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.PropertySummaryResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.PropertyTypeInformationResponse;

class SoapResponseMapperTest {

  // ---------------- Office / property type ----------------

  @Test
  void mapOffices_translatesOfficeCodeAndDescription() {
    OfficeRegistryInformationResponse a = new OfficeRegistryInformationResponse();
    a.setOfficeCode("T");
    a.setFullDescription("Pretoria");
    OfficeRegistryInformationResponse b = new OfficeRegistryInformationResponse();
    b.setOfficeCode("J");
    b.setFullDescription("Johannesburg");

    List<OfficeRegistry> offices = SoapResponseMapper.mapOffices(List.of(a, b));

    assertEquals(2, offices.size());
    assertEquals("T", offices.get(0).officeCode());
    assertEquals("Pretoria", offices.get(0).fullDescription());
    assertEquals("J", offices.get(1).officeCode());
    assertEquals("Johannesburg", offices.get(1).fullDescription());
  }

  @Test
  void mapOffices_nullInput_returnsEmpty() {
    assertTrue(SoapResponseMapper.mapOffices(null).isEmpty());
  }

  @Test
  void mapPropertyTypes_translatesCodeAndDescription() {
    PropertyTypeInformationResponse type = new PropertyTypeInformationResponse();
    type.setPropertyCode("E");
    type.setPropertyDescription("Erf");

    List<DeedsPropertyType> types = SoapResponseMapper.mapPropertyTypes(List.of(type));

    assertEquals(1, types.size());
    assertEquals("E", types.get(0).propertyCode());
    assertEquals("Erf", types.get(0).propertyDescription());
  }

  // ---------------- Person summaries ----------------

  @Test
  void mapPersonSummaries_propagatesOwnerInfoAndParsesDates() {
    PersonInformationResponse env = new PersonInformationResponse();
    PersonDetailsResponse person = new PersonDetailsResponse();
    person.setFullName("Jane Doe");
    person.setIdNumber("8001015009087");
    env.setPersonFullDetailsResponse(person);

    PropertySummaryResponse summary = new PropertySummaryResponse();
    summary.setTitleDeedNumber("T12345/2024");
    summary.setRegistrationDate("2024-03-11");
    summary.setPurchaseDate("11/03/2024");
    summary.setPrice("R 1,250,000");
    summary.setErf("Erf 101");
    summary.setTownship("Pretoria");
    summary.setPropertyTypeDescription("Residential");
    env.getPropertySummaryResponseList().add(summary);

    List<PropertyDetails> result = SoapResponseMapper.mapPersonSummaries(List.of(env));

    assertEquals(1, result.size());
    PropertyDetails property = result.get(0);
    assertEquals("T12345/2024", property.getDeedNumber());
    assertEquals("T12345/2024", property.getTitleDeedReference());
    assertEquals("Jane Doe", property.getRegisteredOwnerName());
    assertEquals("8001015009087", property.getRegisteredOwnerIdNumber());
    assertEquals(LocalDate.of(2024, 3, 11), property.getRegistrationDate());
    assertEquals(LocalDate.of(2024, 3, 11), property.getTransferDate());
    assertEquals(1_250_000.0, property.getPurchasePrice());
    assertNotNull(property.getPropertyDescription());
    assertTrue(property.getPropertyDescription().contains("Residential"));
    assertTrue(property.getPropertyDescription().contains("Erf 101"));
    assertTrue(property.getPropertyDescription().contains("Pretoria"));
  }

  @Test
  void mapPersonSummaries_unparseableDate_returnsNullDate() {
    PersonInformationResponse env = new PersonInformationResponse();
    PropertySummaryResponse summary = new PropertySummaryResponse();
    summary.setTitleDeedNumber("T1/2024");
    summary.setRegistrationDate("not-a-date");
    summary.setPrice("invalid");
    env.getPropertySummaryResponseList().add(summary);

    List<PropertyDetails> result = SoapResponseMapper.mapPersonSummaries(List.of(env));

    assertEquals(1, result.size());
    assertNull(result.get(0).getRegistrationDate());
    assertNull(result.get(0).getPurchasePrice());
  }

  @Test
  void mapPersonSummaries_emptyList_returnsEmpty() {
    assertTrue(SoapResponseMapper.mapPersonSummaries(List.of()).isEmpty());
    assertTrue(SoapResponseMapper.mapPersonSummaries(null).isEmpty());
  }

  // ---------------- combineFullPersonRecords ----------------

  @Test
  void combineFullPersonRecords_mergesPropertiesAcrossOffices() {
    PersonDetails person1 =
        PersonDetails.builder().fullName("Jane Doe").idNumber("8001015009087").build();
    PersonDetails person2 =
        PersonDetails.builder().fullName("Jane Doe").idNumber("8001015009087").build();

    PropertyDetails propertyA =
        new PropertyDetails.Builder().deedNumber("T1/2024").build();
    PropertyDetails propertyB =
        new PropertyDetails.Builder().deedNumber("J2/2024").build();

    PersonFullProperty fromOfficeT =
        PersonFullProperty.builder().person(person1).properties(List.of(propertyA)).build();
    PersonFullProperty fromOfficeJ =
        PersonFullProperty.builder().person(person2).properties(List.of(propertyB)).build();

    PersonFullProperty combined =
        SoapResponseMapper.combineFullPersonRecords(List.of(fromOfficeT, fromOfficeJ));

    assertNotNull(combined);
    assertEquals("Jane Doe", combined.getPerson().getFullName());
    assertEquals(2, combined.getProperties().size());
  }

  @Test
  void combineFullPersonRecords_nullOrEmpty_returnsNull() {
    assertNull(SoapResponseMapper.combineFullPersonRecords(null));
    assertNull(SoapResponseMapper.combineFullPersonRecords(List.of()));
  }

  @Test
  void mapEndorsements_skipsNullEntriesAndKeepsOthers() {
    var endorsement =
        new verigate.adapter.deedsweb.infrastructure.soap.generated.PropertyEndorsementDetailsResponse();
    endorsement.setHolder("ABSA");
    endorsement.setAmount("900000.00");
    endorsement.setDocument("Bond");

    var endorsements = SoapResponseMapper.mapEndorsements(List.of(endorsement));

    assertEquals(1, endorsements.size());
    assertEquals("ABSA", endorsements.get(0).getHolder());
    assertEquals("900000.00", endorsements.get(0).getAmount());
  }
}
