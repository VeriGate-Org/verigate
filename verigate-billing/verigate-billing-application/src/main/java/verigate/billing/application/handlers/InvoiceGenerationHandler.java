/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.application.handlers;

import com.google.inject.Inject;
import java.time.YearMonth;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.domain.models.Invoice;
import verigate.billing.domain.services.BillingService;
import verigate.billing.domain.services.InvoiceService;

/**
 * Orchestrator for monthly invoice generation.
 * Gets active partner IDs, generates and issues an invoice for each.
 */
public class InvoiceGenerationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceGenerationHandler.class);

    private final InvoiceService invoiceService;
    private final BillingService billingService;

    @Inject
    public InvoiceGenerationHandler(
        InvoiceService invoiceService,
        BillingService billingService) {
        this.invoiceService = invoiceService;
        this.billingService = billingService;
    }

    public void handle(YearMonth period) {
        LOG.info("Starting invoice generation for period: {}", period);

        try {
            List<String> partnerIds = billingService.getActivePartnerIds();

            if (partnerIds.isEmpty()) {
                LOG.info("No active partners found for invoice generation");
                return;
            }

            LOG.info("Generating invoices for {} active partners", partnerIds.size());

            int successful = 0;
            int failed = 0;

            for (String partnerId : partnerIds) {
                try {
                    Invoice invoice = invoiceService.generateInvoice(partnerId, period);
                    invoiceService.issueInvoice(invoice.invoiceId());
                    successful++;
                    LOG.info("Invoice generated and issued for partner: {}", partnerId);
                } catch (Exception e) {
                    failed++;
                    LOG.error("Failed to generate invoice for partner: {} in period: {}",
                        partnerId, period, e);
                }
            }

            LOG.info("Invoice generation completed for period: {}. "
                    + "Partners: {}, successful: {}, failed: {}",
                period, partnerIds.size(), successful, failed);

        } catch (Exception e) {
            LOG.error("Invoice generation failed for period: {}", period, e);
            throw new RuntimeException("Invoice generation failed for period: " + period, e);
        }
    }
}
