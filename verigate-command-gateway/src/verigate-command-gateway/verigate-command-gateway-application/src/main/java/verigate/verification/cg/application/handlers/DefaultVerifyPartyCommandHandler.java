/*
 * Arthmatic + Karisani(c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.application.handlers;

import com.google.inject.Inject;
import crosscutting.metrics.Meter;
import domain.exceptions.InvariantViolationException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import infrastructure.featureflags.FeatureFlags;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.verification.cg.application.factories.VerifyPartySpecificationFactory;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.handlers.VerifyPartyCommandHandler;
import verigate.verification.cg.domain.logging.VerificationMdcContext;
import verigate.verification.cg.domain.routing.VerificationCommandRouter;

/**
 * The default implementation of the {@link VerifyPartyCommand} interface.
 *  This class handles the logic for adding a beneficiary to a policy.
 */
public final class DefaultVerifyPartyCommandHandler implements VerifyPartyCommandHandler {
  private final VerificationCommandRouter verificationCommandRouter;
  private final VerifyPartySpecificationFactory verifyPartySpecificationFactory;
  private final FeatureFlags featureFlags;
  private final Meter meter;
  private static final Logger logger =
      LoggerFactory.getLogger(DefaultVerifyPartyCommandHandler.class.getName());

  /**
   * Constructor for the DefaultVerifyPartyCommandHandler class.
   *
   */
  @Inject
  public DefaultVerifyPartyCommandHandler(
      VerificationCommandRouter verificationCommandRouter,
      VerifyPartySpecificationFactory verifyPartySpecificationFactory,
      FeatureFlags featureFlags,
      Meter meter) {
    this.verificationCommandRouter = verificationCommandRouter;
    this.verifyPartySpecificationFactory = verifyPartySpecificationFactory;
    this.featureFlags = featureFlags;
    this.meter = meter;
  }

  /**
   * Handles the {@link VerifyPartyCommand} command.
   *
   * @param command The command to handle
   */
  @Override
  public Map<String, String> handle(VerifyPartyCommand command)
      throws TransientException, PermanentException, InvariantViolationException {
    VerificationMdcContext.populate(command);
    logger.info("Handling verify party command with id: {}", command.getId());

    logger.info("Validating command id: {}", command.getId());
    var specification = verifyPartySpecificationFactory.createSpecification(command);
    command.validate(specification);

    // Check feature flag to allow disabling verification routing
    if (featureFlags.isFeatureEnabled("disable-route-verification-command-to-provider", false)) {
      logger.info("Verification routing disabled by feature flag for command: {}", command.getId());
      return null;
    }

    logger.info("Routing command: {}", command.getId());
    verificationCommandRouter.execute(command);
    meter.incrementCounter("verification.routed",
        "verification_type:" + command.getVerificationType().name());
    return null;
  }
}
