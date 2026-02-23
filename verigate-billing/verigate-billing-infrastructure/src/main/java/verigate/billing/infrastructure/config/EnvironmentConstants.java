/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.config;

/**
 * Environment variable constants for the VeriGate Billing service configuration.
 * All environment-specific values are injected via these variables at deployment time.
 */
public final class EnvironmentConstants {

    // DynamoDB table name overrides
    public static final String USAGE_RECORDS_TABLE_NAME = "USAGE_RECORDS_TABLE_NAME";
    public static final String USAGE_SUMMARIES_TABLE_NAME = "USAGE_SUMMARIES_TABLE_NAME";
    public static final String BILLING_PLANS_TABLE_NAME = "BILLING_PLANS_TABLE_NAME";

    // AWS Region
    public static final String AWS_REGION = "AWS_REGION";

    // Kinesis stream configuration
    public static final String KINESIS_STREAM_NAME = "KINESIS_STREAM_NAME";

    // Aggregation configuration
    public static final String AGGREGATION_BATCH_SIZE = "AGGREGATION_BATCH_SIZE";

    // Logging configuration
    public static final String LOG_LEVEL = "LOG_LEVEL";
    public static final String ENABLE_DEBUG_LOGGING = "ENABLE_DEBUG_LOGGING";

    // Default table names (fallback when environment variables are not set)
    public static final String DEFAULT_USAGE_RECORDS_TABLE = "verigate-usage-records";
    public static final String DEFAULT_USAGE_SUMMARIES_TABLE = "verigate-usage-summaries";
    public static final String DEFAULT_BILLING_PLANS_TABLE = "verigate-billing-plans";

    // Default Kinesis stream
    public static final String DEFAULT_KINESIS_STREAM = "verigate-verification-events";

    private EnvironmentConstants() {
        // Prevent instantiation
    }
}
