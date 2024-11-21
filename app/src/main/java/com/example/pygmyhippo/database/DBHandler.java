package com.example.pygmyhippo.database;

/*
The parent data base handler
Purposes:
    - Will be extended by more specified child db handler's to provide server connectivity to the app
Issues:
    - None
 */

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

/**
 * Parent DBHandler
 */
public abstract class DBHandler {
    protected FirebaseFirestore db;
    protected FirebaseStorage storage;
    protected boolean useFirebase;

    public DBHandler(boolean useFirebase) {
        this.useFirebase = useFirebase;
        this.db = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }
}
