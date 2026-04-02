package verigate.webbff.verification.mapper;

import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;
import verigate.webbff.verification.model.IdentityVerificationDetailResponse;

/**
 * Maps command store auxiliary data to the identity verification detail response DTO.
 */
@Component
public class IdentityVerificationMapper {

  /**
   * Maps the auxiliary data from a completed identity verification to the detail response.
   *
   * @param commandId     the verification command ID
   * @param auxiliaryData the raw auxiliary data map from the command store
   * @return the mapped identity verification detail response
   */
  public IdentityVerificationDetailResponse mapToDetailResponse(
      UUID commandId, Map<String, String> auxiliaryData) {

    if (auxiliaryData == null) {
      return new IdentityVerificationDetailResponse(
          commandId, "UNKNOWN", "UNKNOWN",
          null, null, null, null, null, false, "unknown", null, null);
    }

    String source = getOrDefault(auxiliaryData, "source", "dha");
    boolean isHanis = "hanis".equals(source);

    var citizenDetails = new IdentityVerificationDetailResponse.CitizenDetails(
        getOrDefault(auxiliaryData, "hanisName", ""),
        getOrDefault(auxiliaryData, "hanisSurname", ""),
        getOrDefault(auxiliaryData, "idNumber", ""),
        getOrDefault(auxiliaryData, "citizenshipStatus", "UNKNOWN"),
        getOrDefault(auxiliaryData, "birthCountry", ""),
        parseBoolean(auxiliaryData, "onHanis"),
        parseBoolean(auxiliaryData, "onNpr"));

    var documentInfo = new IdentityVerificationDetailResponse.DocumentInfo(
        parseBoolean(auxiliaryData, "smartCardIssued"),
        getOrDefault(auxiliaryData, "idIssueDate", ""),
        getOrDefault(auxiliaryData, "idSequenceNo", ""),
        parseBoolean(auxiliaryData, "idnBlocked"));

    var maritalInfo = new IdentityVerificationDetailResponse.MaritalInfo(
        getOrDefault(auxiliaryData, "maritalStatus", ""),
        getOrDefault(auxiliaryData, "dateOfMarriage", ""));

    boolean deceased = parseBoolean(auxiliaryData, "deadIndicator");
    var vitalStatusInfo = new IdentityVerificationDetailResponse.VitalStatusInfo(
        getOrDefault(auxiliaryData, "vitalStatus", deceased ? "DECEASED" : "ALIVE"),
        deceased,
        getOrDefault(auxiliaryData, "dateOfDeath", ""));

    boolean photoAvailable = parseBoolean(auxiliaryData, "photoAvailable");

    return new IdentityVerificationDetailResponse(
        commandId,
        getOrDefault(auxiliaryData, "verificationStatus", "UNKNOWN"),
        getOrDefault(auxiliaryData, "outcome", "UNKNOWN"),
        citizenDetails,
        documentInfo,
        maritalInfo,
        vitalStatusInfo,
        getOrDefault(auxiliaryData, "matchDetails", ""),
        photoAvailable,
        source,
        null,
        null);
  }

  private String getOrDefault(Map<String, String> data, String key, String defaultValue) {
    String value = data.get(key);
    return value != null && !value.isBlank() ? value : defaultValue;
  }

  private boolean parseBoolean(Map<String, String> data, String key) {
    String value = data.get(key);
    return "true".equalsIgnoreCase(value);
  }
}
