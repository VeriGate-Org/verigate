/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.services;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import verigate.billing.domain.models.AccountStatement;
import verigate.billing.domain.models.ReconciliationReport;
import verigate.billing.domain.models.RevenueSummary;

/**
 * Service for financial reporting and reconciliation.
 */
public interface ReportingService {

    List<RevenueSummary> getRevenueSummary(YearMonth period);

    ReconciliationReport generateReconciliation(YearMonth period);

    List<String> getPartnersWithOutstandingInvoices();

    AccountStatement generateAccountStatement(String partnerId, LocalDate from, LocalDate to);
}
