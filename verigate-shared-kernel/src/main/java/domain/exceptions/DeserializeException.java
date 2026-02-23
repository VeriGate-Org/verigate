/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.exceptions;

/**
 * Exception thrown when deserialization of an object fails.
 * This exception extends {@link PermanentException}.
 * It contains the data that caused the deserialization failure.
 *
 * @see SerializeException
 */
public final class DeserializeException extends PermanentException {

  private final Object data;

  /**
   * Constructs a new {@code DeserializeException} with the specified data, message, and cause.
   *
   * @param data the data that caused the deserialization failure
   * @param message the detail message
   * @param cause the cause of the exception
   */
  public DeserializeException(Object data, String message, Throwable cause) {
    super(message, cause);
    this.data = data;
  }

  /**
   * Constructs a new {@code DeserializeException} with the specified data and message.
   *
   * @param data the data that caused the deserialization failure
   * @param message the detail message
   */
  public DeserializeException(Object data, String message) {
    super(message);
    this.data = data;
  }

  /**
   * Returns the data associated with this exception cast to the specified type.
   *
   * @param <T> the type to which the data should be cast
   * @param type the class of the type to cast the data to
   * @return the data cast to the specified type
   * @throws IllegalStateException if the data is not set
   * @throws RuntimeException if the data cannot be cast to the specified type
   * @see #getRawData()
   */
  @SuppressWarnings("unchecked")
  public <T> T getData(Class<?> type) {
    if (data == null) {
      throw new IllegalStateException("Data not set");
    }

    try {
      return (T) type.cast(data);
    } catch (ClassCastException e) {
      throw new RuntimeException("Failed to return the data as the specified type", e);
    }
  }

  /**
   * Returns the object that could not be deserialized to the expected type.
   * Unlike {@link #getData(Class)}, this method does not throw exceptions.
   *
   * @return the object, or null
   */
  public Object getRawData() {
    return data;
  }
}
