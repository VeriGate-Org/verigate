/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain;

import static org.junit.Assert.assertNotNull;

import java.util.Locale;
import java.util.Locale.IsoCountryCode;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/** Unit tests for the {@link CountryCode} enumeration. */
public final class CountryCodeTest {

  /**
   * Tests that all ISO country codes are valid and can be converted to a {@link CountryCode}
   * instance.
   */
  @Test
  public void testValidCountryCode_USA() {
    var localeCountries =
        Locale.getISOCountries(IsoCountryCode.PART1_ALPHA3).stream()
            .map(c -> c.toString())
            .collect(Collectors.toList());
    for (var localeCountry : localeCountries) {
      // implicit assertion
      assertNotNull(CountryCode.valueOf(localeCountry));
    }
  }
}
