/*
 * Arthmatic + Karisani(c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium
 * is strictly prohibited. Proprietary and confidential.
 */

package verigate.verification.cg.domain.commands.incoming;

import crosscutting.serialization.DataContract;
import domain.commands.BaseCommand;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import verigate.verification.cg.domain.models.Origination;
import verigate.verification.cg.domain.models.VerificationType;

/**
 * Represents a command to verify a party.
 */
public class VerifyPartyCommand extends BaseCommand {

  @DataContract private final String partnerId;
  @DataContract private final VerificationType verificationType;
  @DataContract private final Origination origination;
  @DataContract private final Map<String, Object> metadata;

  /**
   * Constructs a new {@link VerifyPartyCommand} with default details.
   */
  public VerifyPartyCommand() {
    this.partnerId = null;
    this.verificationType = null;
    this.origination = null;
    this.metadata = null;
  }

  /**
   * Constructor for VerifyPartyCommand.
   *
   * @param verificationType the type of verification to be performed
   * @param origination the location the verification request is being sent from
   * @param metadata the data specific to that verification request
   */
  public VerifyPartyCommand(
      UUID id,
      Instant createdAt,
      String createdBy,
      VerificationType verificationType,
      Origination origination,
      Map<String, Object> metadata) {
    super(id, createdAt, createdBy);
    this.partnerId = metadata != null ? (String) metadata.get("partnerId") : null;
    this.verificationType = verificationType;
    this.origination = origination;
    this.metadata = metadata;
  }

  /**
   * Constructor with explicit partnerId.
   */
  public VerifyPartyCommand(
      UUID id,
      Instant createdAt,
      String createdBy,
      String partnerId,
      VerificationType verificationType,
      Origination origination,
      Map<String, Object> metadata) {
    super(id, createdAt, createdBy);
    this.partnerId = partnerId;
    this.verificationType = verificationType;
    this.origination = origination;
    this.metadata = metadata;
  }

  public String getPartnerId() {
    if (partnerId != null) {
      return partnerId;
    }
    if (metadata != null && metadata.get("partnerId") instanceof String pid) {
      return pid;
    }
    return null;
  }

  public VerificationType getVerificationType() {
    return verificationType;
  }

  public Origination getOrigination() {
    return origination;
  }

  public Map<String, Object> getMetadata() {
    return metadata;
  }
}
