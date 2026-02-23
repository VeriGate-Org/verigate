/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain;

/** The known methods of collecting payments. */
public enum CollectionMethod {
  DIRECT_DEBIT,
  EFT,
  CREDIT_CARD,
  SASSA_GRANT
}
