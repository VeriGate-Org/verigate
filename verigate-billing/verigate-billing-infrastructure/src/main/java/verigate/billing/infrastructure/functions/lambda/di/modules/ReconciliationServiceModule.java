/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.functions.lambda.di.modules;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import verigate.billing.application.handlers.ReconciliationHandler;
import verigate.billing.application.services.DefaultInvoiceService;
import verigate.billing.application.services.DefaultLedgerService;
import verigate.billing.application.services.DefaultReportingService;
import verigate.billing.domain.services.ReportingService;
import verigate.billing.infrastructure.config.EnvironmentConstants;
import verigate.billing.infrastructure.repositories.DynamoDbInvoiceRepository;
import verigate.billing.infrastructure.repositories.DynamoDbLedgerRepository;
import verigate.billing.infrastructure.repositories.DynamoDbReconciliationRepository;

public final class ReconciliationServiceModule extends ServiceModule {

    @Override
    protected void configure() {
        super.configure();
        bind(DefaultLedgerService.LedgerRepository.class).to(DynamoDbLedgerRepository.class).in(Singleton.class);
        bind(DefaultInvoiceService.InvoiceRepository.class).to(DynamoDbInvoiceRepository.class).in(Singleton.class);
        bind(DefaultReportingService.ReconciliationRepository.class).to(DynamoDbReconciliationRepository.class).in(Singleton.class);
        bind(ReportingService.class).to(DefaultReportingService.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    ReconciliationHandler provideReconciliationHandler(ReportingService reportingService) {
        return new ReconciliationHandler(reportingService);
    }

    @Provides @Named("ledgerEntriesTableName")
    String provideLedgerEntriesTableName() {
        return getEnvOrDefault(EnvironmentConstants.LEDGER_ENTRIES_TABLE_NAME, EnvironmentConstants.DEFAULT_LEDGER_ENTRIES_TABLE);
    }

    @Provides @Named("invoicesTableName")
    String provideInvoicesTableName() {
        return getEnvOrDefault(EnvironmentConstants.INVOICES_TABLE_NAME, EnvironmentConstants.DEFAULT_INVOICES_TABLE);
    }

    @Provides @Named("reconciliationReportsTableName")
    String provideReconciliationTableName() {
        return getEnvOrDefault(EnvironmentConstants.RECONCILIATION_REPORTS_TABLE_NAME, EnvironmentConstants.DEFAULT_RECONCILIATION_REPORTS_TABLE);
    }
}
