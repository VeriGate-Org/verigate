/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.formatting;

import static org.junit.Assert.assertEquals;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

public class UserFriendlyCurrencyFormatterTest {

  @Test
  public void largePositiveZAR() {
    String actual = UserFriendlyCurrencyFormatter.format(Money.of(1231165.58, "ZAR"));
    assertEquals("R1,231,165.58", actual);
  }

  @Test
  public void anotherlargePositiveZAR() {
    String actual = UserFriendlyCurrencyFormatter.format(Money.of(3000, "ZAR"));
    assertEquals("R3,000.00", actual);
  }
}
