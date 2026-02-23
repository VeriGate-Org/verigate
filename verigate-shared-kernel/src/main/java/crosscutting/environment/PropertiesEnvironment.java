/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.environment;

import domain.exceptions.PermanentException;
import domain.exceptions.StringExceptionBuilder;
import java.util.Properties;

/**
 * {@link Environment} implementation that loads settings from a properties object.
 * Similar to {@link LocalEnvironmentConfig} but less opinionated, in that you can load the
 * properties from anywhere.
 */
public final class PropertiesEnvironment implements Environment {

  private final Properties properties;

  public PropertiesEnvironment(Properties properties) {
    this.properties = properties;
  }

  @Override
  public String get(String key) throws PermanentException {
    var property = properties.getProperty(key);

    if (property == null) {
      throw new PermanentException(
          StringExceptionBuilder.builder().withDetail("Value not found for: " + key).build());
    }
    return property;
  }

  @Override
  public String get(String key, String defaultValue) throws PermanentException {
    return properties.getProperty(key, defaultValue);
  }
}
