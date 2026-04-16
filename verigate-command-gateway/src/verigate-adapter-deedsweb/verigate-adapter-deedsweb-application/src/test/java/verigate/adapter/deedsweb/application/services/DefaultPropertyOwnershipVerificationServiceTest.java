/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verigate.adapter.deedsweb.domain.models.OwnershipVerificationResult;
import verigate.adapter.deedsweb.domain.models.PropertyDetails;
import verigate.adapter.deedsweb.domain.models.PropertyOwnershipCheck;
import verigate.adapter.deedsweb.domain.models.PropertySearchRequest;
import verigate.adapter.deedsweb.domain.services.DeedsRegistryClient;

class DefaultPropertyOwnershipVerificationServiceTest {

  private DeedsRegistryClient registryClient;
  private DefaultPropertyOwnershipVerificationService service;

  @BeforeEach
  void setUp() {
    registryClient = mock(DeedsRegistryClient.class);
    service = new DefaultPropertyOwnershipVerificationService(registryClient);
  }

  // ---------------- searchProperties dispatch ----------------

  @Test
  void searchProperties_ownerId_dispatchesToFindByIdNumber() throws Exception {
    PropertyDetails property = property("T1/2024", "8001015009087", "Jane Doe", "Gauteng");
    when(registryClient.findPropertiesByIdNumber(eq("8001015009087"), eq("T")))
        .thenReturn(List.of(property));

    List<PropertyDetails> result =
        service.searchProperties(
            PropertySearchRequest.builder()
                .searchType("ownerId")
                .query("8001015009087")
                .officeCode("T")
                .build());

    assertEquals(1, result.size());
    assertEquals("T1/2024", result.get(0).getDeedNumber());
    verify(registryClient).findPropertiesByIdNumber("8001015009087", "T");
  }

  @Test
  void searchProperties_company_dispatchesToFindByCompany() throws Exception {
    PropertyDetails property = property("J9/2024", "9999999999999", "Acme Pty Ltd", "Gauteng");
    when(registryClient.findPropertiesByCompany(eq("Acme Pty Ltd"), eq("Acme Pty Ltd"), eq("J")))
        .thenReturn(List.of(property));

    List<PropertyDetails> result =
        service.searchProperties(
            PropertySearchRequest.builder()
                .searchType("company")
                .query("Acme Pty Ltd")
                .officeCode("J")
                .build());

    assertEquals(1, result.size());
    verify(registryClient).findPropertiesByCompany("Acme Pty Ltd", "Acme Pty Ltd", "J");
  }

  @Test
  void searchProperties_ownerName_returnsEmptyAndDoesNotCallClient() throws Exception {
    List<PropertyDetails> result =
        service.searchProperties(
            PropertySearchRequest.builder().searchType("ownerName").query("Jane").build());

    assertTrue(result.isEmpty());
    verify(registryClient, times(0))
        .findPropertiesByIdNumber(eq("Jane"), org.mockito.ArgumentMatchers.any());
  }

  @Test
  void searchProperties_unknownType_defaultsToFindByIdNumber() throws Exception {
    PropertyDetails property = property("T1/2024", "8001015009087", "Jane Doe", "Gauteng");
    when(registryClient.findPropertiesByIdNumber(eq("8001015009087"), isNull()))
        .thenReturn(List.of(property));

    List<PropertyDetails> result =
        service.searchProperties(
            PropertySearchRequest.builder()
                .searchType("anythingElse")
                .query("8001015009087")
                .build());

    assertEquals(1, result.size());
  }

  @Test
  void searchProperties_nullRequest_returnsEmpty() {
    assertTrue(service.searchProperties(null).isEmpty());
  }

  @Test
  void searchProperties_blankQuery_returnsEmpty() {
    assertTrue(
        service
            .searchProperties(
                PropertySearchRequest.builder().searchType("ownerId").query("   ").build())
            .isEmpty());
  }

  // ---------------- Province filter ----------------

  @Test
  void searchProperties_provinceFiltersResults() throws Exception {
    PropertyDetails gauteng = property("T1/2024", "8001015009087", "Jane Doe", "Gauteng");
    PropertyDetails knz = property("J2/2024", "8001015009087", "Jane Doe", "KwaZulu-Natal");
    when(registryClient.findPropertiesByIdNumber(eq("8001015009087"), isNull()))
        .thenReturn(List.of(gauteng, knz));

    List<PropertyDetails> result =
        service.searchProperties(
            PropertySearchRequest.builder()
                .searchType("ownerId")
                .query("8001015009087")
                .province("Gauteng")
                .build());

    assertEquals(1, result.size());
    assertEquals("T1/2024", result.get(0).getDeedNumber());
  }

  @Test
  void searchProperties_provinceMatchesNone_fallsBackToUnfiltered() throws Exception {
    PropertyDetails gauteng = property("T1/2024", "8001015009087", "Jane Doe", "Gauteng");
    when(registryClient.findPropertiesByIdNumber(eq("8001015009087"), isNull()))
        .thenReturn(List.of(gauteng));

    List<PropertyDetails> result =
        service.searchProperties(
            PropertySearchRequest.builder()
                .searchType("ownerId")
                .query("8001015009087")
                .province("Limpopo")
                .build());

    // No matches — falls back to province-unfiltered list, then ID filter applies.
    assertEquals(1, result.size());
  }

