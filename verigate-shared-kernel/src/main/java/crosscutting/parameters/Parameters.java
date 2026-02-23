/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.parameters;

import domain.exceptions.PermanentException;

/**
 * The Parameters interface provides methods to retrieve parameter values.
 */
public interface Parameters {
  /**
   * Retrieves the value associated with the specified key.
   *
   * @param key the key for the value to be retrieved
   * @return the value associated with the specified key
   */
  String get(String key) throws PermanentException;

  /**
   * Retrieves the value associated with the specified key from the parameters.
   * If the key is not found, returns the specified default value.
   *
   * @param key the key to retrieve the value for
   * @param defaultValue the default value to return if the key is not found
   * @return the value associated with the key, or the default value if the key is not found
   */
  String get(String key, String defaultValue) throws PermanentException;
}
