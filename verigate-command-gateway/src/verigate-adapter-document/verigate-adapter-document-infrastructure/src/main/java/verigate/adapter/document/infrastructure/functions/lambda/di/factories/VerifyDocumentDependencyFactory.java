/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.infrastructure.functions.lambda.di.factories;

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
import verigate.adapter.document.infrastructure.functions.lambda.di.modules.VerifyDocumentServiceModule;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/** Wrapper for core dependencies. Allows for overrides via Guice. */
public class VerifyDocumentDependencyFactory extends DependencyFactory {

  /** Creates a factory with default Guice module. */
  public VerifyDocumentDependencyFactory() {
    super(Guice.createInjector(Stage.PRODUCTION, new VerifyDocumentServiceModule()));
  }

  /** Creates a factory with specified Guice injector. Useful for test purposes. */
  public VerifyDocumentDependencyFactory(Injector injector) {
    super(injector);
  }

  /**
   * Returns the command handler for the {@link VerifyPartyCommand}.
   *
   * @return The command handler for the {@link VerifyPartyCommand}.
   */
  public CommandHandler<VerifyPartyCommand, Map<String, String>>
      getVerifyDocumentCommandHandler() {
    return injector.getInstance(new Key<>() {});
  }

  /**
   * Returns the queue for invalid messages for the {@link VerifyPartyCommand}.
   *
   * @return The queue for invalid messages for the {@link VerifyPartyCommand}.
   */
  public InvalidMessageQueue<SQSMessage> getVerifyDocumentInvalidMessageQueue() {
    return injector.getInstance(
        Key.get(
            new TypeLiteral<InvalidMessageQueue<SQSMessage>>() {},
            Names.named("VerifyDocumentInvalidMessageQueue")));
  }

  /**
   * Returns the dead-letter queue for the {@link VerifyPartyCommand}.
   *
   * @return The dead-letter queue for the {@link VerifyPartyCommand}.
   */
  public DeadLetterQueue<SQSMessage> getVerifyDocumentDeadLetterQueue() {
    return injector.getInstance(
        Key.get(
            new TypeLiteral<DeadLetterQueue<SQSMessage>>() {},
            Names.named("VerifyDocumentDeadLetterMessageQueue")));
  }
}
