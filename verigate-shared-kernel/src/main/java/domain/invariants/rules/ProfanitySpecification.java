/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.invariants.rules;

import com.modernmt.text.profanity.ProfanityFilter;
import domain.invariants.FieldInvariantError;
import domain.invariants.GenericInvariantError;
import domain.invariants.Specification;
import domain.invariants.SpecificationResult;
import domain.invariants.SpecificationUtils;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;

/**
 * Specification to ensure that a string field in an entity does not contain profanity,
 * supporting multiple languages.
 *
 * @param <T> The type of the entity being validated.
 */
public final class ProfanitySpecification<T> implements Specification<T> {

  private static final ProfanityFilter PROFANITY_FILTER = new ProfanityFilter();

  private final String fieldName;
  private final Function<T, String> getStringValue;
  private final Set<String> languages;

  /**
   * Constructs a new ProfanitySpecification for checking if a string field contains
   * profane words.
   *
   * @param fieldName The name of the field to check for profanity.
   * @param getStringValue A function to extract the string value from the entity.
   * @param languages A set of languages to consider when checking for profanity.
   */
  public ProfanitySpecification(
      String fieldName, Function<T, String> getStringValue, Set<String> languages) {
    this.fieldName = fieldName;
    this.getStringValue = getStringValue;
    this.languages = languages;
  }

  /**
   * Validates the specified instance's string field to ensure it does not contain
   * any profane language.
   *
   * @param instance The instance being validated.
   * @return A {@link SpecificationResult} indicating whether the string field
   *         contains profane language or not.
   */
  @Override
  public SpecificationResult isSatisfiedBy(T instance) {

    String value = getStringValue.apply(instance);
    if (StringUtils.isBlank(value)) {
      return SpecificationResult.success();  // Returns success if the string is empty or null.
    }

    Optional<String> profaneLanguage =
        languages.parallelStream()
            .filter(language -> PROFANITY_FILTER.test(language, value))
            .findFirst();

    return SpecificationUtils.getResult(
        profaneLanguage.isEmpty(),
        this.getClass(),
        new FieldInvariantError(GenericInvariantError.STRING_FIELD_CONTAINS_PROFANITY, fieldName));
  }
}
