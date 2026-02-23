/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.commands;

import domain.commands.CommandDispatcher;

/**
 * The {@code HttpCommandDispatcher} interface defines a generic contract for dispatching HTTP
 * commands within the system. Implementors of this interface are responsible for the mechanism by
 * which HTTP commands are dispatched.
 *
 *
 */
public interface HttpCommandDispatcher<CommandT> extends CommandDispatcher<CommandT> {}
