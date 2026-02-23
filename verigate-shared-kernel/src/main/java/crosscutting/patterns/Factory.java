/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.patterns;

import javax.validation.constraints.NotNull;
import org.jetbrains.annotations.Contract;

/**
 * Represents a generic factory interface defining a method for creating objects. The interface is
 * parameterized to allow for creating objects of a specific type from an input of another specific
 * type. It can be implemented by classes that encapsulate the creation logic of specific types of
 * objects, allowing for more flexible and maintainable code by decoupling object creation from its
 * usage.
 *
 * <p>This interface can be seen as a part of the Factory Method pattern, which is a creational
 * design pattern that provides an interface for creating objects in a superclass but allows
 * subclasses to alter the type of objects that will be created. To learn more about the Factory
 * Method pattern, see the Wikipedia article linked below.
 *
 * @param <OutT> the type of object that this factory creates. This is the output type that the
 *     {@link #create(Object)} method will return.
 * @param <InT> the type of the input required to create an object. This is the type of the argument
 *     accepted by the {@link #create(Object)} method.
 * @see <a href="https://en.wikipedia.org/wiki/Factory_method_pattern">Factory Method Pattern on
 *     Wikipedia</a>
 */
public interface Factory<OutT, InT> {

  /**
   * Creates an object of type TOut using the provided input of type TIn. Implementations of this
   * method should provide the necessary logic to create and configure instances of TOut based on
   * the given input.
   *
   * @param arg the input of type TIn used to create the object. This could be any relevant data or
   *     parameters required for the creation process.
   * @return an instance of TOut created based on the provided argument.
   */
  @NotNull
  @Contract("-> new")
  OutT create(InT arg);
}
