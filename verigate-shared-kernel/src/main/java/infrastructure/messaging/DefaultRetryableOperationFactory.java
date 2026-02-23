/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.messaging;

/**
 * A factory class that produces {@link RetryableOperation} instances using an exponential backoff
 * with jitter retry strategy. This class is designed to provide standardized retry behavior for
 * operations that may fail and require retrying under a more robust retry logic.
 */
public final class DefaultRetryableOperationFactory implements RetryableOperationFactory {

  /**
   * Creates and returns a new {@link RetryableOperation} instance that employs an exponential
   * backoff with jitter strategy to handle retries. This strategy helps to mitigate potential
   * issues such as retry storms by randomizing retry intervals.
   *
   * @param <MessageT> the type of message that the retryable operation will handle
   */
  @Override
  public <MessageT> RetryableOperation<MessageT> create() {
    return new BlockingRetryableOperation<>(new ExponentialBackoffWithJitterStrategy());
  }
}
