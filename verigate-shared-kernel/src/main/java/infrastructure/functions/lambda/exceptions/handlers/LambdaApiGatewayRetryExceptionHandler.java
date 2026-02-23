/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.functions.lambda.exceptions.handlers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.common.base.Function;
import domain.exceptions.PermanentException;
import domain.exceptions.StringExceptionBuilder;
import domain.exceptions.TransientException;
import infrastructure.apigateway.ApiGatewayResponse;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.time.Duration;
import java.util.function.Supplier;
import java.util.logging.Logger;

/** Default exception handler that wraps checked exceptions in a RuntimeException. */
public class LambdaApiGatewayRetryExceptionHandler implements LambdaExceptionHandler {
  private static final Logger logger =
      Logger.getLogger(LambdaApiGatewayRetryExceptionHandler.class.getName());

  /**
   * Handles the exception by wrapping checked exceptions in a RuntimeException. The function is
   * then retried 3 times with a 1 second delay. If the function still fails, a 500 status code is
   * returned. If the function succeeds, the result is returned.
   *
   * @param throwingFunction The function that throws the exception.
   * @param <E> The exception type.
   * @return A function that handles the exception.
   */
  @Override
  public <E extends RuntimeException>
      Function<APIGatewayProxyRequestEvent, ApiGatewayResponse> handle(
          LambdaApiGatewayThrowingFunction<E> throwingFunction) {
    return arg -> {
      RetryConfig config =
          RetryConfig.custom()
              .maxAttempts(3)
              .waitDuration(Duration.ofMillis(1000))
              .retryOnException(e -> e instanceof TransientException)
              .build();

      Supplier<ApiGatewayResponse> supplier =
          () -> {
            try {
              return throwingFunction.apply(arg);
            } catch (PermanentException e) {
              logger.severe("Permanent Exception occurred: " + e.getMessage());
              return new ApiGatewayResponse.Builder()
                  .setBody(e.getMessage())
                  .setStatusCode(400)
                  .addJsonHeader()
                  .build();
            } catch (TransientException e) {
              logger.severe("Transient Exception occurred: " + e.getMessage());
              return new ApiGatewayResponse.Builder()
                  .setBody(e.getMessage())
                  .setStatusCode(400)
                  .addJsonHeader()
                  .build();
            } catch (Throwable e) {
              logger.severe("Exception occurred: " + e);
              return new ApiGatewayResponse.Builder()
                  .setBody(
                      StringExceptionBuilder.builder()
                          .withDetail(String.format("{\"error\" : %s}", e.getMessage()))
                          .build())
                  .setStatusCode(500)
                  .addJsonHeader()
                  .build();
            }
          };

      Retry retry = Retry.of("lambdaRetry", config);
      Supplier<ApiGatewayResponse> decoratedSupplier = Retry.decorateSupplier(retry, supplier);

      return decoratedSupplier.get();
    };
  }
}
