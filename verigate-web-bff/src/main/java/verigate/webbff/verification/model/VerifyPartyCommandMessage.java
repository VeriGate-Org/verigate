package verigate.webbff.verification.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class VerifyPartyCommandMessage {

  @JsonProperty("__artifact_type__")
  private final String artifactType = "VerifyPartyCommand";

  private final UUID id;
  private final Instant createdDate;
  private final String createdBy;
  private final VerificationType verificationType;
  private final Origination origination;
  private final Map<String, Object> metadata;

  public VerifyPartyCommandMessage(
      UUID id,
      Instant createdDate,
      String createdBy,
      VerificationType verificationType,
      Origination origination,
      Map<String, Object> metadata) {
    this.id = id;
    this.createdDate = createdDate;
    this.createdBy = createdBy;
    this.verificationType = verificationType;
    this.origination = origination;
    this.metadata = metadata;
  }

  public String getArtifactType() {
    return artifactType;
  }

  public UUID getId() {
    return id;
  }

  public Instant getCreatedDate() {
    return createdDate;
  }

  public String getCreatedBy() {
    return createdBy;
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

  public record Origination(OriginationType originationType, UUID originationId) {}
}
