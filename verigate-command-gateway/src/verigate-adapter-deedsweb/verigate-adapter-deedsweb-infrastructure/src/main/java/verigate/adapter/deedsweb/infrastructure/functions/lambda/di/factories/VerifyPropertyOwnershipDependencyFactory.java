/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.functions.lambda.di.factories;

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
import verigate.adapter.deedsweb.infrastructure.functions.lambda.di.modules.VerifyPropertyOwnershipServiceModule;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/** Wrapper for property ownership lambda dependencies. */
public class VerifyPropertyOwnershipDependencyFactory extends DependencyFactory {

  /** Creates a factory with default Guice module. */
  public VerifyPropertyOwnershipDependencyFactory() {
    super(Guice.createInjector(Stage.PRODUCTION, new VerifyPropertyOwnershipServiceModule()));
  }

  /** Creates a factory with specified Guice injector. Useful for test purposes. */
  public VerifyPropertyOwnershipDependencyFactory(Injector injector) {
    super(injector);
  }

  public CommandHandler<VerifyPartyCommand, Map<String, String>>
      getVerifyPropertyOwnershipCommandHandler() {
    return injector.getInstance(new Key<>() {});
  }

  public InvalidMessageQueue<SQSMessage> getVerifyPropertyOwnershipInvalidMessageQueue() {
    return injector.getInstance(
        Key.get(
            new TypeLiteral<InvalidMessageQueue<SQSMessage>>() {},
            Names.named("VerifyPropertyOwnershipInvalidMessageQueue")));
  }

  public DeadLetterQueue<SQSMessage> getVerifyPropertyOwnershipDeadLetterQueue() {
    return injector.getInstance(
        Key.get(
            new TypeLiteral<DeadLetterQueue<SQSMessage>>() {},
            Names.named("VerifyPropertyOwnershipDeadLetterMessageQueue")));
  }
}
