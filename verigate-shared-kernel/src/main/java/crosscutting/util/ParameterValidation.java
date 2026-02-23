/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.util;

import java.util.Collection;
import org.apache.commons.lang3.StringUtils;

/**
 * Static utilities for parameter validation.
 */
public final class ParameterValidation {

  private ParameterValidation() {}

  /**
   * Require the supplied object to be non-null.
   *
   * @param <T> The type of object
   * @param object The object to evaluate.
   * @param objectName The name of the object type to use in exception messaging.
   * @return The object if non-null.
   * @throws IllegalArgumentException If the object is null.
   */
  public static <T> T requireNonNull(T object, String objectName) throws IllegalArgumentException {
    if (object == null) {
      throw new IllegalArgumentException("%s must not be null".formatted(objectName));
    }
    return object;
  }

  /**
   * Require the supplied string to be non-null and non-blank.
   *
   * @param string The string to evaluate.
   * @return The string if it is non-null and non-blank.
   * @throws IllegalArgumentException If the string is null or blank.
   */
  public static String requireNonBlank(String string) throws IllegalArgumentException {
    if (StringUtils.isBlank(string)) {
      throw new IllegalArgumentException();
    }
    return string;
  }

  /**
   * Require the supplied string to be non-null and non-blank, with custom exception message.
   *
   * @param string The string to evaluate.
   * @param message The custom exception message.
   * @return The string if it is non-null and non-blank.
   * @throws IllegalArgumentException If the string is null or blank.
   */
  public static String requireNonBlank(String string, String message)
      throws IllegalArgumentException {
    if (StringUtils.isBlank(string)) {
      throw new IllegalArgumentException(message);
    }
    return string;
  }

  /**
   * Require the supplied collection to be non-null and non-empty.
   *
   * @param collection The collection to evaluate.
   * @param collectionName The name of the collection used in exception messages.
   * @param <T> The type of collection element.
   * @return The collection if non-null and non-empty.
   * @throws IllegalArgumentException If the collection is null or empty.
   */
  public static <T> Collection<T> requireNonEmpty(Collection<T> collection, String collectionName)
      throws IllegalArgumentException {
    if (collection == null) {
      throw new IllegalArgumentException("%s must not be null".formatted(collectionName));
    }
    if (collection.isEmpty()) {
      throw new IllegalArgumentException("%s must not be empty".formatted(collectionName));
    }
    return collection;
  }

}
