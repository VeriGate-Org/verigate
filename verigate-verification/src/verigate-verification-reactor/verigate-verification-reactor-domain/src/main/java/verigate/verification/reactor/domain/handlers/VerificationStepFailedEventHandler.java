/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.reactor.domain.handlers;

import java.util.UUID;

/**
 * Handler interface for processing verification step failure events.
 */
public interface VerificationStepFailedEventHandler {
    void handle(UUID verificationId, UUID stepId, String failureReason);
}
