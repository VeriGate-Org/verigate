/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.Comparator;

/**
 * Represents the details of an audit entry, encapsulating user information and the timestamp of the
 * update. This record is designed to be serializable, allowing it to be easily transmitted or
 * stored.
 *
 * <p>The record provides a static method to obtain a comparator that can be used to sort
 * collections of {@code AuditDetail} instances based on the update timestamp.
 *
 * @param user the username associated with the audit detail
 * @param updatedAt the timestamp when the audit detail was last updated
 * @param changeReason the reason for the change
 * @param changeCommand the command that triggered the change
 */
public record AuditDetail(
    String user, Instant updatedAt, String changeReason, String changeCommand)
    implements Serializable, Comparable<AuditDetail> {

  /**
   * Provides a comparator for {@code AuditDetail} objects that compares them by their {@code
   * updatedAt} timestamp.
   *
   * <p>This method is particularly useful for sorting or ordering collections of {@code
   * AuditDetail} instances based on when they were updated.
   *
   * @return a {@code SerializableComparator} that compares {@code AuditDetail} objects based on the
   *     {@code updatedAt} timestamp.
   */
  public static SerializableComparator<AuditDetail> comparatorByUpdatedAt() {
    return new SerializableComparator<>() {
      @Override
      public int compare(AuditDetail x, AuditDetail y) {
        return x.updatedAt().compareTo(y.updatedAt());
      }
    };
  }

  /**
   * Provides a comparator for {@code AuditDetail} objects that compares them first by {@code
   * updatedAt} timestamp and then by {@code changeCommand}.
   *
   * @return a {@code SerializableComparator} that compares {@code AuditDetail} objects based on
   *     {@code updatedAt} and {@code changeCommand}.
   */
  public static SerializableComparator<AuditDetail> comparatorByUpdatedAtAndChangeCommand() {
    return new SerializableComparator<>() {
      @Override
      public int compare(AuditDetail x, AuditDetail y) {
        int result = x.updatedAt().compareTo(y.updatedAt());
        if (result == 0) {
          result = x.changeCommand().compareTo(y.changeCommand());
        }
        return result;
      }
    };
  }

  /**
   * Compares this {@code AuditDetail} with the specified {@code AuditDetail} for order.
   *
   * <p>Compares by {@code updatedAt} first and then by {@code changeCommand}.
   *
   * @param other the {@code AuditDetail} to be compared.
   * @return a negative integer, zero, or a positive integer as this object is less than, equal to,
   *     or greater than the specified object.
   */
  @Override
  public int compareTo(AuditDetail other) {
    return comparatorByUpdatedAtAndChangeCommand().compare(this, other);
  }

  /**
   * Defines a serializable comparator.
   *
   * <p>This interface extends {@link Comparator} to add serializability, making it suitable for use
   * in serializable classes.
   *
   * @param <T> the type of objects that may be compared by this comparator
   */
  public interface SerializableComparator<T> extends Comparator<T>, Serializable {}
}
