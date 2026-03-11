package verigate.webbff.observability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class CorrelationIdFilterTest {

  private final CorrelationIdFilter filter = new CorrelationIdFilter();

  @Test
  void generatesCorrelationIdWhenNotProvided() throws Exception {
    var request = new MockHttpServletRequest();
    var response = new MockHttpServletResponse();
    var chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    String responseHeader = response.getHeader(CorrelationIdFilter.HEADER_NAME);
    assertNotNull(responseHeader, "Should generate a correlation ID");
    assertNull(MDC.get(CorrelationIdFilter.MDC_KEY), "MDC should be cleared after filter");
  }

  @Test
  void usesProvidedCorrelationId() throws Exception {
    var request = new MockHttpServletRequest();
    request.addHeader(CorrelationIdFilter.HEADER_NAME, "test-correlation-123");
    var response = new MockHttpServletResponse();
    var chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertEquals("test-correlation-123",
        response.getHeader(CorrelationIdFilter.HEADER_NAME));
  }

  @Test
  void setsCorrelationIdInMdcDuringFilterChain() throws Exception {
    var request = new MockHttpServletRequest();
    request.addHeader(CorrelationIdFilter.HEADER_NAME, "mdc-test-456");
    var response = new MockHttpServletResponse();

    // Capture MDC value during filter chain execution
    final String[] capturedMdcValue = {null};
    var chain = new MockFilterChain() {
      @Override
      public void doFilter(jakarta.servlet.ServletRequest req,
          jakarta.servlet.ServletResponse res) {
        capturedMdcValue[0] = MDC.get(CorrelationIdFilter.MDC_KEY);
      }
    };

    filter.doFilter(request, response, chain);

    assertEquals("mdc-test-456", capturedMdcValue[0],
        "MDC should contain correlation ID during chain execution");
    assertNull(MDC.get(CorrelationIdFilter.MDC_KEY),
        "MDC should be cleared after filter completes");
  }

  @Test
  void ignoresBlankCorrelationIdHeader() throws Exception {
    var request = new MockHttpServletRequest();
    request.addHeader(CorrelationIdFilter.HEADER_NAME, "   ");
    var response = new MockHttpServletResponse();
    var chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    String responseHeader = response.getHeader(CorrelationIdFilter.HEADER_NAME);
    assertNotNull(responseHeader);
    // Should be a generated UUID, not blank
    assertEquals(36, responseHeader.length(), "Should generate a UUID when header is blank");
  }
}
