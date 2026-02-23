/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.invariants.rules;

import domain.invariants.Specification;
import domain.invariants.SpecificationResult;
import java.util.function.Function;

/**
 * Represents a specification for validating entity names according to a set of defined rules. This
 * class enforces multiple constraints including length requirements, absence of special characters,
 * and the presence of unwanted name components like initials or postnominals. It also checks for
 * the use of profanity in different languages.
 *
 * @param <T> the type of entity being validated
 */
public final class SurnameSpecification<T> implements Specification<T> {

  // The name of the field being validated.
  private final String fieldName;

  // Function to extract the string value of the field from an entity.
  private final Function<T, String> getStringValue;

  // Minimum length requirement for the field value.
  private final int requiredMinLength;

  // Maximum length requirement for the field value.
  private final int requiredMaxLength;

  /**
   * Constructs a new {@code NameSpecification} with the specified configurations for name
   * validation.
   *
   * @param fieldName the name of the field
   * @param getStringValue a function to extract the string value from the entity
   * @param requiredMinLength the minimum allowable length of the field value
   * @param requiredMaxLength the maximum allowable length of the field value
   */
  public SurnameSpecification(
      String fieldName,
      Function<T, String> getStringValue,
      int requiredMinLength,
      int requiredMaxLength) {
    this.fieldName = fieldName;
    this.getStringValue = getStringValue;
    this.requiredMinLength = requiredMinLength;
    this.requiredMaxLength = requiredMaxLength;
  }

  /**
   * Evaluates the provided instance against a series of specifications to ensure its name field
   * meets all specified validations. This method creates a composite specification that combines
   * individual rules for character formatting, structural integrity, and content appropriateness of
   * a name field.
   *
   * @param instance The instance being validated
   * @return A {@link SpecificationResult} that indicates whether the instance's name field meets
   *     all the validation criteria.
   */
  public SpecificationResult isSatisfiedBy(T instance) {

    // Initialize a specification to ensure the name starts with a valid letter.
    var nameMustStartWithLetter =
        new NameMustStartWithLetterSpecification<>(this.fieldName, this.getStringValue);

    // Initialize a specification to ensure there are no consecutive apostrophes in the name.
    var nameNoConsecutiveApostrophe =
        new NameNoConsecutiveApostropheSpecification<>(this.fieldName, this.getStringValue);

    // Initialize a specification to ensure there are no consecutive hyphens in the name.
    var nameNoConsecutiveHyphen =
        new NameNoConsecutiveHyphenSpecification<>(this.fieldName, this.getStringValue);

    // Initialize a specification to ensure there are no consecutive spaces in the name.
    var nameNoConsecutiveSpaces =
        new NameNoConsecutiveSpacesSpecification<>(this.fieldName, this.getStringValue);

    // Validate against special characters in the name using a predefined allowed character set.
    var nameSpecialCharacters =
        new NameNoSpecialCharactersSpecification<>(this.fieldName, this.getStringValue);

    // Ensure there are no leading or trailing whitespaces in the name.
    var noWhitespace =
        new NoTrailingAndLeadingWhitespaceSpecification<>(this.fieldName, this.getStringValue);

    // Check that the string's length is within the specified minimum and maximum range.
    var stringLength =
        new StringLengthSpecification<>(
            this.fieldName, this.getStringValue, this.requiredMinLength, this.requiredMaxLength);

    // Combine all individual specifications into a single composite specification. This allows for
    // a single point of validation
    // that checks all rules and only passes if all conditions are satisfied.
    Specification<T> compositeSpecification =
        nameSpecialCharacters
            .and(nameMustStartWithLetter)
            .and(nameNoConsecutiveApostrophe)
            .and(nameNoConsecutiveHyphen)
            .and(nameNoConsecutiveSpaces)
            .and(noWhitespace)
            .and(stringLength);

    return compositeSpecification.isSatisfiedBy(instance);
  }
}
