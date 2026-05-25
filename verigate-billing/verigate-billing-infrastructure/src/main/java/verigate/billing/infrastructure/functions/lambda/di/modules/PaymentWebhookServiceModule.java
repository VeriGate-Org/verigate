/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.functions.lambda.di.modules;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import verigate.billing.application.services.DefaultDunningService;
import verigate.billing.application.services.DefaultInvoiceService;
import verigate.billing.application.services.DefaultLedgerService;
import verigate.billing.application.services.DefaultPaymentService;
import verigate.billing.application.services.DefaultSubscriptionService;
import verigate.billing.domain.services.DunningService;
import verigate.billing.domain.services.InvoiceService;
import verigate.billing.domain.services.LedgerService;
import verigate.billing.domain.services.PaymentService;
import verigate.billing.domain.services.SubscriptionService;
import verigate.billing.infrastructure.config.EnvironmentConstants;
import verigate.billing.infrastructure.http.PayFastHttpAdapter;
import verigate.billing.infrastructure.repositories.DynamoDbDunningRepository;
import verigate.billing.infrastructure.repositories.DynamoDbInvoiceRepository;
import verigate.billing.infrastructure.repositories.DynamoDbLedgerRepository;
import verigate.billing.infrastructure.repositories.DynamoDbPaymentRepository;
import verigate.billing.infrastructure.repositories.DynamoDbSubscriptionRepository;

/**
 * Guice module for the Payment Webhook Lambda.
 */
public final class PaymentWebhookServiceModule extends ServiceModule {

    @Override
    protected void configure() {
        super.configure();

        bind(DefaultPaymentService.PaymentRepository.class)
            .to(DynamoDbPaymentRepository.class)
            .in(Singleton.class);

        bind(DefaultPaymentService.PayFastAdapter.class)
            .to(PayFastHttpAdapter.class)
            .in(Singleton.class);

        bind(DefaultInvoiceService.InvoiceRepository.class)
            .to(DynamoDbInvoiceRepository.class)
            .in(Singleton.class);

        bind(DefaultLedgerService.LedgerRepository.class)
            .to(DynamoDbLedgerRepository.class)
            .in(Singleton.class);

        bind(DefaultDunningService.DunningRepository.class)
            .to(DynamoDbDunningRepository.class)
            .in(Singleton.class);

        bind(DefaultSubscriptionService.SubscriptionRepository.class)
            .to(DynamoDbSubscriptionRepository.class)
            .in(Singleton.class);

        bind(PaymentService.class).to(DefaultPaymentService.class).in(Singleton.class);
        bind(InvoiceService.class).to(DefaultInvoiceService.class).in(Singleton.class);
        bind(LedgerService.class).to(DefaultLedgerService.class).in(Singleton.class);
        bind(DunningService.class).to(DefaultDunningService.class).in(Singleton.class);
        bind(SubscriptionService.class).to(DefaultSubscriptionService.class).in(Singleton.class);
    }

    @Provides
    @Named("paymentsTableName")
    String providePaymentsTableName() {
        return getEnvOrDefault(
            EnvironmentConstants.PAYMENTS_TABLE_NAME,
            EnvironmentConstants.DEFAULT_PAYMENTS_TABLE);
    }

    @Provides
    @Named("invoicesTableName")
    String provideInvoicesTableName() {
        return getEnvOrDefault(
            EnvironmentConstants.INVOICES_TABLE_NAME,
            EnvironmentConstants.DEFAULT_INVOICES_TABLE);
    }

    @Provides
    @Named("ledgerEntriesTableName")
    String provideLedgerEntriesTableName() {
        return getEnvOrDefault(
            EnvironmentConstants.LEDGER_ENTRIES_TABLE_NAME,
            EnvironmentConstants.DEFAULT_LEDGER_ENTRIES_TABLE);
    }

    @Provides
    @Named("dunningSchedulesTableName")
    String provideDunningSchedulesTableName() {
        return getEnvOrDefault(
            EnvironmentConstants.DUNNING_SCHEDULES_TABLE_NAME,
            EnvironmentConstants.DEFAULT_DUNNING_SCHEDULES_TABLE);
    }

    @Provides
    @Named("subscriptionsTableName")
    String provideSubscriptionsTableName() {
        return getEnvOrDefault(
            EnvironmentConstants.SUBSCRIPTIONS_TABLE_NAME,
            EnvironmentConstants.DEFAULT_SUBSCRIPTIONS_TABLE);
    }
}
