package verigate.webbff.verification.support;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import verigate.verification.cg.domain.models.VerificationType;
import verigate.webbff.config.properties.VerificationRoutingProperties;

@Component
public class VerificationQueueResolver {

  private final Map<VerificationType, String> queueMappings = new ConcurrentHashMap<>();

  public VerificationQueueResolver(VerificationRoutingProperties properties) {
    properties
        .getMappings()
        .forEach(
            (key, value) -> {
              if (StringUtils.hasText(key) && StringUtils.hasText(value)) {
                queueMappings.put(VerificationType.valueOf(key.toUpperCase(Locale.ROOT)), value);
              }
            });
  }

  public String resolve(VerificationType verificationType) {
    var queueName = queueMappings.get(verificationType);
    if (!StringUtils.hasText(queueName)) {
      throw new IllegalArgumentException("Queue mapping not configured for verification type: " + verificationType);
    }
    return queueName;
  }
}
