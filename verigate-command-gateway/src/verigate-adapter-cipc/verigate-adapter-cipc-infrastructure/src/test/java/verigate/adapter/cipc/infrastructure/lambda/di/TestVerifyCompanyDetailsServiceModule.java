/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.infrastructure.lambda.di;

import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import domain.commands.CommandHandler;
import domain.messages.DeadLetterQueue;
import domain.messages.EphemeralMessageQueue;
import domain.messages.InvalidMessageQueue;
import java.util.Map;
import verigate.adapter.cipc.application.handlers.DefaultVerifyCompanyDetailsCommandHandler;
import verigate.adapter.cipc.domain.services.CipcCompanyService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Test service module that provides mock implementations and in-memory queues for testing.
 */
public final class TestVerifyCompanyDetailsServiceModule extends TestServiceModule {

  public TestVerifyCompanyDetailsServiceModule() {
    super();
  }

  @Provides
  @Singleton
  @Named("VerifyCompanyDetailsInvalidMessageQueue")
  private InvalidMessageQueue<SQSMessage> provideVerifyCompanyDetailsInvalidMessageQueue() {
    return new EphemeralMessageQueue<>();
  }

  @Provides
  @Singleton
  @Named("VerifyCompanyDetailsDeadLetterMessageQueue")
  private DeadLetterQueue<SQSMessage> provideVerifyCompanyDetailsDeadLetterMessageQueue() {
    return new EphemeralMessageQueue<>();
  }

  /**
   * This method provides the command handler with retry capabilities for testing.
   */
  @Provides
  @Singleton
  public CommandHandler<VerifyPartyCommand, Map<String, String>>
      provideVerifyCompanyDetailsCommandHandler(CipcCompanyService companyService) {

    return new DefaultVerifyCompanyDetailsCommandHandler(companyService);
  }
}
