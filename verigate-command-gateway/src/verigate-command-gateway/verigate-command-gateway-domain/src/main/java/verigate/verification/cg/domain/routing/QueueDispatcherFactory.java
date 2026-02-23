/*
 * Arthmatic + Karisani(c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 *
 */

package verigate.verification.cg.domain.routing;

import infrastructure.commands.QueueCommandDispatcher;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Factory for creating and managing queue command dispatchers.
 * This factory is responsible for creating dispatchers that can send
 * commands to specific queues.
 */
public interface QueueDispatcherFactory {

  /**
   * Returns a dispatcher for sending commands to the specified queue.
   * If a dispatcher for the queue does not exist, one is created and
   * cached for future use.
   *
   * @param queueName The name of the queue to dispatch to
   * @return A dispatcher that can send commands to the appropriate queue
   */
  QueueCommandDispatcher<VerifyPartyCommand> getDispatcher(String queueName);
}
