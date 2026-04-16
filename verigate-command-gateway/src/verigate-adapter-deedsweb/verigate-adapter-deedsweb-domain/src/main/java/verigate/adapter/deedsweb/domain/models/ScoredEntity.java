/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.domain.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Scored entity from DeedsWeb matching results.
 */
public class ScoredEntity {

  private final String id;
  private final String caption;
  private final String schema;
  private final Map<String, List<Object>> properties;
  private final List<String> datasets;
  private final List<String> referents;
  private final Boolean target;
  private final LocalDateTime firstSeen;
  private final LocalDateTime lastSeen;
  private final LocalDateTime lastChange;
  private final Double score;
  private final Map<String, Double> features;
  private final Boolean match;
  private final String token;

  private ScoredEntity(Builder builder) {
    this.id = builder.id;
    this.caption = builder.caption;
    this.schema = builder.schema;
    this.properties = builder.properties;
    this.datasets = builder.datasets;
    this.referents = builder.referents;
    this.target = builder.target;
    this.firstSeen = builder.firstSeen;
    this.lastSeen = builder.lastSeen;
    this.lastChange = builder.lastChange;
    this.score = builder.score;
    this.features = builder.features;
    this.match = builder.match;
    this.token = builder.token;
  }

  // Getters
  public String getId() {
    return id;
  }

  public String getCaption() {
    return caption;
  }

  public String getSchema() {
    return schema;
  }

  public Map<String, List<Object>> getProperties() {
    return properties;
  }

  public List<String> getDatasets() {
    return datasets;
  }

  public List<String> getReferents() {
    return referents;
  }

  public Boolean getTarget() {
    return target;
  }

  public LocalDateTime getFirstSeen() {
    return firstSeen;
  }

  public LocalDateTime getLastSeen() {
    return lastSeen;
  }

  public LocalDateTime getLastChange() {
    return lastChange;
  }

  public Double getScore() {
    return score;
  }

  public Map<String, Double> getFeatures() {
    return features;
  }

  public Boolean getMatch() {
    return match;
  }

  public String getToken() {
    return token;
  }

  /**
   * Builder for ScoredEntity.
   */
  public static class Builder {
    private String id;
    private String caption;
    private String schema;
    private Map<String, List<Object>> properties;
    private List<String> datasets;
    private List<String> referents;
    private Boolean target;
    private LocalDateTime firstSeen;
    private LocalDateTime lastSeen;
    private LocalDateTime lastChange;
    private Double score;
    private Map<String, Double> features;
    private Boolean match;
    private String token;

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder caption(String caption) {
      this.caption = caption;
      return this;
    }

    public Builder schema(String schema) {
      this.schema = schema;
      return this;
    }

    public Builder properties(Map<String, List<Object>> properties) {
      this.properties = properties;
      return this;
    }

    public Builder datasets(List<String> datasets) {
      this.datasets = datasets;
      return this;
    }

    public Builder referents(List<String> referents) {
      this.referents = referents;
      return this;
    }

    public Builder target(Boolean target) {
      this.target = target;
      return this;
    }

    public Builder firstSeen(LocalDateTime firstSeen) {
      this.firstSeen = firstSeen;
      return this;
    }

    public Builder lastSeen(LocalDateTime lastSeen) {
      this.lastSeen = lastSeen;
      return this;
    }

    public Builder lastChange(LocalDateTime lastChange) {
      this.lastChange = lastChange;
      return this;
    }

    public Builder score(Double score) {
      this.score = score;
      return this;
    }

    public Builder features(Map<String, Double> features) {
      this.features = features;
      return this;
    }

    public Builder match(Boolean match) {
      this.match = match;
      return this;
    }

    public Builder token(String token) {
      this.token = token;
      return this;
    }

    public ScoredEntity build() {
      return new ScoredEntity(this);
    }
  }
}
