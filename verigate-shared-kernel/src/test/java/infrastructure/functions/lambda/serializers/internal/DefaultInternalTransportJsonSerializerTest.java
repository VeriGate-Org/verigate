/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.functions.lambda.serializers.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import domain.exceptions.DeserializeException;
import infrastructure.functions.lambda.serializers.model.EventA;
import infrastructure.functions.lambda.serializers.model.RootEvent;
import java.nio.ByteBuffer;
import java.util.List;
import org.junit.jupiter.api.Test;

final class DefaultInternalTransportJsonSerializerTest {

  @Test
  void testSerializeWithStaticTyping() {

    var serializer = new DefaultInternalTransportJsonSerializer(); // no withTyping();

    var eventA = new EventA(123, "bar");

    var serializedTextA = serializer.<RootEvent>serialize(eventA);
    var deserializedObjectA = serializer.deserialize(serializedTextA, EventA.class);
    assertEquals(eventA, deserializedObjectA);
  }

  @Test
  void testSerializeWithDynamicTyping() {

    var serializer = DefaultInternalTransportJsonSerializer.withTyping();

    serializer.registerClassType(EventA.class.getSimpleName(), EventA.class);

    var eventA = new EventA(123, "bar");

    var serializedTextA = serializer.<RootEvent>serialize(eventA);
    var deserializedObjectA = serializer.<RootEvent>deserialize(serializedTextA);
    assertEquals(eventA, deserializedObjectA);
  }

  @Test
  void testDeserializeWhenNullThenThrowsException() {

    var serializer = new DefaultInternalTransportJsonSerializer();

    assertThrows(DeserializeException.class, () -> serializer.deserialize((String) null, EventA.class));
    assertThrows(DeserializeException.class, () -> serializer.deserialize((String) null));

    assertThrows(DeserializeException.class, () -> serializer.deserialize(ByteBuffer.wrap(new byte[0]), EventA.class));
    assertThrows(DeserializeException.class, () -> serializer.deserialize(ByteBuffer.wrap(new byte[0])));

    assertThrows(DeserializeException.class, () -> serializer.deserializeList(null, EventA.class));
  }

  @Test
  void testDeserializeWhenMalformedThenThrowsException() {

    var serializer = new DefaultInternalTransportJsonSerializer();

    assertThrows(DeserializeException.class, () -> serializer.deserialize("malformed!", EventA.class));
    assertThrows(DeserializeException.class, () -> serializer.deserialize((String) "malformed!"));

    assertThrows(DeserializeException.class, () -> serializer.deserialize(ByteBuffer.wrap("malformed!".getBytes()), EventA.class));
    assertThrows(DeserializeException.class, () -> serializer.deserialize(ByteBuffer.wrap("malformed!".getBytes())));

    assertThrows(DeserializeException.class, () -> serializer.deserializeList("malformed!", EventA.class));
  }


  @Test
  void testDeserializeWhenNoMappingThenThrowsException() {

    var serializer = new DefaultInternalTransportJsonSerializer();

    assertThrows(DeserializeException.class, () -> serializer.deserialize("""
        {
          "rootEventField": "NOT AN INT!,
          "eventAField": "bar"
        }
        """, EventA.class)
    );

    assertThrows(DeserializeException.class, () -> serializer.deserialize("""
        {
          "rootEventField": "NOT AN INT!,
          "eventAField": "bar"
        }
        """)
    );

    assertThrows(DeserializeException.class, () -> serializer.deserialize(ByteBuffer.wrap("""
        {
          "rootEventField": "NOT AN INT!,
          "eventAField": "bar"
        }
        """.getBytes()), EventA.class)
    );

    assertThrows(DeserializeException.class, () -> serializer.deserialize(ByteBuffer.wrap("""
        {
          "rootEventField": "NOT AN INT!,
          "eventAField": "bar"
        }
        """.getBytes()))
    );

    assertThrows(DeserializeException.class, () -> serializer.deserializeList("""
          [
            [
              {
                "rootEventField": "NOT AN INT!,
                "eventAField": "bar"
              }
            ]
          ]
        """, EventA.class)
    );
  }

  @Test
  void testDeserializeWithStaticTyping() {

    final var serializer = new DefaultInternalTransportJsonSerializer(); // no withTyping();

    final var eventA = new EventA(123, "bar");

    assertEquals(eventA,
        serializer.deserialize("""
                {
                  "rootEventField": 123,
                  "eventAField": "bar"
                }
                """,
            EventA.class) // type is explicit
    );

    assertEquals(eventA,
        serializer.deserialize(ByteBuffer.wrap("""
                {
                  "rootEventField": 123,
                  "eventAField": "bar"
                }
                """.getBytes()),
            EventA.class)
    );

    assertEquals(eventA,
        serializer.deserializeList("""
                [
                  {
                    "rootEventField": 123,
                    "eventAField": "bar"
                  }
                ]
                """, EventA.class)
            .get(0)
    );
  }

  @Test
  void testDeserializeWithDynamicTyping() {

    var serializer = DefaultInternalTransportJsonSerializer.withTyping();

    serializer.registerClassType(EventA.class.getSimpleName(), EventA.class);
    serializer.registerClassType(List.class.getSimpleName(), List.class);

    final var eventA = new EventA(123, "bar");

    assertEquals(eventA,
        serializer.deserialize("""
            {
              "__artifact_type__": "EventA",
              "rootEventField": 123,
              "eventAField": "bar"
            }
            """) // no explicit type
    );

    assertEquals(eventA,
        serializer.deserialize(ByteBuffer.wrap("""
            {
              "__artifact_type__": "EventA",
              "rootEventField": 123,
              "eventAField": "bar"
            }
            """.getBytes()))
    );

    // deserializeList currently requires explicit type for list element.
    // Testing that this works even if .withTyping() has been configured.

    assertEquals(eventA,
        serializer.deserializeList("""
                  [
                    "List",
                    [
                      {
                      "__artifact_type__": "EventA",
                        "rootEventField": 123,
                        "eventAField": "bar"
                      }
                    ]
                  ]
                """, EventA.class)
            .get(0)
    );
  }
}
