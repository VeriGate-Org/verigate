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
 * Specifies a validation rule that an entity's name field must not contain any suffixes. This class
 * uses a human name parser to analyze the name and identify if any segment of the name includes
 * suffixes. It is particularly useful in contexts where name suffixes might affect data processing
 * or where uniformity in name representation is required.
 *
 * @param <T> the type of entity being validated
 */
public final class NameNoSuffixSpecification<T> implements Specification<T> {

  // The name of the field to be validated.
  private final String fieldName;

  // Function to retrieve the string value of the field from an entity.
  private final Function<T, String> getStringValue;

  /**
   * Constructs a new specification for ensuring that a name does not contain suffixes.
   *
   * @param fieldName The field name to extract the string value from the entity.
   * @param getStringValue Function to extract the string value from the entity.
   */
  public NameNoSuffixSpecification(String fieldName, Function<T, String> getStringValue) {
    this.fieldName = fieldName;
    this.getStringValue = getStringValue;
  }

  /**
   * Evaluates the instance to determine if the specified field's value contains suffixes.
   *
   * @param instance the instance to be validated
   * @return a {@link SpecificationResult} indicating whether the field's value meets the
   *     specification
   */
  @Override
  public SpecificationResult isSatisfiedBy(T instance) {

    HumanNameParserBuilder builder = new HumanNameParserBuilder(getStringValue.apply(instance));
    HumanNameParserParser parser = builder.build();

    var passed = StringUtils.isBlank(parser.getSuffix());

    return SpecificationUtils.getResult(
        passed, this.getClass(), GenericInvariantError.NAME_FIELD_CONTAINS_SUFFIX, fieldName);
  }
}
