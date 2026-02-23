package domain.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.invariants.SpecificationResult;

/** This class represents an exception with a SpecificationResult detail. */
public class SpecificationExceptionBuilder {
  private SpecificationResult detail;
  private boolean success;

  private SpecificationExceptionBuilder() {
    // Private constructor
  }

  public static SpecificationExceptionBuilder builder() {
    return new SpecificationExceptionBuilder();
  }

  public SpecificationExceptionBuilder withDetail(SpecificationResult detail) {
    this.detail = detail;
    return this;
  }

  public SpecificationExceptionBuilder withSuccess(boolean success) {
    this.success = success;
    return this;
  }

  /**
   * Builds the exception.
   *
   * @return the exception as a json string.
   */
  public String build() {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }
  }

  public SpecificationResult getDetail() {
    return detail;
  }

  public boolean isSuccess() {
    return success;
  }
}
