package verigate.billing.domain.models;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class UsageRecordTest {

    private static final String USAGE_ID = "USG-123";
    private static final String PARTNER_ID = "partner-001";
    private static final String VERIFICATION_TYPE = "SANCTIONS_SCREENING";
    private static final String VERIFICATION_ID = "ver-456";
    private static final String OUTCOME = "SUCCESS";
    private static final LocalDateTime TIMESTAMP = LocalDateTime.of(2025, 6, 15, 10, 30, 0);

    @Test
    void shouldCreateValidUsageRecord() {
        UsageRecord record = new UsageRecord(
            USAGE_ID, PARTNER_ID, VERIFICATION_TYPE, VERIFICATION_ID, OUTCOME, TIMESTAMP);

        assertEquals(USAGE_ID, record.usageId());
        assertEquals(PARTNER_ID, record.partnerId());
        assertEquals(VERIFICATION_TYPE, record.verificationType());
        assertEquals(VERIFICATION_ID, record.verificationId());
        assertEquals(OUTCOME, record.outcome());
        assertEquals(TIMESTAMP, record.eventTimestamp());
    }

    @Test
    void shouldRejectNullUsageId() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> new UsageRecord(null, PARTNER_ID, VERIFICATION_TYPE, VERIFICATION_ID, OUTCOME, TIMESTAMP));
        assertEquals("usageId must not be null or blank", ex.getMessage());
    }

    @Test
    void shouldRejectBlankUsageId() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> new UsageRecord("  ", PARTNER_ID, VERIFICATION_TYPE, VERIFICATION_ID, OUTCOME, TIMESTAMP));
        assertEquals("usageId must not be null or blank", ex.getMessage());
    }

    @Test
    void shouldRejectNullPartnerId() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> new UsageRecord(USAGE_ID, null, VERIFICATION_TYPE, VERIFICATION_ID, OUTCOME, TIMESTAMP));
        assertEquals("partnerId must not be null or blank", ex.getMessage());
    }

    @Test
    void shouldRejectBlankPartnerId() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> new UsageRecord(USAGE_ID, "", VERIFICATION_TYPE, VERIFICATION_ID, OUTCOME, TIMESTAMP));
        assertEquals("partnerId must not be null or blank", ex.getMessage());
    }

    @Test
    void shouldRejectNullVerificationType() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> new UsageRecord(USAGE_ID, PARTNER_ID, null, VERIFICATION_ID, OUTCOME, TIMESTAMP));
        assertEquals("verificationType must not be null or blank", ex.getMessage());
    }

    @Test
    void shouldRejectNullVerificationId() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> new UsageRecord(USAGE_ID, PARTNER_ID, VERIFICATION_TYPE, null, OUTCOME, TIMESTAMP));
        assertEquals("verificationId must not be null or blank", ex.getMessage());
    }

    @Test
    void shouldRejectNullOutcome() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> new UsageRecord(USAGE_ID, PARTNER_ID, VERIFICATION_TYPE, VERIFICATION_ID, null, TIMESTAMP));
        assertEquals("outcome must not be null or blank", ex.getMessage());
    }

    @Test
    void shouldRejectNullEventTimestamp() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> new UsageRecord(USAGE_ID, PARTNER_ID, VERIFICATION_TYPE, VERIFICATION_ID, OUTCOME, null));
        assertEquals("eventTimestamp must not be null", ex.getMessage());
    }

    @Test
    void shouldSupportEquality() {
        UsageRecord record1 = new UsageRecord(
            USAGE_ID, PARTNER_ID, VERIFICATION_TYPE, VERIFICATION_ID, OUTCOME, TIMESTAMP);
        UsageRecord record2 = new UsageRecord(
            USAGE_ID, PARTNER_ID, VERIFICATION_TYPE, VERIFICATION_ID, OUTCOME, TIMESTAMP);

        assertEquals(record1, record2);
        assertEquals(record1.hashCode(), record2.hashCode());
    }

    @Test
    void shouldSupportInequality() {
        UsageRecord record1 = new UsageRecord(
            USAGE_ID, PARTNER_ID, VERIFICATION_TYPE, VERIFICATION_ID, OUTCOME, TIMESTAMP);
        UsageRecord record2 = new UsageRecord(
            "USG-999", PARTNER_ID, VERIFICATION_TYPE, VERIFICATION_ID, OUTCOME, TIMESTAMP);

        assertNotEquals(record1, record2);
    }
}
