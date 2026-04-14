package verigate.webbff.verification.controller;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import verigate.webbff.auth.PartnerContextHolder;
import verigate.webbff.verification.service.DocumentAutoFillService;

/**
 * REST controller for Document AI auto-fill. Accepts multiple document verification
 * results and aggregates extracted fields into a pre-filled form.
 */
@RestController
@RequestMapping("/api/partner/document-auto-fill")
public class DocumentAutoFillController {

  private static final Logger logger = LoggerFactory.getLogger(DocumentAutoFillController.class);

  private final DocumentAutoFillService autoFillService;

  public DocumentAutoFillController(DocumentAutoFillService autoFillService) {
    this.autoFillService = autoFillService;
  }

  @PostMapping
  public ResponseEntity<DocumentAutoFillService.AutoFillResult> autoFill(
      @RequestBody AutoFillRequest request) {
    String partnerId = PartnerContextHolder.requirePartnerId();
    logger.info("Document auto-fill request: partnerId={}, commandIds={}",
        partnerId, request.commandIds().size());

    DocumentAutoFillService.AutoFillResult result =
        autoFillService.aggregateFields(request.commandIds());

    logger.info("Document auto-fill complete: sessionId={}, fields={}, needsReview={}",
        result.sessionId(), result.fieldsAutoFilled(), result.fieldsNeedReview());

    return ResponseEntity.ok(result);
  }

  public record AutoFillRequest(List<UUID> commandIds) {}
}
