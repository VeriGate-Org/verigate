package verigate.webbff.verification.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import verigate.webbff.auth.PartnerContextHolder;
import verigate.webbff.verification.model.CommandStatus;
import verigate.webbff.verification.repository.CommandStatusRepository;
import verigate.webbff.verification.repository.model.PageResult;
import verigate.webbff.verification.repository.model.VerificationCommandStoreItem;

@WebMvcTest(controllers = ServiceHistoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class ServiceHistoryControllerTest {

    private static final String PARTNER_ID = "partner-test";

    @Autowired private MockMvc mockMvc;
    @MockBean private CommandStatusRepository commandStatusRepository;

    @AfterEach
    void tearDown() {
        PartnerContextHolder.clear();
    }

    // ── Identity ─────────────────────────────────────────────────────────

    @Nested
    class IdentityHistory {

        @Test
        void returnsVerifiedIdentity() throws Exception {
            PartnerContextHolder.setPartnerId(PARTNER_ID);

            var item = buildItem("cmd-1", CommandStatus.COMPLETED, Map.of(
                    "source", "dha",
                    "idNumber", "9001015009087",
                    "fullName", "Thabo Mokwena",
                    "outcome", "SUCCEEDED",
                    "verificationStatus", "VERIFIED"));

            stubQuery(List.of(item));

            mockMvc.perform(get("/api/partner/identity/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].verificationId").value("cmd-1"))
                    .andExpect(jsonPath("$.items[0].idNumber").value("9001015009087"))
                    .andExpect(jsonPath("$.items[0].fullName").value("Thabo Mokwena"))
                    .andExpect(jsonPath("$.items[0].outcome").value("VERIFIED"));
        }

        @Test
        void returnsDeceasedIdentity() throws Exception {
            PartnerContextHolder.setPartnerId(PARTNER_ID);

            var item = buildItem("cmd-2", CommandStatus.COMPLETED, Map.of(
                    "source", "dha",
                    "idNumber", "8501015009087",
                    "fullName", "Sipho Dlamini",
                    "outcome", "SUCCEEDED",
                    "verificationStatus", "DECEASED"));

            stubQuery(List.of(item));

            mockMvc.perform(get("/api/partner/identity/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items[0].outcome").value("DECEASED"));
        }

        @Test
        void filtersOutNonIdentityItems() throws Exception {
            PartnerContextHolder.setPartnerId(PARTNER_ID);

            var identityItem = buildItem("cmd-id", CommandStatus.COMPLETED, Map.of(
                    "source", "dha",
                    "idNumber", "9001015009087",
                    "fullName", "Thabo Mokwena",
                    "outcome", "SUCCEEDED",
                    "verificationStatus", "VERIFIED"));

            var documentItem = buildItem("cmd-doc", CommandStatus.COMPLETED, Map.of(
                    "documentType", "passport",
                    "documentNumber", "A12345678",
                    "outcome", "SUCCEEDED"));

            stubQuery(List.of(identityItem, documentItem));

            mockMvc.perform(get("/api/partner/identity/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].verificationId").value("cmd-id"));
        }
    }

    // ── Bank Account ─────────────────────────────────────────────────────

    @Nested
    class BankAccountHistory {

        @Test
        void returnsVerifiedBankAccount() throws Exception {
            PartnerContextHolder.setPartnerId(PARTNER_ID);

            var item = buildItem("cmd-ba", CommandStatus.COMPLETED, Map.of(
                    "bankAccountStatus", "VERIFIED",
                    "accountNumber", "****1234",
                    "branchName", "FNB",
                    "accountHolderName", "Lerato Molefe",
                    "outcome", "SUCCEEDED"));

            stubQuery(List.of(item));

            mockMvc.perform(get("/api/partner/bank-account/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].verificationId").value("cmd-ba"))
                    .andExpect(jsonPath("$.items[0].accountNumber").value("****1234"))
                    .andExpect(jsonPath("$.items[0].bank").value("FNB"))
                    .andExpect(jsonPath("$.items[0].accountHolder").value("Lerato Molefe"))
                    .andExpect(jsonPath("$.items[0].outcome").value("VERIFIED"));
        }

        @Test
        void mapsSoftFailToNotVerified() throws Exception {
            PartnerContextHolder.setPartnerId(PARTNER_ID);

            var item = buildItem("cmd-ba2", CommandStatus.COMPLETED, Map.of(
                    "bankAccountStatus", "MISMATCH",
                    "accountNumber", "****5678",
                    "branchName", "Standard Bank",
                    "accountHolderName", "Unknown",
                    "outcome", "SOFT_FAIL"));

            stubQuery(List.of(item));

            mockMvc.perform(get("/api/partner/bank-account/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items[0].outcome").value("NOT_VERIFIED"));
        }
    }

    // ── Credit Check ─────────────────────────────────────────────────────

    @Nested
    class CreditCheckHistory {

        @Test
        void returnsCompletedCreditCheck() throws Exception {
            PartnerContextHolder.setPartnerId(PARTNER_ID);

            var item = buildItem("cmd-cc", CommandStatus.COMPLETED, Map.of(
                    "creditScore", "680",
                    "scoreBand", "B",
                    "idNumber", "9001015009087",
                    "fullName", "Thabo Mokwena",
                    "outcome", "SUCCEEDED"));

            stubQuery(List.of(item));

            mockMvc.perform(get("/api/partner/credit-check/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].riskGrade").value("B"))
                    .andExpect(jsonPath("$.items[0].outcome").value("COMPLETED"));
        }

        @Test
        void mapsFailedCreditCheck() throws Exception {
            PartnerContextHolder.setPartnerId(PARTNER_ID);

            var item = buildItem("cmd-cc2", CommandStatus.PERMANENT_FAILURE, Map.of(
                    "creditScore", "0",
                    "idNumber", "8501015009087",
                    "fullName", "Sipho Dlamini"));

            stubQuery(List.of(item));

            mockMvc.perform(get("/api/partner/credit-check/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items[0].outcome").value("FAILED"));
        }
    }

    // ── Tax Compliance ───────────────────────────────────────────────────

    @Nested
    class TaxComplianceHistory {

        @Test
        void returnsCompliantTaxCheck() throws Exception {
            PartnerContextHolder.setPartnerId(PARTNER_ID);

            var item = buildItem("cmd-tax", CommandStatus.COMPLETED, Map.of(
                    "taxComplianceStatus", "COMPLIANT",
                    "certificateNumber", "TCC-001234",
                    "entityName", "Acme PTY Ltd",
                    "outcome", "SUCCEEDED"));

            stubQuery(List.of(item));

            mockMvc.perform(get("/api/partner/tax-compliance/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].taxNumber").value("TCC-001234"))
                    .andExpect(jsonPath("$.items[0].entityName").value("Acme PTY Ltd"))
                    .andExpect(jsonPath("$.items[0].outcome").value("COMPLIANT"));
        }

        @Test
        void returnsNonCompliantTaxCheck() throws Exception {
            PartnerContextHolder.setPartnerId(PARTNER_ID);

            var item = buildItem("cmd-tax2", CommandStatus.COMPLETED, Map.of(
                    "taxComplianceStatus", "NON_COMPLIANT",
                    "certificateNumber", "TCC-005678",
                    "entityName", "Bad Corp",
                    "outcome", "SUCCEEDED"));

            stubQuery(List.of(item));

            mockMvc.perform(get("/api/partner/tax-compliance/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items[0].outcome").value("NON_COMPLIANT"));
        }
    }

    // ── Company ──────────────────────────────────────────────────────────

    @Nested
    class CompanyHistory {

        @Test
        void returnsFoundCompany() throws Exception {
            PartnerContextHolder.setPartnerId(PARTNER_ID);

            var item = buildItem("cmd-co", CommandStatus.COMPLETED, Map.of(
                    "enterpriseNumber", "2020/123456/07",
                    "enterpriseName", "Verigate PTY Ltd",
                    "outcome", "SUCCEEDED"));

            stubQuery(List.of(item));

            mockMvc.perform(get("/api/partner/company/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].registrationNumber").value("2020/123456/07"))
                    .andExpect(jsonPath("$.items[0].companyName").value("Verigate PTY Ltd"))
                    .andExpect(jsonPath("$.items[0].outcome").value("FOUND"));
        }
    }

    // ── Employment ───────────────────────────────────────────────────────

    @Nested
    class EmploymentHistory {

        @Test
        void returnsVerifiedEmployment() throws Exception {
            PartnerContextHolder.setPartnerId(PARTNER_ID);

            var item = buildItem("cmd-emp", CommandStatus.COMPLETED, Map.of(
                    "employmentStatus", "EMPLOYED",
                    "employerName", "Acme Corp",
                    "idNumber", "9001015009087",
                    "fullName", "Thabo Mokwena",
                    "outcome", "SUCCEEDED"));

            stubQuery(List.of(item));

            mockMvc.perform(get("/api/partner/employment/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].employer").value("Acme Corp"))
                    .andExpect(jsonPath("$.items[0].outcome").value("VERIFIED"));
        }

        @Test
        void mapsNotFoundToNotVerified() throws Exception {
            PartnerContextHolder.setPartnerId(PARTNER_ID);

            var item = buildItem("cmd-emp2", CommandStatus.COMPLETED, Map.of(
                    "employmentStatus", "NOT_FOUND",
                    "employerName", "",
                    "idNumber", "8501015009087",
                    "fullName", "Sipho Dlamini",
                    "outcome", "SUCCEEDED"));

            stubQuery(List.of(item));

            mockMvc.perform(get("/api/partner/employment/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items[0].outcome").value("NOT_VERIFIED"));
        }
    }

    // ── Qualification ────────────────────────────────────────────────────

    @Nested
    class QualificationHistory {

        @Test
        void returnsVerifiedQualification() throws Exception {
            PartnerContextHolder.setPartnerId(PARTNER_ID);

            var item = buildItem("cmd-qual", CommandStatus.COMPLETED, Map.of(
                    "qualificationTitle", "BCom Accounting",
                    "institution", "University of Pretoria",
                    "status", "VERIFIED",
                    "idNumber", "9001015009087",
                    "fullName", "Thabo Mokwena",
                    "outcome", "SUCCEEDED"));

            stubQuery(List.of(item));

            mockMvc.perform(get("/api/partner/qualification/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].institution").value("University of Pretoria"))
                    .andExpect(jsonPath("$.items[0].outcome").value("VERIFIED"));
        }
    }

    // ── Negative News ────────────────────────────────────────────────────

    @Nested
    class NegativeNewsHistory {

        @Test
        void returnsClearScreening() throws Exception {
            PartnerContextHolder.setPartnerId(PARTNER_ID);

            var item = buildItem("cmd-nn", CommandStatus.COMPLETED, Map.of(
                    "screeningOutcome", "CLEAR",
                    "subjectName", "Thabo Mokwena",
                    "entityType", "Individual",
                    "adverseCount", "0",
                    "outcome", "SUCCEEDED"));

            stubQuery(List.of(item));

            mockMvc.perform(get("/api/partner/negative-news/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].subjectName").value("Thabo Mokwena"))
                    .andExpect(jsonPath("$.items[0].matchCount").value(0))
                    .andExpect(jsonPath("$.items[0].outcome").value("CLEAR"));
        }

        @Test
        void returnsMatchesFound() throws Exception {
            PartnerContextHolder.setPartnerId(PARTNER_ID);

            var item = buildItem("cmd-nn2", CommandStatus.COMPLETED, Map.of(
                    "screeningOutcome", "MATCHES_FOUND",
                    "subjectName", "John Doe",
                    "entityType", "Individual",
                    "adverseCount", "3",
                    "outcome", "SUCCEEDED"));

            stubQuery(List.of(item));

            mockMvc.perform(get("/api/partner/negative-news/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items[0].matchCount").value(3))
                    .andExpect(jsonPath("$.items[0].outcome").value("MATCHES_FOUND"));
        }
    }

    // ── Fraud Watchlist ──────────────────────────────────────────────────

    @Nested
    class FraudWatchlistHistory {

        @Test
        void returnsClearFraudCheck() throws Exception {
            PartnerContextHolder.setPartnerId(PARTNER_ID);

            var item = buildItem("cmd-fw", CommandStatus.COMPLETED, Map.of(
                    "fraudStatus", "CLEAR",
                    "idNumber", "9001015009087",
                    "fullName", "Thabo Mokwena",
                    "alertCount", "0",
                    "outcome", "SUCCEEDED"));

            stubQuery(List.of(item));

            mockMvc.perform(get("/api/partner/fraud-watchlist/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].idNumber").value("9001015009087"))
                    .andExpect(jsonPath("$.items[0].outcome").value("CLEAR"));
        }

        @Test
        void returnsListedFraudCheck() throws Exception {
            PartnerContextHolder.setPartnerId(PARTNER_ID);

            var item = buildItem("cmd-fw2", CommandStatus.COMPLETED, Map.of(
                    "fraudStatus", "LISTED",
                    "idNumber", "8501015009087",
                    "fullName", "Bad Actor",
                    "alertCount", "2",
                    "outcome", "SUCCEEDED"));

            stubQuery(List.of(item));

            mockMvc.perform(get("/api/partner/fraud-watchlist/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items[0].outcome").value("LISTED"));
        }
    }

    // ── Property ─────────────────────────────────────────────────────────

    @Nested
    class PropertyHistory {

        @Test
        void returnsFoundProperty() throws Exception {
            PartnerContextHolder.setPartnerId(PARTNER_ID);

            var item = buildItem("cmd-prop", CommandStatus.COMPLETED, Map.of(
                    "provider", "DeedsWeb",
                    "searchType", "Owner Name",
                    "recordCount", "3",
                    "query", "123 Main Street",
                    "outcome", "SUCCEEDED"));

            stubQuery(List.of(item));

            mockMvc.perform(get("/api/partner/property/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].query").value("123 Main Street"))
                    .andExpect(jsonPath("$.items[0].searchType").value("Owner Name"))
                    .andExpect(jsonPath("$.items[0].resultCount").value(3))
                    .andExpect(jsonPath("$.items[0].outcome").value("FOUND"));
        }

        @Test
        void returnsNotFoundProperty() throws Exception {
            PartnerContextHolder.setPartnerId(PARTNER_ID);

            var item = buildItem("cmd-prop2", CommandStatus.COMPLETED, Map.of(
                    "provider", "DeedsWeb",
                    "searchType", "Title Deed",
                    "recordCount", "0",
                    "query", "Nonexistent Address",
                    "outcome", "SUCCEEDED"));

            stubQuery(List.of(item));

            mockMvc.perform(get("/api/partner/property/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items[0].outcome").value("NOT_FOUND"));
        }
    }

    // ── Cross-service filtering ──────────────────────────────────────────

    @Nested
    class CrossServiceFiltering {

        @Test
        void eachEndpointOnlyReturnsItsOwnServiceItems() throws Exception {
            PartnerContextHolder.setPartnerId(PARTNER_ID);

            var identity = buildItem("cmd-id", CommandStatus.COMPLETED, Map.of(
                    "source", "dha", "idNumber", "9001015009087",
                    "fullName", "Thabo", "outcome", "SUCCEEDED",
                    "verificationStatus", "VERIFIED"));

            var bankAccount = buildItem("cmd-ba", CommandStatus.COMPLETED, Map.of(
                    "bankAccountStatus", "VERIFIED", "accountNumber", "****1234",
                    "branchName", "FNB", "accountHolderName", "Lerato",
                    "outcome", "SUCCEEDED"));

            var creditCheck = buildItem("cmd-cc", CommandStatus.COMPLETED, Map.of(
                    "creditScore", "700", "scoreBand", "A",
                    "idNumber", "9001015009087", "fullName", "Thabo",
                    "outcome", "SUCCEEDED"));

            stubQuery(List.of(identity, bankAccount, creditCheck));

            // Identity should only return identity item
            mockMvc.perform(get("/api/partner/identity/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].verificationId").value("cmd-id"));

            // Bank account should only return bank account item
            mockMvc.perform(get("/api/partner/bank-account/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].verificationId").value("cmd-ba"));

            // Credit check should only return credit check item
            mockMvc.perform(get("/api/partner/credit-check/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].verificationId").value("cmd-cc"));
        }

        @Test
        void returnsEmptyListWhenNoMatchingItems() throws Exception {
            PartnerContextHolder.setPartnerId(PARTNER_ID);

            stubQuery(List.of());

            mockMvc.perform(get("/api/partner/identity/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items.length()").value(0))
                    .andExpect(jsonPath("$.hasMore").value(false));
        }

        @Test
        void excludesItemsWithNullAuxiliaryData() throws Exception {
            PartnerContextHolder.setPartnerId(PARTNER_ID);

            var nullAuxItem = buildItem("cmd-null", CommandStatus.COMPLETED, null);

            var validItem = buildItem("cmd-ok", CommandStatus.COMPLETED, Map.of(
                    "source", "dha", "idNumber", "9001015009087",
                    "fullName", "Thabo", "outcome", "SUCCEEDED",
                    "verificationStatus", "VERIFIED"));

            stubQuery(List.of(nullAuxItem, validItem));

            mockMvc.perform(get("/api/partner/identity/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.items[0].verificationId").value("cmd-ok"));
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private void stubQuery(List<VerificationCommandStoreItem> items) {
        when(commandStatusRepository.findByPartnerId(
                eq(PARTNER_ID), isNull(), eq("VerifyPartyCommand"), eq(50), isNull()))
                .thenReturn(new PageResult<>(items, Map.of()));
    }

    private VerificationCommandStoreItem buildItem(
            String commandId, CommandStatus status, Map<String, String> auxiliaryData) {
        var item = new VerificationCommandStoreItem();
        item.setCommandId(commandId);
        item.setCommandName("VerifyPartyCommand");
        item.setStatus(status);
        item.setPartnerId(PARTNER_ID);
        item.setCreatedAt("2025-06-15T10:00:00Z");
        item.setAuxiliaryData(auxiliaryData != null ? new HashMap<>(auxiliaryData) : null);
        return item;
    }
}
