/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.dynamodb;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import infrastructure.functions.lambda.serializers.Serializer;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import java.util.Map;
import java.util.function.Function;

public final class DynamoDBReorderMessageQueueTest {

  private final String tableName = "TestTable";
  private final String causalityKey = "TestCausalityKey";

  @Mock private DynamoDbClient mockDynamoDbClient;
  @Mock private Serializer mockSerializer;
  @Mock private Function<TestMessage, String> mockGetCausalityKey;
  @Mock private Function<TestMessage, Long> mockGetMessageLogicalClock;
  @InjectMocks private DynamoDBReorderMessageQueue<TestMessage> queue;

  @BeforeEach
  public void setup() throws Exception {
    System.setProperty("aws.region", "us-east-1");

    try (var mockedStatic = MockitoAnnotations.openMocks(this)) {
      queue =
          new DynamoDBReorderMessageQueue<>(
              mockDynamoDbClient,
              tableName,
              mockSerializer,
              causalityKey,
              mockGetCausalityKey,
              mockGetMessageLogicalClock,
              "TestLogger");
    }
  }

  @Test
  public void testEnqueue() {
    TestMessage message = new TestMessage("key", 1L);

    when(mockGetCausalityKey.apply(message)).thenReturn(causalityKey);
    when(mockGetMessageLogicalClock.apply(message)).thenReturn(1L);
    when(mockSerializer.serialize(message)).thenReturn("SerializedMessage");

    queue.enqueue(message);

    ArgumentCaptor<PutItemRequest> captor = ArgumentCaptor.forClass(PutItemRequest.class);
    verify(mockDynamoDbClient).putItem(captor.capture());

    PutItemRequest request = captor.getValue();
    assertEquals(tableName, request.tableName());
    assertEquals(causalityKey, request.item().get("CausalityKey").s());
    assertEquals("1", request.item().get("LogicalClock").n());
    assertEquals("SerializedMessage", request.item().get("Message").s());
  }

  @Test
  public void testEnqueue_ConditionalCheckFailedException() {
    TestMessage message = new TestMessage("key", 1L);

    when(mockGetCausalityKey.apply(message)).thenReturn(causalityKey);
    when(mockGetMessageLogicalClock.apply(message)).thenReturn(1L);
    when(mockSerializer.serialize(message)).thenReturn("SerializedMessage");
    doThrow(ConditionalCheckFailedException.builder().build())
        .when(mockDynamoDbClient)
        .putItem(any(PutItemRequest.class));

    queue.enqueue(message);

    verify(mockDynamoDbClient).putItem(any(PutItemRequest.class));
    verifyNoMoreInteractions(mockDynamoDbClient);
  }

  @Test
  public void testDequeue() {
    TestMessage message = new TestMessage("key", 1L);

    when(mockGetCausalityKey.apply(message)).thenReturn(causalityKey);
    when(mockGetMessageLogicalClock.apply(message)).thenReturn(1L);

    queue.dequeue(message);

    ArgumentCaptor<DeleteItemRequest> captor = ArgumentCaptor.forClass(DeleteItemRequest.class);
    verify(mockDynamoDbClient).deleteItem(captor.capture());

    DeleteItemRequest request = captor.getValue();
    assertEquals(tableName, request.tableName());
    assertEquals(causalityKey, request.key().get("CausalityKey").s());
    assertEquals("1", request.key().get("LogicalClock").n());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testPeek() throws Exception {
    TestMessage expectedMessage = new TestMessage("key", 1L);

    Map<String, AttributeValue> mockItem =
        Map.of("Message", AttributeValue.builder().s("SerializedMessage").build());

    QueryResponse mockQueryResponse =
        QueryResponse.builder().items(Collections.singletonList(mockItem)).build();

    doReturn(mockQueryResponse).when(mockDynamoDbClient).query(any(QueryRequest.class));
    doReturn(expectedMessage).when(mockSerializer).deserialize(eq("SerializedMessage"));

    TestMessage result = queue.peek();

    assertEquals(expectedMessage, result);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testPeek_NoMessageFound() {
    when(mockDynamoDbClient.query(any(QueryRequest.class)))
        .thenReturn(QueryResponse.builder().items(Map.of()).build());

    TestMessage result = queue.peek();

    assertNull(result);
  }

  static class TestMessage {
    private final String key;
    private final Long logicalClock;

    public TestMessage(String key, Long logicalClock) {
      this.key = key;
      this.logicalClock = logicalClock;
    }

    public String getKey() {
      return key;
    }

    public Long getLogicalClock() {
      return logicalClock;
    }
  }
}
