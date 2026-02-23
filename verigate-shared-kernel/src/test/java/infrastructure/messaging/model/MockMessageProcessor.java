package infrastructure.messaging.model;

import java.util.function.Supplier;

public final class MockMessageProcessor {
  private final Supplier<RuntimeException> exceptionSupplier;
  private final int throwUntil;

  private int processCount;


  public MockMessageProcessor(Supplier<RuntimeException> exceptionSupplier, int throwCount) {
    this.exceptionSupplier = exceptionSupplier;
    this.throwUntil = throwCount;
    this.processCount = 0;
  }

  public void process() {
    processCount++;

    if (processCount <= throwUntil) {
      throw exceptionSupplier.get();
    }
  }

  public int getProcessCount() {
    return processCount;
  }
}
