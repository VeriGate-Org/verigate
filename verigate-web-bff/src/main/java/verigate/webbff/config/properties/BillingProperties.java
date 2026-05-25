package verigate.webbff.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "verigate.billing")
public class BillingProperties {

  private String invoicesTableName = "verigate-invoices";
  private String invoiceBucketName = "verigate-invoices";

  public String getInvoicesTableName() {
    return invoicesTableName;
  }

  public void setInvoicesTableName(String invoicesTableName) {
    this.invoicesTableName = invoicesTableName;
  }

  public String getInvoiceBucketName() {
    return invoiceBucketName;
  }

  public void setInvoiceBucketName(String invoiceBucketName) {
    this.invoiceBucketName = invoiceBucketName;
  }
}
