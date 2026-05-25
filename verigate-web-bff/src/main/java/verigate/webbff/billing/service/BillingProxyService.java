package verigate.webbff.billing.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import verigate.webbff.config.properties.BillingProperties;

@Service
public class BillingProxyService {

  private static final Logger logger = LoggerFactory.getLogger(BillingProxyService.class);
  private static final Duration PRESIGNED_URL_EXPIRY = Duration.ofMinutes(15);
  private static final TypeReference<List<Map<String, Object>>> LINE_ITEMS_TYPE =
      new TypeReference<>() {};

  private final DynamoDbClient dynamoDbClient;
  private final S3Presigner s3Presigner;
  private final BillingProperties billingProperties;
  private final ObjectMapper objectMapper;

  public BillingProxyService(
      DynamoDbClient dynamoDbClient,
      S3Presigner s3Presigner,
      BillingProperties billingProperties,
      ObjectMapper objectMapper) {
    this.dynamoDbClient = dynamoDbClient;
    this.s3Presigner = s3Presigner;
    this.billingProperties = billingProperties;
    this.objectMapper = objectMapper;
  }

  /**
   * Queries all invoices for a given partner, sorted by createdAt descending.
   */
  public List<InvoiceSummary> getInvoicesForPartner(String partnerId) {
    logger.debug("Querying invoices for partner: {}", partnerId);

    QueryRequest queryRequest = QueryRequest.builder()
        .tableName(billingProperties.getInvoicesTableName())
        .keyConditionExpression("partnerId = :pid")
        .expressionAttributeValues(Map.of(
            ":pid", AttributeValue.builder().s(partnerId).build()))
        .build();

    var response = dynamoDbClient.query(queryRequest);

    List<InvoiceSummary> invoices = response.items().stream()
        .map(this::toInvoiceSummary)
        .sorted(Comparator.comparing(InvoiceSummary::createdAt,
            Comparator.nullsLast(Comparator.reverseOrder())))
        .toList();

    logger.info("Found {} invoices for partner {}", invoices.size(), partnerId);
    return invoices;
  }

  /**
   * Gets a single invoice by partner ID and invoice ID, including parsed line items.
   */
  public InvoiceDetail getInvoiceDetail(String partnerId, String invoiceId) {
    logger.debug("Getting invoice detail: partnerId={}, invoiceId={}", partnerId, invoiceId);

    Map<String, AttributeValue> key = new HashMap<>();
    key.put("partnerId", AttributeValue.builder().s(partnerId).build());
    key.put("invoiceId", AttributeValue.builder().s(invoiceId).build());

    GetItemRequest getItemRequest = GetItemRequest.builder()
        .tableName(billingProperties.getInvoicesTableName())
        .key(key)
        .build();

    var response = dynamoDbClient.getItem(getItemRequest);

    if (!response.hasItem() || response.item().isEmpty()) {
      logger.warn("Invoice not found: partnerId={}, invoiceId={}", partnerId, invoiceId);
      return null;
    }

    return toInvoiceDetail(response.item());
  }

  /**
   * Generates a presigned GET URL for downloading an invoice PDF from S3.
   */
  public PdfDownloadResult generateInvoicePdfUrl(String partnerId, String invoiceId) {
    logger.debug("Generating PDF URL: partnerId={}, invoiceId={}", partnerId, invoiceId);

    InvoiceDetail invoice = getInvoiceDetail(partnerId, invoiceId);
    if (invoice == null) {
      logger.warn("Cannot generate PDF URL - invoice not found: {}", invoiceId);
      return null;
    }

    if (invoice.pdfS3Key() == null || invoice.pdfS3Key().isBlank()) {
      logger.warn("No PDF available for invoice: {}", invoiceId);
      return null;
    }

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(billingProperties.getInvoiceBucketName())
        .key(invoice.pdfS3Key())
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(PRESIGNED_URL_EXPIRY)
        .getObjectRequest(getObjectRequest)
        .build();

    String downloadUrl = s3Presigner.presignGetObject(presignRequest).url().toString();

    logger.info("Generated presigned PDF URL for invoice: {}", invoiceId);
    return new PdfDownloadResult(downloadUrl, (int) PRESIGNED_URL_EXPIRY.toSeconds());
  }

  /**
   * Voids an invoice by updating its status to VOID.
   */
  public boolean voidInvoice(String partnerId, String invoiceId) {
    logger.info("Voiding invoice: partnerId={}, invoiceId={}", partnerId, invoiceId);

    // Verify the invoice exists first
    InvoiceDetail invoice = getInvoiceDetail(partnerId, invoiceId);
    if (invoice == null) {
      logger.warn("Cannot void - invoice not found: partnerId={}, invoiceId={}",
          partnerId, invoiceId);
      return false;
    }

    Map<String, AttributeValue> key = new HashMap<>();
    key.put("partnerId", AttributeValue.builder().s(partnerId).build());
    key.put("invoiceId", AttributeValue.builder().s(invoiceId).build());

    UpdateItemRequest updateRequest = UpdateItemRequest.builder()
        .tableName(billingProperties.getInvoicesTableName())
        .key(key)
        .updateExpression("SET #st = :status, updatedAt = :updatedAt")
        .expressionAttributeNames(Map.of("#st", "status"))
        .expressionAttributeValues(Map.of(
            ":status", AttributeValue.builder().s("VOID").build(),
            ":updatedAt", AttributeValue.builder().s(Instant.now().toString()).build()))
        .build();

    dynamoDbClient.updateItem(updateRequest);
    logger.info("Invoice voided: invoiceId={}", invoiceId);
    return true;
  }

