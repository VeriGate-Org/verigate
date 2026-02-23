/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.commands;

import crosscutting.serialization.DataContract;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents macro command.
 */
@NoArgsConstructor
@Getter
public abstract class MacroCommand<CommandT> extends BaseCommand {

  @DataContract private List<CommandT> commands;

  /**
   * Creates a new instance of the MacroCommand.
   */
  public MacroCommand(UUID id, Instant createdDate, String createdBy, List<CommandT> commands) {
    super(id, createdDate, createdBy);
    this.commands = commands;
  }

  /**
   * Add a command to the list of commands.
   *
   * @param command the command to add.
   */
  public void addCommand(CommandT command) {
    if (this.commands == null) {
      this.commands = new ArrayList<CommandT>();
    }
    this.commands.add(command);
  }
}
