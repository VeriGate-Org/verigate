/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.invariants.rules;

import com.tupilabs.human_name_parser.HumanNameParserBuilder;
import com.tupilabs.human_name_parser.HumanNameParserParser;
import domain.invariants.GenericInvariantError;
import domain.invariants.Specification;
import domain.invariants.SpecificationResult;
import domain.invariants.SpecificationUtils;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;

/**
 * Specifies a validation rule that an entity's name field must not contain salutations such as
 * "Mr.", "Mrs.", "Dr.", etc. This class uses a human name parser to dissect the name and identify
 * if any segment of the name includes salutations. It is particularly useful in formal data
 * processing environments where salutations may need to be separated from actual names.
 *
 * @param <T> the type of entity being validated
 */
public final class NameNoSalutationSpecification<T> implements Specification<T> {

  // The name of the field to be validated.
  private final String fieldName;

  // Function to retrieve the string value of the field from an entity.
  private final Function<T, String> getStringValue;

  /**
   * Constructs a new specification for ensuring that a name does not contain salutations.
   *
   * @param fieldName the name of the field to validate
   * @param getStringValue a function to extract the string value from the entity
   */
  public NameNoSalutationSpecification(String fieldName, Function<T, String> getStringValue) {
    this.fieldName = fieldName;
    this.getStringValue = getStringValue;
  }

  /**
   * Evaluates the instance to determine if the specified field's value contains salutations.
   *
   * @param instance the instance to be validated
   * @return a {@link SpecificationResult} indicating whether the field's value meets the
   *     specification
   */
  @Override
  public SpecificationResult isSatisfiedBy(T instance) {

    HumanNameParserBuilder builder = new HumanNameParserBuilder(getStringValue.apply(instance));
    HumanNameParserParser parser = builder.build();

    var passed = StringUtils.isBlank(parser.getSalutation());

    return SpecificationUtils.getResult(
        passed, this.getClass(), GenericInvariantError.NAME_FIELD_CONTAINS_SALUTATIONS, fieldName);
  }
}
