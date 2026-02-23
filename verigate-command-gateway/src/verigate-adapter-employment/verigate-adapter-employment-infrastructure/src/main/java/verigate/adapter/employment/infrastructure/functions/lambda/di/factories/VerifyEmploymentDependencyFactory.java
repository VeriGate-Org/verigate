/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.employment.infrastructure.functions.lambda.di.factories;

import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import domain.commands.CommandHandler;
import domain.messages.DeadLetterQueue;
import domain.messages.InvalidMessageQueue;
import java.util.Map;
import verigate.adapter.employment.infrastructure.functions.lambda.di.modules.VerifyEmploymentServiceModule;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/** Wrapper for core dependencies. Allows for overrides via Guice. */
public class VerifyEmploymentDependencyFactory extends DependencyFactory {

  /** Creates a factory with default Guice module. */
  public VerifyEmploymentDependencyFactory() {
    super(Guice.createInjector(Stage.PRODUCTION, new VerifyEmploymentServiceModule()));
  }

  /** Creates a factory with specified Guice injector. Useful for test purposes. */
  public VerifyEmploymentDependencyFactory(Injector injector) {
    super(injector);
  }

  /**
   * Returns the command handler for the {@link VerifyPartyCommand}.
   *
   * @return The command handler for the {@link VerifyPartyCommand}.
   */
  public CommandHandler<VerifyPartyCommand, Map<String, String>>
      getVerifyEmploymentCommandHandler() {
    return injector.getInstance(new Key<>() {});
  }

  /**
   * Returns the queue for invalid messages for the {@link VerifyPartyCommand}.
   *
   * @return The queue for invalid messages for the {@link VerifyPartyCommand}.
   */
  public InvalidMessageQueue<SQSMessage> getVerifyEmploymentInvalidMessageQueue() {
    return injector.getInstance(
        Key.get(
            new TypeLiteral<InvalidMessageQueue<SQSMessage>>() {},
            Names.named("VerifyEmploymentInvalidMessageQueue")));
  }

  /**
   * Returns the dead-letter queue for the {@link VerifyPartyCommand}.
   *
   * @return The dead-letter queue for the {@link VerifyPartyCommand}.
   */
  public DeadLetterQueue<SQSMessage> getVerifyEmploymentDeadLetterQueue() {
    return injector.getInstance(
        Key.get(
            new TypeLiteral<DeadLetterQueue<SQSMessage>>() {},
            Names.named("VerifyEmploymentDeadLetterMessageQueue")));
  }
}
