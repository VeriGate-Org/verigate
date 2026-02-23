/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.reactor.domain.handlers;

import verigate.verification.domain.events.VerificationStepCompletedEvent;

/**
 * Handler interface for processing verification step completed events.
 */
public interface VerificationStepCompletedEventHandler {
    void handle(VerificationStepCompletedEvent event);
}
