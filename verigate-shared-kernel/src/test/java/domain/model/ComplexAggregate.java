/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.model;

import crosscutting.serialization.DataContract;
import crosscutting.serialization.memento.ObjectMapperObjToMementoFactory;
import domain.AggregateRoot;
import domain.Percentage;
import domain.invariants.SpecificationResult;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

public final class ComplexAggregate extends AggregateRoot<Integer, ComplexAggregate> {

  @DataContract private ComplexEntity complexEntity;
  @DataContract private boolean someBoolean;
  @DataContract private long someLong;
  @DataContract private final long someFinalLong;
  @DataContract private MoneyValueObject moneyValueObject;
  @DataContract private List<Integer> someIntegerList;
  @DataContract private List<String> someStringList;
  @DataContract private List<Percentage> somePercentageList;
  @DataContract private Map<String, Object> someMap;
  @DataContract private Map<String, Integer> someMapOfInts;
  @DataContract private Map<String, MoneyValueObject> someMapOfMoney;
  @DataContract private Map<String, Map<UUID, Integer>> someMapOfMaps;
  @DataContract private Map<String, List<Integer>> someMapOfLists;
  @DataContract private Map<String, Integer[]> someMapOfArrays;
  @DataContract private int[] someIntArray;
  @DataContract private MoneyValueObject[] someMoneyArray;
  private Set<String> someSet;
  @DataContract private Queue<String> someQueue;
  @DataContract private Deque<String> someDeque;
  @DataContract private Vector<String> someVector;
  @DataContract private LinkedList<String> someLinkedList;
  @DataContract private LinkedList<MoneyValueObject> moneyLinkedList;

  private transient long someTransient;

  private ComplexAggregate() {
    this.someFinalLong = 0;
  }

  @Override
  public SpecificationResult checkSpecification() {
    return null;
  }

  public ComplexAggregate(
      Integer id,
      ComplexEntity complexEntity,
      boolean someBoolean,
      long someLong,
      long someFinalLong,
      MoneyValueObject moneyValueObject,
      List<Integer> someIntegerList,
      List<String> someStringList,
      List<Percentage> somePercentageList,
      Map<String, Object> someMap,
      Map<String, Integer> someMapOfInts,
      Map<String, MoneyValueObject> someMapOfMoney,
      Map<String, Map<UUID, Integer>> someMapOfMaps,
      Map<String, List<Integer>> someMapOfLists,
      Map<String, Integer[]> someMapOfArrays,
      int[] someIntArray,
      MoneyValueObject[] someMoneyArray,
      Set<String> someSet,
      Queue<String> someQueue,
      Deque<String> someDeque,
      Vector<String> someVector,
      LinkedList<String> someLinkedList,
      LinkedList<MoneyValueObject> moneyLinkedList) {
    super(
        id,
        true,
        new HashSet<>(),
        1,
        new ObjectMapperObjToMementoFactory<>(ComplexAggregate.class));
    this.complexEntity = complexEntity;
    this.someBoolean = someBoolean;
    this.someLong = someLong;
    this.someFinalLong =
        someFinalLong; // This parameter is redundant because it's already initialized in your
                       // provided constructor. However, included for completeness.
    this.moneyValueObject = moneyValueObject;
    this.someIntegerList = someIntegerList;
    this.someStringList = someStringList;
    this.somePercentageList = somePercentageList;
    this.someMap = someMap;
    this.someMapOfInts = someMapOfInts;
    this.someMapOfMoney = someMapOfMoney;
    this.someMapOfMaps = someMapOfMaps;
    this.someMapOfLists = someMapOfLists;
    this.someMapOfArrays = someMapOfArrays;
    this.someIntArray = someIntArray;
    this.someMoneyArray = someMoneyArray;
    this.someSet = someSet;
    this.someQueue = someQueue;
    this.someDeque = someDeque;
    this.someVector = someVector;
    this.someLinkedList = someLinkedList;
    this.moneyLinkedList = moneyLinkedList;
  }
}
