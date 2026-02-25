package verigate.webbff.admin.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;

public class CreatePartnerCommandMessage {

  @JsonProperty("__artifact_type__")
  private final String artifactType = "CreatePartnerCommand";

  private final UUID id;
  private final Instant createdDate;
  private final String createdBy;
  private final String partnerName;
  private final String contactEmail;
  private final String partnerType;

  public CreatePartnerCommandMessage(
      UUID id,
      Instant createdDate,
      String createdBy,
      String partnerName,
      String contactEmail,
      String partnerType) {
    this.id = id;
    this.createdDate = createdDate;
    this.createdBy = createdBy;
    this.partnerName = partnerName;
    this.contactEmail = contactEmail;
    this.partnerType = partnerType;
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

  public String getPartnerName() {
    return partnerName;
  }

  public String getContactEmail() {
    return contactEmail;
  }

  public String getPartnerType() {
    return partnerType;
  }
}
