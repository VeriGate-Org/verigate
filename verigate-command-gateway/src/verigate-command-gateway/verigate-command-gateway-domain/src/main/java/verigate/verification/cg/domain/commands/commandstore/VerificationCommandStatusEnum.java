/*
 * Arthmatic + Karisani(c) 2024 - 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.domain.commands.commandstore;

/**
 * This enum represents the status of a verification command.
 */
public enum VerificationCommandStatusEnum {
  NOT_FOUND,
  PENDING,
  TRANSIENT_ERROR,
  COMPLETED,
  INVARIANT_FAILURE,
  PERMANENT_FAILURE
}
