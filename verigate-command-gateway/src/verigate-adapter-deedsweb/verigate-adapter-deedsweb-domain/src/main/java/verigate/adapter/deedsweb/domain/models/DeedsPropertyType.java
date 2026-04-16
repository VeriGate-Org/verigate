/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.domain.models;

/**
 * Property type returned by {@code getDeedsPropertyTypeList}. Used to qualify
 * property searches (erf, farm, scheme, etc.).
 *
 * @param propertyCode short code identifying the property type
 * @param propertyDescription human-readable description
 */
public record DeedsPropertyType(String propertyCode, String propertyDescription) {
}
