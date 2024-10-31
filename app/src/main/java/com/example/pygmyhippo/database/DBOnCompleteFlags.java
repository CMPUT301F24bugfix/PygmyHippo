package com.example.pygmyhippo.database;

/**
 * Standard flag values that can be passed to OnComplete() of DBOnCompleteListener.
 *
 * The convention is for negative flags to be error related, 0 is generic success, and positive
 * flags should be non-error related.
 * These are no all the flags that can be passed to OnComplete() which depends on how the querying
 * method is implemented.
 */
public enum DBOnCompleteFlags {
    ERROR(-1),
    SUCCESS(0),
    NO_DOCUMENTS(1),
    SINGLE_DOCUMENT(2),
    MULTIPLE_DOCUMENTS(3);



    public final int value;

    DBOnCompleteFlags(int value) {
        this.value = value;
    }
}
