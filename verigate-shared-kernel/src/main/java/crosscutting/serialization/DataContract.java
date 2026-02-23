/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code MappedProperty} annotation is used to define a mapping between a class field and a
 * property name. This annotation facilitates the abstraction of field naming strategies, allowing
 * for flexible mapping of class fields to external representations without directly coupling the
 * domain model to specific serialization frameworks or conventions.
 *
 * <p>This annotation must be placed on fields, and it requires a single parameter that specifies
 * the external property name to which the field is mapped. This approach allows for easy adaptation
 * and reconfiguration of field mappings to accommodate various external systems or data formats.
 *
 * <p>Usage example:
 *
 * <pre>
 * public class ExampleModel {
 *     {@code @DataContract("custom_name")}
 *     private String exampleField;
 *
 *     // Getter and Setter
 * }
 * </pre>
 *
 * @implNote This annotation is intended for use with reflection-based utilities that dynamically
 *     interpret field-to-property mappings at runtime. Implementors should ensure that any utility
 *     leveraging this annotation can gracefully handle access restrictions, inheritance, and other
 *     complexities of Java field semantics.
 * @see ElementType#FIELD
 * @see RetentionPolicy#RUNTIME
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataContract {

  /**
   * Specifies the name of the property to which the annotated field is mapped. This name is used to
   * link the field with an external representation, such as a key in a JSON object, a column name
   * in a database table, or another similar mapping in various data formats or communication
   * protocols.
   *
   * @return the name of the property corresponding to the annotated field
   */
  String value() default "";
}
