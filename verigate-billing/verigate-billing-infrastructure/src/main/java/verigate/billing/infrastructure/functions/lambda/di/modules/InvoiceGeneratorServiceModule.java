/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.functions.lambda.di.modules;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import software.amazon.awssdk.services.s3.S3Client;
import verigate.billing.application.handlers.InvoiceGenerationHandler;
import verigate.billing.application.services.DefaultCreditNoteService;
import verigate.billing.application.services.DefaultInvoiceService;
import verigate.billing.application.services.DefaultLedgerService;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.models.TaxConfiguration;
import verigate.billing.domain.services.BillingService;
import verigate.billing.domain.services.CreditNoteService;
import verigate.billing.domain.services.InvoiceService;
import verigate.billing.domain.services.LedgerService;
import verigate.billing.infrastructure.config.EnvironmentConstants;
import verigate.billing.infrastructure.pdf.HtmlInvoicePdfGenerator;
import verigate.billing.infrastructure.repositories.DynamoDbCreditNoteRepository;
import verigate.billing.infrastructure.repositories.DynamoDbInvoiceRepository;
import verigate.billing.infrastructure.repositories.DynamoDbInvoiceSequenceRepository;
import verigate.billing.infrastructure.repositories.DynamoDbLedgerRepository;

/**
 * Guice module for the Invoice Generator Lambda.
 * Extends the base ServiceModule with invoice-specific bindings.
 */
public final class InvoiceGeneratorServiceModule extends ServiceModule {

    @Override
    protected void configure() {
        super.configure();

        bind(DefaultInvoiceService.InvoiceRepository.class)
            .to(DynamoDbInvoiceRepository.class)
            .in(Singleton.class);

        bind(DefaultInvoiceService.InvoiceNumberGenerator.class)
            .to(DynamoDbInvoiceSequenceRepository.class)
            .in(Singleton.class);

        bind(DefaultInvoiceService.InvoicePdfGenerator.class)
            .to(HtmlInvoicePdfGenerator.class)
            .in(Singleton.class);

        bind(DefaultLedgerService.LedgerRepository.class)
            .to(DynamoDbLedgerRepository.class)
            .in(Singleton.class);

        bind(DefaultCreditNoteService.CreditNoteRepository.class)
            .to(DynamoDbCreditNoteRepository.class)
            .in(Singleton.class);

        bind(InvoiceService.class)
            .to(DefaultInvoiceService.class)
            .in(Singleton.class);

        bind(LedgerService.class)
            .to(DefaultLedgerService.class)
            .in(Singleton.class);

        bind(CreditNoteService.class)
            .to(DefaultCreditNoteService.class)
            .in(Singleton.class);
    }

    @Provides
    @Singleton
    InvoiceGenerationHandler provideInvoiceGenerationHandler(
        InvoiceService invoiceService,
        BillingService billingService) {
        return new InvoiceGenerationHandler(invoiceService, billingService);
    }

    @Provides
    @Singleton
    S3Client provideS3Client() {
        return S3Client.builder().build();
    }

    @Provides
    @Singleton
    TaxConfiguration provideTaxConfiguration() {
        return new TaxConfiguration(
            DomainConstants.VAT_RATE,
            getEnvOrDefault("SELLER_VAT_NUMBER", ""),
            DomainConstants.SELLER_COMPANY_NAME,
            DomainConstants.SELLER_REGISTRATION_NUMBER,
            DomainConstants.SELLER_ADDRESS,
            true
        );
    }

    @Provides
    @Named("invoicesTableName")
    String provideInvoicesTableName() {
        return getEnvOrDefault(
            EnvironmentConstants.INVOICES_TABLE_NAME,
            EnvironmentConstants.DEFAULT_INVOICES_TABLE);
    }

    @Provides
    @Named("invoiceSequencesTableName")
    String provideInvoiceSequencesTableName() {
        return getEnvOrDefault(
            EnvironmentConstants.INVOICE_SEQUENCES_TABLE_NAME,
            EnvironmentConstants.DEFAULT_INVOICE_SEQUENCES_TABLE);
    }

    @Provides
    @Named("invoiceBucketName")
    String provideInvoiceBucketName() {
        return getEnvOrDefault(
            EnvironmentConstants.INVOICE_BUCKET_NAME,
            "verigate-invoices");
    }

    @Provides
    @Named("invoiceSenderEmail")
    String provideInvoiceSenderEmail() {
        return getEnvOrDefault(
            EnvironmentConstants.INVOICE_SENDER_EMAIL,
            "billing@verigate.co.za");
    }

    @Provides
    @Named("ledgerEntriesTableName")
    String provideLedgerEntriesTableName() {
        return getEnvOrDefault(
            EnvironmentConstants.LEDGER_ENTRIES_TABLE_NAME,
            EnvironmentConstants.DEFAULT_LEDGER_ENTRIES_TABLE);
    }

    @Provides
    @Named("creditNotesTableName")
    String provideCreditNotesTableName() {
        return getEnvOrDefault(
            EnvironmentConstants.CREDIT_NOTES_TABLE_NAME,
            EnvironmentConstants.DEFAULT_CREDIT_NOTES_TABLE);
    }
}
