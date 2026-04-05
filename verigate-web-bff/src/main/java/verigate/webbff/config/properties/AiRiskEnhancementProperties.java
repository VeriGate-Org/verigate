package verigate.webbff.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "verigate.ai.risk-enhancement")
public class AiRiskEnhancementProperties {

  private String tableName = "ai-risk-enhancements";

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }
}
