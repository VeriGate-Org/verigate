/*
 * Arthmatic + Karisani(c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.domain.routing;

import com.google.inject.Inject;
import crosscutting.environment.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.constants.DomainConstants;

/**
 * Default implementation of {@link VerificationCommandRouter} that achieves routing by
 * placing commands on the relevant queue based on the provider and command type.
 */
public class DefaultCommandRouter implements VerificationCommandRouter {

  private static final Logger logger = LoggerFactory.getLogger(DefaultCommandRouter.class);

  private final QueueDispatcherFactory dispatcherFactory;
  private final Environment environment;

  /**
   * Creates a new instance.
   *
   * @param dispatcherFactory the factory for creating queue dispatchers
   */
  @Inject
  public DefaultCommandRouter(QueueDispatcherFactory dispatcherFactory, Environment environment) {

    this.dispatcherFactory = dispatcherFactory;
    this.environment = environment;
  }

  @Override
  public void execute(VerifyPartyCommand command) {

    logger.info("Routing command: {}", command);

    // TODO: This is a temporary solution. The provider should be resolved using the provider
    // resolver.
    String queueName =
        environment.get(
            DomainConstants.VERIFICATION_TYPE_TO_QUEUE_NAME_MAP.get(command.getVerificationType()));

    logger.info("Resolved queue name: {}", queueName);

    dispatcherFactory.getDispatcher(queueName).dispatch(command);
  }
}
