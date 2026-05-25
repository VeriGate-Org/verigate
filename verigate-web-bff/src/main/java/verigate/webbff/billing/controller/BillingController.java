package verigate.webbff.billing.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import verigate.webbff.auth.PartnerContextHolder;
import verigate.webbff.billing.service.BillingProxyService;
import verigate.webbff.billing.service.BillingProxyService.InvoiceDetail;
import verigate.webbff.billing.service.BillingProxyService.InvoiceSummary;
import verigate.webbff.billing.service.BillingProxyService.PdfDownloadResult;

@RestController
@RequestMapping("/api/partner/billing")
public class BillingController {

  private static final Logger logger = LoggerFactory.getLogger(BillingController.class);

  private final BillingProxyService billingProxyService;

  public BillingController(BillingProxyService billingProxyService) {
    this.billingProxyService = billingProxyService;
  }

  @GetMapping("/invoices")
  public ResponseEntity<List<InvoiceSummary>> listInvoices() {
    String partnerId = PartnerContextHolder.requirePartnerId();
    logger.info("Listing invoices for partner: {}", partnerId);
    List<InvoiceSummary> invoices = billingProxyService.getInvoicesForPartner(partnerId);
    return ResponseEntity.ok(invoices);
  }

  @GetMapping("/invoices/{invoiceId}")
  public ResponseEntity<InvoiceDetailResponse> getInvoiceDetail(
      @PathVariable String invoiceId) {
    String partnerId = PartnerContextHolder.requirePartnerId();
    logger.info("Getting invoice detail: partnerId={}, invoiceId={}", partnerId, invoiceId);

    InvoiceDetail detail = billingProxyService.getInvoiceDetail(partnerId, invoiceId);
    if (detail == null) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(toResponse(detail));
  }

  @GetMapping("/invoices/{invoiceId}/pdf")
  public ResponseEntity<PdfDownloadResponse> getInvoicePdf(
      @PathVariable String invoiceId) {
    String partnerId = PartnerContextHolder.requirePartnerId();
    logger.info("Generating PDF download URL: partnerId={}, invoiceId={}", partnerId, invoiceId);

    PdfDownloadResult result = billingProxyService.generateInvoicePdfUrl(partnerId, invoiceId);
    if (result == null) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(new PdfDownloadResponse(result.downloadUrl(), result.expiresInSeconds()));
  }

  // ── Mapping ─────────────────────────────────────────────────────────

  private InvoiceDetailResponse toResponse(InvoiceDetail detail) {
    List<LineItem> lineItems = detail.lineItems().stream()
        .map(li -> new LineItem(
            li.lineItemId(),
            li.verificationType(),
            li.description(),
            li.quantity(),
            li.unitPriceExVat(),
            li.lineSubtotal(),
            li.vatAmount(),
            li.lineTotal()))
        .toList();

    return new InvoiceDetailResponse(
        detail.invoiceId(),
        detail.invoiceNumber(),
        detail.partnerId(),
        detail.partnerName(),
        detail.billingPeriod(),
        detail.status(),
        detail.issueDate(),
        detail.dueDate(),
        lineItems,
        detail.subtotal(),
        detail.vatRate(),
        detail.vatAmount(),
        detail.total(),
        detail.monthlyMinimumApplied(),
        detail.currency(),
        detail.paymentId(),
        detail.createdAt(),
        detail.notes());
  }

  // ── Response records ────────────────────────────────────────────────

  public record InvoiceDetailResponse(
      String invoiceId,
      String invoiceNumber,
      String partnerId,
      String partnerName,
      String billingPeriod,
      String status,
      String issueDate,
      String dueDate,
      List<LineItem> lineItems,
      String subtotal,
      String vatRate,
      String vatAmount,
      String total,
      boolean monthlyMinimumApplied,
      String currency,
      String paymentId,
      String createdAt,
      String notes) {}

  public record LineItem(
      String lineItemId,
      String verificationType,
      String description,
      long quantity,
      String unitPriceExVat,
      String lineSubtotal,
      String vatAmount,
      String lineTotal) {}

  public record PdfDownloadResponse(String downloadUrl, int expiresInSeconds) {}
}
