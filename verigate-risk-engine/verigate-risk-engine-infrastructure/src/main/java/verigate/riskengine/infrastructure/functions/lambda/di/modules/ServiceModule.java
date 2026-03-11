/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.infrastructure.functions.lambda.di.modules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import verigate.riskengine.application.services.DefaultRiskAggregator;
import verigate.riskengine.domain.services.DefaultScoreNormalizer;
import verigate.riskengine.domain.services.RiskAggregator;
import verigate.riskengine.domain.services.RiskAssessmentRepository;
import verigate.riskengine.domain.services.RiskScoringConfigRepository;
import verigate.riskengine.domain.services.ScoreNormalizer;
import verigate.riskengine.domain.services.WorkflowRepository;
import verigate.riskengine.infrastructure.config.EnvironmentConstants;
import verigate.riskengine.infrastructure.repositories.DynamoDbRiskAssessmentRepository;
import verigate.riskengine.infrastructure.repositories.DynamoDbRiskScoringConfigRepository;
import verigate.riskengine.infrastructure.repositories.DynamoDbWorkflowRepository;

public class ServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        // Repository bindings
        bind(RiskScoringConfigRepository.class)
            .to(DynamoDbRiskScoringConfigRepository.class)
            .in(Singleton.class);
        bind(RiskAssessmentRepository.class)
            .to(DynamoDbRiskAssessmentRepository.class)
            .in(Singleton.class);
        bind(WorkflowRepository.class)
            .to(DynamoDbWorkflowRepository.class)
            .in(Singleton.class);

        // Domain service bindings
        bind(ScoreNormalizer.class)
            .to(DefaultScoreNormalizer.class)
            .in(Singleton.class);
        bind(RiskAggregator.class)
            .to(DefaultRiskAggregator.class)
            .in(Singleton.class);
    }

    @Provides
    @Singleton
    DynamoDbClient provideDynamoDbClient() {
        return DynamoDbClient.builder().build();
    }

    @Provides
    @Singleton
    DynamoDbEnhancedClient provideDynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();
    }

    @Provides
    @Singleton
    ObjectMapper provideObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Provides
    @Named("riskScoringConfigTableName")
    String provideRiskScoringConfigTableName() {
        return getEnvOrDefault(
            EnvironmentConstants.RISK_SCORING_CONFIG_TABLE_NAME,
            EnvironmentConstants.DEFAULT_RISK_SCORING_CONFIG_TABLE);
    }

    @Provides
    @Named("riskAssessmentsTableName")
    String provideRiskAssessmentsTableName() {
        return getEnvOrDefault(
            EnvironmentConstants.RISK_ASSESSMENTS_TABLE_NAME,
            EnvironmentConstants.DEFAULT_RISK_ASSESSMENTS_TABLE);
    }

    @Provides
    @Named("verificationWorkflowsTableName")
    String provideVerificationWorkflowsTableName() {
        return getEnvOrDefault(
            EnvironmentConstants.VERIFICATION_WORKFLOWS_TABLE_NAME,
            EnvironmentConstants.DEFAULT_VERIFICATION_WORKFLOWS_TABLE);
    }

    protected String getEnvOrDefault(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}
