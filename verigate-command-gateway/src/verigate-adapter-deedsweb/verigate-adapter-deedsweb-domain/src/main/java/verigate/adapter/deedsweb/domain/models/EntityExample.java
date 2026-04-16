/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.domain.models;

import java.util.List;
import java.util.Map;

/**
 * Entity example for DeedsWeb matching requests.
 */
public class EntityExample {

  private final String id;
  private final String schema;
  private final Map<String, List<String>> properties;

  private EntityExample(Builder builder) {
    this.id = builder.id;
    this.schema = builder.schema;
    this.properties = builder.properties;
  }

  // Getters
  public String getId() {
    return id;
  }

  public String getSchema() {
    return schema;
  }

  public Map<String, List<String>> getProperties() {
    return properties;
  }

  /**
   * Builder for EntityExample.
   */
  public static class Builder {
    private String id;
    private String schema;
    private Map<String, List<String>> properties;

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder schema(String schema) {
      this.schema = schema;
      return this;
    }

    public Builder properties(Map<String, List<String>> properties) {
      this.properties = properties;
      return this;
    }

    public EntityExample build() {
      return new EntityExample(this);
    }
  }
}
