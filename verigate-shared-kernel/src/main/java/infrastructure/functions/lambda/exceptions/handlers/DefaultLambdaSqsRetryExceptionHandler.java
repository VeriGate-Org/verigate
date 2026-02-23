/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.functions.lambda.exceptions.handlers;

import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse.BatchItemFailure;
import domain.exceptions.TransientException;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryConfig.Builder;
import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class implements the LambdaSqsExceptionHandler interface and provides a mechanism
 * to handle exceptions thrown by a LambdaSqsThrowingFunction with retry logic.
 */
public class DefaultLambdaSqsRetryExceptionHandler implements LambdaSqsRetryExceptionHandler {

  @Override
  public <E extends RuntimeException, I> Function<I, BatchItemFailure> handle(
      LambdaSqsThrowingFunction<I, E> throwingFunction) {
    return handle(throwingFunction, builder -> {});
  }

  @Override
  public <E extends RuntimeException, I> Function<I, BatchItemFailure> handle(
      LambdaSqsThrowingFunction<I, E> throwingFunction, Consumer<Builder<Object>> configModifier) {
    Builder<Object> configBuilder =
        RetryConfig.custom()
            .maxAttempts(3)
            .waitDuration(Duration.ofMillis(100))
            .retryOnException(e -> e instanceof TransientException);
    configModifier.accept(configBuilder);
    RetryConfig config = configBuilder.build();

    return arg -> {
      Supplier<BatchItemFailure> supplier =
          () -> {
            return throwingFunction.apply(arg);
          };

      Retry retry = Retry.of("lambdaRetry", config);
      Supplier<BatchItemFailure> decoratedSupplier = Retry.decorateSupplier(retry, supplier);

      return decoratedSupplier.get();
    };
  }
}
