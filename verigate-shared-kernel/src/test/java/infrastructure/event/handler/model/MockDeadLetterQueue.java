/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler.model;

import domain.messages.DeadLetterQueue;
import java.util.function.Supplier;

public final class MockDeadLetterQueue<T> implements DeadLetterQueue<T> {

  private int callCount;

  private final Supplier<RuntimeException> exceptionSupplier;

  public MockDeadLetterQueue() {
    this.exceptionSupplier = null;
    this.callCount = 0;
  }
  public MockDeadLetterQueue(Supplier<RuntimeException> exceptionSupplier) {
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
