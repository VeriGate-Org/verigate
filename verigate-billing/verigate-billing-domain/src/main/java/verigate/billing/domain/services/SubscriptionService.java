/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.services;

import java.util.Optional;
import verigate.billing.domain.enums.CancellationType;
import verigate.billing.domain.enums.SubscriptionStatus;
import verigate.billing.domain.models.Subscription;

/**
 * Service for managing partner subscription lifecycle.
 */
public interface SubscriptionService {

    Subscription createSubscription(String partnerId, String planId);

    Optional<Subscription> getActiveSubscription(String partnerId);

    Subscription cancelSubscription(String subscriptionId, CancellationType type, String reason);

    Subscription renewPeriod(String subscriptionId);

    Subscription updateStatus(String subscriptionId, SubscriptionStatus status);
}
