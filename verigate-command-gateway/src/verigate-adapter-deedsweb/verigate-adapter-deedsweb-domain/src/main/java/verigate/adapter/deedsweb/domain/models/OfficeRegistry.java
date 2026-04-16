/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.domain.models;

/**
 * Deeds office returned by {@code getOfficeRegistryList}. Used by partners to
 * pin a search to a specific deeds office, and by the adapter's fan-out path
 * when no office is supplied.
 *
 * @param officeCode short code (e.g. {@code "T"} for Pretoria)
 * @param fullDescription human-readable office description
 */
public record OfficeRegistry(String officeCode, String fullDescription) {
}
