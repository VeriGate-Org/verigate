/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.reactor.domain.handlers;

/**
 * Routes verification events to appropriate handlers based on event type.
 */
public interface VerificationEventRouter {
    void routeEvent(String eventType, String eventPayload);
}
