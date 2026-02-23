/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.functions.lambda.mappers;

/**
 * The BaseCommandMapper interface defines a contract for mapping from a source object of type S to
 * a target object of type T.
 *
 * @param <T> the target object type
 * @param <S> the source object type
 */
public interface BaseCommandMapper<T, S> {

  /**
   * Converts a CommandDto object of type S to a command object of type T.
   *
   * @param commandDto the CommandDto object to be converted
   * @return the converted command object
   */
  T toCommand(S commandDto);
}
