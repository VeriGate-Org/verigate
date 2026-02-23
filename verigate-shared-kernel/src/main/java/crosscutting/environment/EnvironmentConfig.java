/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.environment;

import domain.exceptions.PermanentException;
import domain.exceptions.StringExceptionBuilder;
import java.util.Properties;

/** Represents a configuration class for environment variable config retrieval. */
public class EnvironmentConfig implements Environment {
  /** Represents a configuration class for environment variable config retrieval. */
  private Properties properties;

  /**
   * Constructs a new EnvironmentConfig object.
   *
   * @throws PermanentException if an error occurs while reading the properties file
   */
  public EnvironmentConfig() throws PermanentException {
    loadVariables();
  }

  @Override
  public String get(String key) throws PermanentException {
    loadVariables();
    var property = properties.getProperty(key);

    if (property == null) {
      throw new PermanentException(
          StringExceptionBuilder.builder()
              .withDetail("Environment variable value not found for: " + key)
              .build());
    }

    return property;
  }

  @Override
  public String get(String key, String defaultValue) throws PermanentException {
    loadVariables();
    return properties.getProperty(key, defaultValue);
  }

  private void loadVariables() {
    Properties prop = new Properties();

    var envVars = System.getenv();
    envVars.entrySet().stream()
        .forEach(envVar -> prop.setProperty(envVar.getKey(), envVar.getValue()));

    this.properties = prop;
  }
}
