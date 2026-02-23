/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.functions.lambda.exceptions.handlers;

import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse.BatchItemFailure;
import io.github.resilience4j.retry.RetryConfig.Builder;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This interface represents a handler for exceptions.
 *
 * @param <BatchItemFailure> The return type.
 * @param <I> The input type.
 * @param <E> The exception type.
 * @param <LambdaSqsThrowingFunction> The function that throws the exception.
 */
public interface LambdaSqsRetryExceptionHandler {
  <E extends RuntimeException, I> Function<I, BatchItemFailure> handle(
      LambdaSqsThrowingFunction<I, E> throwingFunction);

  <E extends RuntimeException, I> Function<I, BatchItemFailure> handle(
      LambdaSqsThrowingFunction<I, E> throwingFunction, Consumer<Builder<Object>> configModifier);
}
