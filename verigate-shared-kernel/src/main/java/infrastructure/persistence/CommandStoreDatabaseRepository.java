/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.persistence;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;

/**
 * This interface represents a repository for command stores.
 */
public interface CommandStoreDatabaseRepository<EntityT, IdT> {

  /**
   * Finds an command store entity by id.
   *
   * @param id the id to search for
   */
  EntityT get(IdT id) throws TransientException, PermanentException;

  /**
   * Adds a new command store entity to the repository.
   *
   * @param quote the command store entity to be added
   */
  void add(EntityT quote) throws TransientException, PermanentException;
}
