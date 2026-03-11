/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.domain.models;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import verigate.riskengine.domain.enums.VerificationOutcome;
import verigate.riskengine.domain.enums.VerificationType;
import verigate.riskengine.domain.enums.WorkflowStatus;

class VerificationWorkflowTest {

    private AdapterScore score(VerificationType type, int confidence) {
        return new AdapterScore(type, VerificationOutcome.SUCCEEDED, confidence,
            Map.of(), Instant.now());
    }

    // --- Workflow Completion Detection ---

    @Test
    void isComplete_noCompletedChecks_returnsFalse() {
        var workflow = new VerificationWorkflow(
            UUID.randomUUID(), "partner-1", "policy-1",
            Map.of(
                VerificationType.IDENTITY_VERIFICATION, UUID.randomUUID(),
                VerificationType.SANCTIONS_SCREENING, UUID.randomUUID(),
                VerificationType.CREDIT_CHECK, UUID.randomUUID()
            ),
            Map.of(),
            WorkflowStatus.PENDING,
            Instant.now(), null
        );

        assertFalse(workflow.isComplete());
    }

    @Test
    void isComplete_partialCompletion_returnsFalse() {
        var workflow = new VerificationWorkflow(
            UUID.randomUUID(), "partner-1", "policy-1",
            Map.of(
                VerificationType.IDENTITY_VERIFICATION, UUID.randomUUID(),
                VerificationType.SANCTIONS_SCREENING, UUID.randomUUID(),
                VerificationType.CREDIT_CHECK, UUID.randomUUID()
            ),
            Map.of(
                VerificationType.IDENTITY_VERIFICATION, score(VerificationType.IDENTITY_VERIFICATION, 90),
                VerificationType.SANCTIONS_SCREENING, score(VerificationType.SANCTIONS_SCREENING, 100)
            ),
            WorkflowStatus.PENDING,
            Instant.now(), null
        );

        assertFalse(workflow.isComplete());
        assertEquals(2, workflow.completedChecks().size());
    }

    @Test
    void isComplete_allChecksCompleted_returnsTrue() {
        var completed = new HashMap<VerificationType, AdapterScore>();
        completed.put(VerificationType.IDENTITY_VERIFICATION,
            score(VerificationType.IDENTITY_VERIFICATION, 90));
        completed.put(VerificationType.SANCTIONS_SCREENING,
            score(VerificationType.SANCTIONS_SCREENING, 100));
        completed.put(VerificationType.CREDIT_CHECK,
            score(VerificationType.CREDIT_CHECK, 72));

        var workflow = new VerificationWorkflow(
            UUID.randomUUID(), "partner-1", "policy-1",
            Map.of(
                VerificationType.IDENTITY_VERIFICATION, UUID.randomUUID(),
                VerificationType.SANCTIONS_SCREENING, UUID.randomUUID(),
                VerificationType.CREDIT_CHECK, UUID.randomUUID()
            ),
            completed,
            WorkflowStatus.PENDING,
            Instant.now(), null
        );

        assertTrue(workflow.isComplete());
    }

    @Test
    void isComplete_singleCheck_completesImmediately() {
        var completed = new HashMap<VerificationType, AdapterScore>();
        completed.put(VerificationType.COMPANY_VERIFICATION,
            score(VerificationType.COMPANY_VERIFICATION, 85));

        var workflow = new VerificationWorkflow(
            UUID.randomUUID(), "partner-1", "policy-1",
            Map.of(VerificationType.COMPANY_VERIFICATION, UUID.randomUUID()),
            completed,
            WorkflowStatus.PENDING,
            Instant.now(), null
        );

        assertTrue(workflow.isComplete());
    }

    // --- Validation ---

    @Test
    void constructor_nullWorkflowId_throws() {
        assertThrows(IllegalArgumentException.class, () ->
            new VerificationWorkflow(
                null, "partner-1", "policy-1",
                Map.of(VerificationType.IDENTITY_VERIFICATION, UUID.randomUUID()),
                Map.of(), WorkflowStatus.PENDING, Instant.now(), null
            )
        );
    }

    @Test
    void constructor_blankPartnerId_throws() {
        assertThrows(IllegalArgumentException.class, () ->
            new VerificationWorkflow(
                UUID.randomUUID(), "", "policy-1",
                Map.of(VerificationType.IDENTITY_VERIFICATION, UUID.randomUUID()),
                Map.of(), WorkflowStatus.PENDING, Instant.now(), null
            )
        );
    }

    @Test
    void constructor_emptyExpectedChecks_throws() {
        assertThrows(IllegalArgumentException.class, () ->
            new VerificationWorkflow(
                UUID.randomUUID(), "partner-1", "policy-1",
                Map.of(),
                Map.of(), WorkflowStatus.PENDING, Instant.now(), null
            )
        );
    }

    @Test
    void constructor_nullCompletedChecks_defaultsToEmpty() {
        var workflow = new VerificationWorkflow(
            UUID.randomUUID(), "partner-1", "policy-1",
            Map.of(VerificationType.IDENTITY_VERIFICATION, UUID.randomUUID()),
            null,
            WorkflowStatus.PENDING,
            Instant.now(), null
        );

        assertNotNull(workflow.completedChecks());
        assertTrue(workflow.completedChecks().isEmpty());
    }

    @Test
    void constructor_nullStatus_defaultsToPending() {
        var workflow = new VerificationWorkflow(
            UUID.randomUUID(), "partner-1", "policy-1",
            Map.of(VerificationType.IDENTITY_VERIFICATION, UUID.randomUUID()),
            Map.of(), null, Instant.now(), null
        );

        assertEquals(WorkflowStatus.PENDING, workflow.status());
    }
}
