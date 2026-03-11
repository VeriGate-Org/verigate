package verigate.billing.domain.events;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VerificationCompletedEventTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void shouldCreateValidEvent() {
        VerificationCompletedEvent event = new VerificationCompletedEvent(
            "evt-001", "ver-001", "partner-001", "SANCTIONS_SCREENING",
            "SUCCESS", LocalDateTime.of(2025, 6, 15, 10, 30), "WorldCheck", "corr-001");

        assertEquals("evt-001", event.eventId());
        assertEquals("ver-001", event.verificationId());
        assertEquals("partner-001", event.partnerId());
        assertEquals("SANCTIONS_SCREENING", event.verificationType());
        assertEquals("SUCCESS", event.outcome());
        assertEquals("WorldCheck", event.provider());
        assertEquals("corr-001", event.correlationId());
    }

    @Test
    void shouldAllowNullEventId() {
        // eventId is not validated
        VerificationCompletedEvent event = new VerificationCompletedEvent(
            null, "ver-001", "partner-001", "SANCTIONS_SCREENING",
            "SUCCESS", null, null, null);

        assertNull(event.eventId());
    }

    @Test
    void shouldAllowNullOptionalFields() {
        // eventTimestamp, provider, correlationId are optional
        VerificationCompletedEvent event = new VerificationCompletedEvent(
            "evt-001", "ver-001", "partner-001", "SANCTIONS_SCREENING",
            "SUCCESS", null, null, null);

        assertNull(event.eventTimestamp());
        assertNull(event.provider());
        assertNull(event.correlationId());
    }

    @Test
    void shouldRejectNullVerificationId() {
        assertThrows(IllegalArgumentException.class,
            () -> new VerificationCompletedEvent(
                "evt-001", null, "partner-001", "SANCTIONS_SCREENING",
                "SUCCESS", null, null, null));
    }

    @Test
    void shouldRejectBlankVerificationId() {
        assertThrows(IllegalArgumentException.class,
            () -> new VerificationCompletedEvent(
                "evt-001", "  ", "partner-001", "SANCTIONS_SCREENING",
                "SUCCESS", null, null, null));
    }

    @Test
    void shouldRejectNullPartnerId() {
        assertThrows(IllegalArgumentException.class,
            () -> new VerificationCompletedEvent(
                "evt-001", "ver-001", null, "SANCTIONS_SCREENING",
                "SUCCESS", null, null, null));
    }

    @Test
    void shouldRejectNullVerificationType() {
        assertThrows(IllegalArgumentException.class,
            () -> new VerificationCompletedEvent(
                "evt-001", "ver-001", "partner-001", null,
                "SUCCESS", null, null, null));
    }

    @Test
    void shouldRejectNullOutcome() {
        assertThrows(IllegalArgumentException.class,
            () -> new VerificationCompletedEvent(
                "evt-001", "ver-001", "partner-001", "SANCTIONS_SCREENING",
                null, null, null, null));
    }

    @Test
    void shouldDeserializeFromJson() throws Exception {
        String json = """
            {
                "eventId": "evt-001",
                "verificationId": "ver-001",
                "partnerId": "partner-001",
                "verificationType": "SANCTIONS_SCREENING",
                "outcome": "SUCCESS",
                "eventTimestamp": "2025-06-15T10:30:00",
                "provider": "WorldCheck",
                "correlationId": "corr-001"
            }
            """;

        VerificationCompletedEvent event = objectMapper.readValue(json, VerificationCompletedEvent.class);

        assertEquals("evt-001", event.eventId());
        assertEquals("ver-001", event.verificationId());
        assertEquals("partner-001", event.partnerId());
        assertEquals("SANCTIONS_SCREENING", event.verificationType());
        assertEquals("SUCCESS", event.outcome());
        assertEquals(LocalDateTime.of(2025, 6, 15, 10, 30), event.eventTimestamp());
        assertEquals("WorldCheck", event.provider());
        assertEquals("corr-001", event.correlationId());
    }

    @Test
    void shouldDeserializeWithMissingOptionalFields() throws Exception {
        String json = """
            {
                "verificationId": "ver-001",
                "partnerId": "partner-001",
                "verificationType": "CREDIT_BUREAU_CHECK",
                "outcome": "FAILURE"
            }
            """;

        VerificationCompletedEvent event = objectMapper.readValue(json, VerificationCompletedEvent.class);

        assertEquals("ver-001", event.verificationId());
        assertNull(event.eventId());
        assertNull(event.eventTimestamp());
        assertNull(event.provider());
        assertNull(event.correlationId());
    }

    @Test
    void shouldIgnoreUnknownJsonFields() throws Exception {
        String json = """
            {
                "verificationId": "ver-001",
                "partnerId": "partner-001",
                "verificationType": "SANCTIONS_SCREENING",
                "outcome": "SUCCESS",
                "unknownField": "someValue",
                "anotherUnknown": 42
            }
            """;

        VerificationCompletedEvent event = objectMapper.readValue(json, VerificationCompletedEvent.class);
        assertEquals("ver-001", event.verificationId());
    }

    @Test
    void shouldFailDeserializationWithMissingRequiredFields() {
        String json = """
            {
                "eventId": "evt-001",
                "partnerId": "partner-001",
                "verificationType": "SANCTIONS_SCREENING",
                "outcome": "SUCCESS"
            }
            """;

        // Missing verificationId will cause IllegalArgumentException during construction
        // Jackson wraps this in a JsonMappingException
        assertThrows(Exception.class,
            () -> objectMapper.readValue(json, VerificationCompletedEvent.class));
    }

    @Test
    void shouldSerializeAndDeserializeRoundTrip() throws Exception {
        VerificationCompletedEvent original = new VerificationCompletedEvent(
            "evt-001", "ver-001", "partner-001", "SANCTIONS_SCREENING",
            "SUCCESS", LocalDateTime.of(2025, 6, 15, 10, 30), "WorldCheck", "corr-001");

        String json = objectMapper.writeValueAsString(original);
        VerificationCompletedEvent deserialized = objectMapper.readValue(json, VerificationCompletedEvent.class);

        assertEquals(original.eventId(), deserialized.eventId());
        assertEquals(original.verificationId(), deserialized.verificationId());
        assertEquals(original.partnerId(), deserialized.partnerId());
        assertEquals(original.verificationType(), deserialized.verificationType());
        assertEquals(original.outcome(), deserialized.outcome());
        assertEquals(original.eventTimestamp(), deserialized.eventTimestamp());
        assertEquals(original.provider(), deserialized.provider());
        assertEquals(original.correlationId(), deserialized.correlationId());
    }
}
