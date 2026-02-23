/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler.model;

import infrastructure.event.handler.DeadLetterTracker;
import java.util.Set;
import java.util.function.Supplier;

public final class MockDeadLetterTracking implements DeadLetterTracker<Integer> {

  private int trackCount = 0;
  private final Supplier<RuntimeException> exceptionSupplier;
  private boolean isKeyPresent;

  public MockDeadLetterTracking(boolean isKeyPresent) {
    this.exceptionSupplier = null;
    this.isKeyPresent = isKeyPresent;
  }

  public MockDeadLetterTracking(
      Supplier<RuntimeException> exceptionSupplier, boolean isKeyPresent) {
    this.exceptionSupplier = exceptionSupplier;
    this.isKeyPresent = isKeyPresent;
  }

  @Override
  public boolean isKeyPresent(Integer key) {
    if (this.exceptionSupplier != null) {
      throw this.exceptionSupplier.get();
    }

    return this.isKeyPresent;
  }

  @Override
  public void trackKey(Integer key) {
    if (this.exceptionSupplier != null) {
      throw this.exceptionSupplier.get();
    }

    trackCount++;
  }

  @Override
  public void untrackKey(Integer key) {
    if (this.exceptionSupplier != null) {
      throw this.exceptionSupplier.get();
    }
  }

  @Override
  public Set<Integer> fetchAllKeys() {
    return null;
  }

  @Override
  public boolean isTransient() {
    return false;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  public int getTrackCount() {
    return trackCount;
  }
}
