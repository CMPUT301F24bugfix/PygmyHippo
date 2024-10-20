package com.example.pygmyhippo.database;

import com.google.firebase.firestore.FirebaseFirestore;

public class DBConnector {
    private FirebaseFirestore db;

    public FirebaseFirestore getDB() {
        return this.db;
    }
}
