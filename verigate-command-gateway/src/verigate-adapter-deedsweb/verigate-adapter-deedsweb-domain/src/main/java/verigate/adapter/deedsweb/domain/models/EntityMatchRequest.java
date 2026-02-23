/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.domain.models;

import java.util.List;
import java.util.Map;

/**
 * Request model for OpenSanctions entity matching.
 */
public class EntityMatchRequest {

  private final String dataset;
  private final Map<String, EntityExample> queries;
  private final Integer limit;
  private final Double threshold;
  private final Double cutoff;
  private final String algorithm;
  private final List<String> includeDatasets;
  private final List<String> excludeDatasets;
  private final List<String> excludeSchemas;
  private final List<String> topics;
  private final String changedSince;
  private final List<String> excludeEntityIds;

  private EntityMatchRequest(Builder builder) {
    this.dataset = builder.dataset;
    this.queries = builder.queries;
    this.limit = builder.limit;
    this.threshold = builder.threshold;
    this.cutoff = builder.cutoff;
    this.algorithm = builder.algorithm;
    this.includeDatasets = builder.includeDatasets;
    this.excludeDatasets = builder.excludeDatasets;
    this.excludeSchemas = builder.excludeSchemas;
    this.topics = builder.topics;
    this.changedSince = builder.changedSince;
    this.excludeEntityIds = builder.excludeEntityIds;
  }

  // Getters
  public String getDataset() {
    return dataset;
  }

  public Map<String, EntityExample> getQueries() {
    return queries;
  }

  public Integer getLimit() {
    return limit;
  }

  public Double getThreshold() {
    return threshold;
  }

  public Double getCutoff() {
    return cutoff;
  }

  public String getAlgorithm() {
    return algorithm;
  }

  public List<String> getIncludeDatasets() {
    return includeDatasets;
  }

  public List<String> getExcludeDatasets() {
    return excludeDatasets;
  }

  public List<String> getExcludeSchemas() {
    return excludeSchemas;
  }

  public List<String> getTopics() {
    return topics;
  }

  public String getChangedSince() {
    return changedSince;
  }

  public List<String> getExcludeEntityIds() {
    return excludeEntityIds;
  }

  /**
   * Builder for EntityMatchRequest.
   */
  public static class Builder {
    private String dataset;
    private Map<String, EntityExample> queries;
    private Integer limit;
    private Double threshold;
    private Double cutoff;
    private String algorithm;
    private List<String> includeDatasets;
    private List<String> excludeDatasets;
    private List<String> excludeSchemas;
    private List<String> topics;
    private String changedSince;
    private List<String> excludeEntityIds;

    public Builder dataset(String dataset) {
      this.dataset = dataset;
      return this;
    }

    public Builder queries(Map<String, EntityExample> queries) {
      this.queries = queries;
      return this;
    }

    public Builder limit(Integer limit) {
      this.limit = limit;
      return this;
    }

    public Builder threshold(Double threshold) {
      this.threshold = threshold;
      return this;
    }

    public Builder cutoff(Double cutoff) {
      this.cutoff = cutoff;
      return this;
    }

    public Builder algorithm(String algorithm) {
      this.algorithm = algorithm;
      return this;
    }

    public Builder includeDatasets(List<String> includeDatasets) {
      this.includeDatasets = includeDatasets;
      return this;
    }

    public Builder excludeDatasets(List<String> excludeDatasets) {
      this.excludeDatasets = excludeDatasets;
      return this;
    }

    public Builder excludeSchemas(List<String> excludeSchemas) {
      this.excludeSchemas = excludeSchemas;
      return this;
    }

    public Builder topics(List<String> topics) {
      this.topics = topics;
      return this;
    }

    public Builder changedSince(String changedSince) {
      this.changedSince = changedSince;
      return this;
    }

    public Builder excludeEntityIds(List<String> excludeEntityIds) {
      this.excludeEntityIds = excludeEntityIds;
      return this;
    }

    public EntityMatchRequest build() {
      return new EntityMatchRequest(this);
    }
  }
}
