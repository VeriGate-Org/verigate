/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.domain.repositories;

import java.util.UUID;

import verigate.verification.domain.models.VerificationAggregateRoot;

/**
 * Repository interface for managing verification aggregates.
 */
public interface VerificationRepository {

    /**
     * Saves a verification aggregate.
     */
    void addOrUpdate(VerificationAggregateRoot verification);

    /**
     * Finds a verification by its ID.
     */
    VerificationAggregateRoot get(UUID verificationId);

}