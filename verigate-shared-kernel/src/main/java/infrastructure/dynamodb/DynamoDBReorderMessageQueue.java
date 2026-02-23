/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.dynamodb;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import domain.messages.MessageQueue;
import infrastructure.functions.lambda.serializers.Serializer;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

/**
 * Implementation of a message queue using DynamoDB for storing and reordering messages based on a
 * causality key and logical clock.
 *
 * @param <T> the type of messages handled by the queue
 */
public final class DynamoDBReorderMessageQueue<T> implements MessageQueue<T> {

  private static final String CAUSALITY_KEY = "CausalityKey";
  private static final String LOGICAL_CLOCK = "LogicalClock";
  private static final String INSERTED_AT = "InsertedAt";
  private static final String MESSAGE = "Message";
  private static final String CAUSALITY_KEY_PLACEHOLDER = ":causalityKey";

  private final String causalityKey;
  private final Logger logger;
  private final DynamoDbClient client;
  private final String tableName;
  private final Serializer serializer;
  private final Function<T, String> getMessageCausalityKey;
  private final Function<T, Long> getMessageLogicalClock;

  /**
   * Constructor for creating a new DynamoDBReorderMessageQueue.
   *
   * @param tableName the name of the DynamoDB table
   * @param region the AWS region
   * @param credentialsProvider the AWS credentials provider
   * @param serializer the serializer for converting messages to/from strings
   * @param causalityKey the causality key for the messages
   * @param getCausalityKey function to extract the causality key from a message
   * @param getMessageLogicalClock function to extract the logical clock value from a message
   * @param loggerName the name of the logger
   */
  public DynamoDBReorderMessageQueue(
      String tableName,
      Region region,
      AwsCredentialsProvider credentialsProvider,
      Serializer serializer,
      String causalityKey,
      Function<T, String> getCausalityKey,
      Function<T, Long> getMessageLogicalClock,
      String loggerName) {
    this(
        DynamoDbClient.builder().region(region).credentialsProvider(credentialsProvider).build(),
        tableName,
        serializer,
        causalityKey,
        getCausalityKey,
        getMessageLogicalClock,
        loggerName);
  }

  /**
   * Constructor for creating a new DynamoDBReorderMessageQueue with an existing DynamoDbClient.
   *
   * @param dynamoDbClient the DynamoDB client
   * @param tableName the name of the DynamoDB table
   * @param serializer the serializer for converting messages to/from strings
   * @param causalityKey the causality key for the messages
   * @param getMessageCausalityKey function to extract the causality key from a message
   * @param getMessageLogicalClock function to extract the logical clock value from a message
   * @param loggerName the name of the logger
   */
  public DynamoDBReorderMessageQueue(
      DynamoDbClient dynamoDbClient,
      String tableName,
      Serializer serializer,
      String causalityKey,
      Function<T, String> getMessageCausalityKey,
      Function<T, Long> getMessageLogicalClock,
      String loggerName) {
    this.causalityKey = causalityKey;
    this.logger =
        LoggerFactory.getLogger(
            String.format("%s|%s", DynamoDBReorderMessageQueue.class.getSimpleName(), loggerName));
    this.serializer = serializer;
    this.getMessageCausalityKey = getMessageCausalityKey;
    this.getMessageLogicalClock = getMessageLogicalClock;
    this.client = dynamoDbClient;
    this.tableName = tableName;

    this.logger.debug("DynamoDB reorder message queue created for table: {}", tableName);
  }

  /**
   * Enqueues a message into the DynamoDB table.
   *
   * @param message the message to enqueue
   */
  @Override
  public void enqueue(T message) {
    logger.debug("Enqueue message: {}", message);

    var messageCausalityKey = this.getMessageCausalityKey.apply(message);
    checkCausalityKey(messageCausalityKey);

    Map<String, AttributeValue> item = new HashMap<>();

    // Add CausalityKey
    item.put(CAUSALITY_KEY, AttributeValue.builder().s(this.causalityKey).build());

    // Add LogicalClock
    var logicalClock = this.getMessageLogicalClock.apply(message);
    item.put(LOGICAL_CLOCK, AttributeValue.builder().n(logicalClock.toString()).build());

    // Add DateTime (wall clock time)
    var now = Instant.now();
    String formattedNow = DateTimeFormatter.ISO_INSTANT.format(now);
    item.put(INSERTED_AT, AttributeValue.builder().s(formattedNow).build());

    // Add Message
    try {
      var serializedMessage = serializer.serialize(message);
      item.put(MESSAGE, AttributeValue.builder().s(serializedMessage).build());
    } catch (Exception e) {
      this.logger.error("Error serializing message: {}", message, e);
      throw new PermanentException("Error serializing message", e);
    }

    PutItemRequest request =
        PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .conditionExpression(
                String.format(
                    "attribute_not_exists(%s) AND attribute_not_exists(%s)",
                    CAUSALITY_KEY, LOGICAL_CLOCK))
            .build();

    try {
      this.client.putItem(request);
    } catch (ConditionalCheckFailedException e) {
      // If the item already exists, log and return without throwing an exception
      this.logger.warn(
          "Item already exists, ignoring: causality key -> {} logical clock -> {}",
          causalityKey,
          logicalClock);
    } catch (SdkClientException e) {
      this.logger.error(
          "Transient error querying first message by causality key: {}", this.causalityKey, e);
      throw new TransientException(
          "Temporary error querying first message by causality key: " + this.causalityKey, e);
    } catch (DynamoDbException e) {
      this.logger.error("Error enqueuing message: {}", message, e);
      throw new PermanentException("Error enqueuing message", e);
    }
  }

