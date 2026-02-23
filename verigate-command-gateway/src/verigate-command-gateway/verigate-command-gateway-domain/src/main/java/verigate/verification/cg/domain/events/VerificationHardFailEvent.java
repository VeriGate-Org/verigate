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
 * Event emitted when a verification is completed with a hard fail.
 */
public final class VerificationHardFailEvent extends VerificationEvent {

  /**
   * No-args constructor for serialization.
   */
  @SuppressWarnings("unused")
  private VerificationHardFailEvent() {
    super();
  }

  /**
   * Constructor for creating a VerificationHardFailEvent.
   *
   * @param verificationType the type of verification that succeeded
   * @param origination the location the verification request is being sent from
   */
  public VerificationHardFailEvent(
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
        DomainConstants.VERIFICATION_HARD_FAIL_EVENT,
        logicalClockReading,
        verificationType,
        origination);
  }
}
