/*
 * Arthmatic + Karisani(c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.domain.common;

/**
 * Provides an interface for executing commands of a generic type {@code CommandT}. This interface
 * defines the contract for an invoker that takes a command and performs the operation dictated by
 * the command. It is part of the Command design pattern, typically used to decouple the sender and
 * the receiver of a command.
 *
 * @param <CommandT> the type of command this invoker handles
 */
public interface CommandInvoker<CommandT> {

  /**
   * Executes the specified command. Implementations of this method are responsible for invoking the
   * command's operation and ensuring that any associated action is performed.
   *
   * @param command the command to execute, encapsulating all necessary information for the
   *     operation
   */
  void execute(CommandT command);
}
