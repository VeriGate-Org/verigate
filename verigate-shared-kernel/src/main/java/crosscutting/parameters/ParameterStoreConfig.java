/*
 * VeriGate (c) 2024 - 2025. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.parameters;

import domain.exceptions.PermanentException;
import java.time.Instant;
import java.util.Properties;
import java.util.logging.Logger;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.ParameterNotFoundException;

/**
 * This class is used to load parameters from AWS Parameter Store.
 */
public class ParameterStoreConfig implements Parameters {
  private final Properties properties = new Properties();
  static SsmClient ssmClient;
  private Logger logger = Logger.getLogger(ParameterStoreConfig.class.getName());
  private Instant cachedAt = Instant.now();

  /**
   * This method is used to get the value of a parameter from AWS Parameter Store.
   *
   * @param key The name of the parameter.
   * @return The value of the parameter.
   * @throws PermanentException If the parameter is not found.
   */
  @Override
  public String get(String key) throws PermanentException {
    var value = getSsmParameter(key);
    if (value == null) {
      throw new PermanentException("Parameter not found: " + key);
    }
    return value;
  }

  /**
   * This method is used to get the value of a parameter from AWS Parameter Store.
   *
   * @param key The name of the parameter.
   * @param defaultValue The default value to return if the parameter is not found.
   * @return The value of the parameter.
   * @throws PermanentException If the parameter is not found.
   */
  @Override
  public String get(String key, String defaultValue) throws PermanentException {
    var value = getSsmParameter(key);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  String getSsmParameter(String name) {
    if (properties.containsKey(name) && cachedAt.isAfter(Instant.now().minusSeconds(360))) {
      logger.info("Loading parameter from cache: " + name);
      return properties.getProperty(name);
    }
    try {
      ssmClient = SsmClient.create();
      logger.info("Loading parameters from SSM: " + name);
      var ssmRequest = GetParameterRequest.builder().name(name).build();
      var value = ssmClient.getParameter(ssmRequest).parameter().value();
      properties.setProperty(name, value);
      cachedAt = Instant.now();
      return value;
    } catch (ParameterNotFoundException e) {
      logger.warning("Parameter not found: " + name);
      return null;
    } catch (Exception e) {
      logger.severe("Error loading parameter: " + name);
      logger.severe("Error: " + e.getMessage());
      throw new PermanentException("Error loading parameter: " + name, e);
    }
  }
}
