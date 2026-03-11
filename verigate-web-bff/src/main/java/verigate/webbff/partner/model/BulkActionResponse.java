package verigate.webbff.partner.model;

public record BulkActionResponse(
    String action,
    int processed,
    int failed,
    String message) {}
