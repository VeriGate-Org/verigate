/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.metrics;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.description;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.openMocks;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import io.micrometer.core.annotation.Counted;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * This tests the behaviour of the {@link CountedMethodInterceptor} class, as invoked via
 * <a href="https://github.com/google/guice/wiki/AOP">Guice AOP</a>.
 */
public class CountedMethodInterceptorTest {

  private AutoCloseable openMocks;

  MyService myService;

  @Mock
  Meter meterMock;

  @BeforeEach
  void beforeEach() {

    openMocks = openMocks(this);


    Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(MyService.class);

        // configure Guice to invoke the interceptor for any methods annotated with @Counted.
        bindInterceptor(
            Matchers.any(),
            new CountedMethodMatcher(),
            new CountedMethodInterceptor(meterMock));
      }
    });
    myService = injector.getInstance(MyService.class); // Guice instruments MyService with a subclass that calls the interceptor
  }

  @AfterEach
  void afterEach() throws Exception {
    openMocks.close();
  }

  @Test
  void testSuccessCounter() {

    myService.mySuccessMethod();

    assertTrue(myService.invoked,
        "the service method was called");

    final String[] tags = {"region:eu-west-1", "result:success"};
    verify(meterMock,
        description("the counter for success result was incremented"))
        .incrementCounter("test", tags);

    verifyNoMoreInteractions(meterMock);
  }

  @Test
  void testFailureCounter() {

    assertThrows(Exception.class, () -> myService.myFailMethod()); // any exception will do for this test

    assertTrue(myService.invoked,
        "the service method was called");

    final String[] tags = {"region:eu-west-1", "result:failure"};
    verify(meterMock,
        description("the counter for failure result was incremented"))
        .incrementCounter("test", tags);

    verifyNoMoreInteractions(meterMock);
  }

  @Test
  void testSuccessCounterForCountFailOnly() {
    myService.myCountFailOnlyMethod();

    assertTrue(myService.invoked,
        "the service method was called");

    verifyNoMoreInteractions(meterMock);
  }

  // example class using the Micrometer annotations
  static class MyService {

    boolean invoked = false;

    // this method does not throw an exception
    @Counted(value = "test", extraTags = {"region:eu-west-1"})
    void mySuccessMethod() { // not allowed to be private, or Guice won't override
      invoked = true;
    }

    // this method throws an exception
    @Counted(value = "test", extraTags = {"region:eu-west-1"})
    void myFailMethod() {
      invoked = true;
      throw new RuntimeException("test");
    }

    // this method does not throw an exception. @Counted is configured to count failures only.
    @Counted(value = "test", extraTags = {"region:eu-west-1"}, recordFailuresOnly = true)
    void myCountFailOnlyMethod() {
      invoked = true;
    }
  }
}
