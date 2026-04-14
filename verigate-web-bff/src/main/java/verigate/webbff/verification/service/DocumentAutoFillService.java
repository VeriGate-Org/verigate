package verigate.webbff.verification.service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import verigate.webbff.verification.repository.CommandStatusRepository;
import verigate.webbff.verification.repository.model.VerificationCommandStoreItem;

/**
 * Service for Document AI auto-fill. Aggregates extracted fields from multiple document
 * verifications into a unified form, mapping fields to their source documents with
 * per-field confidence scoring.
 */
@Service
public class DocumentAutoFillService {

  private static final Logger logger = LoggerFactory.getLogger(DocumentAutoFillService.class);

  private final CommandStatusRepository commandStatusRepository;

  public DocumentAutoFillService(CommandStatusRepository commandStatusRepository) {
    this.commandStatusRepository = commandStatusRepository;
  }

  /**
   * Aggregates extracted fields from multiple verification results into a unified form.
   *
   * @param commandIds list of verification command IDs to aggregate
   * @return auto-fill result with per-field values, sources, and confidence
   */
  public AutoFillResult aggregateFields(List<UUID> commandIds) {
    Map<String, AutoFilledField> fields = new LinkedHashMap<>();
    int totalDocuments = 0;
    int fieldsAutoFilled = 0;
    int fieldsNeedReview = 0;

    for (UUID commandId : commandIds) {
      Optional<VerificationCommandStoreItem> optItem = commandStatusRepository.findById(commandId);
      if (optItem.isEmpty()) {
        logger.warn("Command not found for auto-fill: {}", commandId);
        continue;
      }

      VerificationCommandStoreItem item = optItem.get();
      Map<String, String> aux = item.getAuxiliaryData();
      if (aux == null) continue;

      String documentType = aux.getOrDefault("documentType", "UNKNOWN");
      totalDocuments++;

      mapFieldsFromDocument(fields, aux, documentType);
    }

    for (AutoFilledField field : fields.values()) {
      if (field.confidence >= 0.80) {
        fieldsAutoFilled++;
      } else {
        fieldsNeedReview++;
      }
    }

    return new AutoFillResult(
        UUID.randomUUID().toString(),
        fields,
        totalDocuments,
        fieldsAutoFilled,
        fieldsNeedReview,
        Instant.now().toString()
    );
  }

  private void mapFieldsFromDocument(
      Map<String, AutoFilledField> fields,
      Map<String, String> aux,
      String documentType) {

    switch (documentType.toUpperCase()) {
      case "IDENTITY_DOCUMENT", "PASSPORT", "DRIVERS_LICENSE", "ID_CARD" -> {
        addField(fields, "fullName", aux.get("fullName"), documentType, aux);
        addField(fields, "idNumber", aux.get("idNumber"), documentType, aux);
        addField(fields, "dateOfBirth", aux.get("dateOfBirth"), documentType, aux);
        addField(fields, "gender", aux.get("gender"), documentType, aux);
        addField(fields, "nationality", aux.get("nationality"), documentType, aux);
      }
      case "CIPC_REGISTRATION" -> {
        addField(fields, "companyName", aux.get("companyName"), documentType, aux);
        addField(fields, "registrationNumber", aux.get("registrationNumber"), documentType, aux);
        addField(fields, "companyStatus", aux.get("companyStatus"), documentType, aux);
        addField(fields, "directors", aux.get("directors"), documentType, aux);
      }
      case "TAX_CERTIFICATE" -> {
        addField(fields, "taxNumber", aux.get("taxNumber"), documentType, aux);
        addField(fields, "taxCompliant", aux.get("complianceStatus"), documentType, aux);
      }
      case "B_BBEE_CERTIFICATE" -> {
        addField(fields, "bbeeLevel", aux.get("bbeeLevel"), documentType, aux);
        addField(fields, "bbbeeStatus", aux.get("bbbeeStatus"), documentType, aux);
      }
      case "FINANCIAL_STATEMENT" -> {
        addField(fields, "annualRevenue", aux.get("annualRevenue"), documentType, aux);
        addField(fields, "financialYear", aux.get("financialYear"), documentType, aux);
      }
      case "UTILITY_BILL" -> {
        addField(fields, "businessAddress", aux.get("address"), documentType, aux);
      }
      default -> logger.debug("No field mapping for document type: {}", documentType);
    }
  }

  private void addField(
      Map<String, AutoFilledField> fields,
      String fieldName,
      String value,
      String sourceDocument,
      Map<String, String> aux) {

    if (value == null || value.isBlank() || "null".equals(value)) return;

    double confidence = 0.0;
    String confidenceKey = fieldName + "Confidence";
    String confidenceStr = aux.getOrDefault(confidenceKey,
        aux.getOrDefault("overallConfidence", "0.9"));
    try {
      confidence = Double.parseDouble(confidenceStr);
    } catch (NumberFormatException e) {
      confidence = 0.9;
    }

    boolean needsReview = confidence < 0.80;

    // Only overwrite if the new value has higher confidence
    AutoFilledField existing = fields.get(fieldName);
    if (existing == null || confidence > existing.confidence) {
      fields.put(fieldName, new AutoFilledField(value, sourceDocument, confidence, needsReview));
    }
  }

  public record AutoFilledField(
      String value,
      String sourceDocument,
      double confidence,
      boolean needsReview
  ) {}

  public record AutoFillResult(
      String sessionId,
      Map<String, AutoFilledField> fields,
      int totalDocuments,
      int fieldsAutoFilled,
      int fieldsNeedReview,
      String createdAt
  ) {}
}
