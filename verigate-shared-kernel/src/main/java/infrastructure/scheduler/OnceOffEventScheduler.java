/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.scheduler;

import java.time.Instant;

/**
 * This interface is responsible for scheduling a one-off event.
 */
public interface OnceOffEventScheduler {
  public void scheduleQueueMessage(
      String queueName, String eventBody, String scheduleName, Instant scheduledInstant);
}
