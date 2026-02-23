/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.invariants;

import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for validating invariants across domain entities. Provides methods to generate a
 * {@link SpecificationResult} based on whether invariants are passed or failed.
 */
public final class SpecificationUtils {

  /**
   * Evaluates an invariant condition and returns the corresponding result.
   *
   * @param <ClassT> The class type of the domain entity being evaluated.
   * @param passed A boolean indicating if the invariant condition passed.
   * @param clazz The class of the domain entity.
   * @param error The error to be returned if the condition fails.
   * @return A successful {@link SpecificationResult} if the condition passed, otherwise a failed
   *     result encapsulating the provided error.
   */
  @NotNull
  public static <ClassT> SpecificationResult getResult(
      boolean passed, Class<ClassT> clazz, InvariantError error) {
    return passed
        ? SpecificationResult.success()
        : SpecificationUtils.getFailedResult(clazz, error);
  }

  /**
   * Evaluates an invariant condition related to a specific field and returns the result.
   *
   * @param <ClassT> The class type of the domain entity being evaluated.
   * @param passed A boolean indicating if the invariant condition passed.
   * @param clazz The class of the domain entity.
   * @param error The error to be returned if the condition fails.
   * @param fieldName The field related to the invariant condition.
   * @return A successful {@link SpecificationResult} if the condition passed, otherwise a failed
   *     result encapsulating the error specific to the field.
   */
  @NotNull
  public static <ClassT> SpecificationResult getResult(
      boolean passed, Class<ClassT> clazz, InvariantError error, String fieldName) {
    return passed
        ? SpecificationResult.success()
        : SpecificationUtils.getFailedResult(clazz, new FieldInvariantError(error, fieldName));
  }

  /**
   * Evaluates an invariant condition related to a specific field and returns the result.
   *
   * @param passed A boolean indicating if the invariant condition passed.
   * @param name The name of the specification.
   * @param error The error to be returned if the condition fails.
   * @param fieldName The field related to the invariant condition.
   * @return A successful {@link SpecificationResult} if the condition passed, otherwise a failed
   *     result encapsulating the error specific to the field.
   */
  @NotNull
  public static SpecificationResult getResult(
      boolean passed, String name, InvariantError error, String fieldName) {
    return passed
        ? SpecificationResult.success()
        : SpecificationUtils.getFailedResult(name, new FieldInvariantError(error, fieldName));
  }

  /**
   * Evaluates a set of invariant conditions and returns the corresponding result.
   *
   * @param <ClassT> The class type of the domain entity being evaluated.
   * @param passed A boolean indicating if all the invariant conditions passed.
   * @param clazz The class of the domain entity.
   * @param errors A set of errors to be returned if the conditions fail.
   * @return A successful {@link SpecificationResult} if all conditions passed, otherwise a failed
   *     result encapsulating all errors.
   */
  @NotNull
  public static <ClassT> SpecificationResult getResult(
      boolean passed, Class<ClassT> clazz, Set<InvariantError> errors) {
    return passed
        ? SpecificationResult.success()
        : SpecificationUtils.getFailedResult(clazz, errors);
  }

  /**
   * Retrieves a {@link SpecificationResult} based on the evaluation of given specification results
   * for a specified class type. This method filters out any specification results that are not
   * satisfied, collects the corresponding error messages, and determines whether the overall result
   * is successful or contains failures.
   *
   * @param <ClassT> the type of class the specification results are being evaluated against
   * @param clazz the class object representing the type ClassT for which the specification results
   *     are evaluated
   * @param specificationResults a set of {@link SpecificationResult} objects that have been
   *     collected for evaluation
   * @return a {@link SpecificationResult} indicating either success (if no errors were found) or
   *     failure (with associated error messages)
   * @throws NullPointerException if any of the required parameters are null
   */
  @NotNull
  public static <ClassT> SpecificationResult getResult(
      Class<ClassT> clazz, Set<SpecificationResult> specificationResults) {
    Set<InvariantError> errorSet =
        specificationResults.stream()
            .filter(result -> !result.satisfied())
            .flatMap(result -> result.errorMessages().stream())
            .map(ErrorRecord::getError)
            .collect(Collectors.toSet());

    return errorSet.isEmpty()
        ? SpecificationResult.success()
        : SpecificationUtils.getFailedResult(clazz, errorSet);
  }

