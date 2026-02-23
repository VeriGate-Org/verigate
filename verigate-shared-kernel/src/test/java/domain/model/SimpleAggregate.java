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
import java.util.HashSet;
import java.util.List;

public final class SimpleAggregate extends AggregateRoot<Integer, SimpleAggregate> {

  @DataContract private Percentage percentage;

  @DataContract private final String text;

  @DataContract private List<Integer> myList;

  private SimpleAggregate() {
    this.text = null;
  }

  @Override
  public SpecificationResult checkSpecification() {
    return null;
  }

  public SimpleAggregate(Integer id, String text, List<Integer> list, Percentage percentage) {
    super(
        id, true, new HashSet<>(), 1, new ObjectMapperObjToMementoFactory<>(SimpleAggregate.class));
    this.text = text;
    this.myList = list;
    this.percentage = percentage;
  }
}
