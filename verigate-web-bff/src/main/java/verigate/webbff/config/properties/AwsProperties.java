package verigate.webbff.config.properties;

import java.net.URI;
import java.util.Optional;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "verigate.aws")
public class AwsProperties {

  private String region = "eu-west-1";
  private URI sqsEndpoint;
  private URI dynamodbEndpoint;

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public Optional<URI> getSqsEndpoint() {
    return Optional.ofNullable(sqsEndpoint);
  }

  public void setSqsEndpoint(URI sqsEndpoint) {
    this.sqsEndpoint = sqsEndpoint;
  }

  public Optional<URI> getDynamodbEndpoint() {
    return Optional.ofNullable(dynamodbEndpoint);
  }

  public void setDynamodbEndpoint(URI dynamodbEndpoint) {
    this.dynamodbEndpoint = dynamodbEndpoint;
  }
}
