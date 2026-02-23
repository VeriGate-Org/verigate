/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import domain.exceptions.TransientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

class ExceptionLoggingSupplierTest {

  private static final String TEST_MESSAGE = "test message";
  private static final String TEST_RESPONSE = "test response";

  private Logger mockLogger;

  @BeforeEach
  public void setup() {
    mockLogger = mock(Logger.class);
  }

  @Test
  public void noException() {
    final ExceptionLoggingSupplier<String> exceptionLoggingSupplier = new ExceptionLoggingSupplier<>(
        mockLogger,
        () -> TEST_RESPONSE,
        e -> new RuntimeException(TEST_MESSAGE, e));
    final String actualResponse = exceptionLoggingSupplier.get();
    assertEquals(TEST_RESPONSE, actualResponse);
    verifyNoInteractions(mockLogger);
  }

  @Test
  public void withException() {
    final ExceptionLoggingSupplier<String> exceptionLoggingSupplier = new ExceptionLoggingSupplier<>(
        mockLogger, () -> {
        throw new Exception("Test exception");},
        TransientException.wrapInTransientException(TEST_MESSAGE));
    assertThrows(TransientException.class, exceptionLoggingSupplier::get);
    verify(mockLogger).error(eq(TEST_MESSAGE), any(Exception.class));
  }

}