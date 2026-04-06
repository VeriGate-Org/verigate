/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.infrastructure.functions.lambda.di.factories;

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
import verigate.adapter.sars.infrastructure.functions.lambda.di.modules.VerifyVatVendorServiceModule;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/** Wrapper for VAT vendor verification dependencies. Allows for overrides via Guice. */
public class VerifyVatVendorDependencyFactory extends DependencyFactory {

  /** Creates a factory with default Guice module. */
  public VerifyVatVendorDependencyFactory() {
    super(Guice.createInjector(Stage.PRODUCTION, new VerifyVatVendorServiceModule()));
  }

  /** Creates a factory with specified Guice injector. Useful for test purposes. */
  public VerifyVatVendorDependencyFactory(Injector injector) {
    super(injector);
  }

  /**
   * Returns the command handler for the VAT vendor verification.
   *
   * @return the command handler
   */
  public CommandHandler<VerifyPartyCommand, Map<String, String>>
      getVerifyVatVendorCommandHandler() {
    return injector.getInstance(new Key<>() {});
  }

  /**
   * Returns the queue for invalid messages.
   *
   * @return the invalid message queue
   */
  public InvalidMessageQueue<SQSMessage> getVerifyVatVendorInvalidMessageQueue() {
    return injector.getInstance(
        Key.get(
            new TypeLiteral<InvalidMessageQueue<SQSMessage>>() {},
            Names.named("VerifyVatVendorInvalidMessageQueue")));
  }

  /**
   * Returns the dead-letter queue.
   *
   * @return the dead-letter queue
   */
  public DeadLetterQueue<SQSMessage> getVerifyVatVendorDeadLetterQueue() {
    return injector.getInstance(
        Key.get(
            new TypeLiteral<DeadLetterQueue<SQSMessage>>() {},
            Names.named("VerifyVatVendorDeadLetterMessageQueue")));
  }
}
