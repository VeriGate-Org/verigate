/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.functions.lambda.exceptions.handlers;

/**
 * Represents a function that may throw a checked exception.
 *
 * @param <ResponseT> The type of the input to the function.
 * @param <InputT> The type of the result of the function.
 * @param <ErrorT> The type of the checked exception that the function may throw.
 */
@FunctionalInterface
public interface LambdaThrowingFunction<ResponseT, InputT, ErrorT extends RuntimeException> {

  ResponseT apply(InputT input) throws ErrorT;
}
