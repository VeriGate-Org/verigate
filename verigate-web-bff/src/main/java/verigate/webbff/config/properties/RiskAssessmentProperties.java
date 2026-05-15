package verigate.webbff.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "verigate.risk.assessment")
public class RiskAssessmentProperties {

  private String tableName = "verigate-risk-assessments";

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }
}
