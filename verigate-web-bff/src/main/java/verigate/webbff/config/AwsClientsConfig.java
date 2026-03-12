package verigate.webbff.config;

import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sqs.SqsClient;
import verigate.webbff.config.properties.AwsProperties;
import verigate.webbff.config.properties.CaseProperties;
import verigate.webbff.config.properties.CommandStoreProperties;
import verigate.webbff.config.properties.DocumentProperties;
import verigate.webbff.config.properties.PolicyProperties;
import verigate.webbff.config.properties.ResponsePollingProperties;
import verigate.webbff.config.properties.RiskAssessmentProperties;
import verigate.webbff.config.properties.MonitoringProperties;
import verigate.webbff.config.properties.RiskScoringConfigProperties;

@Configuration
@EnableConfigurationProperties({
    AwsProperties.class,
    CaseProperties.class,
    CommandStoreProperties.class,
    ResponsePollingProperties.class,
    RiskAssessmentProperties.class,
    PolicyProperties.class,
    RiskScoringConfigProperties.class,
    DocumentProperties.class,
    MonitoringProperties.class
})
public class AwsClientsConfig {

  private static final ClientOverrideConfiguration CLIENT_OVERRIDE =
      ClientOverrideConfiguration.builder()
          .apiCallTimeout(Duration.ofSeconds(10))
          .apiCallAttemptTimeout(Duration.ofSeconds(5))
          .retryPolicy(RetryPolicy.builder().numRetries(3).build())
          .build();

  @Bean
  Region awsRegion(AwsProperties properties) {
    return Region.of(properties.getRegion());
  }

  @Bean
  SqsClient sqsClient(AwsProperties properties, Region region) {
    var builder = SqsClient.builder()
        .region(region)
        .overrideConfiguration(CLIENT_OVERRIDE);
    properties.getSqsEndpoint().ifPresent(builder::endpointOverride);
    return builder.build();
  }

  @Bean
  DynamoDbClient dynamoDbClient(AwsProperties properties, Region region) {
    var builder = DynamoDbClient.builder()
        .region(region)
        .overrideConfiguration(CLIENT_OVERRIDE);
    properties.getDynamodbEndpoint().ifPresent(builder::endpointOverride);
    return builder.build();
  }

  @Bean
  DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
    return DynamoDbEnhancedClient.builder()
        .dynamoDbClient(dynamoDbClient)
        .build();
  }

  @Bean
  S3Client s3Client(AwsProperties properties, Region region) {
    var builder = S3Client.builder()
        .region(region)
        .overrideConfiguration(CLIENT_OVERRIDE);
    properties.getS3Endpoint().ifPresent(builder::endpointOverride);
    return builder.build();
  }

  @Bean
  S3Presigner s3Presigner(AwsProperties properties, Region region) {
    var builder = S3Presigner.builder()
        .region(region);
    properties.getS3Endpoint().ifPresent(builder::endpointOverride);
    return builder.build();
  }
}
