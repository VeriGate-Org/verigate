/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.functions.lambda.builders;

import domain.exceptions.PermanentException;
import java.util.Map;
import java.util.Set;

/**
 * `ParameterExceptionBuilder` is the class that represents
 * the builder for the parameter exceptions.
 */
public interface ParameterErrorMessageBuilder {
  public ParameterErrorMessageBuilder addRequiredKeys(Set<String> requiredKeys);

  public ParameterErrorMessageBuilder addParameters(Map<String, String> parameters)
      throws PermanentException;

  public Set<String> build();
}
