package verigate.webbff.verification.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import verigate.webbff.auth.PartnerContextHolder;
import verigate.webbff.config.properties.DocumentProperties;
import verigate.webbff.verification.model.OriginationType;
import verigate.webbff.verification.model.VerificationRequest;
import verigate.webbff.verification.model.VerificationType;
import verigate.webbff.verification.repository.CommandStatusRepository;
import verigate.webbff.verification.service.DhaPermitNotificationService;
import verigate.webbff.verification.service.VerificationService;

@RestController
@RequestMapping("/api/partner/documents")
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
    private static final Duration PRESIGNED_URL_EXPIRY = Duration.ofMinutes(15);

    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {};

    private final S3Presigner s3Presigner;
    private final DocumentProperties documentProperties;
    private final CommandStatusRepository commandStatusRepository;
    private final ObjectMapper objectMapper;
    private final VerificationService verificationService;
    private final DhaPermitNotificationService dhaPermitNotificationService;

    public DocumentController(
            S3Presigner s3Presigner,
            DocumentProperties documentProperties,
            CommandStatusRepository commandStatusRepository,
            ObjectMapper objectMapper,
            VerificationService verificationService,
            DhaPermitNotificationService dhaPermitNotificationService) {
        this.s3Presigner = s3Presigner;
        this.documentProperties = documentProperties;
        this.commandStatusRepository = commandStatusRepository;
        this.objectMapper = objectMapper;
        this.verificationService = verificationService;
        this.dhaPermitNotificationService = dhaPermitNotificationService;
    }

    @PostMapping("/presigned-url")
    public ResponseEntity<PresignedUrlResponse> generatePresignedUrl(
            @RequestBody PresignedUrlRequest request) {
        String partnerId = PartnerContextHolder.requirePartnerId();
        String s3ObjectKey = partnerId + "/" + request.documentType() + "/" + UUID.randomUUID() + "/" + request.fileName();
        String bucketName = documentProperties.getS3BucketName();

        logger.info("Generating presigned PUT URL: partnerId={}, documentType={}, fileName={}",
                partnerId, request.documentType(), request.fileName());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3ObjectKey)
                .contentType(request.contentType())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(PRESIGNED_URL_EXPIRY)
                .putObjectRequest(putObjectRequest)
                .build();

        String uploadUrl = s3Presigner.presignPutObject(presignRequest).url().toString();

        logger.info("Presigned URL generated: s3ObjectKey={}", s3ObjectKey);

        return ResponseEntity.ok(new PresignedUrlResponse(uploadUrl, bucketName, s3ObjectKey));
    }

    @GetMapping("/verifications/{commandId}")
    public ResponseEntity<List<DocumentLink>> getVerificationDocuments(
            @PathVariable UUID commandId) {
        String partnerId = PartnerContextHolder.requirePartnerId();

        var command = commandStatusRepository.findById(commandId).orElse(null);
        if (command == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, String> auxiliaryData = command.getAuxiliaryData();
        if (auxiliaryData == null || !auxiliaryData.containsKey("documentS3Keys")) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<String> s3Keys;
        try {
            s3Keys = objectMapper.readValue(auxiliaryData.get("documentS3Keys"), STRING_LIST_TYPE);
        } catch (JsonProcessingException e) {
            logger.warn("Failed to parse documentS3Keys for command {}", commandId, e);
            return ResponseEntity.ok(Collections.emptyList());
        }

        String bucketName = documentProperties.getS3BucketName();
        List<DocumentLink> links = s3Keys.stream()
                .filter(key -> key.startsWith(partnerId + "/"))
                .map(key -> {
                    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build();
                    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                            .signatureDuration(PRESIGNED_URL_EXPIRY)
                            .getObjectRequest(getObjectRequest)
                            .build();
                    String downloadUrl = s3Presigner.presignGetObject(presignRequest).url().toString();
                    return new DocumentLink(key, downloadUrl);
                })
                .toList();

        return ResponseEntity.ok(links);
    }

    @PostMapping("/dha-permit-submission")
    public ResponseEntity<DhaPermitSubmissionResponse> submitDhaPermitVerification(
            @RequestBody DhaPermitSubmissionRequest request) {
        String partnerId = PartnerContextHolder.requirePartnerId();

        logger.info("DHA permit submission: partnerId={}, documentType={}, permitNumber={}",
                partnerId, request.documentType(), request.permitNumber());

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("documentType", request.documentType());
        metadata.put("permitNumber", request.permitNumber());
        metadata.put("nationality", request.nationality());
        if (request.employerName() != null) {
            metadata.put("employerName", request.employerName());
        }
        metadata.put("s3ObjectKeys", request.s3ObjectKeys());
        metadata.put("s3BucketName", request.s3BucketName());

        VerificationRequest verificationRequest = new VerificationRequest(
                VerificationType.DOCUMENT_VERIFICATION,
                OriginationType.ADHOC,
                UUID.randomUUID(),
                partnerId,
                metadata,
                null,
                request.s3ObjectKeys());

        var response = verificationService.submitVerification(verificationRequest);
        UUID commandId = response.commandId();

        boolean emailSent = dhaPermitNotificationService.sendNotification(
                request.documentType(),
                partnerId,
                request.permitNumber(),
                request.nationality(),
                request.employerName(),
                request.s3ObjectKeys(),
                request.s3BucketName(),
                commandId);

        logger.info("DHA permit submission complete: documentType={}, commandId={}, emailSent={}",
                request.documentType(), commandId, emailSent);

        return ResponseEntity.ok(new DhaPermitSubmissionResponse(commandId, "PENDING", emailSent));
    }

    @GetMapping("/history")
    public ResponseEntity<DocumentHistoryResponse> getDocumentHistory(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "50") int limit) {

        String partnerId = PartnerContextHolder.requirePartnerId();
        int clampedLimit = Math.max(1, Math.min(limit, 200));

        logger.debug("Listing document history: partnerId={}, status={}, cursor={}, limit={}",
                partnerId, status, cursor, clampedLimit);

        Map<String, AttributeValue> exclusiveStartKey = null;
        if (cursor != null && !cursor.isBlank()) {
            exclusiveStartKey = new HashMap<>();
            exclusiveStartKey.put("commandId", AttributeValue.builder().s(cursor).build());
            exclusiveStartKey.put("partnerId", AttributeValue.builder().s(partnerId).build());
        }

        var page = commandStatusRepository.findByPartnerId(
                partnerId, status, "VerifyPartyCommand", clampedLimit, exclusiveStartKey);

        List<DocumentHistoryItem> items = page.items().stream()
                .map(this::mapToHistoryItem)
                .toList();

        String nextCursor = null;
        if (page.hasMore()) {
            var lastKey = page.lastEvaluatedKey();
            var cursorCommandId = lastKey.get("commandId");
            nextCursor = cursorCommandId != null ? cursorCommandId.s() : null;
        }

        return ResponseEntity.ok(new DocumentHistoryResponse(items, nextCursor, page.hasMore()));
    }

    private DocumentHistoryItem mapToHistoryItem(
            verigate.webbff.verification.repository.model.VerificationCommandStoreItem item) {
        Map<String, String> aux = item.getAuxiliaryData() != null
                ? item.getAuxiliaryData() : Map.of();

        String documentType = aux.getOrDefault("documentType", "unknown");
        String documentTypeLabel = DOCUMENT_TYPE_LABEL_MAP.getOrDefault(documentType, documentType);
        String documentNumber = aux.getOrDefault("documentNumber", "");

        String outcome;
        if (item.getStatus() != null) {
            switch (item.getStatus()) {
                case COMPLETED -> {
                    String rawOutcome = aux.getOrDefault("outcome", "SUCCEEDED");
                    outcome = switch (rawOutcome) {
                        case "SUCCEEDED" -> "VERIFIED";
                        case "SOFT_FAIL" -> "NOT_VERIFIED";
                        case "HARD_FAIL" -> "FAILED";
                        default -> rawOutcome;
                    };
                }
                case PERMANENT_FAILURE, INVARIANT_FAILURE -> outcome = "FAILED";
                default -> outcome = "PENDING";
            }
        } else {
            outcome = "PENDING";
        }

        double overallConfidence;
        try {
            overallConfidence = Double.parseDouble(aux.getOrDefault("confidenceScore", "0.0"));
        } catch (NumberFormatException e) {
            overallConfidence = 0.0;
        }

        return new DocumentHistoryItem(
                item.getCommandId(),
                documentType,
                documentTypeLabel,
                documentNumber,
                outcome,
                overallConfidence,
                item.getCreatedAt());
    }

    private static final Map<String, String> DOCUMENT_TYPE_LABEL_MAP = Map.ofEntries(
            Map.entry("id_card", "SA ID Card"),
            Map.entry("ID_CARD", "SA ID Card"),
            Map.entry("passport", "Passport"),
            Map.entry("PASSPORT", "Passport"),
            Map.entry("drivers_license", "Driver's License"),
            Map.entry("DRIVERS_LICENSE", "Driver's License"),
            Map.entry("asylum_seeker_permit", "Asylum Seeker Permit"),
            Map.entry("ASYLUM_SEEKER_PERMIT", "Asylum Seeker Permit"),
            Map.entry("general_work_permit", "General Work Permit"),
            Map.entry("GENERAL_WORK_PERMIT", "General Work Permit"),
            Map.entry("critical_skills_visa", "Critical Skills Visa"),
            Map.entry("CRITICAL_SKILLS_VISA", "Critical Skills Visa"),
            Map.entry("corporate_visa", "Corporate Visa"),
            Map.entry("CORPORATE_VISA", "Corporate Visa"),
            Map.entry("b_bbee_certificate", "B-BBEE Certificate"),
            Map.entry("B_BBEE_CERTIFICATE", "B-BBEE Certificate"),
            Map.entry("cipc_registration", "CIPC Registration"),
            Map.entry("CIPC_REGISTRATION", "CIPC Registration"),
            Map.entry("tax_certificate", "Tax Clearance Certificate"),
            Map.entry("TAX_CERTIFICATE", "Tax Clearance Certificate"),
            Map.entry("financial_statement", "Financial Statement"),
            Map.entry("FINANCIAL_STATEMENT", "Financial Statement"),
            Map.entry("utility_bill", "Utility Bill"),
            Map.entry("UTILITY_BILL", "Utility Bill"));

    public record PresignedUrlRequest(String fileName, String contentType, String documentType) {}

    public record PresignedUrlResponse(String uploadUrl, String s3BucketName, String s3ObjectKey) {}

    public record DocumentLink(String s3Key, String downloadUrl) {}

    public record DhaPermitSubmissionRequest(
            String documentType,
            String permitNumber,
            String nationality,
            String employerName,
            List<String> s3ObjectKeys,
            String s3BucketName) {}

    public record DhaPermitSubmissionResponse(UUID commandId, String status, boolean emailSent) {}

    public record DocumentHistoryResponse(
            List<DocumentHistoryItem> items, String cursor, boolean hasMore) {}

    public record DocumentHistoryItem(
            String verificationId,
            String documentType,
            String documentTypeLabel,
            String documentNumber,
            String outcome,
            double overallConfidence,
            String verifiedAt) {}
}
