package verigate.webbff.verification;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.function.serverless.web.ServerlessAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;

/**
 * Integration tests for the Web-BFF verification API.
 * Uses LocalStack for SQS and DynamoDB to test the full REST flow
 * including authentication, SQS dispatch, and DynamoDB queries.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@EnableAutoConfiguration(exclude = ServerlessAutoConfiguration.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class VerificationApiIntegrationIT {

  private static final String COMMAND_STORE_TABLE = "verification-command-store";
  private static final String API_KEYS_TABLE = "verigate-api-keys";
  private static final String PARTNER_HUB_TABLE = "verigate-partner-hub";
  private static final String SQS_QUEUE_NAME = "verify-party";
  private static final String TEST_API_KEY = "test-api-key-12345";
  private static final String TEST_PARTNER_ID = "partner-integration-test";

  @Container
  static final LocalStackContainer localStack =
      new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.8.1"))
          .withServices(Service.DYNAMODB, Service.SQS);

  @Autowired private MockMvc mockMvc;

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    // Set AWS credentials as system properties so the Spring-managed DynamoDbClient
    // (which uses DefaultCredentialsProvider) can authenticate to LocalStack
    System.setProperty("aws.accessKeyId", localStack.getAccessKey());
    System.setProperty("aws.secretAccessKey", localStack.getSecretKey());

    registry.add("verigate.aws.region", localStack::getRegion);
    registry.add(
        "verigate.aws.sqs-endpoint",
        () -> localStack.getEndpointOverride(Service.SQS).toString());
    registry.add(
        "verigate.aws.dynamodb-endpoint",
        () -> localStack.getEndpointOverride(Service.DYNAMODB).toString());
    registry.add("verigate.verification.command-store.table-name", () -> COMMAND_STORE_TABLE);
    registry.add("verigate.auth.api-keys-table", () -> API_KEYS_TABLE);
    registry.add("verigate.partner-hub.table-name", () -> PARTNER_HUB_TABLE);
    registry.add("verigate.verification.queue-name", () -> SQS_QUEUE_NAME);
    registry.add("verigate.verification.response.poll-max-attempts", () -> "1");
    registry.add("verigate.verification.response.poll-timeout-ms", () -> "10");
  }

  @BeforeAll
  static void setupInfrastructure() throws Exception {
    var dynamoDb = createDynamoDbClient();
    var sqs = createSqsClient();

    createCommandStoreTable(dynamoDb);
    createApiKeysTable(dynamoDb);
    createPartnerHubTable(dynamoDb);
    seedApiKey(dynamoDb);
    createSqsQueue(sqs);

    dynamoDb.close();
    sqs.close();
  }

  @Test
  @DisplayName("POST /api/verifications with valid API key returns 202 Accepted")
  void submitVerificationWithValidApiKey() throws Exception {
    var payload =
        """
        {
          "verificationType": "IDENTITY_VERIFICATION",
          "originationType": "CLAIMS",
          "originationId": "%s",
          "requestedBy": "integration-test",
          "metadata": {"firstName": "Jane", "lastName": "Doe", "idNumber": "9001015800085"}
        }
        """
            .formatted(UUID.randomUUID());

    mockMvc
        .perform(
            post("/api/verifications")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-Key", TEST_API_KEY)
                .content(payload))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.commandId").isNotEmpty())
        .andExpect(jsonPath("$.status").value("PENDING"));
  }

  @Test
  @DisplayName("POST /api/verifications without API key returns 403 Forbidden")
  void submitVerificationWithoutApiKey() throws Exception {
    var payload =
        """
        {
          "verificationType": "IDENTITY_VERIFICATION",
          "originationType": "CLAIMS",
          "originationId": "%s",
          "requestedBy": "test",
          "metadata": {"firstName": "Jane", "lastName": "Doe", "idNumber": "9001015800085"}
        }
        """
            .formatted(UUID.randomUUID());

    mockMvc
        .perform(
            post("/api/verifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("POST /api/verifications with invalid API key returns 401 Unauthorized")
  void submitVerificationWithInvalidApiKey() throws Exception {
    var payload =
        """
        {
          "verificationType": "IDENTITY_VERIFICATION",
          "originationType": "CLAIMS",
          "originationId": "%s",
          "requestedBy": "test",
          "metadata": {"firstName": "Jane", "lastName": "Doe", "idNumber": "9001015800085"}
        }
        """
            .formatted(UUID.randomUUID());

    mockMvc
        .perform(
            post("/api/verifications")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-Key", "invalid-key-does-not-exist")
                .content(payload))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("POST /api/verifications with missing required fields returns 400 Bad Request")
  void submitVerificationWithMissingFieldsReturnsBadRequest() throws Exception {
    var payload =
        """
        {
          "verificationType": "IDENTITY_VERIFICATION"
        }
        """;

    mockMvc
        .perform(
            post("/api/verifications")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-Key", TEST_API_KEY)
                .content(payload))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("GET /api/verifications/{commandId} returns 404 for unknown command")
  void getVerificationNotFound() throws Exception {
    mockMvc
        .perform(
            get("/api/verifications/" + UUID.randomUUID())
                .header("X-API-Key", TEST_API_KEY))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("GET /api/verifications lists verifications for authenticated partner")
  void listVerificationsForPartner() throws Exception {
    mockMvc
        .perform(
            get("/api/verifications")
                .header("X-API-Key", TEST_API_KEY))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items").isArray())
        .andExpect(jsonPath("$.hasMore").isBoolean());
  }

  @Test
  @DisplayName("GET /actuator/health is publicly accessible without auth")
  void healthEndpointIsPublic() throws Exception {
    mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
  }

  // --- infrastructure setup helpers ---

  private static DynamoDbClient createDynamoDbClient() {
    return DynamoDbClient.builder()
        .endpointOverride(localStack.getEndpointOverride(Service.DYNAMODB))
        .region(Region.of(localStack.getRegion()))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                    localStack.getAccessKey(), localStack.getSecretKey())))
        .build();
  }

  private static SqsClient createSqsClient() {
    return SqsClient.builder()
        .endpointOverride(localStack.getEndpointOverride(Service.SQS))
        .region(Region.of(localStack.getRegion()))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                    localStack.getAccessKey(), localStack.getSecretKey())))
        .build();
  }

  private static void createCommandStoreTable(DynamoDbClient dynamoDb) {
    dynamoDb.createTable(
        CreateTableRequest.builder()
            .tableName(COMMAND_STORE_TABLE)
            .attributeDefinitions(
                AttributeDefinition.builder()
                    .attributeName("commandId")
                    .attributeType(ScalarAttributeType.S)
                    .build(),
                AttributeDefinition.builder()
                    .attributeName("partnerId")
                    .attributeType(ScalarAttributeType.S)
                    .build(),
                AttributeDefinition.builder()
                    .attributeName("statusCreatedAt")
                    .attributeType(ScalarAttributeType.S)
                    .build())
            .keySchema(
                KeySchemaElement.builder()
                    .attributeName("commandId")
                    .keyType(KeyType.HASH)
                    .build())
            .globalSecondaryIndexes(
                GlobalSecondaryIndex.builder()
                    .indexName("partner-index")
                    .keySchema(
                        KeySchemaElement.builder()
                            .attributeName("partnerId")
                            .keyType(KeyType.HASH)
                            .build(),
                        KeySchemaElement.builder()
                            .attributeName("statusCreatedAt")
                            .keyType(KeyType.RANGE)
                            .build())
                    .projection(
                        Projection.builder().projectionType(ProjectionType.ALL).build())
                    .provisionedThroughput(
                        ProvisionedThroughput.builder()
                            .readCapacityUnits(5L)
                            .writeCapacityUnits(5L)
                            .build())
                    .build())
            .provisionedThroughput(
                ProvisionedThroughput.builder()
                    .readCapacityUnits(5L)
                    .writeCapacityUnits(5L)
                    .build())
            .build());
  }

  private static void createApiKeysTable(DynamoDbClient dynamoDb) {
    dynamoDb.createTable(
        CreateTableRequest.builder()
            .tableName(API_KEYS_TABLE)
            .attributeDefinitions(
                AttributeDefinition.builder()
                    .attributeName("lookupHash")
                    .attributeType(ScalarAttributeType.S)
                    .build())
            .keySchema(
                KeySchemaElement.builder()
                    .attributeName("lookupHash")
                    .keyType(KeyType.HASH)
                    .build())
            .provisionedThroughput(
                ProvisionedThroughput.builder()
                    .readCapacityUnits(5L)
                    .writeCapacityUnits(5L)
                    .build())
            .build());
  }

  private static void createPartnerHubTable(DynamoDbClient dynamoDb) {
    var throughput =
        ProvisionedThroughput.builder().readCapacityUnits(5L).writeCapacityUnits(5L).build();
    dynamoDb.createTable(
        CreateTableRequest.builder()
            .tableName(PARTNER_HUB_TABLE)
            .attributeDefinitions(
                AttributeDefinition.builder()
                    .attributeName("partnerId")
                    .attributeType(ScalarAttributeType.S)
                    .build(),
                AttributeDefinition.builder()
                    .attributeName("entityType")
                    .attributeType(ScalarAttributeType.S)
                    .build(),
                AttributeDefinition.builder()
                    .attributeName("partnerStatus")
                    .attributeType(ScalarAttributeType.S)
                    .build(),
                AttributeDefinition.builder()
                    .attributeName("partnerPolicyId")
                    .attributeType(ScalarAttributeType.S)
                    .build(),
                AttributeDefinition.builder()
                    .attributeName("slug")
                    .attributeType(ScalarAttributeType.S)
                    .build())
            .keySchema(
                KeySchemaElement.builder()
                    .attributeName("partnerId")
                    .keyType(KeyType.HASH)
                    .build(),
                KeySchemaElement.builder()
                    .attributeName("entityType")
                    .keyType(KeyType.RANGE)
                    .build())
            .globalSecondaryIndexes(
                GlobalSecondaryIndex.builder()
                    .indexName("status-index")
                    .keySchema(
                        KeySchemaElement.builder()
                            .attributeName("partnerStatus")
                            .keyType(KeyType.HASH)
                            .build())
                    .projection(
                        Projection.builder().projectionType(ProjectionType.ALL).build())
                    .provisionedThroughput(throughput)
                    .build(),
                GlobalSecondaryIndex.builder()
                    .indexName("policy-id-index")
                    .keySchema(
                        KeySchemaElement.builder()
                            .attributeName("partnerPolicyId")
                            .keyType(KeyType.HASH)
                            .build())
                    .projection(
                        Projection.builder().projectionType(ProjectionType.ALL).build())
                    .provisionedThroughput(throughput)
                    .build(),
                GlobalSecondaryIndex.builder()
                    .indexName("slug-index")
                    .keySchema(
                        KeySchemaElement.builder()
                            .attributeName("slug")
                            .keyType(KeyType.HASH)
                            .build())
                    .projection(
                        Projection.builder().projectionType(ProjectionType.ALL).build())
                    .provisionedThroughput(throughput)
                    .build())
            .provisionedThroughput(throughput)
            .build());
  }

  private static void seedApiKey(DynamoDbClient dynamoDb) throws Exception {
    // Compute unsalted lookup hash (matches DynamoDbApiKeyResolver.hashApiKeyForLookup)
    String lookupHash = hashApiKey(TEST_API_KEY);

    // Generate salt and compute salted verification hash
    byte[] saltBytes = new byte[16];
    new SecureRandom().nextBytes(saltBytes);
    String salt = Base64.getUrlEncoder().withoutPadding().encodeToString(saltBytes);
    String verificationHash = hashApiKeyWithSalt(TEST_API_KEY, saltBytes);

    // Seed API key record
    dynamoDb.putItem(
        PutItemRequest.builder()
            .tableName(API_KEYS_TABLE)
            .item(
                Map.of(
                    "lookupHash", AttributeValue.builder().s(lookupHash).build(),
                    "salt", AttributeValue.builder().s(salt).build(),
                    "verificationHash", AttributeValue.builder().s(verificationHash).build(),
                    "partnerId", AttributeValue.builder().s(TEST_PARTNER_ID).build(),
                    "status", AttributeValue.builder().s("ACTIVE").build()))
            .build());

    // Seed partner record in hub table
    dynamoDb.putItem(
        PutItemRequest.builder()
            .tableName(PARTNER_HUB_TABLE)
            .item(
                Map.of(
                    "partnerId", AttributeValue.builder().s(TEST_PARTNER_ID).build(),
                    "entityType", AttributeValue.builder().s("METADATA").build(),
                    "partnerStatus", AttributeValue.builder().s("ACTIVE").build()))
            .build());
  }

  private static void createSqsQueue(SqsClient sqs) {
    sqs.createQueue(CreateQueueRequest.builder().queueName(SQS_QUEUE_NAME).build());
  }

  private static String hashApiKey(String apiKey) throws Exception {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(apiKey.getBytes(StandardCharsets.UTF_8));
    return bytesToHex(hash);
  }

  private static String hashApiKeyWithSalt(String apiKey, byte[] saltBytes) throws Exception {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] apiKeyBytes = apiKey.getBytes(StandardCharsets.UTF_8);
    byte[] combined = new byte[saltBytes.length + apiKeyBytes.length];
    System.arraycopy(saltBytes, 0, combined, 0, saltBytes.length);
    System.arraycopy(apiKeyBytes, 0, combined, saltBytes.length, apiKeyBytes.length);
    byte[] hash = digest.digest(combined);
    return bytesToHex(hash);
  }

  private static String bytesToHex(byte[] bytes) {
    StringBuilder hexString = new StringBuilder();
    for (byte b : bytes) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }
}
