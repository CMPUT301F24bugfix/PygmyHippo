package com.example.pygmyhippo.database;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public abstract class DBHandler {
    protected FirebaseFirestore db;
    protected FirebaseStorage storage;

    public DBHandler() {
        this.db = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }
}
