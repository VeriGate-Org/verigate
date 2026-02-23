/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.functions.lambda.exceptions.handlers;

import io.github.resilience4j.retry.RetryConfig.Builder;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents an interface for handling exceptions in lambda retry operations.
 *
 * @param <ResponseT> the type of the response returned by the lambda function
 */
public interface LambdaRetryExceptionHandler<ResponseT> {
  /**
   * Handles the lambda function with retry logic.
   *
   * @param throwingFunction the lambda function that may throw an exception
   * @param <ErrorT> the type of the exception that may be thrown
   * @param <InputT> the type of the input parameter for the lambda function
   * @return a function that handles the lambda function with retry logic
   */
  <ErrorT extends RuntimeException, InputT> Function<InputT, ResponseT> handle(
      LambdaThrowingFunction<ResponseT, InputT, ErrorT> throwingFunction);

  /**
   * Handles the lambda function with retry logic and allows modifying the configuration.
   *
   * @param throwingFunction the lambda function that may throw an exception
   * @param configModifier a consumer to modify the configuration of the retry logic
   * @param <ErrorT> the type of the exception that may be thrown
   * @param <InputT> the type of the input parameter for the lambda function
   * @return a function that handles the lambda function with retry logic
   */
  <ErrorT extends RuntimeException, InputT> Function<InputT, ResponseT> handle(
      LambdaThrowingFunction<ResponseT, InputT, ErrorT> throwingFunction,
      Consumer<Builder<Object>> configModifier);
}
