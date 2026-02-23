/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.environment;

import domain.exceptions.PermanentException;

/**
 * The Environment interface provides methods to retrieve configuration values.
 */
public interface Environment {
  /**
   * Retrieves the value associated with the specified key.
   *
   * @param key the key for the value to be retrieved
   * @return the value associated with the specified key
   * @throws PermanentException if the value for the specified key does not exist.
   */
  String get(String key) throws PermanentException;

  /**
   * Retrieves the value associated with the specified key from the configuration.
   * If the key is not found, returns the specified default value.
   *
   * @param key the key to retrieve the value for
   * @param defaultValue the default value to return if the key is not found
   * @return the value associated with the key, or the default value if the key is not found
   */
  String get(String key, String defaultValue) throws PermanentException;
}
