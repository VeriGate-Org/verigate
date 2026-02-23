/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler;

import domain.messages.MessageQueue;

/**
 * Abstract decorator class for handling the enqueueing of messages with retry capabilities.
 *
 * @param <T> the type of message this handles
 */
public abstract class MessageQueueDecorator<T> implements MessageQueue<T> {

  private final MessageQueue<T> messageQueue;

  /**
   * Constructs a new instance.
   *
   * @param messageQueue the original message queue that will attempt to enqueue messages
   */
  protected MessageQueueDecorator(MessageQueue<T> messageQueue) {
    this.messageQueue = messageQueue;
  }

  /** {@inheritDoc} */
  @Override
  public void enqueue(T message) {
    messageQueue.enqueue(message);
  }

  /** {@inheritDoc} */
  @Override
  public void dequeue(T message) {
    messageQueue.dequeue(message);
  }

  /** {@inheritDoc} */
  @Override
  public T peek() {
    return messageQueue.peek();
  }
}
