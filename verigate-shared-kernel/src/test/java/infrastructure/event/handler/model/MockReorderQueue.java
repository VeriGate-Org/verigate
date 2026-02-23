/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler.model;

import domain.messages.MessageQueue;
import java.util.function.Supplier;

public final class MockReorderQueue<T> implements MessageQueue<T> {

  private int callCount;

  private final Supplier<RuntimeException> exceptionSupplier;

  public MockReorderQueue() {
    this.exceptionSupplier = null;
    this.callCount = 0;
  }
  public MockReorderQueue(Supplier<RuntimeException> exceptionSupplier) {
    this.exceptionSupplier = exceptionSupplier;
    this.callCount = 0;
  }

  @Override
  public void enqueue(T message) {
    this.callCount++;
    if (this.exceptionSupplier != null) {
      throw this.exceptionSupplier.get();
    }
  }

  public int getCallCount() {
    return callCount;
  }

  @Override
  public void dequeue(T message) {
    this.callCount++;
    if (this.exceptionSupplier != null) {
      throw this.exceptionSupplier.get();
    }
  }

  @Override
  public T peek() {
    this.callCount++;
    if (this.exceptionSupplier != null) {
      throw this.exceptionSupplier.get();
    }

    return null;
  }
}
