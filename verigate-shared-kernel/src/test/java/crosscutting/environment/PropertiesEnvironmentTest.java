/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.environment;

import static org.junit.jupiter.api.Assertions.*;

import domain.exceptions.PermanentException;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PropertiesEnvironmentTest {

  // class under test
  PropertiesEnvironment environment;

  @BeforeEach
  void beforeEach() {
    Properties properties = new Properties();
    properties.setProperty("key1", "value1");
    environment = new PropertiesEnvironment(properties);
  }

  @Test
  void get() {
    assertEquals("value1", environment.get("key1"));
    assertThrows(PermanentException.class, () -> environment.get("non existent"));
  }

  @Test
  void getWithDefault() {
    assertEquals("value1", environment.get("key1", "some default"));
    assertEquals(
        "some default",
        environment.get("non existent", "some default")
    );
  }
}
