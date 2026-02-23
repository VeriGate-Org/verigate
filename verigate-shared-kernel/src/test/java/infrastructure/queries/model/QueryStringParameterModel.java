/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.queries.model;

import java.util.Map;
import java.util.Objects;

/**
 * QueryStringParameterModel is the model for the query string parameters.
 * This is used to map multiple query string parameters to a single model.
 */
public class QueryStringParameterModel {
  private final String queryString;
  private final String secondaryQueryString;
  private final String pathParameter;

  public QueryStringParameterModel(Map<String, String> params) {
    this.queryString = validate("queryStringValue", "queryString");
    this.secondaryQueryString = validate("secondaryQueryStringValue", "secondaryQueryString");
    this.pathParameter = validate("pathParameterValue", "pathParameter");
  }

  public String getQueryString() {
    return queryString;
  }

  public String getSecondaryQueryString() {
    return secondaryQueryString;
  }

  public String getPathParameter() {
    return pathParameter;
  }

  private static String validate(String value, String key) {
    Objects.requireNonNull(value, "Missing required query parameter: " + key);
    if (value.isEmpty()) {
      throw new IllegalArgumentException("Missing required query parameter: " + key);
    }
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QueryStringParameterModel that = (QueryStringParameterModel) o;
    return Objects.equals(queryString, that.queryString)
        && Objects.equals(secondaryQueryString, that.secondaryQueryString);
  }

  @Override
  public int hashCode() {
    return Objects.hash(queryString, secondaryQueryString);
  }
}
