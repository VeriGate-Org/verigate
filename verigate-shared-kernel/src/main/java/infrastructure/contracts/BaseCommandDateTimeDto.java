/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.contracts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;

/**
 * This class represents a base command data transfer object (DTO). It contains common properties
 * for command DTOs, such as an ID and a created date.
 * TODO: This needs to become the standard basedCommandDto
 */
public class BaseCommandDateTimeDto {

  private final UUID id;
  private final Instant createdDate;
  private final String createdBy;

  /**
   * Constructs a new BaseCommandDto.
   *
   * @param id The ID of the command.
   * @param createdDate The created date of the command.
   */
  @JsonCreator
  public BaseCommandDateTimeDto(
      @JsonProperty("id") UUID id,
      @JsonProperty("createdDate") Instant createdDate,
      @JsonProperty("createdBy") String createdBy) {
    this.id = id;
    this.createdDate = createdDate;
    this.createdBy = createdBy;
  }

  /**
   * Gets the ID of the command.
   *
   * @return The ID of the command.
   */
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  /**
   * Gets the created date of the command.
   *
   * @return The created date of the command.
   */
  @JsonProperty("createdDate")
  public Instant getCreatedDate() {
    return createdDate; // defensive copy
  }

  /**
   * Gets the user who created the command.
   *
   * @return The user who created the command.
   */
  @JsonProperty("createdBy")
  public String getCreatedBy() {
    return createdBy;
  }
}
