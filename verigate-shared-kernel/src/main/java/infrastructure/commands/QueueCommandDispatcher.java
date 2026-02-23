/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.commands;

import domain.commands.CommandDispatcher;

/**
 * The {@code QueueCommandDispatcher} interface defines a generic contract for dispatching
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
 *
 */
public interface QueueCommandDispatcher<CommandT> extends CommandDispatcher<CommandT> {}
