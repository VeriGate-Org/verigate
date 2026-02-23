/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.featureflags;

/**
 * The FeatureFlags interface provides methods for checking if a feature is enabled.
 */
public interface FeatureFlags {
  public boolean isFeatureEnabled(String featureName, boolean defaultValue);
}
