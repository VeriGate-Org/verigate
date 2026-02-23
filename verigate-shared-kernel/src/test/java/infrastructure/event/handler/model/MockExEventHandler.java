package infrastructure.event.handler.model;

import application.handlers.EventHandler;
import java.util.function.Supplier;

public final class MockExEventHandler implements EventHandler<MockEvent> {

  private int callCount = 0;
  private int callCountA = 0;
  private int callCountB = 0;
  private int callCountC = 0;

  private final Supplier<RuntimeException> exceptionSupplier;

  public MockExEventHandler() {
    this.exceptionSupplier = () -> null;
  }

  public MockExEventHandler(Supplier<RuntimeException> exceptionSupplier) {
    this.exceptionSupplier = exceptionSupplier;
  }

  @Override
  public void handle(MockEvent event) {
    switch (event) {
      case ExtendedMockEventA extendedMockEventA -> handle(extendedMockEventA);
      case ExtendedMockEventB extendedMockEventB -> handle(extendedMockEventB);
      case ExtendedMockEventC extendedMockEventC -> handle(extendedMockEventC);
      case null, default -> {
        callCount++;
        var exception = exceptionSupplier.get();
        if (exception != null) {
          throw exception;
        }
      }
    }
  }

  public void handle(ExtendedMockEventA event) {
    callCountA++;
    var exception =  exceptionSupplier.get();
    if (exception != null) {
      throw exception;
    }
  }

  public void handle(ExtendedMockEventB event) {
    callCountB++;
    var exception =  exceptionSupplier.get();
    if (exception != null) {
      throw exception;
    }
  }

  public void handle(ExtendedMockEventC event) {
    callCountC++;
    var exception =  exceptionSupplier.get();
    if (exception != null) {
      throw exception;
    }
  }

  public int getCallCount() {
    return callCount;
  }

  public int getCallCountA() {
    return callCountA;
  }

  public int getCallCountB() {
    return callCountB;
  }

  public int getCallCountC() {
    return callCountC;
  }
}
