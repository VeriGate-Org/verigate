/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.dynamodb;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import domain.exceptions.PermanentException;
import infrastructure.constants.TestConstants;
import infrastructure.dynamodb.model.AccountEvent;
import infrastructure.functions.lambda.serializers.http.DefaultJsonSerializer;
import java.util.UUID;
import java.util.function.Function;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

@Testcontainers
public final class DynamoDBReorderMessageQueueTestIT {

  private static final String TABLE_NAME = "ReorderMessageQueue";

  @Container
  private static final LocalStackContainer localStack =
      new LocalStackContainer(TestConstants.getLocalstackDefaultDockerImage())
          .withServices(Service.DYNAMODB);

  private static DynamoDbClient dynamoDbClient;
  private static DynamoDBReorderMessageQueue<AccountEvent> messageQueue;

  @BeforeEach
  public void setUp() {
    dynamoDbClient =
        DynamoDbClient.builder()
            .endpointOverride(localStack.getEndpointOverride(LocalStackContainer.Service.DYNAMODB))
            .region((Region.of(localStack.getRegion())))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        localStack.getAccessKey(), localStack.getSecretKey())))
            .build();

    createTable();

    var serializer = DefaultJsonSerializer.withTyping();
    serializer.registerClassType("AccountEvent", AccountEvent.class);

    Function<AccountEvent, String> getCausalityKey = AccountEvent::accountNumber;
    Function<AccountEvent, Long> getMessageLogicalClock = AccountEvent::logicalClock;

    messageQueue =
        new DynamoDBReorderMessageQueue<>(
            dynamoDbClient,
            TABLE_NAME,
            serializer,
            "1234567890",
            getCausalityKey,
            getMessageLogicalClock,
            "TestLogger");
  }

  @AfterEach
  public void cleanup() {
    deleteTable();
    dynamoDbClient.close();
  }

  private static void createTable() {
    CreateTableRequest request =
        CreateTableRequest.builder()
            .tableName(TABLE_NAME)
            .attributeDefinitions(
                AttributeDefinition.builder()
                    .attributeName("CausalityKey")
                    .attributeType(ScalarAttributeType.S)
                    .build(),
                AttributeDefinition.builder()
                    .attributeName("LogicalClock")
                    .attributeType(ScalarAttributeType.N)
                    .build())
            .keySchema(
                KeySchemaElement.builder()
                    .attributeName("CausalityKey")
                    .keyType(KeyType.HASH)
                    .build(),
                KeySchemaElement.builder()
                    .attributeName("LogicalClock")
                    .keyType(KeyType.RANGE)
                    .build())
            .provisionedThroughput(
                ProvisionedThroughput.builder()
                    .readCapacityUnits(5L)
                    .writeCapacityUnits(5L)
                    .build())
            .build();

    dynamoDbClient.createTable(request);
  }

  private static void deleteTable() {
    DeleteTableRequest request = DeleteTableRequest.builder().tableName(TABLE_NAME).build();
    dynamoDbClient.deleteTable(request);
  }

  @Test
  public void testEnqueueAndPeek() {
    AccountEvent accountEvent =
        new AccountEvent(UUID.randomUUID(), "1234567890", "Deposit", 1000, 1L);

    messageQueue.enqueue(accountEvent);
    var peekedMessage = messageQueue.peek();

    assertNotNull(peekedMessage);
    assertEquals(accountEvent, peekedMessage);
  }

  @Test
  public void testDequeue() {
    AccountEvent accountEvent =
        new AccountEvent(UUID.randomUUID(), "1234567890", "Deposit", 1000, 1L);

    messageQueue.enqueue(accountEvent);
    messageQueue.dequeue(accountEvent);
    var peekedMessage = messageQueue.peek();

    assertNull(peekedMessage);
  }

  @Test
  public void testReordering() {
    AccountEvent accountEvent1 =
        new AccountEvent(UUID.randomUUID(), "1234567890", "Deposit", 1000, 1L);
    messageQueue.enqueue(accountEvent1);

    AccountEvent accountEvent3 =
        new AccountEvent(UUID.randomUUID(), "1234567890", "Deposit", 300, 3L);
    messageQueue.enqueue(accountEvent3);

    AccountEvent accountEvent2 =
        new AccountEvent(UUID.randomUUID(), "1234567890", "Withdrawal", 500, 2L);
    messageQueue.enqueue(accountEvent2);

    AccountEvent accountEvent4 =
        new AccountEvent(UUID.randomUUID(), "1234567890", "Deposit", 300, 4L);
    messageQueue.enqueue(accountEvent4);

    {
      var accountEvent = messageQueue.peek();
      messageQueue.dequeue(accountEvent);
      assertNotNull(accountEvent);
      assertEquals(accountEvent1, accountEvent);
    }

    {
      var accountEvent = messageQueue.peek();
      messageQueue.dequeue(accountEvent);
      assertNotNull(accountEvent);
      assertEquals(accountEvent2, accountEvent);
    }

    {
      var accountEvent = messageQueue.peek();
      messageQueue.dequeue(accountEvent);
      assertNotNull(accountEvent);
      assertEquals(accountEvent3, accountEvent);
    }

    {
      var accountEvent = messageQueue.peek();
      messageQueue.dequeue(accountEvent);
      assertNotNull(accountEvent);
      assertEquals(accountEvent4, accountEvent);
    }

    {
      var accountEvent = messageQueue.peek();
      assertNull(accountEvent);
    }
  }

  @Test
  public void testIncorrectCausalityKeyForEnqueue() {
    AccountEvent accountEvent =
        new AccountEvent(UUID.randomUUID(), "9234567891", "Deposit", 1000, 1L);

    assertThrows(PermanentException.class, () -> messageQueue.enqueue(accountEvent));
  }

  @Test
  public void testIncorrectCausalityKeyForDequeue() {
    AccountEvent accountEvent =
        new AccountEvent(UUID.randomUUID(), "9234567891", "Deposit", 1000, 1L);

    assertThrows(PermanentException.class, () -> messageQueue.dequeue(accountEvent));
  }

  @Test
  public void testDequeueWithEmptyQueue() {
    AccountEvent accountEvent =
        new AccountEvent(UUID.randomUUID(), "1234567890", "Deposit", 1000, 1L);

    assertDoesNotThrow(() -> messageQueue.dequeue(accountEvent));
  }

  @Test
  public void testPeekWithEmptyQueue() {
    assertDoesNotThrow(() -> messageQueue.peek());

    var peekedMessage = messageQueue.peek();
    assertNull(peekedMessage);
  }

  @Test
  public void testEnqueueWithDuplicateMessages() {
    AccountEvent accountEvent =
        new AccountEvent(UUID.randomUUID(), "1234567890", "Deposit", 1000, 1L);

    messageQueue.enqueue(accountEvent);
    assertDoesNotThrow(() -> messageQueue.enqueue(accountEvent));
  }

  @Test
  public void testReorderingWithDifferentCausalityKeysInTheTable() {
    final String causalityKey1 = "1234567890";
    final String causalityKey2 = "2234567890";
    final String causalityKey3 = "3234567890";

    Function<AccountEvent, String> getCausalityKey = AccountEvent::accountNumber;
    Function<AccountEvent, Long> getMessageLogicalClock = AccountEvent::logicalClock;

    var serializer = DefaultJsonSerializer.withTyping();
    serializer.registerClassType("AccountEvent", AccountEvent.class);

    var messageQueue2 =
        new DynamoDBReorderMessageQueue<>(
            dynamoDbClient,
            TABLE_NAME,
            serializer,
            causalityKey2,
            getCausalityKey,
            getMessageLogicalClock,
            "TestLogger");

    var messageQueue3 =
        new DynamoDBReorderMessageQueue<>(
            dynamoDbClient,
            TABLE_NAME,
            serializer,
            causalityKey3,
            getCausalityKey,
            getMessageLogicalClock,
            "TestLogger");

    messageQueue2.enqueue(new AccountEvent(UUID.randomUUID(), causalityKey2, "Deposit", 1000, 1L));

    AccountEvent accountEvent1 =
        new AccountEvent(UUID.randomUUID(), causalityKey1, "Deposit", 1000, 1L);
    messageQueue.enqueue(accountEvent1);

    messageQueue2.enqueue(new AccountEvent(UUID.randomUUID(), causalityKey2, "Deposit", 1000, 2L));
    messageQueue3.enqueue(new AccountEvent(UUID.randomUUID(), causalityKey3, "Deposit", 1000, 1L));

    AccountEvent accountEvent3 =
        new AccountEvent(UUID.randomUUID(), causalityKey1, "Deposit", 300, 3L);
    messageQueue.enqueue(accountEvent3);

    messageQueue2.enqueue(new AccountEvent(UUID.randomUUID(), causalityKey2, "Deposit", 1000, 3L));
    messageQueue3.enqueue(new AccountEvent(UUID.randomUUID(), causalityKey3, "Deposit", 1000, 1L));

    AccountEvent accountEvent2 =
        new AccountEvent(UUID.randomUUID(), causalityKey1, "Withdrawal", 500, 2L);
    messageQueue.enqueue(accountEvent2);

    messageQueue2.enqueue(new AccountEvent(UUID.randomUUID(), causalityKey2, "Deposit", 1000, 4L));

    AccountEvent accountEvent4 =
        new AccountEvent(UUID.randomUUID(), causalityKey1, "Deposit", 300, 4L);
    messageQueue.enqueue(accountEvent4);

    messageQueue3.enqueue(new AccountEvent(UUID.randomUUID(), causalityKey3, "Deposit", 1000, 1L));
    messageQueue2.enqueue(new AccountEvent(UUID.randomUUID(), causalityKey2, "Deposit", 1000, 5L));

    {
      var accountEvent = messageQueue.peek();
      messageQueue.dequeue(accountEvent);
      assertNotNull(accountEvent);
      assertEquals(accountEvent1, accountEvent);
    }

    {
      var accountEvent = messageQueue.peek();
      messageQueue.dequeue(accountEvent);
      assertNotNull(accountEvent);
      assertEquals(accountEvent2, accountEvent);
    }

    {
      var accountEvent = messageQueue.peek();
      messageQueue.dequeue(accountEvent);
      assertNotNull(accountEvent);
      assertEquals(accountEvent3, accountEvent);
    }

    {
      var accountEvent = messageQueue.peek();
      messageQueue.dequeue(accountEvent);
      assertNotNull(accountEvent);
      assertEquals(accountEvent4, accountEvent);
    }

    {
      var accountEvent = messageQueue.peek();
      assertNull(accountEvent);
    }
  }
}
