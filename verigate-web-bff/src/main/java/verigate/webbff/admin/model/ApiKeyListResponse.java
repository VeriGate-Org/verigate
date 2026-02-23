package verigate.webbff.admin.model;

import java.util.List;

public record ApiKeyListResponse(
    String partnerId,
    List<ApiKeyItem> keys) {

  public record ApiKeyItem(
      String keyPrefix,
      String status,
      String createdAt,
      String createdBy) {}
}
