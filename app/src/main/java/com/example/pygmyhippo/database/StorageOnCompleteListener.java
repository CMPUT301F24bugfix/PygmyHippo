package com.example.pygmyhippo.database;

import androidx.annotation.NonNull;

/*
This interface is used as a call back listener for image storage.
Issues:
    - None at the moment
 */

import java.util.ArrayList;

/**
 * OnCompleteListener interface for database queries.
 *
 * @param <T> Type documents should be formatted as.
 */

public interface StorageOnCompleteListener<T> {

    /**
     * Method called when a query (GET/SET/UPDATE/etc.) completes.
     * @param docs - Documents retrieved from DB (if it was a get query).
     * @param queryID - ID of query completed.
     * @param flags - Flags to indicate query status/set how to process query result.
     */
    void OnCompleteStorage(@NonNull ArrayList<T> docs, int queryID, int flags);
}
