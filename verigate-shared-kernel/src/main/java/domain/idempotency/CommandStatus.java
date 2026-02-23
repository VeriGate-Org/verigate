/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.idempotency;

/**
 * The {@code CommandStatus} enum represents the various statuses that a command can have during its
 * lifecycle.
 */
public enum CommandStatus {

  /** Command is currently in progress. */
  PENDING,

  /** Command has been successfully completed. */
  SUCCEEDED,

  /** Command has failed during processing. */
  FAILED
}
