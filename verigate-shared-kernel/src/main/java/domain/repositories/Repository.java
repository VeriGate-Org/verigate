/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.repositories;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;

/**
 * This interface is the base interface for all database repositories in the application. 
 * It contains common functionality for interacting with databases, 
 * such as initializing the database client and
 * table, and adding, updating, and deleting items in the table.
 */
public interface Repository<T, I> {
  
  /**
   * Adds a new item to the repository.
   *
   * @param model the item to be added
   * @throws TransientException must be retried
   * @throws PermanentException must not be retried
   */
  void addOrUpdate(T model) throws TransientException, PermanentException;

  /**
   * Gets an existing item from the repository.
   *
   * @param id the item to be retrieved
   * @throws TransientException must be retried
   * @throws PermanentException must not be retried
   */
  T get(I id) throws TransientException, PermanentException;
}