  /**
   * Dequeues a message from the DynamoDB table.
   *
   * @param message the message to dequeue
   */
  @Override
  public void dequeue(T message) {
    this.logger.debug("Dequeue message: {}", message);

    var messageCausalityKey = this.getMessageCausalityKey.apply(message);
    checkCausalityKey(messageCausalityKey);

    Map<String, AttributeValue> key = new HashMap<>();
    key.put(CAUSALITY_KEY, AttributeValue.builder().s(this.causalityKey).build());
    key.put(
        LOGICAL_CLOCK,
        AttributeValue.builder().n(getMessageLogicalClock.apply(message).toString()).build());

    DeleteItemRequest request = DeleteItemRequest.builder().tableName(tableName).key(key).build();
    try {
      client.deleteItem(request);
    } catch (SdkClientException e) {
      this.logger.error("Transient error de-queuing message: {}", message, e);
      throw new TransientException("Temporary error de-queuing message", e);
    } catch (DynamoDbException e) {
      this.logger.error("Error de-queuing message: {}", message, e);
      throw new PermanentException("Error de-queuing message", e);
    }

    this.logger.debug("Dequeued message: {}", message);
  }

  /**
   * Peeks the next message in the queue without removing it.
   *
   * @return the next message, or null if no message is found
   */
  @Override
  public T peek() {
    this.logger.debug("Peek the next message");

    Optional<Map<String, AttributeValue>> optionalMap =
        getFirstMessageByCausalityKey(this.causalityKey);

    if (optionalMap.isEmpty()) {
      this.logger.warn("No message found for causality key: {}", this.causalityKey);
      return null;
    }

    Map<String, AttributeValue> map = optionalMap.get();
    var rawMessage = map.get(MESSAGE);
    if (rawMessage == null) {
      this.logger.warn("No raw message found for causality key: {}", this.causalityKey);
      return null;
    }

    try {
      T message = this.serializer.deserialize(rawMessage.s());
      this.logger.debug("Peeked message: {}", rawMessage.s());
      return message;
    } catch (Exception e) {
      this.logger.error("Error deserializing message: {}", rawMessage, e);
      throw new PermanentException("Error deserializing message", e);
    }
  }

  /**
   * Retrieves the first message by causality key.
   *
   * @param causalityKey the causality key
   * @return an optional containing the first message attributes if found, otherwise empty
   */
  public Optional<Map<String, AttributeValue>> getFirstMessageByCausalityKey(String causalityKey) {
    QueryRequest queryRequest =
        QueryRequest.builder()
            .tableName(this.tableName)
            .keyConditionExpression(CAUSALITY_KEY + " = " + CAUSALITY_KEY_PLACEHOLDER)
            .expressionAttributeValues(
                Map.of(CAUSALITY_KEY_PLACEHOLDER, AttributeValue.builder().s(causalityKey).build()))
            .scanIndexForward(true) // Sort in ascending order
            .limit(1) // Limit to 1 item
            .build();

    try {
      QueryResponse response = client.query(queryRequest);
      if (!response.items().isEmpty()) {
        // Return the first item (the one with the smallest logical clock)
        return Optional.of(response.items().getFirst());
      }
    } catch (DynamoDbException e) {
      this.logger.error("Error querying first message by causality key: {}", causalityKey, e);
      throw new PermanentException(
          "Error querying first message by causality key: " + causalityKey, e);
    }

    return Optional.empty(); // No items found for the causality key or error occurred
  }

  /**
   * Checks if the message causality key matches the queue causality key.
   *
   * @param messageCausalityKey the causality key of the message
   */
  private void checkCausalityKey(String messageCausalityKey) {
    if (!Objects.equals(messageCausalityKey, this.causalityKey)) {
      throw new PermanentException(
          "Message causality key does not match queue causality key: "
              + messageCausalityKey
              + " != "
              + this.causalityKey);
    }
  }
}
