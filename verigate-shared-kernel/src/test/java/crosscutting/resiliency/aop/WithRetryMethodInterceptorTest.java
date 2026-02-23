/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.resiliency.aop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * This tests the behaviour of the {@link WithRetryMethodInterceptor} class, as invoked via
 * <a href="https://github.com/google/guice/wiki/AOP">Guice AOP</a>.
 */
@ExtendWith(MockitoExtension.class)
public class WithRetryMethodInterceptorTest {

  MyService myService;

  @BeforeEach
  void beforeEach() {

    Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(MyService.class);

        // configure Guice to invoke the interceptor for any methods annotated with @Counted.
        bindInterceptor(
            Matchers.any(),
            new WithRetryMethodMatcher(),
            new WithRetryMethodInterceptor());
      }
    });
    myService = injector.getInstance(MyService.class); // Guice instruments MyService with a subclass that calls the interceptor
  }

  @Test
  void success() {

    myService.mySuccessMethod();

    assertEquals(1, myService.invocationCount,
        "the service method was invoked");
  }

  @Test
  void defaultRetries() {

    assertThrows(Exception.class, () -> myService.myFailMethodWithDefaultRetries()); // any exception will do for this test
    assertEquals(3, myService.invocationCount);
  }


  @Test
  void customRetries() {

    assertThrows(Exception.class, () -> myService.myFailMethodWith2Retries());
    assertEquals(2, myService.invocationCount);
  }

  @Test
  void whenExceptionThrownIsRetryExceptionThenRetry() {

    assertThrows(Exception.class, () -> myService.myFailMethodWithRetryExceptions(new Exception("test")));
    assertEquals(1, myService.invocationCount,
        "The annotation does not specify to retry for the exception type that was thrown.");
  }

  @Test
  void whenExceptionThrownIsNotRetryExceptionThenNoRetry() {

    assertThrows(RuntimeException.class, () -> myService.myFailMethodWithRetryExceptions(new RuntimeException("test")));
    assertEquals(3, myService.invocationCount);
  }

  @Test
  void whenExceptionThrownIsIgnoreExceptionThenNoRetry() {

    assertThrows(RuntimeException.class, () -> myService.myFailMethodWithIgnoreExceptions(new RuntimeException("test")));
    assertEquals(1, myService.invocationCount,
        "The annotation specifies to ignore retrying for the exception thrown.");
  }

  @Test
  void whenExceptionThrownIsNotIgnoreExceptionThenRetry() {

    assertThrows(Exception.class, () -> myService.myFailMethodWithIgnoreExceptions(new Exception("test")));
    assertEquals(3, myService.invocationCount);
  }

  // example class using the Micrometer annotations
  static class MyService {

    int invocationCount = 0;

    // this method does not throw an exception
    @WithRetry
    void mySuccessMethod() { // not allowed to be private, or Guice won't override
      invocationCount++;
    }

    // this method throws an exception
    @WithRetry
    void myFailMethodWithDefaultRetries() {
      invocationCount++;
      throw new RuntimeException("test");
    }

    // this method throws an exception
    @WithRetry(maxRetries = 2)
    void myFailMethodWith2Retries() {
      invocationCount++;
      throw new RuntimeException("test");
    }

    // this method throws an exception
    @WithRetry(retryExceptions = {RuntimeException.class})
    void myFailMethodWithRetryExceptions(Exception e) throws Exception {
      invocationCount++;
      throw e;
    }

    // this method throws an exception
    @WithRetry(ignoreExceptions = {RuntimeException.class})
    void myFailMethodWithIgnoreExceptions(Exception e) throws Exception {
      invocationCount++;
      throw e;
    }
  }
}
