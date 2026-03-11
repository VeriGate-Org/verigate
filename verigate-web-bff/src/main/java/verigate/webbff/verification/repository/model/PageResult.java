package verigate.webbff.verification.repository.model;

import java.util.List;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public record PageResult<T>(
    List<T> items,
    Map<String, AttributeValue> lastEvaluatedKey) {

  public boolean hasMore() {
    return lastEvaluatedKey != null && !lastEvaluatedKey.isEmpty();
  }
}
