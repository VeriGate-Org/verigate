/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.builders;

import static org.junit.jupiter.api.Assertions.assertEquals;

import crosscutting.constants.HttpProtocols;
import org.junit.jupiter.api.Test;

/**
 * DefaultUrlBuilderTest is a test class that tests the DefaultUrlBuilder class.
 */
public class DefaultUrlBuilderTest {

  @Test
  void testBuildUrl() {
    var urlBuilder = new DefaultUrlBuilder();

    var url =
        urlBuilder
            .setProtocol(HttpProtocols.HTTPS)
            .addPath("verigate.co.za")
            .addPath("quote")
            .addPath("id")
            .build();

    assertEquals("https://verigate.co.za/quote/id/", url);
  }
}
