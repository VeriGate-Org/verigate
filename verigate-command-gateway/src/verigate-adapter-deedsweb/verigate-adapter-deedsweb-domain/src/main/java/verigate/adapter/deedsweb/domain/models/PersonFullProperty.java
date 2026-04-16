/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.domain.models;

import java.util.Collections;
import java.util.List;

/**
 * Aggregates the full payload returned by
 * {@code getPersonFullPropertyInformationByIdentityNumber} — the registered
 * person, the properties they own, and any endorsements or history attached
 * to those properties.
 */
public final class PersonFullProperty {

  private final PersonDetails person;
  private final List<PropertyDetails> properties;
  private final List<PropertyEndorsement> endorsements;
  private final List<PropertyHistoryEntry> history;

  private PersonFullProperty(Builder builder) {
    this.person = builder.person;
    this.properties = List.copyOf(builder.properties);
    this.endorsements = List.copyOf(builder.endorsements);
    this.history = List.copyOf(builder.history);
  }

  public PersonDetails getPerson() {
    return person;
  }

  public List<PropertyDetails> getProperties() {
    return properties;
  }

  public List<PropertyEndorsement> getEndorsements() {
    return endorsements;
  }

  public List<PropertyHistoryEntry> getHistory() {
    return history;
  }

  public static Builder builder() {
    return new Builder();
  }

  /** Builder for PersonFullProperty. */
  public static final class Builder {
    private PersonDetails person;
    private List<PropertyDetails> properties = Collections.emptyList();
    private List<PropertyEndorsement> endorsements = Collections.emptyList();
    private List<PropertyHistoryEntry> history = Collections.emptyList();

    public Builder person(PersonDetails person) {
      this.person = person;
      return this;
    }

    public Builder properties(List<PropertyDetails> properties) {
      this.properties = properties == null ? Collections.emptyList() : properties;
      return this;
    }

    public Builder endorsements(List<PropertyEndorsement> endorsements) {
      this.endorsements = endorsements == null ? Collections.emptyList() : endorsements;
      return this;
    }

    public Builder history(List<PropertyHistoryEntry> history) {
      this.history = history == null ? Collections.emptyList() : history;
      return this;
    }

    public PersonFullProperty build() {
      return new PersonFullProperty(this);
    }
  }
}
