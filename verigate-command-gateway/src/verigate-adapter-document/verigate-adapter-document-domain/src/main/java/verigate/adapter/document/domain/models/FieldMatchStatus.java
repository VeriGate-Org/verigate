/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.domain.models;

/**
 * Result of comparing a single extracted document field against the corresponding live CIPC
 * registry value.
 */
public enum FieldMatchStatus {
  /** Extracted value and CIPC value agree after normalization. */
  MATCH,

  /** Extracted value and CIPC value disagree. */
  MISMATCH,

  /** The field was not extracted from the document, so no comparison could be made. */
  NOT_EXTRACTED,

  /** CIPC did not return a value for this field, so no comparison could be made. */
  NOT_IN_CIPC_RECORD,

  /** The CIPC lookup could not be performed at all (company not found or CIPC unreachable). */
  NOT_AVAILABLE
}
