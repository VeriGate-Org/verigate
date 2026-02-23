/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler;

/**
 * A record that holds configuration parameters for retrying operations. This configuration includes
 * the maximum number of retry attempts, the base delay between retries, and the maximum delay
 * allowed between retries.
 *
 * @param maxRetryAttempts the maximum number of times an operation should be retried before giving
 *     up
 * @param baseDelayMs the initial delay between retries, in milliseconds
 * @param maxDelayMs the maximum delay between retries, in milliseconds
 */
public record RetryConfig(int maxRetryAttempts, long baseDelayMs, long maxDelayMs) {}
