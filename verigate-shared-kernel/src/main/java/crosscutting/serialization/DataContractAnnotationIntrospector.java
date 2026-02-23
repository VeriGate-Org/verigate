/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.serialization;

import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

/**
 * Extends Jackson's AnnotationIntrospector to customize the process of mapping Java object
 * properties to JSON keys (and vice versa) during serialization and deserialization. This
 * customization is based on the presence of {@link DataContract} annotations on fields or methods,
 * allowing for dynamic property naming based on annotation values.
 * <p>
 * If a {@link DataContract} annotation is present on an element, its {@code value} is used as the
 * property name in JSON. This allows for a flexible mapping strategy that can accommodate different
 * naming conventions without modifying the serialized class structure. When {@link DataContract} is
 * absent, this class falls back to Jackson' default property naming strategy, ensuring
 * compatibility and flexibility.
 * </p>
 */
public final class DataContractAnnotationIntrospector extends JacksonAnnotationIntrospector {

  /**
   * Determines the property name for serialization purposes from the given annotated element.
   * If the element is annotated with {@link DataContract}, the value specified in the annotation
   * is used as the property name. Otherwise, the method falls back to the superclass implementation
   * for determining the name.
   *
   * @param a The annotated element to inspect.
   * @return A {@link PropertyName} object representing the name to use for serialization.
   *         This could be the value from a {@link DataContract} annotation or the default Jackson
   *         naming.
   */
  @Override
  public PropertyName findNameForSerialization(Annotated a) {
    DataContract dc = a.getAnnotation(DataContract.class);
    if (dc != null) {
      return new PropertyName(dc.value());
    }

    // Fall back to default behavior if no @DataContract is present
    return super.findNameForSerialization(a);
  }

  /**
   * Determines the property name for deserialization purposes from the given annotated element.
   * Similar to {@link #findNameForSerialization(Annotated)}, this method checks for a
   * {@link DataContract} annotation and uses its value if present. Otherwise, it relies on the
   * superclass's implementation.
   *
   * @param a The annotated element to inspect.
   * @return A {@link PropertyName} object representing the name to use for deserialization,
   *         derived either from a {@link DataContract} annotation or the default Jackson naming
   *         strategy.
   */
  @Override
  public PropertyName findNameForDeserialization(Annotated a) {
    DataContract dc = a.getAnnotation(DataContract.class);
    if (dc != null) {
      return new PropertyName(dc.value());
    }

    // Fall back to default behavior if no @DataContract is present
    return super.findNameForDeserialization(a);
  }
}
