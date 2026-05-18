package verigate.adapter.dharesponse.application.handlers;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.dharesponse.domain.models.DhaPermitVerificationOutcome;
import verigate.adapter.dharesponse.domain.models.DhaResponseVerificationResult;
import verigate.adapter.dharesponse.domain.services.DhaResponseParserService;
import verigate.adapter.dharesponse.domain.services.DhaResponseReportService;

public class DefaultProcessDhaResponseCommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(
            DefaultProcessDhaResponseCommandHandler.class);

    private static final Pattern COMMAND_ID_PATTERN =
            Pattern.compile("VeriGate Ref:\\s+([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})",
                    Pattern.CASE_INSENSITIVE);

    private final EmailFetcher emailFetcher;
    private final MimeParser mimeParser;
    private final CommandStoreAdapter commandStoreAdapter;
    private final DhaResponseParserService aiAnalyzer;
    private final DhaResponseReportService reportService;
    private final PartnerNotifier partnerNotifier;

    public DefaultProcessDhaResponseCommandHandler(
            EmailFetcher emailFetcher,
            MimeParser mimeParser,
            CommandStoreAdapter commandStoreAdapter,
            DhaResponseParserService aiAnalyzer,
            DhaResponseReportService reportService,
            PartnerNotifier partnerNotifier) {
        this.emailFetcher = emailFetcher;
        this.mimeParser = mimeParser;
        this.commandStoreAdapter = commandStoreAdapter;
        this.aiAnalyzer = aiAnalyzer;
        this.reportService = reportService;
        this.partnerNotifier = partnerNotifier;
    }

    /**
     * Processes an inbound DHA email response.
     *
     * @param bucketName S3 bucket containing the raw email
     * @param objectKey  S3 object key for the raw email
     */
    public void handle(String bucketName, String objectKey) {
        logger.info("Processing DHA email response: bucket={}, key={}", bucketName, objectKey);

        // 1. Fetch raw email from S3
        byte[] rawEmail = emailFetcher.fetch(bucketName, objectKey);

        // 2. Parse MIME to extract subject, body, headers
        ParsedEmail parsedEmail = mimeParser.parse(rawEmail);
        logger.info("Parsed email: subject='{}', from='{}'", parsedEmail.subject(), parsedEmail.from());

        // 3. Extract commandId from quoted text
        String commandId = extractCommandId(parsedEmail.body());
        if (commandId == null) {
            throw new PermanentProcessingException(
                    "Could not extract VeriGate reference (commandId) from email body");
        }
        logger.info("Extracted commandId: {}", commandId);

        // 4. Look up command in DynamoDB, verify status is PENDING
        CommandRecord command = commandStoreAdapter.findById(commandId);
        if (command == null) {
            throw new PermanentProcessingException("Command not found: " + commandId);
        }
        if (!"PENDING".equals(command.status())) {
            logger.warn("Command {} is not PENDING (status={}), skipping", commandId, command.status());
            return;
        }

        // 5. Call Bedrock for AI extraction of structured results
        Map<String, String> auxData = command.auxiliaryData() != null ? command.auxiliaryData() : Map.of();
        String permitNumber = auxData.getOrDefault("permitNumber", "");
        String permitType = auxData.getOrDefault("documentType", "");
        String nationality = auxData.getOrDefault("nationality", "");

        DhaResponseVerificationResult result;
        try {
            result = aiAnalyzer.analyzeResponse(parsedEmail.body(), permitNumber, permitType, nationality);
        } catch (Exception e) {
            logger.error("Bedrock AI analysis failed for commandId {}", commandId, e);
            throw new TransientProcessingException("AI analysis failed", e);
        }

        // 6. Map outcome to command status and update
        String commandStatus = mapOutcomeToCommandStatus(result.outcome());
        Map<String, String> updatedAuxData = new HashMap<>(auxData);
        updatedAuxData.put("dhaOutcome", result.outcome().name());
        updatedAuxData.put("dhaIsAuthentic", String.valueOf(result.isAuthentic()));
        updatedAuxData.put("dhaIsCurrentlyValid", String.valueOf(result.isCurrentlyValid()));
        updatedAuxData.put("dhaConfidenceScore", String.valueOf(result.confidenceScore()));
        updatedAuxData.put("dhaResponseReceivedAt", Instant.now().toString());
        if (result.holderName() != null) {
            updatedAuxData.put("dhaHolderName", result.holderName());
        }
        if (result.expiryDate() != null) {
            updatedAuxData.put("dhaExpiryDate", result.expiryDate());
        }
        if (result.additionalNotes() != null) {
            updatedAuxData.put("dhaAdditionalNotes", result.additionalNotes());
        }
        if (result.employerMatch() != null) {
            updatedAuxData.put("dhaEmployerMatch", result.employerMatch());
        }
        updatedAuxData.put("rawEmailS3Key", objectKey);

        // 7. Generate PDF report (best-effort)
        try {
            String reportDocumentId = reportService.generateReport(commandId, command.partnerId(), result);
            if (reportDocumentId != null) {
                updatedAuxData.put("reportDocumentId", reportDocumentId);
            }
        } catch (Exception e) {
            logger.error("PDF report generation failed for commandId {}, continuing", commandId, e);
        }

        // Update command store
        commandStoreAdapter.updateStatus(commandId, commandStatus, updatedAuxData);
        logger.info("Command {} updated: status={}, outcome={}", commandId, commandStatus, result.outcome());

        // 8. Send partner notification (fire-and-forget)
        try {
            partnerNotifier.notifyPartner(command.partnerId(), commandId, result);
        } catch (Exception e) {
            logger.error("Partner notification failed for commandId {}, continuing", commandId, e);
        }
    }

    private String extractCommandId(String body) {
        if (body == null) {
            return null;
        }
        Matcher matcher = COMMAND_ID_PATTERN.matcher(body);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String mapOutcomeToCommandStatus(DhaPermitVerificationOutcome outcome) {
        return switch (outcome) {
            case VERIFIED, PARTIAL_MATCH, EXPIRED -> "COMPLETED";
            case INVALID, NOT_FOUND -> "PERMANENT_FAILURE";
            case UNABLE_TO_VERIFY, INCONCLUSIVE -> "TRANSIENT_ERROR";
        };
    }

    // ── Port interfaces (implemented by infrastructure layer) ──

    public interface EmailFetcher {
        byte[] fetch(String bucketName, String objectKey);
    }

    public interface MimeParser {
        ParsedEmail parse(byte[] rawEmail);
    }

    public interface CommandStoreAdapter {
        CommandRecord findById(String commandId);
        void updateStatus(String commandId, String status, Map<String, String> auxiliaryData);
    }

    public interface PartnerNotifier {
        void notifyPartner(String partnerId, String commandId,
                           DhaResponseVerificationResult result);
    }

    public record ParsedEmail(String from, String subject, String body, String inReplyTo) {}
    public record CommandRecord(String commandId, String partnerId, String status,
                                 Map<String, String> auxiliaryData) {}

    public static class PermanentProcessingException extends RuntimeException {
        public PermanentProcessingException(String message) { super(message); }
    }

    public static class TransientProcessingException extends RuntimeException {
        public TransientProcessingException(String message, Throwable cause) { super(message, cause); }
    }
}
