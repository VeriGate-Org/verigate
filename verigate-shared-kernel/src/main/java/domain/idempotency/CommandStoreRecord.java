/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.idempotency;

import java.util.UUID;

/**
 * The {@code CommandStoreRecord} class represents a record in a command store, encapsulating a
 * unique identifier and the status of a command. This class is implemented as a Java record, which
 * provides a compact syntax for defining immutable data classes.
 */
public record CommandStoreRecord(
    UUID id, String contractId, CommandStatus status, Integer sequenceNumber) {

  /**
   * Returns the hash code value for this record. The hash code is computed based solely on the
   * {@code id} field.
   *
   * @return the hash code value for this record
   */
  @Override
  public int hashCode() {
    return id.hashCode();
  }

  /**
   * Indicates whether some other object is "equal to" this one. Two {@code CommandStoreRecord}
   * objects are considered equal if they have the same {@code id}.
   *
   * @param obj the reference object with which to compare
   * @return {@code true} if this object is the same as the {@code obj} argument; {@code false}
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    var other = (CommandStoreRecord) obj;
    return id.equals(other.id);
  }
}
