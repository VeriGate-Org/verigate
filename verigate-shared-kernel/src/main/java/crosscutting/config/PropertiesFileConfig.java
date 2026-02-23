/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.config;

import domain.exceptions.PermanentException;
import domain.exceptions.StringExceptionBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

/** Represents a configuration class for properties file. */
public class PropertiesFileConfig implements Config {
  /** Represents a configuration class for properties file. */
  private final Properties properties = new Properties();

  /**
   * Constructs a new PropertiesFileConfig object.
   *
   * @throws PermanentException if an error occurs while reading the properties file
   */
  public PropertiesFileConfig() throws PermanentException {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();

    try {
      // Load all application.properties files in the classpath
      Enumeration<URL> resources = loader.getResources("application.properties");

      while (resources.hasMoreElements()) {
        URL resource = resources.nextElement();
        try (InputStream stream = resource.openStream()) {
          Properties prop = new Properties();
          prop.load(stream);
          properties.putAll(prop);
        } catch (IOException e) {
          throw new PermanentException(
              StringExceptionBuilder.builder()
                  .withDetail("Error loading properties from: " + resource + " - " + e.getMessage())
                  .build());
        }
      }

    } catch (IOException e) {
      throw new PermanentException(
          StringExceptionBuilder.builder()
              .withDetail("Error loading properties files: " + e.getMessage())
              .build());
    }
  }

  @Override
  public String get(String key) throws PermanentException {
    var property = properties.getProperty(key);

    if (property == null) {
      throw new PermanentException(
          StringExceptionBuilder.builder().withDetail("Config not found for: " + key).build());
    }

    return property;
  }

  @Override
  public String get(String key, String defaultValue) throws PermanentException {
    return properties.getProperty(key, defaultValue);
  }
}
