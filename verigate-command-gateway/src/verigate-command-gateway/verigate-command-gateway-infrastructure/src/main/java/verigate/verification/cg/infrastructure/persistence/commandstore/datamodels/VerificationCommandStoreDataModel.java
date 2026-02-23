/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.infrastructure.persistence.commandstore.datamodels;

import java.util.List;
import java.util.Map;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import verigate.verification.cg.domain.commands.commandstore.VerificationCommandStatusEnum;

/**
 * This class represents the verification command store data model in
 * DynamoDB using the AWS SDK for Java v2's DynamoDB Enhanced Client.
 */
@DynamoDbBean
public final class VerificationCommandStoreDataModel {
  private String commandId;
  private String commandName;
  private VerificationCommandStatusEnum status;
  private List<String> errorDetails;
  private Map<String, String> auxiliaryData;

  @DynamoDbPartitionKey
  @DynamoDbAttribute("commandId")
  public String getCommandId() {
    return commandId;
  }

  public void setCommandId(String commandId) {
    this.commandId = commandId;
  }

  @DynamoDbAttribute("commandName")
  public String getCommandName() {
    return commandName;
  }

  public void setCommandName(String commandName) {
    this.commandName = commandName;
  }

  @DynamoDbAttribute("status")
  public VerificationCommandStatusEnum getStatus() {
    return status;
  }

  public void setStatus(VerificationCommandStatusEnum status) {
    this.status = status;
  }

  @DynamoDbAttribute("errorDetails")
  public List<String> getErrorDetails() {
    return errorDetails;
  }

  public void setErrorDetails(List<String> errors) {
    this.errorDetails = errors;
  }

  @DynamoDbAttribute("auxiliaryData")
  public Map<String, String> getAuxiliaryData() {
    return auxiliaryData;
  }

  public void setAuxiliaryData(Map<String, String> auxiliaryData) {
    this.auxiliaryData = auxiliaryData;
  }
}
