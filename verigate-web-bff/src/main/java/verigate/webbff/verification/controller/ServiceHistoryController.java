package verigate.webbff.verification.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import verigate.webbff.auth.PartnerContextHolder;
import verigate.webbff.verification.model.CommandStatus;
import verigate.webbff.verification.repository.CommandStatusRepository;
import verigate.webbff.verification.repository.model.PageResult;
import verigate.webbff.verification.repository.model.VerificationCommandStoreItem;

@RestController
@RequestMapping("/api/partner")
public class ServiceHistoryController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceHistoryController.class);
    private static final String VERIFY_PARTY_COMMAND = "VerifyPartyCommand";

    private final CommandStatusRepository commandStatusRepository;

    public ServiceHistoryController(CommandStatusRepository commandStatusRepository) {
        this.commandStatusRepository = commandStatusRepository;
    }

    // ── Identity Verification ────────────────────────────────────────────

    @GetMapping("/identity/history")
    public ResponseEntity<IdentityHistoryResponse> getIdentityHistory(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "50") int limit) {

        var page = queryPartnerHistory(status, cursor, limit, "identity");

        List<IdentityHistoryItem> items = page.items().stream()
                .filter(item -> hasAuxKey(item, "source"))
                .map(this::mapIdentityItem)
                .toList();

        return ResponseEntity.ok(new IdentityHistoryResponse(
                items, extractCursor(page), page.hasMore()));
    }

    private IdentityHistoryItem mapIdentityItem(VerificationCommandStoreItem item) {
        Map<String, String> aux = safeAux(item);
        String idNumber = aux.getOrDefault("idNumber", "");
        String fullName = aux.getOrDefault("fullName", "");

        String outcome;
        if (isCompleted(item)) {
            String rawOutcome = aux.getOrDefault("outcome", "SUCCEEDED");
            if ("SUCCEEDED".equals(rawOutcome)) {
                String verificationStatus = aux.getOrDefault("verificationStatus", "VERIFIED");
                outcome = switch (verificationStatus) {
                    case "VERIFIED" -> "VERIFIED";
                    case "NOT_FOUND" -> "NOT_FOUND";
                    case "DECEASED" -> "DECEASED";
                    default -> "FAILED";
                };
            } else {
                outcome = "FAILED";
            }
        } else if (isFailed(item)) {
            outcome = "FAILED";
        } else {
            outcome = "PENDING";
        }

        return new IdentityHistoryItem(
                item.getCommandId(), idNumber, fullName, outcome, item.getCreatedAt());
    }

    // ── Bank Account Verification ────────────────────────────────────────

    @GetMapping("/bank-account/history")
    public ResponseEntity<BankAccountHistoryResponse> getBankAccountHistory(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "50") int limit) {

        var page = queryPartnerHistory(status, cursor, limit, "bank-account");

        List<BankAccountHistoryItem> items = page.items().stream()
                .filter(item -> hasAuxKey(item, "bankAccountStatus"))
                .map(this::mapBankAccountItem)
                .toList();

        return ResponseEntity.ok(new BankAccountHistoryResponse(
                items, extractCursor(page), page.hasMore()));
    }

    private BankAccountHistoryItem mapBankAccountItem(VerificationCommandStoreItem item) {
        Map<String, String> aux = safeAux(item);
        String accountNumber = aux.getOrDefault("accountNumber", "");
        String bank = aux.getOrDefault("branchName", "");
        String accountHolder = aux.getOrDefault("accountHolderName", "");

        String outcome = mapStandardOutcome(item, aux, "VERIFIED", "NOT_VERIFIED");

        return new BankAccountHistoryItem(
                item.getCommandId(), accountNumber, bank, accountHolder,
                outcome, item.getCreatedAt());
    }

    // ── Credit Check ─────────────────────────────────────────────────────

    @GetMapping("/credit-check/history")
    public ResponseEntity<CreditCheckHistoryResponse> getCreditCheckHistory(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "50") int limit) {

        var page = queryPartnerHistory(status, cursor, limit, "credit-check");

        List<CreditCheckHistoryItem> items = page.items().stream()
                .filter(item -> hasAuxKey(item, "creditScore"))
                .map(this::mapCreditCheckItem)
                .toList();

        return ResponseEntity.ok(new CreditCheckHistoryResponse(
                items, extractCursor(page), page.hasMore()));
    }

    private CreditCheckHistoryItem mapCreditCheckItem(VerificationCommandStoreItem item) {
        Map<String, String> aux = safeAux(item);
        String idNumber = aux.getOrDefault("idNumber", "");
        String fullName = aux.getOrDefault("fullName", "");
        String riskGrade = aux.getOrDefault("scoreBand", aux.getOrDefault("riskLevel", ""));

        String outcome;
        if (isCompleted(item)) {
            String rawOutcome = aux.getOrDefault("outcome", "SUCCEEDED");
            outcome = "SUCCEEDED".equals(rawOutcome) ? "COMPLETED" : "FAILED";
        } else if (isFailed(item)) {
            outcome = "FAILED";
        } else {
            outcome = "PENDING";
        }

        return new CreditCheckHistoryItem(
                item.getCommandId(), idNumber, fullName, riskGrade,
                outcome, item.getCreatedAt());
    }

    // ── Income Verification ──────────────────────────────────────────────

    @GetMapping("/income/history")
    public ResponseEntity<IncomeHistoryResponse> getIncomeHistory(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "50") int limit) {

        var page = queryPartnerHistory(status, cursor, limit, "income");

        List<IncomeHistoryItem> items = page.items().stream()
                .filter(item -> hasAuxKey(item, "verifiedMonthlyIncome")
                        || hasAuxKey(item, "declaredMonthlyIncome"))
                .map(this::mapIncomeItem)
                .toList();

        return ResponseEntity.ok(new IncomeHistoryResponse(
                items, extractCursor(page), page.hasMore()));
    }

    private IncomeHistoryItem mapIncomeItem(VerificationCommandStoreItem item) {
        Map<String, String> aux = safeAux(item);
        String idNumber = aux.getOrDefault("idNumber", "");
        String fullName = aux.getOrDefault("fullName", "");
        String employer = aux.getOrDefault("employer", "");

        String outcome;
        if (isCompleted(item)) {
            String rawOutcome = aux.getOrDefault("outcome", "SUCCEEDED");
            if ("SUCCEEDED".equals(rawOutcome)) {
                String verificationStatus = aux.getOrDefault("verificationStatus", "VERIFIED");
                outcome = "VERIFIED".equals(verificationStatus) ? "VERIFIED" : "NOT_VERIFIED";
            } else {
                outcome = "FAILED";
            }
        } else if (isFailed(item)) {
            outcome = "FAILED";
        } else {
            outcome = "PENDING";
        }

        return new IncomeHistoryItem(
                item.getCommandId(), idNumber, fullName, employer,
                outcome, item.getCreatedAt());
    }

    // ── Tax Compliance ───────────────────────────────────────────────────

    @GetMapping("/tax-compliance/history")
    public ResponseEntity<TaxComplianceHistoryResponse> getTaxComplianceHistory(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "50") int limit) {

        var page = queryPartnerHistory(status, cursor, limit, "tax-compliance");

        List<TaxComplianceHistoryItem> items = page.items().stream()
                .filter(item -> hasAuxKey(item, "taxComplianceStatus"))
                .map(this::mapTaxComplianceItem)
                .toList();

        return ResponseEntity.ok(new TaxComplianceHistoryResponse(
                items, extractCursor(page), page.hasMore()));
    }

    private TaxComplianceHistoryItem mapTaxComplianceItem(VerificationCommandStoreItem item) {
        Map<String, String> aux = safeAux(item);
        String taxNumber = aux.getOrDefault("taxNumber",
                aux.getOrDefault("certificateNumber", ""));
        String entityName = aux.getOrDefault("entityName",
                aux.getOrDefault("fullName", ""));

        String outcome;
        if (isCompleted(item)) {
            String rawOutcome = aux.getOrDefault("outcome", "SUCCEEDED");
            if ("SUCCEEDED".equals(rawOutcome)) {
                String complianceStatus = aux.getOrDefault("taxComplianceStatus", "COMPLIANT");
                outcome = switch (complianceStatus) {
                    case "COMPLIANT", "TCC_VALID" -> "COMPLIANT";
                    case "NON_COMPLIANT", "TCC_EXPIRED" -> "NON_COMPLIANT";
                    default -> "FAILED";
                };
            } else {
                outcome = "FAILED";
            }
        } else if (isFailed(item)) {
            outcome = "FAILED";
        } else {
            outcome = "PENDING";
        }

        return new TaxComplianceHistoryItem(
                item.getCommandId(), taxNumber, entityName,
                outcome, item.getCreatedAt());
    }

    // ── Company Verification ─────────────────────────────────────────────

    @GetMapping("/company/history")
    public ResponseEntity<CompanyHistoryResponse> getCompanyHistory(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "50") int limit) {

        var page = queryPartnerHistory(status, cursor, limit, "company");

        List<CompanyHistoryItem> items = page.items().stream()
                .filter(item -> hasAuxKey(item, "enterpriseNumber"))
                .map(this::mapCompanyItem)
                .toList();

        return ResponseEntity.ok(new CompanyHistoryResponse(
                items, extractCursor(page), page.hasMore()));
    }

    private CompanyHistoryItem mapCompanyItem(VerificationCommandStoreItem item) {
        Map<String, String> aux = safeAux(item);
        String registrationNumber = aux.getOrDefault("enterpriseNumber", "");
        String companyName = aux.getOrDefault("enterpriseName", "");

        String outcome = mapStandardOutcome(item, aux, "FOUND", "NOT_FOUND");

        return new CompanyHistoryItem(
                item.getCommandId(), registrationNumber, companyName,
                outcome, item.getCreatedAt());
    }

    // ── Employment Verification ──────────────────────────────────────────

    @GetMapping("/employment/history")
    public ResponseEntity<EmploymentHistoryResponse> getEmploymentHistory(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "50") int limit) {

        var page = queryPartnerHistory(status, cursor, limit, "employment");

        List<EmploymentHistoryItem> items = page.items().stream()
                .filter(item -> hasAuxKey(item, "employmentStatus"))
                .map(this::mapEmploymentItem)
                .toList();

        return ResponseEntity.ok(new EmploymentHistoryResponse(
                items, extractCursor(page), page.hasMore()));
    }

    private EmploymentHistoryItem mapEmploymentItem(VerificationCommandStoreItem item) {
        Map<String, String> aux = safeAux(item);
        String idNumber = aux.getOrDefault("idNumber", "");
        String fullName = aux.getOrDefault("fullName", "");
        String employer = aux.getOrDefault("employerName", "");

        String outcome;
        if (isCompleted(item)) {
            String rawOutcome = aux.getOrDefault("outcome", "SUCCEEDED");
            if ("SUCCEEDED".equals(rawOutcome)) {
                String empStatus = aux.getOrDefault("employmentStatus", "EMPLOYED");
                outcome = switch (empStatus) {
                    case "EMPLOYED", "ON_LEAVE", "SUSPENDED" -> "VERIFIED";
                    default -> "NOT_VERIFIED";
                };
            } else {
                outcome = "FAILED";
            }
        } else if (isFailed(item)) {
            outcome = "FAILED";
        } else {
            outcome = "PENDING";
        }

        return new EmploymentHistoryItem(
                item.getCommandId(), idNumber, fullName, employer,
                outcome, item.getCreatedAt());
    }

    // ── Qualification Verification ───────────────────────────────────────

    @GetMapping("/qualification/history")
    public ResponseEntity<QualificationHistoryResponse> getQualificationHistory(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "50") int limit) {

        var page = queryPartnerHistory(status, cursor, limit, "qualification");

        List<QualificationHistoryItem> items = page.items().stream()
                .filter(item -> hasAuxKey(item, "qualificationTitle"))
                .map(this::mapQualificationItem)
                .toList();

        return ResponseEntity.ok(new QualificationHistoryResponse(
                items, extractCursor(page), page.hasMore()));
    }

    private QualificationHistoryItem mapQualificationItem(VerificationCommandStoreItem item) {
        Map<String, String> aux = safeAux(item);
        String idNumber = aux.getOrDefault("idNumber", "");
        String fullName = aux.getOrDefault("fullName", "");
        String institution = aux.getOrDefault("institution", "");

        String outcome;
        if (isCompleted(item)) {
            String rawOutcome = aux.getOrDefault("outcome", "SUCCEEDED");
            if ("SUCCEEDED".equals(rawOutcome)) {
                String qualStatus = aux.getOrDefault("status", "VERIFIED");
                outcome = "VERIFIED".equals(qualStatus) ? "VERIFIED" : "NOT_VERIFIED";
            } else {
                outcome = "FAILED";
            }
        } else if (isFailed(item)) {
            outcome = "FAILED";
        } else {
            outcome = "PENDING";
        }

        return new QualificationHistoryItem(
                item.getCommandId(), idNumber, fullName, institution,
                outcome, item.getCreatedAt());
    }

    // ── Negative News Screening ──────────────────────────────────────────

    @GetMapping("/negative-news/history")
    public ResponseEntity<NegativeNewsHistoryResponse> getNegativeNewsHistory(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "50") int limit) {

        var page = queryPartnerHistory(status, cursor, limit, "negative-news");

        List<NegativeNewsHistoryItem> items = page.items().stream()
                .filter(item -> hasAuxKey(item, "screeningOutcome"))
                .map(this::mapNegativeNewsItem)
                .toList();

        return ResponseEntity.ok(new NegativeNewsHistoryResponse(
                items, extractCursor(page), page.hasMore()));
    }

    private NegativeNewsHistoryItem mapNegativeNewsItem(VerificationCommandStoreItem item) {
        Map<String, String> aux = safeAux(item);
        String subjectName = aux.getOrDefault("subjectName",
                aux.getOrDefault("fullName", ""));
        String entityType = aux.getOrDefault("entityType", "Individual");

        int matchCount = 0;
        try {
            matchCount = Integer.parseInt(aux.getOrDefault("adverseCount", "0"));
        } catch (NumberFormatException ignored) {}

        String outcome;
        if (isCompleted(item)) {
            String rawOutcome = aux.getOrDefault("outcome", "SUCCEEDED");
            if ("SUCCEEDED".equals(rawOutcome)) {
                outcome = matchCount > 0 ? "MATCHES_FOUND" : "CLEAR";
            } else {
                outcome = "FAILED";
            }
        } else if (isFailed(item)) {
            outcome = "FAILED";
        } else {
            outcome = "PENDING";
        }

        return new NegativeNewsHistoryItem(
                item.getCommandId(), subjectName, entityType, matchCount,
                outcome, item.getCreatedAt());
    }

    // ── Fraud Watchlist Screening ────────────────────────────────────────

    @GetMapping("/fraud-watchlist/history")
    public ResponseEntity<FraudWatchlistHistoryResponse> getFraudWatchlistHistory(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "50") int limit) {

        var page = queryPartnerHistory(status, cursor, limit, "fraud-watchlist");

        List<FraudWatchlistHistoryItem> items = page.items().stream()
                .filter(item -> hasAuxKey(item, "fraudStatus"))
                .map(this::mapFraudWatchlistItem)
                .toList();

        return ResponseEntity.ok(new FraudWatchlistHistoryResponse(
                items, extractCursor(page), page.hasMore()));
    }

    private FraudWatchlistHistoryItem mapFraudWatchlistItem(VerificationCommandStoreItem item) {
        Map<String, String> aux = safeAux(item);
        String idNumber = aux.getOrDefault("idNumber", "");
        String fullName = aux.getOrDefault("fullName", "");

        int alertCount = 0;
        try {
            alertCount = Integer.parseInt(aux.getOrDefault("alertCount", "0"));
        } catch (NumberFormatException ignored) {}

        String outcome;
        if (isCompleted(item)) {
            String rawOutcome = aux.getOrDefault("outcome", "SUCCEEDED");
            if ("SUCCEEDED".equals(rawOutcome)) {
                outcome = alertCount > 0 ? "LISTED" : "CLEAR";
            } else {
                outcome = "FAILED";
            }
        } else if (isFailed(item)) {
            outcome = "FAILED";
        } else {
            outcome = "PENDING";
        }

        return new FraudWatchlistHistoryItem(
                item.getCommandId(), idNumber, fullName,
                outcome, item.getCreatedAt());
    }

    // ── Property Ownership ───────────────────────────────────────────────

    @GetMapping("/property/history")
    public ResponseEntity<PropertyHistoryResponse> getPropertyHistory(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "50") int limit) {

        var page = queryPartnerHistory(status, cursor, limit, "property");

        List<PropertyHistoryItem> items = page.items().stream()
                .filter(item -> hasAuxKey(item, "provider"))
                .map(this::mapPropertyItem)
                .toList();

        return ResponseEntity.ok(new PropertyHistoryResponse(
                items, extractCursor(page), page.hasMore()));
    }

    private PropertyHistoryItem mapPropertyItem(VerificationCommandStoreItem item) {
        Map<String, String> aux = safeAux(item);
        String query = aux.getOrDefault("query",
                aux.getOrDefault("searchQuery", ""));
        String searchType = aux.getOrDefault("searchType", "");

        int resultCount = 0;
        try {
            resultCount = Integer.parseInt(aux.getOrDefault("recordCount", "0"));
        } catch (NumberFormatException ignored) {}

        String outcome;
        if (isCompleted(item)) {
            String rawOutcome = aux.getOrDefault("outcome", "SUCCEEDED");
            if ("SUCCEEDED".equals(rawOutcome)) {
                outcome = resultCount > 0 ? "FOUND" : "NOT_FOUND";
            } else {
                outcome = "FAILED";
            }
        } else if (isFailed(item)) {
            outcome = "FAILED";
        } else {
            outcome = "PENDING";
        }

        return new PropertyHistoryItem(
                item.getCommandId(), query, searchType, resultCount,
                outcome, item.getCreatedAt());
    }

    // ── Shared helpers ───────────────────────────────────────────────────

    private PageResult<VerificationCommandStoreItem> queryPartnerHistory(
            String status, String cursor, int limit, String serviceName) {

        String partnerId = PartnerContextHolder.requirePartnerId();
        int clampedLimit = Math.max(1, Math.min(limit, 200));

        logger.debug("Listing {} history: partnerId={}, status={}, cursor={}, limit={}",
                serviceName, partnerId, status, cursor, clampedLimit);

        Map<String, AttributeValue> exclusiveStartKey = null;
        if (cursor != null && !cursor.isBlank()) {
            exclusiveStartKey = new HashMap<>();
            exclusiveStartKey.put("commandId", AttributeValue.builder().s(cursor).build());
            exclusiveStartKey.put("partnerId", AttributeValue.builder().s(partnerId).build());
        }

        return commandStatusRepository.findByPartnerId(
                partnerId, status, VERIFY_PARTY_COMMAND, clampedLimit, exclusiveStartKey);
    }

    private boolean hasAuxKey(VerificationCommandStoreItem item, String key) {
        Map<String, String> aux = item.getAuxiliaryData();
        return aux != null && aux.containsKey(key);
    }

    private Map<String, String> safeAux(VerificationCommandStoreItem item) {
        return item.getAuxiliaryData() != null ? item.getAuxiliaryData() : Map.of();
    }

    private boolean isCompleted(VerificationCommandStoreItem item) {
        return item.getStatus() == CommandStatus.COMPLETED;
    }

    private boolean isFailed(VerificationCommandStoreItem item) {
        return item.getStatus() == CommandStatus.PERMANENT_FAILURE
                || item.getStatus() == CommandStatus.INVARIANT_FAILURE;
    }

    private String mapStandardOutcome(VerificationCommandStoreItem item,
            Map<String, String> aux, String successLabel, String softFailLabel) {
        if (isCompleted(item)) {
            String rawOutcome = aux.getOrDefault("outcome", "SUCCEEDED");
            return switch (rawOutcome) {
                case "SUCCEEDED" -> successLabel;
                case "SOFT_FAIL" -> softFailLabel;
                case "HARD_FAIL" -> "FAILED";
                default -> rawOutcome;
            };
        } else if (isFailed(item)) {
            return "FAILED";
        }
        return "PENDING";
    }

    private String extractCursor(PageResult<VerificationCommandStoreItem> page) {
        if (page.hasMore()) {
            var lastKey = page.lastEvaluatedKey();
            var cursorCommandId = lastKey.get("commandId");
            return cursorCommandId != null ? cursorCommandId.s() : null;
        }
        return null;
    }

    // ── Response records ─────────────────────────────────────────────────

    public record IdentityHistoryResponse(
            List<IdentityHistoryItem> items, String cursor, boolean hasMore) {}

    public record IdentityHistoryItem(
            String verificationId, String idNumber, String fullName,
            String outcome, String verifiedAt) {}

    public record BankAccountHistoryResponse(
            List<BankAccountHistoryItem> items, String cursor, boolean hasMore) {}

    public record BankAccountHistoryItem(
            String verificationId, String accountNumber, String bank,
            String accountHolder, String outcome, String verifiedAt) {}

    public record CreditCheckHistoryResponse(
            List<CreditCheckHistoryItem> items, String cursor, boolean hasMore) {}

    public record CreditCheckHistoryItem(
            String verificationId, String idNumber, String fullName,
            String riskGrade, String outcome, String verifiedAt) {}

    public record IncomeHistoryResponse(
            List<IncomeHistoryItem> items, String cursor, boolean hasMore) {}

    public record IncomeHistoryItem(
            String verificationId, String idNumber, String fullName,
            String employer, String outcome, String verifiedAt) {}

    public record TaxComplianceHistoryResponse(
            List<TaxComplianceHistoryItem> items, String cursor, boolean hasMore) {}

    public record TaxComplianceHistoryItem(
            String verificationId, String taxNumber, String entityName,
            String outcome, String verifiedAt) {}

    public record CompanyHistoryResponse(
            List<CompanyHistoryItem> items, String cursor, boolean hasMore) {}

    public record CompanyHistoryItem(
            String verificationId, String registrationNumber, String companyName,
            String outcome, String verifiedAt) {}

    public record EmploymentHistoryResponse(
            List<EmploymentHistoryItem> items, String cursor, boolean hasMore) {}

    public record EmploymentHistoryItem(
            String verificationId, String idNumber, String fullName,
            String employer, String outcome, String verifiedAt) {}

    public record QualificationHistoryResponse(
            List<QualificationHistoryItem> items, String cursor, boolean hasMore) {}

    public record QualificationHistoryItem(
            String verificationId, String idNumber, String fullName,
            String institution, String outcome, String verifiedAt) {}

    public record NegativeNewsHistoryResponse(
            List<NegativeNewsHistoryItem> items, String cursor, boolean hasMore) {}

    public record NegativeNewsHistoryItem(
            String verificationId, String subjectName, String entityType,
            int matchCount, String outcome, String screenedAt) {}

    public record FraudWatchlistHistoryResponse(
            List<FraudWatchlistHistoryItem> items, String cursor, boolean hasMore) {}

    public record FraudWatchlistHistoryItem(
            String verificationId, String idNumber, String fullName,
            String outcome, String screenedAt) {}

    public record PropertyHistoryResponse(
            List<PropertyHistoryItem> items, String cursor, boolean hasMore) {}

    public record PropertyHistoryItem(
            String verificationId, String query, String searchType,
            int resultCount, String outcome, String searchedAt) {}
}
