package verigate.webbff.billing.controller;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import verigate.webbff.billing.service.BillingProxyService;
import verigate.webbff.billing.service.BillingProxyService.InvoiceSummary;

@RestController
@RequestMapping("/api/admin/billing")
public class AdminBillingController {

  private static final Logger logger = LoggerFactory.getLogger(AdminBillingController.class);

  private final BillingProxyService billingProxyService;

  public AdminBillingController(BillingProxyService billingProxyService) {
    this.billingProxyService = billingProxyService;
  }

  @GetMapping("/partners/{partnerId}/invoices")
  public ResponseEntity<List<InvoiceSummary>> listPartnerInvoices(
      @PathVariable String partnerId) {
    logger.info("Admin listing invoices for partner: {}", partnerId);
    List<InvoiceSummary> invoices = billingProxyService.getInvoicesForPartner(partnerId);
    return ResponseEntity.ok(invoices);
  }

  @PostMapping("/partners/{partnerId}/invoices/{invoiceId}/void")
  public ResponseEntity<Map<String, String>> voidInvoice(
      @PathVariable String partnerId,
      @PathVariable String invoiceId) {
    logger.info("Admin voiding invoice: partnerId={}, invoiceId={}", partnerId, invoiceId);

    boolean voided = billingProxyService.voidInvoice(partnerId, invoiceId);
    if (!voided) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(Map.of("error", "Invoice not found"));
    }

    return ResponseEntity.ok(Map.of(
        "invoiceId", invoiceId,
        "partnerId", partnerId,
        "status", "VOID"));
  }
}
