/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.functions.lambda.exceptions.handlers;

import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse.BatchItemFailure;

/**
 * Represents a function that may throw a checked exception.
 *
 * @param <T> The type of the input to the function.
 * @param <R> The type of the result of the function.
 * @param <E> The type of the checked exception that the function may throw.
 */
@FunctionalInterface
public interface LambdaSqsThrowingFunction<T, E extends RuntimeException> {

  /**
   * Applies this function to the given argument.
   *
   * @return The function result.
   * @throws E The exception that may be thrown.
   */
  BatchItemFailure apply(T message) throws E;
}
