package verigate.webbff.config.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "verigate.verification.routing")
public class VerificationRoutingProperties {

  private Map<String, String> mappings = new HashMap<>();

  public Map<String, String> getMappings() {
    return mappings;
  }

  public void setMappings(Map<String, String> mappings) {
    this.mappings = Optional.ofNullable(mappings).orElseGet(HashMap::new);
  }
}
