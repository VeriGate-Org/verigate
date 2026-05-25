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

    // DynamoDB table name overrides — usage/billing (existing)
    public static final String USAGE_RECORDS_TABLE_NAME = "USAGE_RECORDS_TABLE_NAME";
    public static final String USAGE_SUMMARIES_TABLE_NAME = "USAGE_SUMMARIES_TABLE_NAME";
    public static final String BILLING_PLANS_TABLE_NAME = "BILLING_PLANS_TABLE_NAME";

    // DynamoDB table name overrides — invoicing
    public static final String INVOICES_TABLE_NAME = "INVOICES_TABLE_NAME";
    public static final String INVOICE_SEQUENCES_TABLE_NAME = "INVOICE_SEQUENCES_TABLE_NAME";
    public static final String PAYMENTS_TABLE_NAME = "PAYMENTS_TABLE_NAME";

    // DynamoDB table name overrides — subscription lifecycle
    public static final String PLAN_CHANGES_TABLE_NAME = "PLAN_CHANGES_TABLE_NAME";
    public static final String TRIALS_TABLE_NAME = "TRIALS_TABLE_NAME";
    public static final String SUBSCRIPTIONS_TABLE_NAME = "SUBSCRIPTIONS_TABLE_NAME";

    // DynamoDB table name overrides — dunning & credit
    public static final String DUNNING_SCHEDULES_TABLE_NAME = "DUNNING_SCHEDULES_TABLE_NAME";
    public static final String CREDIT_NOTES_TABLE_NAME = "CREDIT_NOTES_TABLE_NAME";

    // DynamoDB table name overrides — financial reporting
    public static final String LEDGER_ENTRIES_TABLE_NAME = "LEDGER_ENTRIES_TABLE_NAME";
    public static final String RECONCILIATION_REPORTS_TABLE_NAME = "RECONCILIATION_REPORTS_TABLE_NAME";

    // S3
    public static final String INVOICE_BUCKET_NAME = "INVOICE_BUCKET_NAME";
    public static final String INVOICE_SENDER_EMAIL = "INVOICE_SENDER_EMAIL";

    // PayFast
    public static final String PAYFAST_MERCHANT_ID = "PAYFAST_MERCHANT_ID";
    public static final String PAYFAST_MERCHANT_KEY = "PAYFAST_MERCHANT_KEY";
    public static final String PAYFAST_PASSPHRASE = "PAYFAST_PASSPHRASE";
    public static final String PAYFAST_BASE_URL = "PAYFAST_BASE_URL";

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
    public static final String DEFAULT_INVOICES_TABLE = "verigate-invoices";
    public static final String DEFAULT_INVOICE_SEQUENCES_TABLE = "verigate-invoice-sequences";
    public static final String DEFAULT_PAYMENTS_TABLE = "verigate-payments";
    public static final String DEFAULT_PLAN_CHANGES_TABLE = "verigate-plan-changes";
    public static final String DEFAULT_TRIALS_TABLE = "verigate-trials";
    public static final String DEFAULT_SUBSCRIPTIONS_TABLE = "verigate-subscriptions";
    public static final String DEFAULT_DUNNING_SCHEDULES_TABLE = "verigate-dunning-schedules";
    public static final String DEFAULT_CREDIT_NOTES_TABLE = "verigate-credit-notes";
    public static final String DEFAULT_LEDGER_ENTRIES_TABLE = "verigate-ledger-entries";
    public static final String DEFAULT_RECONCILIATION_REPORTS_TABLE = "verigate-reconciliation-reports";

    // Default Kinesis stream
    public static final String DEFAULT_KINESIS_STREAM = "verigate-verification-events";

    // Default PayFast
    public static final String DEFAULT_PAYFAST_BASE_URL = "https://www.payfast.co.za";

    private EnvironmentConstants() {
        // Prevent instantiation
    }
}
