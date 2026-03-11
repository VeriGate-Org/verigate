/*
 * Arthmatic + Karisani(c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.domain.events;

import crosscutting.serialization.DataContract;
import domain.events.BaseEvent;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.MDC;
import verigate.verification.cg.domain.models.Origination;
import verigate.verification.cg.domain.models.VerificationType;

/**
 * Event emitted when a verification is completed.
 */
public class VerificationEvent extends BaseEvent<Object> {

  @DataContract VerificationType verificationType;
  @DataContract Origination origination;
  @DataContract String correlationId;
  @DataContract String partnerId;

  /**
   * No-args constructor for serialization.
   */
  @SuppressWarnings("unused")
  public VerificationEvent() {
    super();
    this.verificationType = null;
    this.origination = null;
    this.correlationId = null;
    this.partnerId = null;
  }

  /**
   * Constructor for creating a VerificationEvent.
   *
   * @param verificationType the type of verification that succeeded
   * @param origination the location the verification request is being sent from
   */
  public VerificationEvent(
      UUID id,
      Instant noticedDate,
      Instant effectedDate,
      String eventType,
      Integer logicalClockReading,
      VerificationType verificationType,
      Origination origination) {
    super(id, eventType, noticedDate, effectedDate, logicalClockReading);
    this.verificationType = verificationType;
    this.origination = origination;
    this.correlationId = MDC.get("correlationId");
    this.partnerId = MDC.get("partnerId");
  }

  public String getCorrelationId() {
    return correlationId;
  }

  public String getPartnerId() {
    return partnerId;
  }
}
