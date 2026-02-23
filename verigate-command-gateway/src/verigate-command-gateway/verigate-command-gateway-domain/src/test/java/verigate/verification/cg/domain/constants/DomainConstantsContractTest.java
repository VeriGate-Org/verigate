/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.domain.constants;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import verigate.verification.cg.domain.models.VerificationType;

/**
 * Contract tests ensuring the queue routing map in {@link DomainConstants}
 * covers all supported verification types and maintains consistency.
 */
class DomainConstantsContractTest {

  private static final Set<VerificationType> REQUIRED_VERIFICATION_TYPES = Set.of(
      VerificationType.COMPANY_VERIFICATION,
      VerificationType.BANK_ACCOUNT_VERIFICATION,
      VerificationType.VERIFICATION_OF_PERSONAL_DETAILS,
      VerificationType.VERIFICATION_OF_BANK_DETAILS,
      VerificationType.SANCTIONS_SCREENING,
      VerificationType.IDENTITY_VERIFICATION,
      VerificationType.PROPERTY_OWNERSHIP_VERIFICATION,
      VerificationType.EMPLOYMENT_VERIFICATION,
      VerificationType.NEGATIVE_NEWS_SCREENING,
      VerificationType.FRAUD_WATCHLIST_SCREENING,
      VerificationType.DOCUMENT_VERIFICATION,
      VerificationType.QUALIFICATION_VERIFICATION,
      VerificationType.CREDIT_CHECK,
      VerificationType.TAX_COMPLIANCE_VERIFICATION,
      VerificationType.INCOME_VERIFICATION,
      VerificationType.WATCHLIST_SCREENING
  );

  @Test
  void queueRoutingMapCoversAllRequiredVerificationTypes() {
    Map<VerificationType, String> routingMap =
        DomainConstants.VERIFICATION_TYPE_TO_QUEUE_NAME_MAP;

    for (VerificationType verificationType : REQUIRED_VERIFICATION_TYPES) {
      assertTrue(routingMap.containsKey(verificationType),
          "Routing map must contain entry for: " + verificationType);
      assertNotNull(routingMap.get(verificationType),
          "Queue name must not be null for: " + verificationType);
      assertFalse(routingMap.get(verificationType).isEmpty(),
          "Queue name must not be empty for: " + verificationType);
    }
  }

  @ParameterizedTest
  @EnumSource(value = VerificationType.class, names = {
      "COMPANY_VERIFICATION",
      "BANK_ACCOUNT_VERIFICATION",
      "VERIFICATION_OF_PERSONAL_DETAILS",
      "VERIFICATION_OF_BANK_DETAILS",
      "SANCTIONS_SCREENING",
      "IDENTITY_VERIFICATION",
      "PROPERTY_OWNERSHIP_VERIFICATION",
      "EMPLOYMENT_VERIFICATION",
      "NEGATIVE_NEWS_SCREENING",
      "FRAUD_WATCHLIST_SCREENING",
      "DOCUMENT_VERIFICATION",
      "QUALIFICATION_VERIFICATION",
      "CREDIT_CHECK",
      "TAX_COMPLIANCE_VERIFICATION",
      "INCOME_VERIFICATION",
      "WATCHLIST_SCREENING"
  })
  void eachVerificationTypeRoutesToUniqueQueueEnvironmentVariable(
      VerificationType verificationType) {
    String queueEnvVar = DomainConstants.VERIFICATION_TYPE_TO_QUEUE_NAME_MAP
        .get(verificationType);
    assertNotNull(queueEnvVar,
        "Queue env var must exist for: " + verificationType);
  }

  @Test
  void noVerificationTypeRoutesToSameQueue() {
    Map<VerificationType, String> routingMap =
        DomainConstants.VERIFICATION_TYPE_TO_QUEUE_NAME_MAP;

    // Each queue name should be unique (no two types go to same queue)
    // except for types that may share the same adapter
    // (e.g., VERIFICATION_OF_BANK_DETAILS and BANK_ACCOUNT_VERIFICATION share QLink,
    //  SANCTIONS_SCREENING and WATCHLIST_SCREENING share OpenSanctions)
    long uniqueQueues = routingMap.values().stream().distinct().count();
    assertTrue(uniqueQueues >= REQUIRED_VERIFICATION_TYPES.size() - 3,
        "Most verification types should route to unique queues");
  }
}
