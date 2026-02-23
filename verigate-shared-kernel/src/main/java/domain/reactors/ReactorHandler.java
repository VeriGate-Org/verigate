/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.reactors;

/**
 * `ReactorHandler` is the interface that represents
 * the handler for the reactor.
 */
public interface ReactorHandler<EventT> {
  void handle(EventT event);
}
