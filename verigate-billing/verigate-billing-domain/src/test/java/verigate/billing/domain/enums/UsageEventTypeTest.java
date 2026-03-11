package verigate.billing.domain.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class UsageEventTypeTest {

    @ParameterizedTest
    @EnumSource(UsageEventType.class)
    void shouldResolveAllTypesFromValue(UsageEventType type) {
        assertEquals(type, UsageEventType.fromValue(type.getValue()));
    }

    @Test
    void shouldReturnNullForUnknownValue() {
        assertNull(UsageEventType.fromValue("UNKNOWN_TYPE"));
    }

    @Test
    void shouldReturnNullForNullValue() {
        assertNull(UsageEventType.fromValue(null));
    }

    @Test
    void shouldHaveExpectedVerificationTypes() {
        assertNotNull(UsageEventType.SANCTIONS_SCREENING);
        assertNotNull(UsageEventType.CREDIT_BUREAU_CHECK);
        assertNotNull(UsageEventType.DOCUMENT_VERIFICATION);
        assertNotNull(UsageEventType.VERIFICATION_OF_PERSONAL_DETAILS);
        assertNotNull(UsageEventType.VERIFICATION_OF_BANK_DETAILS);
        assertNotNull(UsageEventType.VERIFICATION_OF_COMPANY_DETAILS);
        assertNotNull(UsageEventType.VERIFICATION_OF_EMPLOYMENT);
        assertNotNull(UsageEventType.VERIFICATION_OF_QUALIFICATIONS);
        assertNotNull(UsageEventType.VERIFICATION_OF_TAX_COMPLIANCE);
        assertNotNull(UsageEventType.FRAUD_WATCHLIST_SCREENING);
        assertNotNull(UsageEventType.NEGATIVE_NEWS_SCREENING);
        assertNotNull(UsageEventType.PROPERTY_VERIFICATION);
        assertNotNull(UsageEventType.WATCHLIST_SCREENING);
    }

    @Test
    void shouldHave13VerificationTypes() {
        assertEquals(13, UsageEventType.values().length);
    }

    @Test
    void shouldMatchValueToName() {
        assertEquals("SANCTIONS_SCREENING", UsageEventType.SANCTIONS_SCREENING.getValue());
        assertEquals("CREDIT_BUREAU_CHECK", UsageEventType.CREDIT_BUREAU_CHECK.getValue());
    }
}
