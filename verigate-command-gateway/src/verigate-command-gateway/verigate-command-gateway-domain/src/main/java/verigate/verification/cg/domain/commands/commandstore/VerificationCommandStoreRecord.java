/*
 * Arthmatic + Karisani(c) 2024 - 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.domain.commands.commandstore;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class represents a command store record for verification commands.
 */
public class VerificationCommandStoreRecord {

  private UUID commandId;
  private String commandName;
  private VerificationCommandStatusEnum status;
  private List<String> errorDetails;
  private Map<String, String> auxiliaryData;

  /**
   * Constructor for the VerificationCommandStoreRecord.
   *
   * @param commandId the command id
   * @param commandName the command name
   * @param status the status
   * @param errorDetails the error details
   * @param auxiliaryData the auxiliary data
   */
  public VerificationCommandStoreRecord(
      UUID commandId,
      String commandName,
      VerificationCommandStatusEnum status,
      List<String> errorDetails,
      Map<String, String> auxiliaryData) {
    this.commandId = commandId;
    this.commandName = commandName;
    this.status = status;
    this.errorDetails = errorDetails;
    this.auxiliaryData = auxiliaryData;
  }

  public void setStatus(VerificationCommandStatusEnum status) {
    this.status = status;
  }

  public void setErrorDetails(List<String> errorDetails) {
    this.errorDetails = errorDetails;
  }

  public void setAuxiliaryData(Map<String, String> auxiliaryData) {
    this.auxiliaryData = auxiliaryData;
  }

  // getters

  public UUID getCommandId() {
    return commandId;
  }

  public String getCommandName() {
    return commandName;
  }

  public VerificationCommandStatusEnum getStatus() {
    return status;
  }

  public List<String> getErrorDetails() {
    return errorDetails;
  }

  public Map<String, String> getAuxiliaryData() {
    return auxiliaryData;
  }
}
