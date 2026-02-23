/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain;

import crosscutting.serialization.DataContract;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Represents a percentage value.
 *
 * <p>A percentage is a number that represents a fraction of 100.
 *
 * @param value the value of the percentage
 */
public record Percentage(@DataContract BigDecimal value, boolean allowExcess)
    implements Serializable {

  /**
   * Creates a Percentage instance that does not allow values exceeding 100%.
   *
   * @param value The percentage value as BigDecimal.
   * @throws IllegalArgumentException If the value is outside the range of 0 to 100 inclusive.
   */
  public Percentage(BigDecimal value) {
    this(value, false);
    validate();
  }

  /**
   * Creates a Percentage instance from an integer value that does not allow values exceeding 100%.
   *
   * @param value The percentage value as an integer.
   * @throws IllegalArgumentException If the value is outside the range of 0 to 100 inclusive.
   */
  public Percentage(int value) {
    this(new BigDecimal(value), false);
    validate();
  }

  /**
   * Creates a Percentage instance from an integer value that does not allow values exceeding 100%.
   *
   * @param value The percentage value as an integer.
   * @param allowExcess Indicates whether the percentage value can exceed 100%.
   * @throws IllegalArgumentException If the value is outside the range of 0 to 100 inclusive.
   */
  private Percentage(int value, boolean allowExcess) {
    this(new BigDecimal(value), allowExcess);
    validate();
  }

  /**
   * Private constructor for the record.
   *
   * @param value The percentage value as BigDecimal.
   * @param allowExcess Indicates whether the percentage value can exceed 100%.
   */
  public Percentage {
    if (value == null) {
      throw new IllegalArgumentException("Percentage value must be non-null.");
    }
  }

  /**
   * Returns a new {@link Percentage} instance representing the specified integer value. This static
   * method provides a way to create a {@link Percentage} object from an integer, facilitating the
   * use of {@link Percentage} objects when only integer values are available.
   *
   * <p>The method encapsulates the integer value directly into a {@link Percentage}, allowing for
   * simple instantiation with integer-based percentage values. This is particularly useful when
   * dealing with values that are inherently integers, such as when reading from sources that do not
   * support decimal representations.
   *
   * @param value The integer value to be converted into a percentage.
   * @return A new {@link Percentage} object containing the given integer value.
   * @throws IllegalArgumentException if the value is outside the valid range of 0 to 100.
   */
  public static Percentage valueOf(int value) {
    return new Percentage(value);
  }

  /**
   * Creates a new {@link Percentage} instance from an integer value that potentially exceeds 100%.
   * This method is specifically used when the percentage value can go beyond the standard range of
   * 0 to 100, accommodating scenarios where over-100% values are meaningful, such as in efficiency
   * or performance metrics exceeding expected or baseline values.
   *
   * @param value The integer percentage value, potentially exceeding 100.
   * @return A new {@link Percentage} object representing the specified value.
   * @throws IllegalArgumentException If the value is negative, since percentages less than 0 are
   *     not valid.
   */
  public static Percentage fromExcess(int value) {
    return new Percentage(value, true);
  }

  /**
   * Creates a new {@link Percentage} instance from an integer value that potentially exceeds 100%.
   * This method is specifically used when the percentage value can go beyond the standard range of
   * 0 to 100, accommodating scenarios where over-100% values are meaningful, such as in efficiency
   * or performance metrics exceeding expected or baseline values.
   *
   * @param value The BigDecimal percentage value, potentially exceeding 100.
   * @return A new {@link Percentage} object representing the specified value.
   * @throws IllegalArgumentException If the value is negative, since percentages less than 0 are
   *     not valid.
   */
  public static Percentage fromExcess(BigDecimal value) {
    return new Percentage(value, true);
  }

  /**
   * Adds two percentages.
   *
   * @param other The other percentage to add.
   * @return A new Percentage representing the sum of this and the other percentage.
   */
  public Percentage add(Percentage other) {
    return Percentage.fromExcess(this.value.add(other.value));
  }

  /**
   * Subtracts a percentage from this percentage.
   *
   * @param other The other percentage to subtract.
   * @return A new Percentage representing the difference between this and the other percentage.
   */
  public Percentage subtract(Percentage other) {
    return new Percentage(this.value.subtract(other.value));
  }

  /**
   * Determines if this percentage is greater than another percentage.
   *
   * @param other The other percentage to compare.
   * @return true if this percentage is greater than the other percentage.
   */
  public boolean isGreaterThan(Percentage other) {
    return this.value.compareTo(other.value) > 0;
  }

  /**
   * Determines if this percentage is less than another percentage.
   *
   * @param other The other percentage to compare.
   * @return true if this percentage is less than the other percentage.
   */
  public boolean isLessThan(Percentage other) {
    return this.value.compareTo(other.value) < 0;
  }

  /**
   * Determines if this percentage is greater than or equal to another percentage.
   *
   * @param other The other percentage to compare.
   * @return true if this percentage is greater than or equal to the other percentage.
   */
  public boolean isGreaterThanOrEqualTo(Percentage other) {
    return this.value.compareTo(other.value) >= 0;
  }

  /**
   * Determines if this percentage is less than or equal to another percentage.
   *
   * @param other The other percentage to compare.
   * @return true if this percentage is less than or equal to the other percentage.
   */
  public boolean isLessThanOrEqualTo(Percentage other) {
    return this.value.compareTo(other.value) <= 0;
  }

  /** Returns a percentage value of 0%. */
  public static final Percentage ZERO = new Percentage(0);

  /** Returns a percentage value of 100%. */
  public static final Percentage ONE_HUNDRED = new Percentage(100);

  /** Validates the percentage value based on the `allowExcess` flag. */
  private void validate() {
    if (!allowExcess
        && (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(BigDecimal.valueOf(100)) > 0)) {
      throw new IllegalArgumentException("Percentage must be between 0 and 100");
    }
  }

  // more methods can be added if needed
}
