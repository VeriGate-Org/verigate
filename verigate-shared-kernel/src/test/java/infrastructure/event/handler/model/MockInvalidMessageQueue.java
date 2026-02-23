package infrastructure.event.handler.model;

import domain.messages.InvalidMessageQueue;
import java.util.function.Supplier;

public final class MockInvalidMessageQueue implements InvalidMessageQueue<String> {

  private int callCount;

  private final Supplier<RuntimeException> exceptionSupplier;

  public MockInvalidMessageQueue() {
    this.exceptionSupplier = null;
    this.callCount = 0;
  }
  public MockInvalidMessageQueue(Supplier<RuntimeException> exceptionSupplier) {
    this.exceptionSupplier = exceptionSupplier;
    this.callCount = 0;
  }

  @Override
  public void enqueue(String message) {
    this.callCount++;
    if (this.exceptionSupplier != null) {
      throw this.exceptionSupplier.get();
    }
  }

  @Override
  public void dequeue(String message) {
    this.callCount++;
    if (this.exceptionSupplier != null) {
      throw this.exceptionSupplier.get();
    }
  }

  @Override
  public String peek() {
    this.callCount++;
    if (this.exceptionSupplier != null) {
      throw this.exceptionSupplier.get();
    }

    return null;
  }

  public int getCallCount() {
    return callCount;
  }
}
