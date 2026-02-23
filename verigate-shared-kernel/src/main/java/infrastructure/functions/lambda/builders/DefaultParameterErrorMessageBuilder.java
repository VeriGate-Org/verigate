/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.functions.lambda.builders;

import domain.exceptions.PermanentException;
import domain.exceptions.StringExceptionBuilder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * `ParameterExceptionBuilder` is the class that represents the builder for the parameter
 * exceptions.
 */
public class DefaultParameterErrorMessageBuilder implements ParameterErrorMessageBuilder {
  private Set<String> errorMessages = new HashSet<>();
  private Map<String, String> parameters = new HashMap<String, String>();
  private Set<String> requiredKeys = new HashSet<>();

  public void setExceptions(Set<String> errorMessages) {
    this.errorMessages = errorMessages;
  }

  /**
   * Add required keys to the builder.
   *
   * @param requiredKeys the required keys for the dto.
   * @return the builder.
   */
  public DefaultParameterErrorMessageBuilder addRequiredKeys(Set<String> requiredKeys) {
    this.requiredKeys.addAll(requiredKeys);
    return this;
  }

  /**
   * Add parameters to the builder.
   *
   * @param parameters the path parameters. This is generally for http path and query parameters.
   * @return the builder.
   * @throws PermanentException if the parameters are null.
   */
  public DefaultParameterErrorMessageBuilder addParameters(Map<String, String> parameters)
      throws PermanentException {

    if (parameters == null) {
      throw new PermanentException(
          StringExceptionBuilder.builder().withDetail(("Parameters cannot be null")).build());
    }

    this.parameters.putAll(parameters);
    return this;
  }

  /**
   * Build the exceptions.
   *
   * @return the exceptions.
   */
  public Set<String> build() {
    for (String key : requiredKeys) {
      if (!parameters.containsKey(key)) {
        errorMessages.add(key + " is required");
      }
    }
    return errorMessages;
  }
}
