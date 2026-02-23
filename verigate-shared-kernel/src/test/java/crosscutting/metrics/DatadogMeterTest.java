/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.metrics;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.timgroup.statsd.StatsDClient;
import crosscutting.environment.Environment;
import org.junit.jupiter.api.Test;

class DatadogMeterTest {

  private Environment mockEnvironment = mock(Environment.class);
  private StatsDClient mockStatsDClient = mock(StatsDClient.class);

  @Test
  public void noServiceNamePrefix() {
    when(mockEnvironment.get(eq("DD_SERVICE"), isNull())).thenReturn(null);
    final DatadogMeter datadogMeter = new DatadogMeter(mockEnvironment, mockStatsDClient);
    datadogMeter.incrementCounter("test-metric-Name", "tag1:value1", "tag2:value-2");
    verify(mockStatsDClient).recordDistributionValue("test_metric_name", 1L,
        "tag1:value1", "tag2:value-2");
  }

  @Test
  public void withServiceNamePrefix() {
    when(mockEnvironment.get(eq("DD_SERVICE"), isNull())).thenReturn( "Test-Service");
    final DatadogMeter datadogMeter = new DatadogMeter(mockEnvironment, mockStatsDClient);
    datadogMeter.incrementCounter("test-metric-Name", "tag1:value1", "tag2:value-2");
    verify(mockStatsDClient).recordDistributionValue("test_service.test_metric_name", 1L,
        "tag1:value1", "tag2:value-2");
  }

}