  /**
   * Utility method to evaluate and aggregate {@link SpecificationResult} from a set of
   * specification checks. This method is typically used in the context of the Specification pattern
   * to determine if an entity meets all required specifications based on the results of checks on
   * its dependencies or attributes.
   *
   * @param <ClassT> The class type of the entity for which the specification results are being
   *     aggregated.
   * @param clazz The class of the entity. Used for logging or reporting purposes.
   * @param specificationResults A set of {@link SpecificationResult} representing the outcomes of
   *     various specification checks.
   * @param error An optional additional {@link InvariantError} to include in the result if there
   *     are any failed specifications. This could represent a higher-level error that encapsulates
   *     the specific errors.
   * @return {@link SpecificationResult} which represents either a success if all specifications are
   *     satisfied or a failure with a set of errors if one or more specifications are not
   *     satisfied.
   */
  @NotNull
  public static <ClassT> SpecificationResult getResult(
      Class<ClassT> clazz, Set<SpecificationResult> specificationResults, InvariantError error) {

    // Stream through specification results to collect all errors from unsatisfied specifications.
    Set<InvariantError> errorSet =
        specificationResults.stream()
            .filter(result -> !result.satisfied()) // Filter to include only unsatisfied results.
            .flatMap(
                result -> result.errorMessages().stream()) // Flatten to stream of error messages.
            .map(ErrorRecord::getError) // Extract InvariantError from each ErrorRecord.
            .collect(Collectors.toSet()); // Collect all errors into a Set.

    // Optionally add a standalone error to the error set if it exists and is relevant.
    if (!errorSet.isEmpty() && error != null) {
      errorSet.add(error);
    }

    // Return a success result if there are no errors, otherwise return a failure result with the
    // collected errors.
    return errorSet.isEmpty()
        ? SpecificationResult.success()
        : SpecificationUtils.getFailedResult(clazz, errorSet);
  }

  /**
   * Creates a failed {@link SpecificationResult} for a single error.
   *
   * @param <ClassT> The class type of the domain entity.
   * @param clazz The class of the domain entity.
   * @param error The error associated with the failure.
   * @return A {@link SpecificationResult} encapsulating the failure.
   */
  @NotNull
  private static <ClassT> SpecificationResult getFailedResult(
      Class<ClassT> clazz, InvariantError error) {
    return SpecificationResult.failure(
        Set.of(new DefaultErrorRecord(clazz.getSimpleName(), error)));
  }

  /**
   * Creates a failed {@link SpecificationResult} for a single error. This method is applicable when
   * the domain entity or context can be identified simply by a name rather than a class type.
   *
   * @param name The name representing the domain entity or context. This is used instead of a class
   *     to identify the subject of the error in scenarios where a class reference is not applicable
   *     or available.
   * @param error The error associated with the failure. This encapsulates the specific failure
   *     reason.
   * @return A {@link SpecificationResult} encapsulating the failure. The result provides a clear
   *     indication of the non-successful outcome with details of the associated error.
   */
  @NotNull
  private static SpecificationResult getFailedResult(String name, InvariantError error) {
    return SpecificationResult.failure(Set.of(new DefaultErrorRecord(name, error)));
  }

  /**
   * Creates a failed {@link SpecificationResult} for a set of errors.
   *
   * @param <ClassT> The class type of the domain entity.
   * @param clazz The class of the domain entity.
   * @param errors The set of errors associated with the failures.
   * @return A {@link SpecificationResult} encapsulating all failures.
   */
  @NotNull
  private static <ClassT> SpecificationResult getFailedResult(
      Class<ClassT> clazz, Set<InvariantError> errors) {
    Set<ErrorRecord> errorRecords =
        errors.stream()
            .map(error -> new DefaultErrorRecord(clazz.getSimpleName(), error))
            .collect(Collectors.toSet());
    return SpecificationResult.failure(errorRecords);
  }
}
