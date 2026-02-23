/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.functions.lambda.exceptions.handlers;

import domain.exceptions.TransientException;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryConfig.Builder;
import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A default implementation of the LambdaRetryExceptionHandler interface.
 * This class provides a way to handle exceptions thrown by lambda functions
 * and retry the function execution.
 *
 * @param <ResponseT> the type of the response returned by the lambda function
 */
public class DefaultLambdaRetryExceptionHandler<ResponseT>
    implements LambdaRetryExceptionHandler<ResponseT> {

  @Override
  public <ErrorT extends RuntimeException, InputT> Function<InputT, ResponseT> handle(
      LambdaThrowingFunction<ResponseT, InputT, ErrorT> throwingFunction) {
    return handle(throwingFunction, builder -> {});
  }

  @Override
  public <ErrorT extends RuntimeException, InputT> Function<InputT, ResponseT> handle(
      LambdaThrowingFunction<ResponseT, InputT, ErrorT> throwingFunction,
      Consumer<Builder<Object>> configModifier) {
    Builder<Object> configBuilder =
        RetryConfig.custom()
            .maxAttempts(3)
            .waitDuration(Duration.ofMillis(100))
            .retryOnException(e -> e instanceof TransientException);
    configModifier.accept(configBuilder);
    RetryConfig config = configBuilder.build();

    return arg -> {
      Supplier<ResponseT> supplier =
          () -> {
            return throwingFunction.apply(arg);
          };

      Retry retry = Retry.of("lambdaRetry", config);
      Supplier<ResponseT> decoratedSupplier = Retry.decorateSupplier(retry, supplier);

      return decoratedSupplier.get();
    };
  }
}
