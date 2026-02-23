/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import crosscutting.patterns.Memento;
import crosscutting.patterns.MementoFactory;
import crosscutting.serialization.memento.ObjectMapperObjToMementoFactory;
import crosscutting.serialization.memento.ObjetMapperMementoToObjFactory;
import domain.model.ComplexAggregate;
import domain.model.ComplexEntity;
import domain.model.DummyAggregate;
import domain.model.MoneyValueObject;
import domain.model.SimpleAggregate;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import org.junit.jupiter.api.Test;

final class MementoTest {

  @Test
  public void testADummyAggregateToAMemento() {

    UUID id = UUID.randomUUID();
    String name = "Test DummyAggregate";
    List<String> items = Arrays.asList("Item1", "Item2");
    Set<Integer> uniqueNumbers = new HashSet<>(Arrays.asList(1, 2, 3));
    Map<String, String> properties = new HashMap<>();
    properties.put("Key1", "Value1");
    properties.put("Key2", "Value2");
    int[] numbersArray = {1, 2, 3, 4, 5};
    BigDecimal balanceAmount = new BigDecimal("100.00");
    String currencyCode = "USD";
    String detailName = "Detail Test";
    Date detailCreationDate = new Date();
    boolean isActive = true;
    byte sampleByte = 127;
    short sampleShort = 32000;
    int sampleInt = 123456789;
    long sampleLong = 12345678901L;
    float sampleFloat = 10.10f;
    double sampleDouble = 20.20;
    char sampleChar = 'A';
    String websiteUrl = "http://example.com";
    Date creationDate = new Date();
    Date activePeriodStart = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DATE, 10);
    Date activePeriodEnd = calendar.getTime();
    boolean isActiveState = true;
    Set<UUID> processedCommands = new HashSet<>(List.of(UUID.randomUUID()));
    Integer version = 1;
    MementoFactory<Memento, DummyAggregate> mementoFactory =
        new ObjectMapperObjToMementoFactory<>(DummyAggregate.class);

    DummyAggregate aggregate = null;
    try {
      aggregate =
          new DummyAggregate(
              id,
              name,
              items,
              uniqueNumbers,
              properties,
              numbersArray,
              balanceAmount,
              currencyCode,
              detailName,
              detailCreationDate,
              isActive,
              sampleByte,
              sampleShort,
              sampleInt,
              sampleLong,
              sampleFloat,
              sampleDouble,
              sampleChar,
              websiteUrl,
              creationDate,
              activePeriodStart,
              activePeriodEnd,
              isActiveState,
              processedCommands,
              version,
              mementoFactory);
    } catch (MalformedURLException e) {
      System.err.println("Invalid URL provided: " + e.getMessage());
      fail("Failed due to an invalid URL");
    } catch (URISyntaxException e) {
      System.err.println("Invalid URI syntax: " + e.getMessage());
      fail("Failed due to URI syntax error");
    } catch (Exception e) {
      throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
    }

