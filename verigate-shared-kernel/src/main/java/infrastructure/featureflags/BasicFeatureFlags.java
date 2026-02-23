/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.featureflags;

import com.google.inject.Inject;
import crosscutting.environment.Environment;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The BasicFeatureFlags class provides a basic implementation of the FeatureFlags interface.
 * It reads feature flag mappings from a properties file,
 * and checks if the feature is enabled in the environment variables.
 */
public class BasicFeatureFlags implements FeatureFlags {
  private Properties featureFlagEnvMappings;
  private Environment env;

  /**
   * Initializes a new instance of the BasicFeatureFlags class.
   *
   * @param env The environment.
   * @throws IOException If an I/O error occurs.
   */
  @Inject
  public BasicFeatureFlags(Environment env) throws IOException {
    this.env = env;
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    InputStream stream = loader.getResourceAsStream("basic_feature_flags.properties");
    featureFlagEnvMappings = new Properties();
    featureFlagEnvMappings.load(stream);
  }

  @Override
  public boolean isFeatureEnabled(String featureName, boolean defaultValue) {
    var featureFlagEnvVal = featureFlagEnvMappings.getProperty(featureName);
    if (featureFlagEnvVal == null) {
      return false;
    }
    try {
      return env.get(featureFlagEnvVal).equals("true");
    } catch (Exception e) {
      return defaultValue;
    }
  }
}
