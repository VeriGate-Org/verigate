/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.environment;

import domain.exceptions.PermanentException;
import domain.exceptions.StringExceptionBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * {@link Environment} implementation that loads settings from an
 * {@code application.localenvironment} file, expected on the classpath.
 * The intent is to use this for local development and testing. This cannot be used when deploying
 * to a cloud environment. This class could be modified to use in-memory properties instead of
 * reading from a file. which will allow you to generate a mocked environment for testing.
 */
public class LocalEnvironmentConfig implements Environment {
  private final Properties properties;
  private final Map<String, String> overrides = new HashMap<>();

  /**
   * Constructs a new EnvironmentConfig object.
   *
   * @throws PermanentException if an error occurs while reading the properties file
   */
  public LocalEnvironmentConfig() throws PermanentException {
    Properties prop = new Properties();
    ClassLoader loader = Thread.currentThread().getContextClassLoader();

    try {
      InputStream stream = loader.getResourceAsStream("application.localenvironment");
      prop.load(stream);
    } catch (IOException e) {
      throw new PermanentException(
          StringExceptionBuilder.builder().withDetail(e.getMessage()).build());
    } catch (NullPointerException e) {
      throw new PermanentException(
          StringExceptionBuilder.builder()
              .withDetail("Local environment file 'application.localenvironment' not found")
              .build());
    }

    this.properties = prop;
  }

  public void setOverride(String key, String value) {
    overrides.put(key, value);
  }

  @Override
  public String get(String key) throws PermanentException {
    if (overrides.containsKey(key)) {
      return overrides.get(key);
    }

    var property = properties.getProperty(key);

    if (property == null) {
      throw new PermanentException(
          StringExceptionBuilder.builder().withDetail("Value not found for: " + key).build());
    }

    return property;
  }

  @Override
  public String get(String key, String defaultValue) throws PermanentException {
    if (overrides.containsKey(key)) {
      return overrides.get(key);
    }

    return properties.getProperty(key, defaultValue);
  }
}
