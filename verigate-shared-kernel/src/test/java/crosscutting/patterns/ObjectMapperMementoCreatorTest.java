/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.patterns;

import static org.junit.jupiter.api.Assertions.*;

import crosscutting.patterns.model.Address;
import crosscutting.patterns.model.Child;
import crosscutting.patterns.model.CollectionHolder;
import crosscutting.patterns.model.ComplexClass;
import crosscutting.patterns.model.Name;
import crosscutting.patterns.model.NestedClass;
import crosscutting.patterns.model.Person;
import crosscutting.patterns.model.PrimitiveHolder;
import crosscutting.patterns.model.SimpleHolder;
import crosscutting.serialization.memento.ObjectMapperObjToMementoFactory;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

final class ObjectMapperMementoCreatorTest {

  @Test
  public void testSaveStateToMementoWithPrimitives() {
    // Create an instance of PrimitiveHolder with sample values
    PrimitiveHolder sample = new PrimitiveHolder((byte) 1, (short) 2, 3, 4L, 5.0f, 6.0, 'a', true);

    // Create an instance of FooMementoCreator for PrimitiveHolder
    ObjectMapperObjToMementoFactory<PrimitiveHolder> creator =
        new ObjectMapperObjToMementoFactory<>(PrimitiveHolder.class);

    // Use saveStateToMemento to serialize the PrimitiveHolder instance
    Memento memento = creator.create(sample);

    // Retrieve the serialized state as a Map
    Map<String, Object> serializedState = memento.asMap().get();

    // Assert that all primitives are correctly serialized
    assertEquals(((Integer) serializedState.get("aByte")).byteValue(), (byte) 1);
    assertEquals(((Short) serializedState.get("aShort")).shortValue(), (short) 2);
    assertEquals(serializedState.get("anInt"), 3);
    assertEquals(serializedState.get("aLong"), 4L);
    assertEquals(serializedState.get("aFloat"), 5.0f);
    assertEquals(serializedState.get("aDouble"), 6.0);
    assertEquals(serializedState.get("aChar"), "a");
    assertEquals(serializedState.get("aBoolean"), true);
  }

  @Test
  public void testSaveStateToMementoWithEnhancedSimpleHolder() {
    // Create a SimpleHolder instance with sample values
    Date now = new Date();
    BigDecimal number = new BigDecimal("123.456");
    Instant dateTime = Instant.now();
    SimpleHolder.Status status = SimpleHolder.Status.ACTIVE;
    SimpleHolder holder =
        new SimpleHolder(
            "Sample text", now, Arrays.asList("item1", "item2"), number, dateTime, status);

    // Serialize the SimpleHolder instance
    ObjectMapperObjToMementoFactory<SimpleHolder> creator =
        new ObjectMapperObjToMementoFactory<>(SimpleHolder.class);
    Memento memento = creator.create(holder);

    // Retrieve the serialized state as a Map
    Map<String, Object> serializedState = memento.asMap().get();

    // Assert the correctness of the serialized values
    assertEquals("Sample text", serializedState.get("text"));
    assertEquals(now, new Date((Long) serializedState.get("date")));
    assertInstanceOf(List.class, serializedState.get("list"));
    assertEquals(new BigDecimal(serializedState.get("number").toString()), number);
    // Note: Instant might be serialized to a string or timestamp, depending on serialization
    // settings
    assertNotNull(serializedState.get("dateTime"));
    assertEquals(status.toString(), serializedState.get("status").toString());
  }

  @Test
  public void testSaveStateToMementoWithCollections() {
    // Initialize collection instances with some sample data
    List<String> stringList = Arrays.asList("one", "two", "three");
    Set<Integer> integerSet = new HashSet<>(Arrays.asList(1, 2, 3));
    Map<String, String> stringMap = new HashMap<>();
    stringMap.put("key1", "value1");
    stringMap.put("key2", "value2");
    Queue<Double> doubleQueue = new LinkedList<>(Arrays.asList(1.1, 2.2, 3.3));

    // Create a CollectionHolder instance
    CollectionHolder holder = new CollectionHolder(stringList, integerSet, stringMap, doubleQueue);

    // Use FooMementoCreator to serialize the CollectionHolder instance
    ObjectMapperObjToMementoFactory<CollectionHolder> creator =
        new ObjectMapperObjToMementoFactory<>(CollectionHolder.class);
    Memento memento = creator.create(holder);

    // Retrieve the serialized state as a Map
    Map<String, Object> serializedState = memento.asMap().get();

    // Assert the correctness of the serialized collections
    assertEquals(stringList, serializedState.get("stringList"));
    assertEquals(
        integerSet,
        new HashSet<>(
            (List<Integer>) serializedState.get("integerSet"))); // Set may be serialized as List
    assertEquals(stringMap, serializedState.get("stringMap"));
    assertEquals(
        new LinkedList<>(Arrays.asList(1.1, 2.2, 3.3)),
        new LinkedList<>(
            (List<Double>) serializedState.get("doubleQueue"))); // Queue may be serialized as List
  }

