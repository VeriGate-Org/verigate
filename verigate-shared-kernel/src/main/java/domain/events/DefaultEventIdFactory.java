/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.events;

import crosscutting.util.ConsistentlyTransformedUUID;
import domain.commands.BaseCommand;
import domain.exceptions.PermanentException;
import domain.exceptions.StringExceptionBuilder;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/**
 * Implements the {@code EventIdFactory} interface, providing concrete methods to generate UUIDs for
 * events. This implementation offers two methods for UUID creation: one that generates a completely
 * random UUID, and another that produces a UUID based on a provided command ID and a position byte
 * through a cryptographic transformation. This allows for both non-deterministic and deterministic
 * generation of event IDs depending on the needs of the application.
 */
public final class DefaultEventIdFactory implements EventIdFactory {

  /**
   * Generates a universally unique identifier (UUID) using Java's built-in UUID generation. This
   * method is suitable for scenarios where the event ID needs to be entirely random and unique,
   * without any relation to other identifiers or inputs.
   *
   * @return A randomly generated UUID.
   */
  @Override
  public UUID create() {
    return UUID.randomUUID();
  }

  /**
   * Generates a UUID for an event based on a given command ID and a byte position. This method
   * applies a cryptographic hash function to the combination of the command ID and the position,
   * ensuring a deterministic yet unique output. The resulting UUID is thus directly related to its
   * inputs, making it suitable for scenarios where event IDs need to be traceable or reproducible
   * given the same inputs.
   *
   * @param commandId The UUID of the command associated with the event. This UUID acts as a base or
   *     context for the generated event ID, linking the event to its triggering command.
   * @param clazz The class of the command
   * @return A UUID generated through a cryptographic transformation of the input command ID and
   *     position byte.
   */
  @Override
  public UUID createFromCommand(UUID commandId, Class<? extends BaseCommand> clazz) {
    return createFromOrigination(commandId, clazz, (byte) 0);
  }

  /**
   * Generates a UUID for an event based on a given command ID and a byte position. This method
   * applies a cryptographic hash function to the combination of the command ID and the position,
   * ensuring a deterministic yet unique output. The resulting UUID is thus directly related to its
   * inputs, making it suitable for scenarios where event IDs need to be traceable or reproducible
   * given the same inputs.
   *
   * @param commandId The UUID of the command associated with the event. This UUID acts as a base or
   *     context for the generated event ID, linking the event to its triggering command.
   * @param clazz The class of the command
   * @param position A byte value that adds additional specificity to the generation process,
   *     allowing for multiple events to be uniquely identified even if they relate to the same
   *     command.
   * @return A UUID generated through a cryptographic transformation of the input command ID and
   *     position byte.
   */
  @Override
  public UUID createFromCommand(UUID commandId, Class<? extends BaseCommand> clazz, byte position) {
    return createFromOrigination(commandId, clazz, position);
  }

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
  @Override
  public UUID createFromEvent(UUID eventId, Class<? extends BaseEvent<?>> clazz) {
    return createFromOrigination(eventId, clazz, (byte) 0);
  }

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
  @Override
  public UUID createFromEvent(UUID eventId, Class<? extends BaseEvent<?>> clazz, byte position) {
    return createFromOrigination(eventId, clazz, position);
  }

  @Override
  public UUID createFromScheduledEvent(UUID eventId, Class<? extends BaseEvent<?>> clazz) {
    return createFromOrigination(eventId, clazz, (byte) 0);
  }

  private @NotNull UUID createFromOrigination(UUID originationId, Class<?> clazz, byte position) {
    // Validate that commandId is not null
    if (originationId == null) {
      throw new PermanentException(
          StringExceptionBuilder.builder().withDetail("commandId must not be null").build());
    }

    // Validate that position is greater than 0
    if (position < 0) {
      throw new PermanentException(
          StringExceptionBuilder.builder()
              .withDetail("position must be equals or greater than 0")
              .build());
    }

    return ConsistentlyTransformedUUID.generateUUIDFromHash(originationId, clazz, position);
  }
}
