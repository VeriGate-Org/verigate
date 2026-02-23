/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.util;

import static org.junit.jupiter.api.Assertions.*;

import crosscutting.mappers.AgeCalculator;
import java.time.LocalDate;
import org.junit.Test;

/**
 * Test class for the AgeCalculator class.
 */
public class AgeCalculatorTest {

  @Test
  public void calculateAge() {
    var calculator = new AgeCalculator();
    assertEquals(23, calculator.calculate(LocalDate.of(2000, 2, 1), LocalDate.of(2024, 1, 1)));
    assertEquals(24, calculator.calculate(LocalDate.of(2000, 2, 1), LocalDate.of(2024, 5, 5)));
  }
}
