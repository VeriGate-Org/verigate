/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain;

import crosscutting.serialization.DataContract;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an identifier. This class includes a private final property for the identifier value
 * and methods for validation and comparison.
 */
public abstract class Identity implements Serializable {

  @DataContract private final String value;

  protected Identity() {
    value = null;
  }

  /**
   * Creates a new identity instance.
   *
   * @param value The identifier value.
   * @throws IllegalArgumentException Thrown when the identifier value is invalid.
   */
  public Identity(String value) {
    if (!validate(value)) {
      throw new IllegalArgumentException("Invalid Id");
    }

    this.value = value;
  }

  /**
   * Validates the identifier value.
   *
   * @param value The identifier value.
   * @return Returns true if the identifier value is valid, otherwise false.
   */
  private boolean validate(String value) {
    return value != null && !value.isEmpty();
  }

  /**
   * Getter for the 'value' property.
   *
   * @return The identifier value.
   */
  public String getValue() {
    return value;
  }

  /**
   * Checks if this Identity is equal to another object.
   *
   * <p>The method first checks if the provided object is the same instance as this one. If so, it
   * returns true immediately. If the provided object is null or not an instance of Identity, the
   * method returns false. Otherwise, it casts the object to Identity and compares their ids.
   *
   * @param other the object to be compared for equality with this Identity
   * @return true if the specified object is equal to this Identity
   */
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    Identity productId = (Identity) other;
    // Assuming id is a field in ProductId
    return value.equals(productId.value);
  }

  /**
   * Returns a hash code value for the object.
   *
   * <p>This method is supported for the benefit of hash tables such as those provided by HashMap.
   * The hash code is generated based on the id of the Identity. It's crucial that objects which are
   * considered equal (as determined by the equals method) return the same hash code. This ensures
   * consistent behavior in collections that rely on hashing.
   *
   * @return a hash code value for this Identity.
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.value);
  }

  public UUID toUUID() {
    return UUID.fromString(value);
  }

  /**
   * Returns a string representation of this Identity.
   *
   * @return A string that describes this Identity.
   */
  @Override
  public String toString() {
    return value;
  }
}
