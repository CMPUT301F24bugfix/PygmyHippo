package com.example.pygmyhippo.database;

import com.google.firebase.firestore.FirebaseFirestore;

public abstract class DBHandler {
    protected FirebaseFirestore db;

    public DBHandler() {
        this.db = FirebaseFirestore.getInstance();
    }
}
