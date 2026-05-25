/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.functions.lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import java.time.YearMonth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.application.handlers.InvoiceGenerationHandler;
import verigate.billing.infrastructure.functions.lambda.di.factories.InvoiceGeneratorDependencyFactory;

/**
 * AWS Lambda handler for monthly invoice generation.
 * Triggered by EventBridge on the 1st of each month at 02:00 UTC.
 * Generates invoices for the <strong>previous</strong> month.
 */
public class InvoiceGeneratorLambdaHandler implements RequestHandler<ScheduledEvent, Void> {

    private static final Logger LOG =
        LoggerFactory.getLogger(InvoiceGeneratorLambdaHandler.class);

    private final InvoiceGenerationHandler invoiceGenerationHandler;

    public InvoiceGeneratorLambdaHandler() {
        this(new InvoiceGeneratorDependencyFactory());
    }

    public InvoiceGeneratorLambdaHandler(InvoiceGeneratorDependencyFactory factory) {
        this.invoiceGenerationHandler = factory.getInvoiceGenerationHandler();
    }

    @Override
    public Void handleRequest(ScheduledEvent event, Context context) {
        YearMonth previousMonth = YearMonth.now().minusMonths(1);

        LOG.info("Invoice generation triggered by EventBridge. "
                + "Event time: {}, billing period: {}",
            event != null ? event.getTime() : "N/A", previousMonth);

        try {
            invoiceGenerationHandler.handle(previousMonth);
            LOG.info("Invoice generation completed for period: {}", previousMonth);
        } catch (Exception e) {
            LOG.error("Invoice generation failed for period: {}", previousMonth, e);
            throw new RuntimeException(
                "Invoice generation failed for period: " + previousMonth, e);
        }

        return null;
    }
}
