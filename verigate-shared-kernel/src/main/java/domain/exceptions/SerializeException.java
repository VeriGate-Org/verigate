/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.exceptions;


/**
 * Exception thrown when serialization of an object fails.
 * This exception extends {@link PermanentException}.
 * It contains the data that caused the serialization failure.
 *
 * @see DeserializeException
 */
public class SerializeException extends PermanentException {

  private final Object data;

  public SerializeException(Object data, String msg) {
    super(msg);
    this.data = data;
  }

  public SerializeException(Object data, String msg, Throwable cause) {
    super(msg, cause);
    this.data = data;
  }

  /**
   * Returns the object that could not be serialized to the expected type.
   * This method does not throw exceptions.
   *
   * @return the object, or null
   */
  public Object getRawData() {
    return data;
  }
}
