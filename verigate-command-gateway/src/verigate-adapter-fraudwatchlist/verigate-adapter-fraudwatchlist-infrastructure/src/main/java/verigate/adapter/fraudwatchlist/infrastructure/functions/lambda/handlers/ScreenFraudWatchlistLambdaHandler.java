/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.fraudwatchlist.infrastructure.functions.lambda.handlers;

import infrastructure.commands.handler.sqs.ResilientSqsCommandLambdaHandler;
import java.util.Map;
import verigate.adapter.fraudwatchlist.infrastructure.functions.lambda.di.factories.ScreenFraudWatchlistDependencyFactory;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Handles the Lambda function for {@link VerifyPartyCommand} using fraud watchlist screening.
 */
public class ScreenFraudWatchlistLambdaHandler
    extends ResilientSqsCommandLambdaHandler<VerifyPartyCommand, Map<String, String>> {

  /** Default no-arg constructor for AWS Lambda init. */
  public ScreenFraudWatchlistLambdaHandler() {
    this(new ScreenFraudWatchlistDependencyFactory());
  }

  /** Allow construction with an arbitrary ScreenFraudWatchlistDependencyFactory. */
  public ScreenFraudWatchlistLambdaHandler(
      ScreenFraudWatchlistDependencyFactory dependencyFactory) {
    super(
        (messageBody) ->
            dependencyFactory.getSerializer().deserialize(messageBody, VerifyPartyCommand.class),
        dependencyFactory.getScreenFraudWatchlistCommandHandler(),
        dependencyFactory.getScreenFraudWatchlistInvalidMessageQueue(),
        dependencyFactory.getScreenFraudWatchlistDeadLetterQueue());
  }
}
