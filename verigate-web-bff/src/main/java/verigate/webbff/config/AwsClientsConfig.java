package verigate.webbff.config;

import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient.Builder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import verigate.webbff.config.properties.AwsProperties;
import verigate.webbff.config.properties.CommandStoreProperties;
import verigate.webbff.config.properties.ResponsePollingProperties;

@Configuration
@EnableConfigurationProperties({
    AwsProperties.class,
    CommandStoreProperties.class,
    ResponsePollingProperties.class
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
  DynamoDbEnhancedClient dynamoDbEnhancedClient(AwsProperties properties, Region region) {
    Builder builder = DynamoDbEnhancedClient.builder();
    builder.dynamoDbClient(dynamoDbClient(properties, region));
    return builder.build();
  }

  private DynamoDbClient dynamoDbClient(AwsProperties properties, Region region) {
    var builder = DynamoDbClient.builder()
        .region(region)
        .overrideConfiguration(CLIENT_OVERRIDE);
    properties.getDynamodbEndpoint().ifPresent(builder::endpointOverride);
    return builder.build();
  }
}
