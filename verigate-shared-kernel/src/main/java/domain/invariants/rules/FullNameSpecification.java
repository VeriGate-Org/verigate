/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.invariants.rules;

import domain.invariants.Specification;
import domain.invariants.SpecificationResult;
import java.util.Set;
import java.util.function.Function;

/**
 * Represents a specification for validating entity names according to a set of defined rules. This
 * class enforces multiple constraints including length requirements, absence of special characters,
 * and the presence of unwanted name components like initials or postnominals. It also checks for
 * the use of profanity in different languages.
 *
 * @param <T> the type of entity being validated
 */
public final class FullNameSpecification<T> implements Specification<T> {

  // The name of the field being validated.
  private final String fieldName;

  // Function to extract the string value of the field from an entity.
  private final Function<T, String> getStringValue;

  // Minimum length requirement for the field value.
  private final int requiredMinLength;

  // Maximum length requirement for the field value.
  private final int requiredMaxLength;

  // Set of languages to check for profanity within the field value.
  private final Set<String> profanityLanguages;

  // Flags to indicate prohibition of various name components.
  private final boolean checkForProfanities;
  private final boolean prohibitInitials;
  private final boolean prohibitPostnominals;
  private final boolean prohibitSuffixes;
  private final boolean prohibitSalutations;

  /**
   * Constructs a new {@code NameSpecification} with the specified configurations for name
   * validation.
   *
   * @param fieldName the name of the field
   * @param getStringValue a function to extract the string value from the entity
   * @param requiredMinLength the minimum allowable length of the field value
   * @param requiredMaxLength the maximum allowable length of the field value
   * @param profanityLanguages a set of languages to check for profanity
   * @param prohibitInitials whether initials are prohibited
   * @param prohibitPostnominals whether postnominals are prohibited
   * @param prohibitSuffixes whether suffixes are prohibited
   * @param prohibitSalutations whether salutations are prohibited
   */
  public FullNameSpecification(
      String fieldName,
      Function<T, String> getStringValue,
      int requiredMinLength,
      int requiredMaxLength,
      Set<String> profanityLanguages,
      boolean checkForProfanities,
      boolean prohibitInitials,
      boolean prohibitPostnominals,
      boolean prohibitSuffixes,
      boolean prohibitSalutations) {
    this.fieldName = fieldName;
    this.getStringValue = getStringValue;
    this.requiredMinLength = requiredMinLength;
    this.requiredMaxLength = requiredMaxLength;
    this.profanityLanguages = profanityLanguages;
    this.checkForProfanities = checkForProfanities;
    this.prohibitInitials = prohibitInitials;
    this.prohibitPostnominals = prohibitPostnominals;
    this.prohibitSuffixes = prohibitSuffixes;
    this.prohibitSalutations = prohibitSalutations;
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

    // Check for profanity in the specified languages if configured to do so.
    var profanity =
        new ProfanitySpecification<>(this.fieldName, this.getStringValue, this.profanityLanguages);

    // Prohibit certain components like initials, postnominals, suffixes, and salutations based on
    // configuration flags.
    var nameProhibitedComponents =
        new NameNoSpecialComponentsSpecification<>(
            this.fieldName,
            this.getStringValue,
            this.prohibitInitials,
            this.prohibitPostnominals,
            this.prohibitSuffixes,
            this.prohibitSalutations);

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
            .and(stringLength)
            .conditionalAnd(profanity, checkForProfanities)
            .and(nameProhibitedComponents);

    return compositeSpecification.isSatisfiedBy(instance);
  }
}
