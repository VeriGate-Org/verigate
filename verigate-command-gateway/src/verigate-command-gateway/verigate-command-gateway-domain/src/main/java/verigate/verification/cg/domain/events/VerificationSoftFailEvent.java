/*
 * Arthmatic + Karisani(c) 2024 - 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.domain.events;

import java.time.Instant;
import java.util.UUID;
import verigate.verification.cg.domain.constants.DomainConstants;
import verigate.verification.cg.domain.models.Origination;
import verigate.verification.cg.domain.models.VerificationType;

/**
 * Event emitted when a verification is completed with a soft fail.
 */
public final class VerificationSoftFailEvent extends VerificationEvent {

  /**
   * No-args constructor for serialization.
   */
  @SuppressWarnings("unused")
  private VerificationSoftFailEvent() {
    super();
  }

  /**
   * Constructor for creating a VerificationSoftFailEvent.
   *
   * @param verificationType the type of verification that succeeded
   */
  public VerificationSoftFailEvent(
      UUID id,
      Instant noticedDate,
      Instant effectedDate,
      Integer logicalClockReading,
      VerificationType verificationType,
      Origination origination) {
    super(
        id,
        noticedDate,
        effectedDate,
        DomainConstants.VERIFICATION_SOFT_FAIL_EVENT,
        logicalClockReading,
        verificationType,
        origination);
  }
}
