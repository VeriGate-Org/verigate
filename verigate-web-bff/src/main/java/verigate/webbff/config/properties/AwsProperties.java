package verigate.webbff.config.properties;

import java.net.URI;
import java.util.Optional;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "verigate.aws")
public class AwsProperties {

  private String region = "eu-west-1";
  private URI sqsEndpoint;
  private URI dynamodbEndpoint;
  private URI s3Endpoint;
  private URI sesEndpoint;

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

  public Optional<URI> getS3Endpoint() {
    return Optional.ofNullable(s3Endpoint);
  }

  public void setS3Endpoint(URI s3Endpoint) {
    this.s3Endpoint = s3Endpoint;
  }

  public Optional<URI> getSesEndpoint() {
    return Optional.ofNullable(sesEndpoint);
  }

  public void setSesEndpoint(URI sesEndpoint) {
    this.sesEndpoint = sesEndpoint;
  }
}
