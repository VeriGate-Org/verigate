/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.bff.validators;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** Represents a valid name string annotation. */
@Documented
@Constraint(validatedBy = DefaultNameValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface ValidName {
  /**
   * Returns the error message template.
   *
   * @return the error message template
   */
  String message() default "INVALID_NAME";

  /**
   * Returns the groups.
   *
   * @return the groups
   */
  Class<?>[] groups() default {};

  /**
   * Returns the payload.
   *
   * @return the payload
   */
  Class<? extends Payload>[] payload() default {};
}
