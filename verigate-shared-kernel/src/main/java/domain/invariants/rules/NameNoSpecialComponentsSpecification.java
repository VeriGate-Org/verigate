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
 * Specifies a composite validation rule for an entity's name field to ensure it does not contain
 * specific components such as initials, postnominals, suffixes, and salutations, each of which can
 * be individually enabled or disabled. This specification class combines multiple specifications
 * into one for comprehensive name validation.
 *
 * @param <T> the type of entity being validated
 */
public final class NameNoSpecialComponentsSpecification<T> implements Specification<T> {

  // The name of the field to be validated.
  private final String fieldName;

  // Function to retrieve the string value of the field from an entity.
  private final Function<T, String> getStringValue;

  // Flags to enable or disable prohibition of specific name components.
  private final boolean prohibitInitials;
  private final boolean prohibitPostnominals;
  private final boolean prohibitSuffixes;
  private final boolean prohibitSalutations;

  /**
   * Primary constructor with options to enable or disable each specific component prohibition.
   *
   * @param fieldName The field name to extract the string value from the entity.
   * @param getStringValue Function to extract the string value from the entity.
   * @param prohibitInitials Boolean flag to enable/disable initials prohibition.
   * @param prohibitPostnominals Boolean flag to enable/disable postnominals prohibition.
   * @param prohibitSuffixes Boolean flag to enable/disable suffixes prohibition.
   * @param prohibitSalutations Boolean flag to enable/disable salutations prohibition.
   */
  public NameNoSpecialComponentsSpecification(
      String fieldName,
      Function<T, String> getStringValue,
      boolean prohibitInitials,
      boolean prohibitPostnominals,
      boolean prohibitSuffixes,
      boolean prohibitSalutations) {
    this.fieldName = fieldName;
    this.getStringValue = getStringValue;
    this.prohibitInitials = prohibitInitials;
    this.prohibitPostnominals = prohibitPostnominals;
    this.prohibitSuffixes = prohibitSuffixes;
    this.prohibitSalutations = prohibitSalutations;
  }

  /**
   * Convenience constructor that defaults all prohibitions to 'true'.
   *
   * @param fieldName The field name to extract the string value from the entity.
   * @param getStringValue Function to extract the string value from the entity.
   */
  public NameNoSpecialComponentsSpecification(
      String fieldName, Function<T, String> getStringValue) {
    this(fieldName, getStringValue, true, true, true, true);
  }

  /**
   * Evaluates the instance to determine if the specified field's value contains any prohibited
   * components.
   *
   * @param instance the instance to be validated
   * @return a {@link SpecificationResult} indicating whether the field's value meets the composite
   *     specifications
   */
  @Override
  public SpecificationResult isSatisfiedBy(T instance) {

    var nameProhibitInitials =
        new NameNoInitialsSpecification<>(this.fieldName, this.getStringValue);
    var nameProhibitPostnominal =
        new NameNoPostnominalSpecification<>(this.fieldName, this.getStringValue);
    var nameProhibitSuffix = new NameNoSuffixSpecification<>(this.fieldName, this.getStringValue);
    var nameProhibitSalutation =
        new NameNoSalutationSpecification<>(this.fieldName, this.getStringValue);

    // Combine all individual specifications into a composite specification based on the
    // configuration flags.
    var compositeSpecification =
        Specification.<T>create()
            .conditionalAnd(nameProhibitInitials, prohibitInitials)
            .conditionalAnd(nameProhibitPostnominal, prohibitPostnominals)
            .conditionalAnd(nameProhibitSuffix, prohibitSuffixes)
            .conditionalAnd(nameProhibitSalutation, prohibitSalutations);

    return compositeSpecification.isSatisfiedBy(instance);
  }
}
