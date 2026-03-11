package verigate.webbff.verification.model;

import java.util.List;

public record VerificationListResponse(
    List<VerificationListItem> items,
    String cursor,
    boolean hasMore) {}