    assertNotNull(aggregate);
    var memento = aggregate.saveStateToMemento();
    assertNotNull(memento);
    assertNotNull(memento.asMap());
    assertEquals(19, memento.asMap().get().size());
  }

  @Test
  public void testADummyAMementoToAggregate() {

    UUID id = UUID.randomUUID();
    String name = "Test DummyAggregate";
    List<String> items = Arrays.asList("Item1", "Item2");
    Set<Integer> uniqueNumbers = new HashSet<>(Arrays.asList(1, 2, 3));
    Map<String, String> properties = new HashMap<>();
    properties.put("Key1", "Value1");
    properties.put("Key2", "Value2");
    int[] numbersArray = {1, 2, 3, 4, 5};
    BigDecimal balanceAmount = new BigDecimal("100.00");
    String currencyCode = "USD";
    String detailName = "Detail Test";
    Date detailCreationDate = new Date();
    boolean isActive = true;
    byte sampleByte = 127;
    short sampleShort = 32000;
    int sampleInt = 123456789;
    long sampleLong = 12345678901L;
    float sampleFloat = 10.10f;
    double sampleDouble = 20.20;
    char sampleChar = 'A';
    String websiteUrl = "http://example.com";
    Date creationDate = new Date();
    Date activePeriodStart = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DATE, 10);
    Date activePeriodEnd = calendar.getTime();
    boolean isActiveState = true;
    Set<UUID> processedCommands = new HashSet<>(List.of(UUID.randomUUID()));
    Integer version = 1;
    MementoFactory<Memento, DummyAggregate> mementoFactory =
        new ObjectMapperObjToMementoFactory<>(DummyAggregate.class);

    DummyAggregate aggregate = null;
    try {
      aggregate =
          new DummyAggregate(
              id,
              name,
              items,
              uniqueNumbers,
              properties,
              numbersArray,
              balanceAmount,
              currencyCode,
              detailName,
              detailCreationDate,
              isActive,
              sampleByte,
              sampleShort,
              sampleInt,
              sampleLong,
              sampleFloat,
              sampleDouble,
              sampleChar,
              websiteUrl,
              creationDate,
              activePeriodStart,
              activePeriodEnd,
              isActiveState,
              processedCommands,
              version,
              mementoFactory);
    } catch (MalformedURLException e) {
      System.err.println("Invalid URL provided: " + e.getMessage());
      fail("Failed due to an invalid URL");
    } catch (URISyntaxException e) {
      System.err.println("Invalid URI syntax: " + e.getMessage());
      fail("Failed due to URI syntax error");
    } catch (Exception e) {
      throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
    }

    assertNotNull(aggregate);
    var memento = aggregate.saveStateToMemento();
    assertNotNull(memento);
    assertNotNull(memento.asMap());
    assertEquals(19, memento.asMap().get().size());

    var aggregateFactory = new ObjetMapperMementoToObjFactory<>(DummyAggregate.class);
    var newAggregate = DummyAggregate.createFromMemento(memento, aggregateFactory);
    assertNotNull(newAggregate);
  }

  @Test
  void shouldGetAggregateFromSimpleMemento() {
    var list = new ArrayList<Integer>();
    list.add(1);
    list.add(2);
    list.add(3);

    var aggregate =
        new SimpleAggregate(999, "Hello World", list, new Percentage(new BigDecimal(100)));
    var memento = aggregate.saveStateToMemento();

    var aggregateFactory = new ObjetMapperMementoToObjFactory<>(SimpleAggregate.class);
    var newAggregate = SimpleAggregate.createFromMemento(memento, aggregateFactory);

    assertNotNull(newAggregate);
  }

  @Test
  void shouldGetAggregateFromComplexMemento() {
    ComplexEntity complexEntity =
        new ComplexEntity(new Percentage(BigDecimal.valueOf(100)), IncomeType.AFTER_DEDUCTIONS);
    boolean someBoolean = true;
    long someLong = 123L;
    long someFinalLong = 456L;
    MoneyValueObject moneyValueObject =
        new MoneyValueObject(BigDecimal.valueOf(101), Currency.getInstance("ZAR"));
    List<Integer> someIntegerList = new ArrayList<>();
    List<String> someStringList = new ArrayList<>();
    List<Percentage> somePercentageList = new ArrayList<>();
    Map<String, Object> someMap = new HashMap<>();
    Map<String, Integer> someMapOfInts = new HashMap<>();
    Map<String, MoneyValueObject> someMapOfMoney = new HashMap<>();
    Map<String, Map<UUID, Integer>> someMapOfMaps = new HashMap<>();
    Map<String, List<Integer>> someMapOfLists = new HashMap<>();
    Map<String, Integer[]> someMapOfArrays = new HashMap<>();
    int[] someIntArray = new int[0];
    MoneyValueObject[] someMoneyArray = new MoneyValueObject[0];
    Set<String> someSet = new HashSet<>();
    Queue<String> someQueue = new LinkedList<>();
    Deque<String> someDeque = new ArrayDeque<>();
    Vector<String> someVector = new Vector<>();
    LinkedList<String> someLinkedList = new LinkedList<>();
    LinkedList<MoneyValueObject> moneyLinkedList = new LinkedList<>();

    ComplexAggregate aggregate =
        new ComplexAggregate(
            7262,
            complexEntity,
            someBoolean,
            someLong,
            someFinalLong,
            moneyValueObject,
            someIntegerList,
            someStringList,
            somePercentageList,
            someMap,
            someMapOfInts,
            someMapOfMoney,
            someMapOfMaps,
            someMapOfLists,
            someMapOfArrays,
            someIntArray,
            someMoneyArray,
            someSet,
            someQueue,
            someDeque,
            someVector,
            someLinkedList,
            moneyLinkedList);

    var memento = aggregate.saveStateToMemento();

    var aggregateFactory = new ObjetMapperMementoToObjFactory<>(ComplexAggregate.class);
    var newAggregate = ComplexAggregate.createFromMemento(memento, aggregateFactory);

    assertNotNull(newAggregate);
  }
}