  // ── Mapping helpers ─────────────────────────────────────────────────

  private InvoiceSummary toInvoiceSummary(Map<String, AttributeValue> item) {
    return new InvoiceSummary(
        getStringAttr(item, "invoiceId"),
        getStringAttr(item, "invoiceNumber"),
        getStringAttr(item, "billingPeriod"),
        getStringAttr(item, "status"),
        getStringAttr(item, "issueDate"),
        getStringAttr(item, "dueDate"),
        getStringAttr(item, "total"),
        getStringAttr(item, "currency"),
        getStringAttr(item, "createdAt"));
  }

  private InvoiceDetail toInvoiceDetail(Map<String, AttributeValue> item) {
    List<LineItem> lineItems = parseLineItems(getStringAttr(item, "lineItemsJson"));

    return new InvoiceDetail(
        getStringAttr(item, "invoiceId"),
        getStringAttr(item, "invoiceNumber"),
        getStringAttr(item, "partnerId"),
        getStringAttr(item, "partnerName"),
        getStringAttr(item, "billingPeriod"),
        getStringAttr(item, "status"),
        getStringAttr(item, "issueDate"),
        getStringAttr(item, "dueDate"),
        lineItems,
        getStringAttr(item, "subtotal"),
        getStringAttr(item, "vatRate"),
        getStringAttr(item, "vatAmount"),
        getStringAttr(item, "total"),
        getBoolAttr(item, "monthlyMinimumApplied"),
        getStringAttr(item, "currency"),
        getStringAttr(item, "pdfS3Key"),
        getStringAttr(item, "paymentId"),
        getStringAttr(item, "createdAt"),
        getStringAttr(item, "updatedAt"),
        getStringAttr(item, "notes"));
  }

  private List<LineItem> parseLineItems(String lineItemsJson) {
    if (lineItemsJson == null || lineItemsJson.isBlank()) {
      return List.of();
    }
    try {
      List<Map<String, Object>> rawItems = objectMapper.readValue(lineItemsJson, LINE_ITEMS_TYPE);
      return rawItems.stream()
          .map(raw -> new LineItem(
              stringVal(raw, "lineItemId"),
              stringVal(raw, "verificationType"),
              stringVal(raw, "description"),
              longVal(raw, "quantity"),
              stringVal(raw, "unitPriceExVat"),
              stringVal(raw, "lineSubtotal"),
              stringVal(raw, "vatAmount"),
              stringVal(raw, "lineTotal")))
          .toList();
    } catch (Exception e) {
      logger.warn("Failed to parse lineItemsJson: {}", e.getMessage());
      return List.of();
    }
  }

  private static String getStringAttr(Map<String, AttributeValue> item, String key) {
    AttributeValue val = item.get(key);
    return val != null && val.s() != null ? val.s() : null;
  }

  private static boolean getBoolAttr(Map<String, AttributeValue> item, String key) {
    AttributeValue val = item.get(key);
    return val != null && val.bool() != null && val.bool();
  }

  private static String stringVal(Map<String, Object> map, String key) {
    Object val = map.get(key);
    return val != null ? val.toString() : null;
  }

  private static long longVal(Map<String, Object> map, String key) {
    Object val = map.get(key);
    if (val instanceof Number number) {
      return number.longValue();
    }
    if (val instanceof String s) {
      try {
        return Long.parseLong(s);
      } catch (NumberFormatException e) {
        return 0;
      }
    }
    return 0;
  }

  // ── DTOs ────────────────────────────────────────────────────────────

  public record InvoiceSummary(
      String invoiceId,
      String invoiceNumber,
      String billingPeriod,
      String status,
      String issueDate,
      String dueDate,
      String total,
      String currency,
      String createdAt) {}

  public record InvoiceDetail(
      String invoiceId,
      String invoiceNumber,
      String partnerId,
      String partnerName,
      String billingPeriod,
      String status,
      String issueDate,
      String dueDate,
      List<LineItem> lineItems,
      String subtotal,
      String vatRate,
      String vatAmount,
      String total,
      boolean monthlyMinimumApplied,
      String currency,
      String pdfS3Key,
      String paymentId,
      String createdAt,
      String updatedAt,
      String notes) {}

  public record LineItem(
      String lineItemId,
      String verificationType,
      String description,
      long quantity,
      String unitPriceExVat,
      String lineSubtotal,
      String vatAmount,
      String lineTotal) {}

  public record PdfDownloadResult(String downloadUrl, int expiresInSeconds) {}
}
