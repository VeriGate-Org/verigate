package verigate.webbff.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "verigate.monitoring")
public class MonitoringProperties {

  private String subjectsTableName = "verigate-monitored-subjects";
  private String alertsTableName = "verigate-monitoring-alerts";

  public String getSubjectsTableName() {
    return subjectsTableName;
  }

  public void setSubjectsTableName(String subjectsTableName) {
    this.subjectsTableName = subjectsTableName;
  }

  public String getAlertsTableName() {
    return alertsTableName;
  }

  public void setAlertsTableName(String alertsTableName) {
    this.alertsTableName = alertsTableName;
  }
}
