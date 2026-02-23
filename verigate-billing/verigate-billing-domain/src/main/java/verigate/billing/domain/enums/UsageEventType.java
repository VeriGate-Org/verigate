/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.enums;

/**
 * Maps to the verification types supported by the VeriGate platform.
 * Each type corresponds to a billable verification category.
 */
public enum UsageEventType {

    VERIFICATION_OF_PERSONAL_DETAILS("VERIFICATION_OF_PERSONAL_DETAILS"),
    VERIFICATION_OF_BANK_DETAILS("VERIFICATION_OF_BANK_DETAILS"),
    VERIFICATION_OF_COMPANY_DETAILS("VERIFICATION_OF_COMPANY_DETAILS"),
    VERIFICATION_OF_EMPLOYMENT("VERIFICATION_OF_EMPLOYMENT"),
    VERIFICATION_OF_QUALIFICATIONS("VERIFICATION_OF_QUALIFICATIONS"),
    VERIFICATION_OF_TAX_COMPLIANCE("VERIFICATION_OF_TAX_COMPLIANCE"),
    SANCTIONS_SCREENING("SANCTIONS_SCREENING"),
    CREDIT_BUREAU_CHECK("CREDIT_BUREAU_CHECK"),
    FRAUD_WATCHLIST_SCREENING("FRAUD_WATCHLIST_SCREENING"),
    NEGATIVE_NEWS_SCREENING("NEGATIVE_NEWS_SCREENING"),
    DOCUMENT_VERIFICATION("DOCUMENT_VERIFICATION"),
    PROPERTY_VERIFICATION("PROPERTY_VERIFICATION"),
    WATCHLIST_SCREENING("WATCHLIST_SCREENING");

    private final String value;

    UsageEventType(String value) {
        this.value = value;
    }

    /**
     * Returns the string value of this usage event type.
     *
     * @return the verification type string value
     */
    public String getValue() {
        return value;
    }

    /**
     * Resolves a {@link UsageEventType} from its string value.
     * Falls back to {@code null} if the value does not match any known type,
     * allowing the caller to handle unknown verification types gracefully.
     *
     * @param value the verification type string
     * @return the matching {@link UsageEventType}, or {@code null} if not found
     */
    public static UsageEventType fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (UsageEventType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }
}
