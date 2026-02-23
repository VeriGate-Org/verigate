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

/** Represents a valid enum validation annotation. */
@Documented
@Constraint(validatedBy = DefaultEnumValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface ValidEnumValue {
  /**
   * Returns the error message template.
   *
   * @return the error message template
   */
  String message() default "Invalid UUID";

  /**
   * Returns the enum class.
   *
   * @return the enum class
   */
  Class<? extends Enum<?>> enumClass();

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
