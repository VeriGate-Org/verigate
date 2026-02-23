package domain.exceptions;

/**
 * An exception type specifically designed for signaling situations where an operation has been
 * delayed and requires waiting for a specified duration before it can be retried or processed
 * further. This exception is intended to be thrown in contexts where operations are subject to
 * delays, and it carries the delay information necessary for handling the delay appropriately.
 *
 * <p>This exception extends {@code RuntimeException}, inheriting its functionalities and adding the
 * capability to include a {@link HandleDelay} instance, which quantifies the delay before the next
 * action should be taken.
 *
 * @see RuntimeException
 * @see HandleDelay
 */
public final class HandlerDelayException extends RuntimeException {
  /**
   * The delay associated with this exception, encapsulated in a {@link HandleDelay} instance. This
   * delay represents the time in milliseconds that should elapse before the action that led to this
   * exception can be retried or continued.
   */
  private final HandleDelay delay;

  /**
   * Constructs a new {@code HandlerDelayException} with the specified cause and delay. This
   * constructor allows for the chaining of exceptions by passing in a {@code Throwable} that
   * represents the cause of this exception, along with a {@link HandleDelay} instance that
   * specifies the required delay before retrying or continuing the operation.
   *
   * @param cause The cause of this exception, which is saved for later retrieval by the {@link
   *     #getCause()} method.
   * @param delay A {@link HandleDelay} instance representing the delay associated with this
   *     exception.
   */
  public HandlerDelayException(Throwable cause, HandleDelay delay) {
    super(cause);
    this.delay = delay;
  }

  /**
   * Returns the {@link HandleDelay} instance associated with this exception. This method can be
   * used to retrieve the delay information necessary to handle the delay appropriately, such as
   * waiting for the specified duration before retrying the operation that led to this exception.
   *
   * @return The {@link HandleDelay} specifying the duration to wait before proceeding with the
   *     operation.
   */
  public HandleDelay getDelay() {
    return delay;
  }
}
