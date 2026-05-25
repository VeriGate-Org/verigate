/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.application.services;

import com.google.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.models.TaxConfiguration;
import verigate.billing.domain.services.TaxCalculationService;

/**
 * Default implementation of {@link TaxCalculationService}.
 * Handles South African VAT at 15%.
 */
public class DefaultTaxCalculationService implements TaxCalculationService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTaxCalculationService.class);

    private final TaxConfiguration taxConfiguration;

    @Inject
    public DefaultTaxCalculationService(TaxConfiguration taxConfiguration) {
        this.taxConfiguration = taxConfiguration;
    }

    @Override
    public BigDecimal calculateVat(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if (!taxConfiguration.vatRegistered()) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(taxConfiguration.vatRate()).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateExVatAmount(BigDecimal inclusiveAmount) {
        if (inclusiveAmount == null || inclusiveAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal divisor = BigDecimal.ONE.add(taxConfiguration.vatRate());
        return inclusiveAmount.divide(divisor, 2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean isVatApplicable(String country) {
        return "ZA".equalsIgnoreCase(country) || "South Africa".equalsIgnoreCase(country);
    }
}
