/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler.model;

import application.handlers.EventHandler;
import java.util.function.Supplier;

public final class MockEventHandler implements EventHandler<MockEvent> {

  private int callCount = 0;

  private final Supplier<RuntimeException> exceptionSupplier;

  public MockEventHandler() {
    this.exceptionSupplier = () -> null;
  }

  public MockEventHandler(Supplier<RuntimeException> exceptionSupplier) {
    this.exceptionSupplier = exceptionSupplier;
  }

  @Override
  public void handle(MockEvent event) {
    callCount++;
    var exception =  exceptionSupplier.get();
    if (exception != null) {
      throw exception;
    }
  }

  public int getCallCount() {
    return callCount;
  }
}
