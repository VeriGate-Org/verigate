package verigate.webbff.verification.repository.model;

import java.util.List;
import java.util.Map;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import verigate.webbff.verification.model.CommandStatus;

@DynamoDbBean
public class VerificationCommandStoreItem {

  private String commandId;
  private String commandName;
  private CommandStatus status;
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
  public CommandStatus getStatus() {
    return status;
  }

  public void setStatus(CommandStatus status) {
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

  private String partnerId;
  private String createdAt;
  private String statusCreatedAt;

  @DynamoDbAttribute("partnerId")
  public String getPartnerId() {
    return partnerId;
  }

  public void setPartnerId(String partnerId) {
    this.partnerId = partnerId;
  }

  @DynamoDbAttribute("createdAt")
  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  @DynamoDbAttribute("statusCreatedAt")
  public String getStatusCreatedAt() {
    return statusCreatedAt;
  }

  public void setStatusCreatedAt(String statusCreatedAt) {
    this.statusCreatedAt = statusCreatedAt;
  }
}
