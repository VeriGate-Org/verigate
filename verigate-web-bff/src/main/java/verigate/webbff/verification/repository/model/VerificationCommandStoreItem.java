package verigate.webbff.verification.repository.model;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import verigate.verification.cg.domain.commands.commandstore.VerificationCommandStatusEnum;
import verigate.verification.cg.domain.commands.commandstore.VerificationCommandStoreRecord;

@DynamoDbBean
public class VerificationCommandStoreItem {

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

  public void setErrorDetails(List<String> errorDetails) {
    this.errorDetails = errorDetails;
  }

  @DynamoDbAttribute("auxiliaryData")
  public Map<String, String> getAuxiliaryData() {
    return auxiliaryData;
  }

  public void setAuxiliaryData(Map<String, String> auxiliaryData) {
    this.auxiliaryData = auxiliaryData;
  }

  public VerificationCommandStoreRecord toDomain() {
    return new VerificationCommandStoreRecord(
        UUID.fromString(commandId), commandName, status, errorDetails, auxiliaryData);
  }
}
