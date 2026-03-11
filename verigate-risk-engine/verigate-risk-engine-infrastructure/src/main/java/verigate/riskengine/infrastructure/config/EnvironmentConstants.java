/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.infrastructure.config;

public final class EnvironmentConstants {

    private EnvironmentConstants() {}

    public static final String RISK_SCORING_CONFIG_TABLE_NAME = "RISK_SCORING_CONFIG_TABLE_NAME";
    public static final String RISK_ASSESSMENTS_TABLE_NAME = "RISK_ASSESSMENTS_TABLE_NAME";
    public static final String VERIFICATION_WORKFLOWS_TABLE_NAME = "VERIFICATION_WORKFLOWS_TABLE_NAME";

    public static final String DEFAULT_RISK_SCORING_CONFIG_TABLE = "risk-scoring-config";
    public static final String DEFAULT_RISK_ASSESSMENTS_TABLE = "risk-assessments";
    public static final String DEFAULT_VERIFICATION_WORKFLOWS_TABLE = "verification-workflows";
}
