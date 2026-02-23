/*
 * Arthmatic + Karisani(c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.domain.events;

import domain.events.BaseEvent;
import java.util.List;

/**
 * Defines a contract for a service that publishes verification-related domain events.
 */
public interface VerificationEventPublisher {

  /**
   * Publishes a list of domain events.
   *
   * @param events A list of domain events derived from the BaseEvent class.
   */
  void publish(List<BaseEvent> events);
}
