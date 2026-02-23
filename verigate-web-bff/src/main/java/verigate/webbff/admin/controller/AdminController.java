package verigate.webbff.admin.controller;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import verigate.webbff.admin.model.ApiKeyListResponse;
import verigate.webbff.admin.model.ApiKeyResponse;
import verigate.webbff.admin.model.CreatePartnerRequest;
import verigate.webbff.admin.service.PartnerService;
import verigate.webbff.auth.ApiKeyRecord;
import verigate.webbff.auth.ApiKeyService;
import verigate.webbff.auth.ApiKeyService.GeneratedApiKey;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

  private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

  private final ApiKeyService apiKeyService;
  private final PartnerService partnerService;

  public AdminController(ApiKeyService apiKeyService, PartnerService partnerService) {
    this.apiKeyService = apiKeyService;
    this.partnerService = partnerService;
  }

  @PostMapping("/partners")
  public ResponseEntity<Map<String, Object>> createPartner(
      @Valid @RequestBody CreatePartnerRequest request) {
    logger.info("Submitting create partner command: name={}, email={}",
        request.name(), request.contactEmail());
    UUID commandId = partnerService.submitCreatePartner(request);
    logger.info("Create partner command submitted: commandId={}", commandId);
    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(Map.of("commandId", commandId.toString(), "status", "ACCEPTED"));
  }

  @PostMapping("/partners/{partnerId}/api-keys")
  public ResponseEntity<ApiKeyResponse> generateApiKey(@PathVariable String partnerId) {
    logger.info("Generating API key for partner: {}", partnerId);
    GeneratedApiKey generated = apiKeyService.generateApiKey(partnerId, "admin");
    ApiKeyRecord record = generated.record();
    ApiKeyResponse response = new ApiKeyResponse(
        generated.rawApiKey(),
        record.partnerId(),
        record.keyPrefix(),
        record.status(),
        record.createdAt());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/partners/{partnerId}/api-keys")
  public ApiKeyListResponse listApiKeys(@PathVariable String partnerId) {
    var records = apiKeyService.listApiKeys(partnerId);
    var items = records.stream()
        .map(r -> new ApiKeyListResponse.ApiKeyItem(
            r.keyPrefix(),
            r.status(),
            r.createdAt() != null ? r.createdAt().toString() : null,
            r.createdBy()))
        .toList();
    return new ApiKeyListResponse(partnerId, items);
  }

  @DeleteMapping("/partners/{partnerId}/api-keys/{keyPrefix}")
  public ResponseEntity<Void> revokeApiKey(
      @PathVariable String partnerId,
      @PathVariable String keyPrefix) {
    logger.info("Revoking API key: partner={}, prefix={}", partnerId, keyPrefix);
    var records = apiKeyService.listApiKeys(partnerId);
    var match = records.stream()
        .filter(r -> keyPrefix.equals(r.keyPrefix()))
        .findFirst();
    if (match.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    apiKeyService.revokeApiKey(match.get().apiKeyHash());
    return ResponseEntity.noContent().build();
  }
}
