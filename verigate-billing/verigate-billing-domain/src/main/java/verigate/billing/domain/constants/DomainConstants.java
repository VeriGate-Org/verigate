/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.constants;

import java.math.BigDecimal;

/**
 * Constants for the VeriGate Billing domain.
 */
public final class DomainConstants {

    // Service identifiers
    public static final String SERVICE_NAME = "verigate-billing";
    public static final String USAGE_ID_PREFIX = "USG-";
    public static final String SUMMARY_ID_PREFIX = "SUM-";

    // DynamoDB table names
    public static final String USAGE_RECORDS_TABLE = "verigate-usage-records";
    public static final String USAGE_SUMMARIES_TABLE = "verigate-usage-summaries";
    public static final String BILLING_PLANS_TABLE = "verigate-billing-plans";

    // DynamoDB index names
    public static final String VERIFICATION_TYPE_INDEX = "verificationType-index";

    // Default billing values
    public static final BigDecimal DEFAULT_MONTHLY_MINIMUM = new BigDecimal("500.00");
    public static final BigDecimal DEFAULT_PRICE_PER_VERIFICATION = new BigDecimal("2.50");

    // Usage event outcomes
    public static final String OUTCOME_SUCCESS = "SUCCESS";
    public static final String OUTCOME_FAILURE = "FAILURE";
    public static final String OUTCOME_SYSTEM_ERROR = "SYSTEM_ERROR";

    // Aggregation defaults
    public static final int DEFAULT_AGGREGATION_BATCH_SIZE = 100;
    public static final int MAX_QUERY_PAGE_SIZE = 1000;

    // Date format patterns
    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String PERIOD_FORMAT_PATTERN = "yyyy-MM";

    // Sort key separators
    public static final String SORT_KEY_SEPARATOR = "#";

    // Invoice constants
    public static final String INVOICE_ID_PREFIX = "INV-";
    public static final String PAYMENT_ID_PREFIX = "PAY-";
    public static final String PLAN_CHANGE_ID_PREFIX = "PCH-";
    public static final String TRIAL_ID_PREFIX = "TRL-";
    public static final String SUBSCRIPTION_ID_PREFIX = "SUB-";
    public static final String CREDIT_NOTE_ID_PREFIX = "CRN-";
    public static final String DUNNING_ID_PREFIX = "DUN-";
    public static final String REFUND_ID_PREFIX = "RFD-";
    public static final String LEDGER_ENTRY_ID_PREFIX = "LED-";
    public static final String RECONCILIATION_ID_PREFIX = "REC-";

    // DynamoDB table names — billing extension
    public static final String INVOICES_TABLE = "verigate-invoices";
    public static final String PAYMENTS_TABLE = "verigate-payments";
    public static final String PLAN_CHANGES_TABLE = "verigate-plan-changes";
    public static final String TRIALS_TABLE = "verigate-trials";
    public static final String SUBSCRIPTIONS_TABLE = "verigate-subscriptions";
    public static final String DUNNING_SCHEDULES_TABLE = "verigate-dunning-schedules";
    public static final String CREDIT_NOTES_TABLE = "verigate-credit-notes";
    public static final String LEDGER_ENTRIES_TABLE = "verigate-ledger-entries";
    public static final String RECONCILIATION_REPORTS_TABLE = "verigate-reconciliation-reports";
    public static final String INVOICE_SEQUENCES_TABLE = "verigate-invoice-sequences";

    // Payment terms
    public static final int PAYMENT_TERMS_DAYS = 30;
    public static final String DEFAULT_CURRENCY = "ZAR";
    public static final BigDecimal VAT_RATE = new BigDecimal("0.15");

    // Seller details
    public static final String SELLER_COMPANY_NAME = "VeriGate (Pty) Ltd";
    public static final String SELLER_REGISTRATION_NUMBER = "2024/123456/07";
    public static final String SELLER_ADDRESS = "12 Long Street, Cape Town, 8001, South Africa";

    // S3
    public static final String INVOICE_PDF_S3_PREFIX = "invoices/";

    // Dunning
    public static final int DUNNING_MAX_RETRIES = 4;
    public static final int[] DUNNING_RETRY_DAYS = {1, 3, 7, 14};

    // Trial
    public static final int DEFAULT_TRIAL_DURATION_DAYS = 14;

    private DomainConstants() {
        // Prevent instantiation
    }
}
