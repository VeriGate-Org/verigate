/*
 * Arthmatic + Karisani(c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.domain.routing;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import crosscutting.environment.Environment;
import infrastructure.commands.QueueCommandDispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.constants.DomainConstants;
import verigate.verification.cg.domain.models.VerificationType;
import verigate.verification.cg.domain.routing.DefaultCommandRouter;
import verigate.verification.cg.domain.routing.QueueDispatcherFactory;

@ExtendWith(MockitoExtension.class)
class DefaultCommandRouterTest {

  @Mock private QueueDispatcherFactory dispatcherFactory;
  @Mock private Environment environment;
  @Mock private QueueCommandDispatcher<VerifyPartyCommand> queueDispatcher;
  @Mock private VerifyPartyCommand command;

  private DefaultCommandRouter router;

  @BeforeEach
  void setUp() {
    router = new DefaultCommandRouter(dispatcherFactory, environment);
  }

  @Test
  @DisplayName("Route personal details verification to Worldcheck queue")
  void routePersonalDetailsVerificationToWorldcheckQueue() {
    // Arrange
    String queueName = "WORLDCHECK_ADAPTER_QUEUE_NAME";
    when(command.getVerificationType())
        .thenReturn(VerificationType.VERIFICATION_OF_PERSONAL_DETAILS);
    when(environment.get(
            DomainConstants.VERIFICATION_TYPE_TO_QUEUE_NAME_MAP.get(
                VerificationType.VERIFICATION_OF_PERSONAL_DETAILS)))
        .thenReturn(queueName);
    when(dispatcherFactory.getDispatcher(queueName)).thenReturn(queueDispatcher);

    // Act
    router.execute(command);

    // Assert
    verify(environment)
        .get(
            DomainConstants.VERIFICATION_TYPE_TO_QUEUE_NAME_MAP.get(
                VerificationType.VERIFICATION_OF_PERSONAL_DETAILS));
    verify(dispatcherFactory).getDispatcher(queueName);
    verify(queueDispatcher).dispatch(command);
  }

  @Test
  @DisplayName("Route bank details verification to QLink queue")
  void routeBankDetailsVerificationToQlinkQueue() {
    // Arrange
    String queueName = "QLINK_ADAPTER_QUEUE_NAME";
    when(command.getVerificationType()).thenReturn(VerificationType.VERIFICATION_OF_BANK_DETAILS);
    when(environment.get(
            DomainConstants.VERIFICATION_TYPE_TO_QUEUE_NAME_MAP.get(
                VerificationType.VERIFICATION_OF_BANK_DETAILS)))
        .thenReturn(queueName);
    when(dispatcherFactory.getDispatcher(queueName)).thenReturn(queueDispatcher);

    // Act
    router.execute(command);

    // Assert
    verify(environment)
        .get(
            DomainConstants.VERIFICATION_TYPE_TO_QUEUE_NAME_MAP.get(
                VerificationType.VERIFICATION_OF_BANK_DETAILS));
    verify(dispatcherFactory).getDispatcher(queueName);
    verify(queueDispatcher).dispatch(command);
  }

  @Test
  @DisplayName("Route sanctions screening to ORMS queue")
  void routeSanctionsScreeningToOrmsQueue() {
    // Arrange
    String queueName = "ORMS_ADAPTER_QUEUE_NAME";
    when(command.getVerificationType()).thenReturn(VerificationType.SANCTIONS_SCREENING);
    when(environment.get(
            DomainConstants.VERIFICATION_TYPE_TO_QUEUE_NAME_MAP.get(
                VerificationType.SANCTIONS_SCREENING)))
        .thenReturn(queueName);
    when(dispatcherFactory.getDispatcher(queueName)).thenReturn(queueDispatcher);

    // Act
    router.execute(command);

    // Assert
    verify(environment)
        .get(
            DomainConstants.VERIFICATION_TYPE_TO_QUEUE_NAME_MAP.get(
                VerificationType.SANCTIONS_SCREENING));
    verify(dispatcherFactory).getDispatcher(queueName);
    verify(queueDispatcher).dispatch(command);
  }

  @Test
  @DisplayName("Should throw exception when verification type is null")
  void shouldThrowExceptionWhenVerificationTypeIsNull() {
    // Arrange
    when(command.getVerificationType()).thenReturn(null);

    // Act & Assert
    org.junit.jupiter.api.Assertions.assertThrows(
        NullPointerException.class, () -> router.execute(command));
  }
}
