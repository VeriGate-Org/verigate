package verigate.billing.infrastructure.repositories.datamodels;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import verigate.billing.domain.models.UsageRecord;

class UsageRecordDataModelTest {

    @Test
    void fromDomain_shouldMapAllFields() {
        LocalDateTime timestamp = LocalDateTime.of(2025, 6, 15, 10, 30, 0);
        UsageRecord record = new UsageRecord(
            "USG-001", "partner-001", "SANCTIONS_SCREENING", "ver-001", "SUCCESS", timestamp);

        UsageRecordDataModel model = UsageRecordDataModel.fromDomain(record);

        assertEquals("partner-001", model.getPartnerId());
        assertEquals("USG-001", model.getUsageId());
        assertEquals("SANCTIONS_SCREENING", model.getVerificationType());
        assertEquals("ver-001", model.getVerificationId());
        assertEquals("SUCCESS", model.getOutcome());
        assertEquals("2025-06-15T10:30", model.getEventTimestamp());
    }

    @Test
    void fromDomain_shouldCreateSortKey() {
        LocalDateTime timestamp = LocalDateTime.of(2025, 6, 15, 10, 30, 0);
        UsageRecord record = new UsageRecord(
            "USG-001", "partner-001", "SANCTIONS_SCREENING", "ver-001", "SUCCESS", timestamp);

        UsageRecordDataModel model = UsageRecordDataModel.fromDomain(record);

        assertEquals("2025-06-15T10:30#USG-001", model.getSortKey());
    }

    @Test
    void toDomain_shouldMapAllFields() {
        UsageRecordDataModel model = new UsageRecordDataModel();
        model.setPartnerId("partner-001");
        model.setUsageId("USG-001");
        model.setVerificationType("SANCTIONS_SCREENING");
        model.setVerificationId("ver-001");
        model.setOutcome("SUCCESS");
        model.setEventTimestamp("2025-06-15T10:30:00");
        model.setSortKey("2025-06-15T10:30:00#USG-001");

        UsageRecord record = model.toDomain();

        assertEquals("USG-001", record.usageId());
        assertEquals("partner-001", record.partnerId());
        assertEquals("SANCTIONS_SCREENING", record.verificationType());
        assertEquals("ver-001", record.verificationId());
        assertEquals("SUCCESS", record.outcome());
        assertEquals(LocalDateTime.of(2025, 6, 15, 10, 30, 0), record.eventTimestamp());
    }

    @Test
    void roundTrip_shouldPreserveAllData() {
        LocalDateTime timestamp = LocalDateTime.of(2025, 6, 15, 10, 30, 45);
        UsageRecord original = new UsageRecord(
            "USG-001", "partner-001", "CREDIT_BUREAU_CHECK", "ver-001", "FAILURE", timestamp);

        UsageRecordDataModel model = UsageRecordDataModel.fromDomain(original);
        UsageRecord restored = model.toDomain();

        assertEquals(original.usageId(), restored.usageId());
        assertEquals(original.partnerId(), restored.partnerId());
        assertEquals(original.verificationType(), restored.verificationType());
        assertEquals(original.verificationId(), restored.verificationId());
        assertEquals(original.outcome(), restored.outcome());
        assertEquals(original.eventTimestamp(), restored.eventTimestamp());
    }

    @Test
    void sortKey_shouldEnableChronologicalOrdering() {
        LocalDateTime earlier = LocalDateTime.of(2025, 6, 15, 8, 0, 0);
        LocalDateTime later = LocalDateTime.of(2025, 6, 15, 16, 0, 0);

        UsageRecordDataModel model1 = UsageRecordDataModel.fromDomain(
            new UsageRecord("USG-001", "partner-001", "TYPE", "ver-001", "SUCCESS", earlier));
        UsageRecordDataModel model2 = UsageRecordDataModel.fromDomain(
            new UsageRecord("USG-002", "partner-001", "TYPE", "ver-002", "SUCCESS", later));

        // Sort keys should be lexicographically ordered by timestamp
        assertTrue(model1.getSortKey().compareTo(model2.getSortKey()) < 0);
    }
}
