/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.messaging;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class ExponentialBackoffWithJitterStrategyTest {

  @Test
  public void testCalculateDelayIncreasesExponentially() {
    ExponentialBackoffWithJitterStrategy strategy = new ExponentialBackoffWithJitterStrategy();
    long baseDelayMs = 100; // Example base delay
    long previousDelay = 0;
    for (int attempt = 1; attempt <= 5; attempt++) {
      long currentDelay = strategy.calculateDelay(attempt, baseDelayMs, 5000);
      assertTrue(currentDelay >= previousDelay, "Expected delay to increase with each attempt");
      previousDelay = currentDelay;
    }
  }

  @Test
  public void testCalculateDelayWithMinimumBaseDelay() {
    ExponentialBackoffWithJitterStrategy strategy = new ExponentialBackoffWithJitterStrategy();
    long baseDelayMs = 1; // Minimum possible delay
    long delay = strategy.calculateDelay(1, baseDelayMs, 5000);
    assertTrue(delay >= 0, "Delay should be non-negative even with minimum base delay");
  }

  @Test
  public void testCalculateDelayWithZeroDelay() {
    ExponentialBackoffWithJitterStrategy strategy = new ExponentialBackoffWithJitterStrategy();
    long baseDelayMs = 10;
    assertThrows(
        Exception.class,
        () -> {
          long delay = strategy.calculateDelay(0, baseDelayMs, 5000);
        });
  }

  @Test
  public void testCalculateDelayWithZeroBaseDelayMs() {
    ExponentialBackoffWithJitterStrategy strategy = new ExponentialBackoffWithJitterStrategy();
    long baseDelayMs = 0;
    assertThrows(
        Exception.class,
        () -> {
          long delay = strategy.calculateDelay(3, baseDelayMs, 5000);
        });
  }
}
