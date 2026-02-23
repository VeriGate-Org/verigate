/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;

class ParameterValidationTest {

  private static final String TEST_STRING = "Test string";
  private static final String TEST_MESSAGE = "Test message";

  @Test
  void requireNonNullObject() {
    IllegalArgumentException illegalArgumentException = assertThrowsExactly(
        IllegalArgumentException.class,
        () -> ParameterValidation.requireNonNull(null, TEST_MESSAGE));
    assertEquals(TEST_MESSAGE + " must not be null", illegalArgumentException.getMessage());
    final Object o = new Object();
    assertSame(o, ParameterValidation.requireNonNull(o, TEST_MESSAGE));
  }

  @Test
  void requireNonBlankNoMessage() {
    assertThrows(IllegalArgumentException.class, () -> ParameterValidation.requireNonBlank(null));
    assertThrows(IllegalArgumentException.class, () -> ParameterValidation.requireNonBlank(""));
    assertThrows(IllegalArgumentException.class, () -> ParameterValidation.requireNonBlank(" "));
    assertEquals(TEST_STRING, ParameterValidation.requireNonBlank(TEST_STRING));
  }

  @Test
  void requireNonBlankWithMessage() {
    IllegalArgumentException illegalArgumentException = assertThrowsExactly(
        IllegalArgumentException.class,
        () -> ParameterValidation.requireNonBlank(null, TEST_MESSAGE));
    assertEquals(TEST_MESSAGE, illegalArgumentException.getMessage());

    illegalArgumentException = assertThrowsExactly(
        IllegalArgumentException.class, () -> ParameterValidation.requireNonBlank("", TEST_MESSAGE));
    assertEquals(TEST_MESSAGE, illegalArgumentException.getMessage());

    illegalArgumentException = assertThrowsExactly(
        IllegalArgumentException.class,
        () -> ParameterValidation.requireNonBlank(" ", TEST_MESSAGE));
    assertEquals(TEST_MESSAGE, illegalArgumentException.getMessage());

    assertEquals(TEST_STRING, ParameterValidation.requireNonBlank(TEST_STRING, TEST_MESSAGE));
  }

  @Test
  void requireNonEmpty() {
    IllegalArgumentException illegalArgumentException = assertThrowsExactly(
        IllegalArgumentException.class,
        () -> ParameterValidation.requireNonEmpty(null, TEST_MESSAGE));
    assertEquals(TEST_MESSAGE + " must not be null", illegalArgumentException.getMessage());

    illegalArgumentException = assertThrowsExactly(
        IllegalArgumentException.class,
        () -> ParameterValidation.requireNonEmpty(List.of(), TEST_MESSAGE));
    assertEquals(TEST_MESSAGE + " must not be empty", illegalArgumentException.getMessage());

    final List<String> testList = List.of("a", "b");
    final Collection<String> result = ParameterValidation.requireNonEmpty(testList, TEST_MESSAGE);
    assertSame(testList, result);
  }

}