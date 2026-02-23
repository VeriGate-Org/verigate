/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for OpenSanctions scored entity.
 */
public class ScoredEntityDto {

  @JsonProperty("id")
  private String id;

  @JsonProperty("caption")
  private String caption;

  @JsonProperty("schema")
  private String schema;

  @JsonProperty("properties")
  private Map<String, List<Object>> properties;

  @JsonProperty("datasets")
  private List<String> datasets;

  @JsonProperty("referents")
  private List<String> referents;

  @JsonProperty("target")
  private Boolean target;

  @JsonProperty("first_seen")
  private LocalDateTime firstSeen;

  @JsonProperty("last_seen")
  private LocalDateTime lastSeen;

  @JsonProperty("last_change")
  private LocalDateTime lastChange;

  @JsonProperty("score")
  private Double score;

  @JsonProperty("features")
  private Map<String, Double> features;

  @JsonProperty("match")
  private Boolean match;

  @JsonProperty("token")
  private String token;

  // Getters and setters
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCaption() {
    return caption;
  }

  public void setCaption(String caption) {
    this.caption = caption;
  }

  public String getSchema() {
    return schema;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }

  public Map<String, List<Object>> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, List<Object>> properties) {
    this.properties = properties;
  }

  public List<String> getDatasets() {
    return datasets;
  }

  public void setDatasets(List<String> datasets) {
    this.datasets = datasets;
  }

  public List<String> getReferents() {
    return referents;
  }

  public void setReferents(List<String> referents) {
    this.referents = referents;
  }

  public Boolean getTarget() {
    return target;
  }

  public void setTarget(Boolean target) {
    this.target = target;
  }

  public LocalDateTime getFirstSeen() {
    return firstSeen;
  }

  public void setFirstSeen(LocalDateTime firstSeen) {
    this.firstSeen = firstSeen;
  }

  public LocalDateTime getLastSeen() {
    return lastSeen;
  }

  public void setLastSeen(LocalDateTime lastSeen) {
    this.lastSeen = lastSeen;
  }

  public LocalDateTime getLastChange() {
    return lastChange;
  }

  public void setLastChange(LocalDateTime lastChange) {
    this.lastChange = lastChange;
  }

  public Double getScore() {
    return score;
  }

  public void setScore(Double score) {
    this.score = score;
  }

  public Map<String, Double> getFeatures() {
    return features;
  }

  public void setFeatures(Map<String, Double> features) {
    this.features = features;
  }

  public Boolean getMatch() {
    return match;
  }

  public void setMatch(Boolean match) {
    this.match = match;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
