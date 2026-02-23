/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.patterns;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ImmutablePropertyBag is a final class that encapsulates a map of properties providing immutable
 * access to them. It ensures that once a property bag is created, the set of properties and their
 * values cannot be modified. This class is useful in scenarios where you need to pass around
 * configuration or context data safely without worrying about unintended modifications.
 *
 * @implNote This class uses {@link Collections#unmodifiableMap(Map)} to create an immutable view of
 *     the property map. Attempts to modify this map directly will result in a {@link
 *     UnsupportedOperationException}.
 */
public final class ImmutablePropertyBag {

  private final Map<String, Object> properties;

  /**
   * Constructs an ImmutablePropertyBag with the given properties. The input map is copied to ensure
   * encapsulation and then wrapped as an unmodifiable map to enforce immutability.
   *
   * @param properties The initial set of properties to be stored in the property bag. Must not be
   *     null, but may be empty.
   * @throws NullPointerException if the properties parameter is null.
   */
  public ImmutablePropertyBag(Map<String, Object> properties) {
    if (properties == null) {
      throw new NullPointerException("properties cannot be null");
    }

    // Create a new HashMap to filter null values and keys
    this.properties = properties.entrySet().stream()
        .filter(entry -> Objects.nonNull(entry.getKey()) && Objects.nonNull(entry.getValue()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  /**
   * Retrieves the property value associated with the specified key, cast to the provided class
   * type.
   *
   * @param <T> The expected type of the property value.
   * @param key The key whose associated value is to be returned. Must not be null.
   * @param type The {@link Class} object corresponding to the type T.
   * @return The property value of type T, or null if no property exists for the given key.
   * @throws ClassCastException If the property value does not match the expected type.
   * @throws NullPointerException if the key or type parameter is null.
   */
  public <T> T getProperty(String key, Class<T> type) {
    if (key == null) {
      throw new NullPointerException("key cannot be null");
    }
    if (type == null) {
      throw new NullPointerException("type cannot be null");
    }
    Object value = properties.get(key);
    if (value == null) {
      return null;
    }
    if (type.isInstance(value)) {
      return type.cast(value);
    }
    throw new ClassCastException("Property " + key + " is not of type " + type.getSimpleName());
  }

  /**
   * Provides an immutable view of the properties contained within this bag. This method allows the
   * properties to be viewed without providing the ability to modify them.
   *
   * @return An unmodifiable {@link Map} representing the properties of this bag.
   */
  public Map<String, Object> asMap() {
    return properties;
  }
}
