/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.model;

import crosscutting.patterns.Memento;
import crosscutting.patterns.MementoFactory;
import crosscutting.serialization.DataContract;
import domain.AggregateRoot;
import domain.invariants.SpecificationResult;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.javamoney.moneta.Money;

public final class DummyAggregate extends AggregateRoot<UUID, DummyAggregate> {

  @DataContract private final String name;
  @DataContract private List<String> items;
  @DataContract private Set<Integer> uniqueNumbers;
  @DataContract private Map<String, String> properties;
  @DataContract private int[] numbersArray;
  @DataContract private Money balance;
  @DataContract private DummyDetail detail;
  @DataContract private boolean isActive;
  @DataContract private byte sampleByte;
  @DataContract private short sampleShort;
  @DataContract private int sampleInt;
  @DataContract private long sampleLong;
  @DataContract private float sampleFloat;
  @DataContract private double sampleDouble;
  @DataContract private char sampleChar;
  @DataContract private URL website;
  @DataContract private Date creationDate;
  @DataContract private DateRange activePeriod;

  private DummyAggregate() {
    this.name = null;
  }

  @Override
  public SpecificationResult checkSpecification() {
    return null;
  }

  public DummyAggregate(
      UUID id,
      boolean isActive,
      Set<UUID> processedCommands,
      Integer version,
      MementoFactory<Memento, DummyAggregate> mementoFactory) {
    super(id, isActive, processedCommands, version, mementoFactory);
    this.name = "Hello";
    this.items = new ArrayList<>();
    this.uniqueNumbers = new HashSet<>();
    this.properties = new HashMap<>();
    this.numbersArray = new int[0];
    this.balance = Money.of(BigDecimal.ZERO, "ZAR");
    this.detail = new DummyDetail("", new Date(0));
    this.isActive = isActive;
    this.sampleByte = 0;
    this.sampleShort = 0;
    this.sampleInt = 0;
    this.sampleLong = 0L;
    this.sampleFloat = 0.0f;
    this.sampleDouble = 0.0;
    this.sampleChar = 'A';

    try {
      URI uri = new URI("http://example.com");
      this.website = uri.toURL();
    } catch (Exception e) {
      this.website = null;
    }
    this.creationDate = new Date(0);
    this.activePeriod = new DateRange(new Date(), new Date());
  }

  public DummyAggregate(
      UUID id,
      String name,
      List<String> items,
      Set<Integer> uniqueNumbers,
      Map<String, String> properties,
      int[] numbersArray,
      BigDecimal balanceAmount,
      String currencyCode,
      String detailName,
      Date detailCreationDate,
      boolean isActive,
      byte sampleByte,
      short sampleShort,
      int sampleInt,
      long sampleLong,
      float sampleFloat,
      double sampleDouble,
      char sampleChar,
      String websiteUrl,
      Date creationDate,
      Date activePeriodStart,
      Date activePeriodEnd,
      boolean isActiveState,
      Set<UUID> processedCommands,
      Integer version,
      MementoFactory<Memento, DummyAggregate> mementoFactory)
      throws URISyntaxException, MalformedURLException {
    super(id, isActiveState, processedCommands, version, mementoFactory);
    this.name = name;
    this.items = items;
    this.uniqueNumbers = uniqueNumbers;
    this.properties = properties;
    this.numbersArray = numbersArray;
    this.balance = Money.of(balanceAmount, currencyCode);
    this.detail = new DummyDetail(detailName, detailCreationDate);
    this.isActive = isActive;
    this.sampleByte = sampleByte;
    this.sampleShort = sampleShort;
    this.sampleInt = sampleInt;
    this.sampleLong = sampleLong;
    this.sampleFloat = sampleFloat;
    this.sampleDouble = sampleDouble;
    this.sampleChar = sampleChar;
    this.website = new URI(websiteUrl).toURL();
    this.creationDate = creationDate;
    this.activePeriod = new DateRange(activePeriodStart, activePeriodEnd);
  }
}
