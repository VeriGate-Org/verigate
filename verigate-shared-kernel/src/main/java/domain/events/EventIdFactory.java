/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.events;

import domain.commands.BaseCommand;
import java.util.UUID;

/**
 * The {@code EventIdFactory} interface defines a contract for generating ids for events. This
 * interface supports the creation of unique identifiers that can be used to uniquely identify
 * events in a system.
 */
public interface EventIdFactory {

  /**
   * Creates a new, unique UUID.
   *
   * <p>This method is intended for scenarios where an event does not necessarily relate to a
   * specific command or other contextual information. The generated UUID is expected to be globally
   * unique, relying on the underlying UUID generation mechanism's guarantees of uniqueness.
   *
   * @return A new, globally unique UUID.
   */
  UUID create();

  /**
   * Creates a UUID that is derived from a given command identifier and a position byte.
   *
   * <p>This method allows for the deterministic creation of a UUID based on specified input
   * parameters. The method is designed to generate the same UUID given the same inputs,
   * facilitating traceability and linkage between events and their initiating commands.
   *
   * <p>The use of a command identifier and a position byte allows for a fine-grained association
   * between events and a certain contexts.
   *
   * @param commandId The UUID of the command related to the event. This parameter serves as a basis
   *     for generating the event's UUID and provides a link back to the initiating command.
   * @param clazz The class of the originating command.
   * @return A UUID derived from the provided commandId and position, designed to be unique for each
   *     unique combination of inputs.
   */
  UUID createFromCommand(UUID commandId, Class<? extends BaseCommand> clazz);

  /**
   * Creates a UUID that is derived from a given command identifier and a position byte.
   *
   * <p>This method allows for the deterministic creation of a UUID based on specified input
   * parameters. The method is designed to generate the same UUID given the same inputs,
   * facilitating traceability and linkage between events and their initiating commands or positions
   * in a sequence.
   *
   * <p>The use of a command identifier and a position byte allows for a fine-grained association
   * between events and a certain contexts.
   *
   * @param commandId The UUID of the command related to the event. This parameter serves as a basis
   *     for generating the event's UUID and provides a link back to the initiating command.
   * @param position A byte value representing the position or sequence of the event relative to
   *     other events generated from the same command. This can be used to differentiate events
   *     originating from the same command.
   * @return A UUID derived from the provided commandId and position, designed to be unique for each
   *     unique combination of inputs.
   */
  UUID createFromCommand(UUID commandId, Class<? extends BaseCommand> clazz, byte position);

  /**
   * Creates a UUID that is derived from a given command identifier and a position byte.
   *
   * <p>This method allows for the deterministic creation of a UUID based on specified input
   * parameters. The method is designed to generate the same UUID given the same inputs,
   * facilitating traceability and linkage to the originating event
   *
   * <p>The use of a command identifier and a position byte allows for a fine-grained association
   * between events and a certain contexts.
   *
   * @param eventId The UUID of the event we are reacting to. This parameter serves as a basis for
   *     generating the event's UUID and provides a link back to the initiating command.
   * @param clazz The class of the originating event.
   * @return A UUID derived from the provided commandId and position, designed to be unique for each
   *     unique combination of inputs.
   */
  UUID createFromEvent(UUID eventId, Class<? extends BaseEvent<?>> clazz);

  /**
   * Creates a UUID that is derived from a given event identifier and a position byte.
   *
   * <p>This method allows for the deterministic creation of a UUID based on specified input
   * parameters. The method is designed to generate the same UUID given the same inputs,
   * facilitating traceability and linkage between events and their initiating events or positions
   * in a sequence.
   *
   * <p>The use of a event identifier and a position byte allows for a fine-grained association
   * between events and a certain contexts.
   *
   * @param eventId The UUID of the originating event. This parameter serves as a basis for
   *     generating the event's UUID and provides a link back to the initiating command.
   * @param clazz The class of the originating event
   * @param position A byte value representing the position or sequence of the event relative to
   *     other events generated from the same command. This can be used to differentiate events
   *     originating from the same command.
   * @return A UUID derived from the provided commandId and position, designed to be unique for each
   *     unique combination of inputs.
   */
  UUID createFromEvent(UUID eventId, Class<? extends BaseEvent<?>> clazz, byte position);

  /**
   * Creates a UUID that is derived from a given command identifier and a position byte.
   *
   * <p>This method allows for the deterministic creation of a UUID based on specified input
   * parameters. The method is designed to generate the same UUID given the same inputs,
   * facilitating traceability and linkage to the originating event
   *
   * <p>The use of a command identifier and a position byte allows for a fine-grained association
   * between events and a certain contexts.
   *
   * @param eventId The UUID of the event we are reacting to. This parameter serves as a basis for
   *     generating the event's UUID and provides a link back to the initiating command.
   * @param clazz The class of the originating event.
   * @return A UUID derived from the provided commandId and position, designed to be unique for each
   *     unique combination of inputs.
   */
  UUID createFromScheduledEvent(UUID eventId, Class<? extends BaseEvent<?>> clazz);
}
