/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.services;

import java.math.BigDecimal;

/**
 * Service for South African VAT calculations.
 */
public interface TaxCalculationService {

    BigDecimal calculateVat(BigDecimal amount);

    BigDecimal calculateExVatAmount(BigDecimal inclusiveAmount);

    boolean isVatApplicable(String country);
}
