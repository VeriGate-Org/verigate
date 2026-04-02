package verigate.webbff.verification.controller;

import jakarta.validation.Valid;
import java.util.List;
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
import verigate.webbff.auth.PartnerContextHolder;
import verigate.webbff.verification.model.BulkVerificationRequest;
import verigate.webbff.verification.model.BulkVerificationResponse;
import verigate.webbff.verification.model.BulkVerificationResultDto;

/**
 * REST controller for HANIS bulk identity verification operations.
 * Provides endpoints for creating bulk jobs, checking status, and retrieving results.
 */
@RestController
@RequestMapping("/api/bulk-verifications")
public class BulkVerificationController {

  private static final Logger logger = LoggerFactory.getLogger(BulkVerificationController.class);

  @PostMapping
  public ResponseEntity<BulkVerificationResponse> createBulkJob(
      @Valid @RequestBody BulkVerificationRequest request) {

    String partnerId = PartnerContextHolder.requirePartnerId();
    logger.info("Creating bulk verification job: partnerId={}, idCount={}",
        partnerId, request.idNumbers().size());

    // Mock response for now - actual implementation will call DefaultBulkVerificationService
    var response = new BulkVerificationResponse(
        java.util.UUID.randomUUID().toString(),
        partnerId,
        "CREATED",
        null,
        buildBillingGroupString(request.billingGroups()),
        request.idNumbers().size(),
        java.time.Instant.now(),
        null, null, 0, null);

    return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
  }

  @GetMapping
  public ResponseEntity<List<BulkVerificationResponse>> listBulkJobs(
      @RequestParam(defaultValue = "25") int limit) {

    String partnerId = PartnerContextHolder.requirePartnerId();
    logger.debug("Listing bulk jobs: partnerId={}, limit={}", partnerId, limit);

    // Mock empty list for now
    return ResponseEntity.ok(List.of());
  }

  @GetMapping("/{jobId}")
  public BulkVerificationResponse getBulkJob(@PathVariable String jobId) {
    logger.debug("Fetching bulk job: jobId={}", jobId);

    // Mock response
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bulk job not found: " + jobId);
  }

  @GetMapping("/{jobId}/results")
  public ResponseEntity<List<BulkVerificationResultDto>> getBulkJobResults(
      @PathVariable String jobId,
      @RequestParam(defaultValue = "0") int offset,
      @RequestParam(defaultValue = "50") int limit) {

    logger.debug("Fetching bulk job results: jobId={}, offset={}, limit={}", jobId, offset, limit);

    // Mock empty results
    return ResponseEntity.ok(List.of());
  }

  private String buildBillingGroupString(BulkVerificationRequest.BillingGroupsDto groups) {
    if (groups == null) return "Y,Y,Y,Y,Y,N,N,N,N,N";
    return String.join(",",
        groups.demographics() ? "Y" : "N",
        groups.identity() ? "Y" : "N",
        groups.maritalStatus() ? "Y" : "N",
        groups.deathInfo() ? "Y" : "N",
        groups.smartCard() ? "Y" : "N",
        groups.photo() ? "Y" : "N",
        groups.address() ? "Y" : "N",
        groups.fingerprint() ? "Y" : "N",
        groups.reserved1() ? "Y" : "N",
        groups.reserved2() ? "Y" : "N");
  }
}
