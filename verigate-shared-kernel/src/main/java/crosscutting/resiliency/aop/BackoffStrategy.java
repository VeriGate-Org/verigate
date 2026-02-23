/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.resiliency.aop;

/**
 * Indicates the strategy to use for backoff between retries.
 */
public enum BackoffStrategy {

  /**
   * The wait duration between retries remains constant.
   */
  NONE,

  /**
   * The wait duration between retries increases exponentially.
   */
  EXPONENTIAL;
}
