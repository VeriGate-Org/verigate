/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** InMemoryDeadLetterTrackingDao. */
public final class InMemoryDeadLetterTracking<KeyT> implements DeadLetterTracker<KeyT> {
  private final HashSet<KeyT> trackedKeys;

  public InMemoryDeadLetterTracking() {
    this.trackedKeys = new HashSet<>();
  }


  @Override
  public boolean isKeyPresent(KeyT key) {
    return trackedKeys.contains(key);
  }

  @Override
  public void trackKey(KeyT key) {
    trackedKeys.add(key);
  }

  @Override
  public void untrackKey(KeyT key) {
    trackedKeys.remove(key);
  }

  @Override
  public Set<KeyT> fetchAllKeys() {
    return Collections.unmodifiableSet(trackedKeys);
  }

  @Override
  public boolean isTransient() {
    return true;
  }

  @Override
  public boolean isEmpty() {
    return trackedKeys.isEmpty();
  }
}
