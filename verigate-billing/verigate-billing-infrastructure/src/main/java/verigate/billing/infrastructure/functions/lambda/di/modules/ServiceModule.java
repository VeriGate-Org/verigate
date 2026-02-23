/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.functions.lambda.di.modules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import verigate.billing.application.services.DefaultBillingService;
import verigate.billing.application.services.DefaultUsageTrackingService;
import verigate.billing.domain.services.BillingService;
import verigate.billing.domain.services.UsageTrackingService;
import verigate.billing.infrastructure.config.EnvironmentConstants;
import verigate.billing.infrastructure.repositories.DynamoDbBillingPlanRepository;
import verigate.billing.infrastructure.repositories.DynamoDbUsageRepository;
import verigate.billing.infrastructure.repositories.DynamoDbUsageSummaryRepository;

/**
 * Base Guice module for the VeriGate Billing service.
 * Configures common bindings for DynamoDB clients, ObjectMapper,
 * repository implementations, and service implementations.
 */
public class ServiceModule extends AbstractModule {

    /**
     * Configures the core bindings for the billing service.
     */
    @Override
    protected void configure() {
        // Repository bindings
        bind(DefaultUsageTrackingService.UsageRecordRepository.class)
            .to(DynamoDbUsageRepository.class)
            .in(Singleton.class);

        bind(DefaultUsageTrackingService.UsageSummaryRepository.class)
            .to(DynamoDbUsageSummaryRepository.class)
            .in(Singleton.class);

        bind(DefaultBillingService.BillingPlanRepository.class)
            .to(DynamoDbBillingPlanRepository.class)
            .in(Singleton.class);

        // Service bindings
        bind(UsageTrackingService.class)
            .to(DefaultUsageTrackingService.class)
            .in(Singleton.class);

        bind(BillingService.class)
            .to(DefaultBillingService.class)
            .in(Singleton.class);
    }

    /**
     * Provides the DynamoDB client instance.
     *
     * @return a configured DynamoDB client
     */
    @Provides
    @Singleton
    DynamoDbClient provideDynamoDbClient() {
        return DynamoDbClient.builder().build();
    }

    /**
     * Provides the DynamoDB enhanced client for type-safe table operations.
     *
     * @param dynamoDbClient the underlying DynamoDB client
     * @return a configured DynamoDB enhanced client
     */
    @Provides
    @Singleton
    DynamoDbEnhancedClient provideDynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();
    }

    /**
     * Provides the Jackson ObjectMapper configured for Java time types.
     *
     * @return a configured ObjectMapper instance
     */
    @Provides
    @Singleton
    ObjectMapper provideObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    /**
     * Provides the usage records DynamoDB table name.
     * Reads from the environment variable, falling back to the default.
     *
     * @return the table name
     */
    @Provides
    @Named("usageRecordsTableName")
    String provideUsageRecordsTableName() {
        return getEnvOrDefault(
            EnvironmentConstants.USAGE_RECORDS_TABLE_NAME,
            EnvironmentConstants.DEFAULT_USAGE_RECORDS_TABLE);
    }

    /**
     * Provides the usage summaries DynamoDB table name.
     * Reads from the environment variable, falling back to the default.
     *
     * @return the table name
     */
    @Provides
    @Named("usageSummariesTableName")
    String provideUsageSummariesTableName() {
        return getEnvOrDefault(
            EnvironmentConstants.USAGE_SUMMARIES_TABLE_NAME,
            EnvironmentConstants.DEFAULT_USAGE_SUMMARIES_TABLE);
    }

    /**
     * Provides the billing plans DynamoDB table name.
     * Reads from the environment variable, falling back to the default.
     *
     * @return the table name
     */
    @Provides
    @Named("billingPlansTableName")
    String providesBillingPlansTableName() {
        return getEnvOrDefault(
            EnvironmentConstants.BILLING_PLANS_TABLE_NAME,
            EnvironmentConstants.DEFAULT_BILLING_PLANS_TABLE);
    }

    /**
     * Reads an environment variable with a default fallback.
     *
     * @param envVar       the environment variable name
     * @param defaultValue the default value if the variable is not set
     * @return the environment variable value or the default
     */
    protected String getEnvOrDefault(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}
