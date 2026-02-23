package verigate.webbff.admin.model;

import java.time.LocalDateTime;

public record ApiKeyResponse(
    String apiKey,
    String partnerId,
    String keyPrefix,
    String status,
    LocalDateTime createdAt) {}
