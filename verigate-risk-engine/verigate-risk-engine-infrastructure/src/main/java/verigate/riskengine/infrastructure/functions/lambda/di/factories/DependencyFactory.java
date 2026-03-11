/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.infrastructure.functions.lambda.di.factories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import verigate.riskengine.application.handlers.VerificationEventHandler;
import verigate.riskengine.application.handlers.WorkflowTimeoutHandler;
import verigate.riskengine.domain.services.RiskAggregator;
import verigate.riskengine.domain.services.RiskAssessmentRepository;
import verigate.riskengine.domain.services.RiskScoringConfigRepository;
import verigate.riskengine.domain.services.ScoreNormalizer;
import verigate.riskengine.domain.services.WorkflowRepository;
import verigate.riskengine.infrastructure.functions.lambda.di.modules.ServiceModule;
import verigate.riskengine.infrastructure.functions.lambda.handlers.KinesisEventPublisher;

public class DependencyFactory {

    protected final Injector injector;

    public DependencyFactory() {
        this.injector = Guice.createInjector(Stage.PRODUCTION, new ServiceModule());
    }

    public DependencyFactory(Injector injector) {
        this.injector = injector;
    }

    public ObjectMapper getObjectMapper() {
        return injector.getInstance(ObjectMapper.class);
    }

    public VerificationEventHandler getVerificationEventHandler() {
        return new VerificationEventHandler(
            injector.getInstance(WorkflowRepository.class),
            injector.getInstance(RiskScoringConfigRepository.class),
            injector.getInstance(RiskAssessmentRepository.class),
            injector.getInstance(ScoreNormalizer.class),
            injector.getInstance(RiskAggregator.class),
            new KinesisEventPublisher(getObjectMapper())
        );
    }

    public WorkflowTimeoutHandler getWorkflowTimeoutHandler() {
        return new WorkflowTimeoutHandler(
            injector.getInstance(WorkflowRepository.class),
            getVerificationEventHandler()
        );
    }
}
