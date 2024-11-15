package com.example.pygmyhippo.database;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * OnCompleteListener interface for database queries.
 *
 * No issues
 *
 * Database clients (e.g. Fragments) who interact with database can implement this interface and
 * pass themselves as a listener to their handlers to execute logic when the query completes. An
 * example of its usage follows.
 * e.g.
 * class SomeFragment implements DBOnCompleteListener {
 *     private SomeFragmentDB dbHandler;
 *     ...
 *
 *     private void someMethod() {
 *         ...
 *         dbHandler.getDocuments(this);
 *     }
 *
 *     public void OnComplete(@Nullable ArrayList<T> docs, int queryID, int flags) {
 *          switch (queryID) {
 *              case SOME_ID:
 *                  do something
 *                  ...
 *                  break;
 *              ...
 *          }
 *     }
 * }
 *
 * class SomeFragmentDB extends DBHandler {
 *     public void getDocuments(DBOnCompleteListener<SomeDataclass> listener) {
 *         db.collection("some_collection")
 *          .get()
 *          .addOnCompleteListener(task -> {
 *              ...
 *              listener.onComplete(docs, queryID, flags);
 *          })
 *     }
 * }
 *
 * @param <T> Type documents should be formatted as.
 */
public interface DBOnCompleteListener<T> {

    /**
     * Method called when a query (GET/SET/UPDATE/etc.) completes.
     * @param docs - Documents retrieved from DB (if it was a get query).
     * @param queryID - ID of query completed.
     * @param flags - Flags to indicate query status/set how to process query result.
     */
    void OnCompleteDB(@NonNull ArrayList<T> docs, int queryID, int flags);
}