  @Test
  public void testSaveStateToMementoWithNestedObjects() {
    // Create sample nested objects
    Name name = new Name("John", "Doe");
    Address address = new Address("123 Elm St", "Springfield");
    Person person = new Person(name, address);

    // Use FooMementoCreator to serialize the Person instance
    ObjectMapperObjToMementoFactory<Person> creator = new ObjectMapperObjToMementoFactory<>(Person.class);
    Memento memento = creator.create(person);

    // Retrieve the serialized state as a Map
    Map<String, Object> serializedState = memento.asMap().get();

    // Assert the correctness of the serialized nested objects
    assertNotNull(serializedState.get("name"));
    assertNotNull(serializedState.get("address"));

    // Since the nested objects are likely serialized as Maps, you can further check their contents
    Map<String, String> nameMap = (Map<String, String>) serializedState.get("name");
    assertEquals("John", nameMap.get("firstName"));
    assertEquals("Doe", nameMap.get("lastName"));

    Map<String, String> addressMap = (Map<String, String>) serializedState.get("address");
    assertEquals("123 Elm St", addressMap.get("street"));
    assertEquals("Springfield", addressMap.get("city"));
  }

  @Test
  public void testSerializationWithInheritance() {
    Child child = new Child("Parent's property", "Child's property");

    ObjectMapperObjToMementoFactory<Child> creator = new ObjectMapperObjToMementoFactory<>(Child.class);
    Memento memento = creator.create(child);

    Map<String, Object> serializedChild = memento.asMap().get();

    assertEquals("Parent's property", serializedChild.get("parentProperty"));
    assertEquals("Child's property", serializedChild.get("childProperty"));
  }

  @Test
  public void testSerializationOfComplexClass() {
    // Setup complex object with various elements
    NestedClass nested = new NestedClass("Nested detail");
    List<String> stringList = Arrays.asList("One", "Two", "Three");
    Map<String, Integer> map = new HashMap<>();
    map.put("Key1", 1);
    map.put("Key2", 2);
    Set<Double> doubleSet = new HashSet<>(Arrays.asList(1.1, 2.2, 3.3));

    ComplexClass complexObject =
        new ComplexClass(99, "ComplexClass", Money.of(100, "ZAR"), nested, stringList, map,
            doubleSet);

    // Serialize using FooMementoCreator
    ObjectMapperObjToMementoFactory<ComplexClass> creator =
        new ObjectMapperObjToMementoFactory<>(ComplexClass.class);

    Memento memento = creator.create(complexObject);

    // Verify serialized content
    Map<String, Object> serialized = memento.asMap().get();
    assertEquals(99, serialized.get("baseNumber"));
    assertEquals("ComplexClass", serialized.get("name"));

    var amountSerialized = (Map<String, Object>) serialized.get("amount");
    assertEquals(100, (Double) amountSerialized.get("number"));

    // For nested and collections, ensure they are serialized correctly
    Map<String, Object> nestedSerialized = (Map<String, Object>) serialized.get("nestedObject");
    assertEquals("Nested detail", nestedSerialized.get("detail"));

    List<String> listSerialized = (List<String>) serialized.get("stringList");
    assertTrue(listSerialized.containsAll(Arrays.asList("One", "Two", "Three")));

    Map<String, Integer> mapSerialized = (Map<String, Integer>) serialized.get("map");
    assertTrue(mapSerialized.keySet().containsAll(Arrays.asList("Key1", "Key2")));

    Set<Double> setSerialized =
        new HashSet<>(
            (List<Double>)
                serialized.get("doubleSet")); // Assuming serialization converts set to list
    assertTrue(setSerialized.containsAll(Arrays.asList(1.1, 2.2, 3.3)));

    // Ensure transient field is not serialized
    assertNull(serialized.get("transientField"));
  }

  @Test
  public void testSerializationOfComplexClassWithANullField() {
    // Setup complex object with various elements
    NestedClass nested = new NestedClass("Nested details");
    List<String> stringList = Arrays.asList("One", "Two", "Three");
    Map<String, Integer> map = new HashMap<>();
    map.put("Key1", 1);
    map.put("Key2", 2);
    Set<Double> doubleSet = new HashSet<>(Arrays.asList(1.1, 2.2, 3.3));

    ComplexClass complexObject =
        new ComplexClass(99, "ComplexClass", null, nested, stringList, null, doubleSet);

    // Serialize using FooMementoCreator
    ObjectMapperObjToMementoFactory<ComplexClass> creator =
        new ObjectMapperObjToMementoFactory<>(ComplexClass.class);

    Memento memento = creator.create(complexObject);

    // Verify serialized content
    Map<String, Object> serialized = memento.asMap().get();
    assertEquals(99, serialized.get("baseNumber"));
    assertEquals("ComplexClass", serialized.get("name"));

    assertNull(serialized.get("amount"));

    // For nested and collections, ensure they are serialized correctly
    Map<String, Object> nestedSerialized = (Map<String, Object>) serialized.get("nestedObject");
    assertEquals("Nested details", nestedSerialized.get("detail"));

    List<String> listSerialized = (List<String>) serialized.get("stringList");
    assertTrue(listSerialized.containsAll(Arrays.asList("One", "Two", "Three")));

    Map<String, Integer> mapSerialized = (Map<String, Integer>) serialized.get("map");
    assertNull(mapSerialized);

    Set<Double> setSerialized =
        new HashSet<>(
            (List<Double>)
                serialized.get("doubleSet")); // Assuming serialization converts set to list
    assertTrue(setSerialized.containsAll(Arrays.asList(1.1, 2.2, 3.3)));

    // Ensure transient field is not serialized
    assertNull(serialized.get("transientField"));
  }
}
