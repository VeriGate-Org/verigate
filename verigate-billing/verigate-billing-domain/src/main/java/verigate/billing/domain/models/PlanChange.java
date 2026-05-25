/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.models;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import verigate.billing.domain.enums.PlanChangeStatus;
import verigate.billing.domain.enums.PlanChangeType;

/**
 * Records a partner's request to change billing plans.
 */
public record PlanChange(
    String planChangeId,
    String partnerId,
    String fromPlanId,
    String toPlanId,
    PlanChangeType changeType,
    PlanChangeStatus status,
    BigDecimal proratedCredit,
    BigDecimal proratedCharge,
    LocalDate effectiveDate,
    Instant requestedDate,
    String requestedBy
) {
    public PlanChange {
        if (planChangeId == null || planChangeId.isBlank()) {
            throw new IllegalArgumentException("planChangeId must not be null or blank");
        }
        if (partnerId == null || partnerId.isBlank()) {
            throw new IllegalArgumentException("partnerId must not be null or blank");
        }
        if (fromPlanId == null || fromPlanId.isBlank()) {
            throw new IllegalArgumentException("fromPlanId must not be null or blank");
        }
        if (toPlanId == null || toPlanId.isBlank()) {
            throw new IllegalArgumentException("toPlanId must not be null or blank");
        }
        if (changeType == null) {
            throw new IllegalArgumentException("changeType must not be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        if (effectiveDate == null) {
            throw new IllegalArgumentException("effectiveDate must not be null");
        }
        if (requestedDate == null) {
            throw new IllegalArgumentException("requestedDate must not be null");
        }
    }
}
