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

    private DomainConstants() {
        // Prevent instantiation
    }
}