  // ---------------- Search-type filter ----------------

  @Test
  void searchProperties_ownerIdFilterDropsUnmatchedOwners() throws Exception {
    PropertyDetails matching = property("T1/2024", "8001015009087", "Jane Doe", "Gauteng");
    PropertyDetails other = property("T2/2024", "9999999999999", "Bob Other", "Gauteng");
    when(registryClient.findPropertiesByIdNumber(eq("8001015009087"), isNull()))
        .thenReturn(List.of(matching, other));

    List<PropertyDetails> result =
        service.searchProperties(
            PropertySearchRequest.builder()
                .searchType("ownerId")
                .query("8001015009087")
                .build());

    assertEquals(1, result.size());
    assertEquals("T1/2024", result.get(0).getDeedNumber());
  }

  // ---------------- Error mapping ----------------

  @Test
  void searchProperties_transientException_propagatesAsRuntime() throws Exception {
    when(registryClient.findPropertiesByIdNumber(eq("8001015009087"), isNull()))
        .thenThrow(new TransientException("boom"));

    RuntimeException ex =
        assertThrows(
            RuntimeException.class,
            () ->
                service.searchProperties(
                    PropertySearchRequest.builder()
                        .searchType("ownerId")
                        .query("8001015009087")
                        .build()));
    assertTrue(ex.getCause() instanceof TransientException);
  }

  @Test
  void searchProperties_permanentException_propagatesAsRuntime() throws Exception {
    when(registryClient.findPropertiesByIdNumber(eq("8001015009087"), isNull()))
        .thenThrow(new PermanentException("nope"));

    RuntimeException ex =
        assertThrows(
            RuntimeException.class,
            () ->
                service.searchProperties(
                    PropertySearchRequest.builder()
                        .searchType("ownerId")
                        .query("8001015009087")
                        .build()));
    assertTrue(ex.getCause() instanceof PermanentException);
  }

  // ---------------- findPropertiesByOwner ----------------

  @Test
  void findPropertiesByOwner_delegatesWithOwnerIdSearchType() throws Exception {
    PropertyDetails property = property("T1/2024", "8001015009087", "Jane Doe", "Gauteng");
    when(registryClient.findPropertiesByIdNumber(eq("8001015009087"), isNull()))
        .thenReturn(List.of(property));

    List<PropertyDetails> result = service.findPropertiesByOwner("8001015009087");

    assertEquals(1, result.size());
    verify(registryClient).findPropertiesByIdNumber("8001015009087", null);
  }

  // ---------------- checkOwnership ----------------

  @Test
  void checkOwnership_idMatch_confirmedWithFullConfidenceOnNameMatch() {
    PropertyDetails property = property("T1/2024", "8001015009087", "Jane Doe", "Gauteng");

    OwnershipVerificationResult result =
        service.checkOwnership("8001015009087", "Jane Doe", List.of(property));

    assertTrue(result.isOwnershipConfirmed());
    assertEquals(1.0, result.getMatchConfidence(), 0.0001);
  }

  @Test
  void checkOwnership_idMatch_partialNameMatchUsesPartialConfidence() {
    PropertyDetails property = property("T1/2024", "8001015009087", "Jane M Doe", "Gauteng");

    OwnershipVerificationResult result =
        service.checkOwnership("8001015009087", "Jane Doe", List.of(property));

    assertTrue(result.isOwnershipConfirmed());
    // Substring match → 0.9
    assertEquals(0.9, result.getMatchConfidence(), 0.0001);
  }

  @Test
  void checkOwnership_idMismatch_returnsOwnerMismatch() {
    PropertyDetails property = property("T1/2024", "9999999999999", "Bob Other", "Gauteng");

    OwnershipVerificationResult result =
        service.checkOwnership("8001015009087", "Jane Doe", List.of(property));

    assertFalse(result.isOwnershipConfirmed());
  }

  @Test
  void checkOwnership_emptyList_returnsNotFound() {
    OwnershipVerificationResult result =
        service.checkOwnership("8001015009087", "Jane Doe", Collections.emptyList());

    assertFalse(result.isOwnershipConfirmed());
  }

  // ---------------- verifyOwnership end-to-end ----------------

  @Test
  void verifyOwnership_returnsOwnershipCheckWithMatchedProperty() throws Exception {
    PropertyDetails property = property("T1/2024", "8001015009087", "Jane Doe", "Gauteng");
    when(registryClient.findPropertiesByIdNumber(eq("8001015009087"), isNull()))
        .thenReturn(List.of(property));

    PropertyOwnershipCheck check =
        service.verifyOwnership("8001015009087", "Jane Doe", "Erf 101 Gauteng");

    assertNotNull(check);
    assertEquals("8001015009087", check.getSubjectIdNumber());
    assertEquals(1, check.getPropertiesFound().size());
    assertTrue(check.getResult().isOwnershipConfirmed());
  }

  // ---------------- helpers ----------------

  private static PropertyDetails property(
      String deedNumber, String ownerId, String ownerName, String province) {
    return new PropertyDetails.Builder()
        .deedNumber(deedNumber)
        .titleDeedReference(deedNumber)
        .registeredOwnerIdNumber(ownerId)
        .registeredOwnerName(ownerName)
        .province(province)
        .build();
  }
}
