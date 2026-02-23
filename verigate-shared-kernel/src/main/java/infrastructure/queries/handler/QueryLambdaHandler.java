/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.queries.handler;

import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * An interface for Lambda request handlers supporting some kind of query processing resiliency.
 *
 * @param <QueryT> The type of input into the Lambda handler.
 * @param <OutputT> The output of the Lambda handler.
 */
public interface QueryLambdaHandler<QueryT, OutputT> extends RequestHandler<QueryT, OutputT> {}
