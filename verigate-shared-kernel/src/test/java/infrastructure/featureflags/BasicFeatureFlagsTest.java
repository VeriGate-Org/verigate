package infrastructure.featureflags;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import crosscutting.environment.EnvironmentConfig;
import domain.exceptions.PermanentException;
import java.io.IOException;
import org.junit.jupiter.api.Test;

/**
 * The BasicFeatureFlagsTest class provides tests for the BasicFeatureFlags class.
 */
public class BasicFeatureFlagsTest {

  @Test
  public void testIsFeatureEnabled() throws PermanentException, IOException {
    var environment = mock(EnvironmentConfig.class);
    when(environment.get("FF_FLAG1")).thenReturn("true");
    when(environment.get("FF_FLAG2")).thenReturn("false");
    when(environment.get("FF_FLAG3")).thenReturn(null);

    BasicFeatureFlags basicFeatureFlags = new BasicFeatureFlags(environment);

    boolean result1 = basicFeatureFlags.isFeatureEnabled("feature1", true);
    assertTrue(result1);

    boolean result2 = basicFeatureFlags.isFeatureEnabled("feature2", true);
    assertFalse(result2);

    boolean result3 = basicFeatureFlags.isFeatureEnabled("feature3", true);
    assertFalse(result3);

    boolean result4 = basicFeatureFlags.isFeatureEnabled("nonexistent_feature", true);
    assertFalse(result4);
  }
}
