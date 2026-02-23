/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.commands;

/**
 * The {@code CommandDispatcherWithReturn} interface defines a generic contract for dispatching
 * commands within the system. Implementors of this interface are responsible for
 * the mechanism by which commands of a specific type, extending from {@link BaseCommand},
 * are dispatched to the appropriate command handlers.
 *
 * <p>This interface facilitates a decoupled architecture by allowing components to send commands
 * without being concerned about the specifics of how these commands are processed or who processes
 * them. It is a key component in the implementation of a command-driven architecture, where the
 * flow of the program is determined by commands.
 *
 * @param <CommandT> The type of the command this dispatcher is designed to handle.
 */
public interface CommandDispatcherWithReturn<CommandT, ReturnT> {

  /**
   * Dispatches the given command to the appropriate command handler. The method of delivery
   * and the specifics of how the command is processed are determined by the implementation
   * of this interface.
   *
   * <p>This method is designed to be asynchronous in nature, allowing the calling thread to
   * continue execution without waiting for the command to be processed. However, synchronous or
   * blocking implementations can also be provided depending on the requirements of the system.
   *
   * @param command The command to be dispatched. It is an instance of {@code CommandT},
   *     encapsulating all the information necessary for the command handler to process the command
   *     appropriately.
   */
  ReturnT dispatch(CommandT command);
}
