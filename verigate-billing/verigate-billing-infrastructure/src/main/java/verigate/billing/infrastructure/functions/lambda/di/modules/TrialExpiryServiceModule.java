/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.functions.lambda.di.modules;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import verigate.billing.application.handlers.TrialExpiryHandler;
import verigate.billing.application.services.DefaultSubscriptionService;
import verigate.billing.application.services.DefaultTrialService;
import verigate.billing.domain.services.SubscriptionService;
import verigate.billing.domain.services.TrialService;
import verigate.billing.infrastructure.config.EnvironmentConstants;
import verigate.billing.infrastructure.repositories.DynamoDbSubscriptionRepository;
import verigate.billing.infrastructure.repositories.DynamoDbTrialRepository;

public final class TrialExpiryServiceModule extends ServiceModule {

    @Override
    protected void configure() {
        super.configure();
        bind(DefaultTrialService.TrialRepository.class).to(DynamoDbTrialRepository.class).in(Singleton.class);
        bind(DefaultSubscriptionService.SubscriptionRepository.class).to(DynamoDbSubscriptionRepository.class).in(Singleton.class);
        bind(TrialService.class).to(DefaultTrialService.class).in(Singleton.class);
        bind(SubscriptionService.class).to(DefaultSubscriptionService.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    TrialExpiryHandler provideTrialExpiryHandler(TrialService trialService) {
        return new TrialExpiryHandler(trialService);
    }

    @Provides
    @Named("trialsTableName")
    String provideTrialsTableName() {
        return getEnvOrDefault(EnvironmentConstants.TRIALS_TABLE_NAME, EnvironmentConstants.DEFAULT_TRIALS_TABLE);
    }

    @Provides
    @Named("subscriptionsTableName")
    String provideSubscriptionsTableName() {
        return getEnvOrDefault(EnvironmentConstants.SUBSCRIPTIONS_TABLE_NAME, EnvironmentConstants.DEFAULT_SUBSCRIPTIONS_TABLE);
    }
}
