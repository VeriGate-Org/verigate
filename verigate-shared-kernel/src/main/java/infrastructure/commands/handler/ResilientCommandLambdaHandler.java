/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.commands.handler;

import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * An interface for Lambda request handlers supporting some kind of command processing resiliency.
 *
 * @param <InputT> The type of input into the Lambda handler.
 * @param <OutputT> The output of the Lambda handler.
 * @param <CommandT> The type of command this handler is expected to support.
 */
public interface ResilientCommandLambdaHandler<InputT, OutputT, CommandT>
    extends RequestHandler<InputT, OutputT> {
}
