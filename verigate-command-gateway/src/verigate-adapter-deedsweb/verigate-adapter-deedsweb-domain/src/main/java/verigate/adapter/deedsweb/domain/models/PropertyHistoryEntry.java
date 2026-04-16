/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.domain.models;

/**
 * Historical entry for a property returned by the DeedsWeb registry.
 * Mirrors the {@code propertyHistoryDetailsResponse} SOAP type minus the
 * embedded error. Distinct from {@link PropertyEndorsement} only semantically;
 * the SOAP shape happens to be identical.
 */
public final class PropertyHistoryEntry {

  private final String amount;
  private final String document;
  private final String holder;
  private final String microfilmReference;

  private PropertyHistoryEntry(Builder builder) {
    this.amount = builder.amount;
    this.document = builder.document;
    this.holder = builder.holder;
    this.microfilmReference = builder.microfilmReference;
  }

  public String getAmount() {
    return amount;
  }

  public String getDocument() {
    return document;
  }

  public String getHolder() {
    return holder;
  }

  public String getMicrofilmReference() {
    return microfilmReference;
  }

  public static Builder builder() {
    return new Builder();
  }

  /** Builder for PropertyHistoryEntry. */
  public static final class Builder {
    private String amount;
    private String document;
    private String holder;
    private String microfilmReference;

    public Builder amount(String amount) {
      this.amount = amount;
      return this;
    }

    public Builder document(String document) {
      this.document = document;
      return this;
    }

    public Builder holder(String holder) {
      this.holder = holder;
      return this;
    }

    public Builder microfilmReference(String microfilmReference) {
      this.microfilmReference = microfilmReference;
      return this;
    }

    public PropertyHistoryEntry build() {
      return new PropertyHistoryEntry(this);
    }
  }
}
