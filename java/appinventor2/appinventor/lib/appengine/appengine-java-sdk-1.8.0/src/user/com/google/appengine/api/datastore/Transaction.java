// Copyright 2008 Google Inc. All Rights Reserved.
package com.google.appengine.api.datastore;

import java.util.ConcurrentModificationException;
import java.util.concurrent.Future;

/**
 * Describes a logical unit of work to be performed against the datastore.
 * Operations performed as part of a single {@link Transaction} succeed or fail
 * as a unit.  Transactions can be committed and rolled back synchronously
 * and asynchronously.
 *
 */
public interface Transaction {

  /**
   * Commits the transaction.  Whether this call succeeds or fails, all
   * subsequent method invocations on this object will throw
   * {@link IllegalStateException}.
   * @throws IllegalStateException If the transaction has already been
   * committed, rolled back, a commit or a rollback is in progress (via an
   * async call), or an attempt to commit or roll back has already failed.  If
   * there are any outstanding async datastore calls when this method is
   * invoked, this method will block on the completion of those calls before
   * proceeding.
   * @throws DatastoreFailureException If a datastore error occurs.
   * @throws ConcurrentModificationException If some other transaction modified
   *   the same entity groups concurrently.
   */
  void commit();

  /**
   * An asynchronous implementation of {@link #commit()}.
   *
   * @see #commit()
   *
   * @return A Future associated with the call.
   */
  Future<Void> commitAsync();

  /**
   * Rolls back the transaction.  Whether this call succeeds or fails, all
   * subsequent method invocations on this object will throw
   * {@link IllegalStateException}.
   * @throws IllegalStateException If the transaction has already been
   * committed, rolled back, a commit or a rollback is in progress (via an
   * async call), or an attempt to commit or roll back has already failed.  If
   * there are any outstanding async datastore calls when this method is
   * invoked, this method will block on the completion of those calls before
   * proceeding.
   * @throws DatastoreFailureException If a datastore error occurs.
   */
  void rollback();

  /**
   * An asynchronous implementation of {@link #rollback()}.
   *
   * @see #rollback()
   *
   * @return A Future associated with the call.
   */
  Future<Void> rollbackAsync();

  /**
   * @return The globally unique identifier for the {@code Transaction}.
   */
  String getId();

  /**
   * @return The application id for the {@code Transaction}.
   */
  String getApp();

  /**
   * @return {@code true} if the transaction is active, {@code false} otherwise.
   */
  boolean isActive();
}
