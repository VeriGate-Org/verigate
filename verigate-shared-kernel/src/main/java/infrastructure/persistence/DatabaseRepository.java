/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.persistence;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;

/**
 * This interface represents a repository for managing {@link TAggregate} instances. It provides an
 * abstraction over the underlying storage mechanism, allowing for different implementations such as
 * in-memory, relational database, NoSQL database, etc.
 */
public interface DatabaseRepository<AggregateT, IdT> {

  /**
   * Finds an aggregate by id.
   *
   * @param id the id to search for
   */
  AggregateT get(IdT id) throws TransientException, PermanentException;

  /**
   * Adds a new aggregate to the repository.
   *
   * @param quote the aggregate to be added
   */
  void add(AggregateT quote) throws TransientException, PermanentException;
}
