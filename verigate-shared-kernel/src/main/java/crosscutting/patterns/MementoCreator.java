/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.patterns;

import javax.validation.constraints.NotNull;

/**
 * The {@code MementoCreator} interface defines the contract for creating memento objects that
 * capture and encapsulate the current state of an object. Implementors of this interface can save
 * their internal state to a memento, allowing the state to be restored at a later point without
 * violating encapsulation principles. This interface is a key component in the Memento Design
 * Pattern, facilitating the creation of checkpoints or snapshots of an object's state.
 *
 * @param <MementoT> the type of memento object that this creator will produce. The specific type
 *     allows for flexibility in the kind of state information that is saved and restored. This type
 *     should adhere to a contract or structure suitable for encapsulating the state of the
 *     implementing object.
 */
public interface MementoCreator<MementoT> {

  /**
   * Saves the current state of the implementor into a memento object. The created memento object
   * should capture all necessary information needed to restore the implementor's state at a later
   * time. The specifics of what constitutes the "state" is determined by the implementor's
   * requirements and design.
   *
   * @return a {@code MementoT} instance that encapsulates the current state of the implementor.
   *     This memento can be used to restore the implementor's state at a future point.
   */
  @NotNull
  MementoT saveStateToMemento();
}
