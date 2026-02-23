package domain.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class represents an exception with a string detail. */
public class StringExceptionBuilder {
  private static final Logger LOGGER = LoggerFactory.getLogger(StringExceptionBuilder.class);

  private String detail;
  private boolean success;

  private StringExceptionBuilder() {
    // Private constructor
  }

  public static StringExceptionBuilder builder() {
    return new StringExceptionBuilder();
  }

  public StringExceptionBuilder withDetail(String detail) {
    this.detail = detail;
    return this;
  }

  public StringExceptionBuilder withSuccess(boolean success) {
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
      LOGGER.error("Failed to build string exception message, default to null", e);
      return null;
    }
  }

  public String getDetail() {
    return detail;
  }

  public boolean isSuccess() {
    return success;
  }
}
