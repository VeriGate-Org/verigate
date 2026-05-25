/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.functions.lambda.di.modules;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import verigate.billing.application.handlers.PendingDowngradeHandler;
import verigate.billing.application.services.DefaultPlanChangeService;
import verigate.billing.infrastructure.config.EnvironmentConstants;
import verigate.billing.infrastructure.repositories.DynamoDbPlanChangeRepository;

public final class PendingDowngradeServiceModule extends ServiceModule {

    @Override
    protected void configure() {
        super.configure();
        bind(DefaultPlanChangeService.PlanChangeRepository.class).to(DynamoDbPlanChangeRepository.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    PendingDowngradeHandler providePendingDowngradeHandler(DefaultPlanChangeService.PlanChangeRepository repo) {
        return new PendingDowngradeHandler(repo);
    }

    @Provides
    @Named("planChangesTableName")
    String providePlanChangesTableName() {
        return getEnvOrDefault(EnvironmentConstants.PLAN_CHANGES_TABLE_NAME, EnvironmentConstants.DEFAULT_PLAN_CHANGES_TABLE);
    }
}
