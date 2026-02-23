/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.patterns;

import javax.validation.constraints.NotNull;
import org.jetbrains.annotations.Contract;

/**
 * This interface represents a simple factory for creating objects of a specific type.
 * It defines a method to create instances of a generic type {@code OutT}.
 *
 * @param <OutT> the type of object this factory creates
 */
public interface SimpleFactory<OutT> {

  /**
   * Creates an object of type TOut using the provided input of type TIn. Implementations of this
   * method should provide the necessary logic to create and configure instances of TOut based on
   * the given input.
   *
   * @return an instance of TOut created based on the provided argument.
   */
  @NotNull
  @Contract("-> new")
  OutT create();
}
