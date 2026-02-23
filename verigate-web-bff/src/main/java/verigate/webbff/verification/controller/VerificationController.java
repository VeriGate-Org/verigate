package verigate.webbff.verification.controller;

import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import verigate.webbff.verification.model.VerificationRequest;
import verigate.webbff.verification.model.VerificationResponse;
import verigate.webbff.verification.model.VerificationStatusResponse;
import verigate.webbff.verification.service.VerificationService;

@RestController
@RequestMapping("/api/verifications")
public class VerificationController {

  private static final Logger logger = LoggerFactory.getLogger(VerificationController.class);

  private final VerificationService verificationService;

  public VerificationController(VerificationService verificationService) {
    this.verificationService = verificationService;
  }

  @PostMapping
  public ResponseEntity<VerificationResponse> submitVerification(
      @Valid @RequestBody VerificationRequest request) {
    logger.info("Submitting verification: type={}, originationType={}",
        request.verificationType(), request.originationType());
    var response = verificationService.submitVerification(request);
    logger.info("Verification submitted: commandId={}", response.commandId());
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
  }

  @GetMapping("/{commandId}")
  public VerificationStatusResponse getVerification(@PathVariable UUID commandId) {
    logger.debug("Fetching verification status: commandId={}", commandId);
    return verificationService
        .findVerification(commandId)
        .map(
            record ->
                new VerificationStatusResponse(
                    record.getCommandId(),
                    record.getStatus(),
                    record.getErrorDetails(),
                    record.getAuxiliaryData()))
        .orElseThrow(() -> {
          logger.warn("Verification not found: commandId={}", commandId);
          return new ResponseStatusException(HttpStatus.NOT_FOUND);
        });
  }

  @GetMapping
  public ResponseEntity<List<Object>> listVerifications(
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "0") int offset,
      @RequestParam(defaultValue = "50") int limit) {
    logger.warn("listVerifications is a stub — returning empty list (status={}, offset={}, limit={})",
        status, offset, limit);
    return ResponseEntity.ok(Collections.emptyList());
  }
}
