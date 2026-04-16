/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.domain.models;

/**
 * Property endorsement returned by the DeedsWeb registry (e.g., bond, servitude).
 * Mirrors the {@code propertyEndorsementDetailsResponse} SOAP type minus the
 * embedded error.
 */
public final class PropertyEndorsement {

  private final String amount;
  private final String document;
  private final String holder;
  private final String microfilmReference;

  private PropertyEndorsement(Builder builder) {
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

  /** Builder for PropertyEndorsement. */
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

    public PropertyEndorsement build() {
      return new PropertyEndorsement(this);
    }
  }
}
