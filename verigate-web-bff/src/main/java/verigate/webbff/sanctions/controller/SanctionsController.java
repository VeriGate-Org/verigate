package verigate.webbff.sanctions.controller;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import verigate.webbff.sanctions.service.SanctionsProxyService;

@RestController
@RequestMapping("/api/partner/sanctions")
public class SanctionsController {

  private static final Logger logger = LoggerFactory.getLogger(SanctionsController.class);

  private final SanctionsProxyService sanctionsProxyService;

  public SanctionsController(SanctionsProxyService sanctionsProxyService) {
    this.sanctionsProxyService = sanctionsProxyService;
  }

  @GetMapping("/entities/{entityId}")
  public ResponseEntity<Map<String, Object>> getEntity(@PathVariable String entityId) {
    logger.info("Getting entity details for: {}", entityId);
    Map<String, Object> entity = sanctionsProxyService.getEntity(entityId);
    return ResponseEntity.ok(entity);
  }

  @GetMapping("/entities/{entityId}/adjacent")
  public ResponseEntity<Map<String, Object>> getAdjacentEntities(
      @PathVariable String entityId) {
    logger.info("Getting adjacent entities for: {}", entityId);
    Map<String, Object> adjacent = sanctionsProxyService.getAdjacentEntities(entityId);
    return ResponseEntity.ok(adjacent);
  }

  @PostMapping("/dispositions")
  public ResponseEntity<Map<String, Object>> submitDisposition(
      @RequestBody Map<String, Object> disposition) {
    logger.info("Submitting disposition for entity: {}", disposition.get("entityId"));
    Map<String, Object> result = sanctionsProxyService.submitDisposition(disposition);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/history")
  public ResponseEntity<Map<String, Object>> getScreeningHistory() {
    logger.info("Getting screening history");
    Map<String, Object> history = sanctionsProxyService.getScreeningHistory();
    return ResponseEntity.ok(history);
  }
}
