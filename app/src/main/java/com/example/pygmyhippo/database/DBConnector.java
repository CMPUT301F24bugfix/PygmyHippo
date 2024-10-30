package com.example.pygmyhippo.database;

import com.google.firebase.firestore.FirebaseFirestore;


// DBCOnnector Class
// Initializes the connection to the database
public class DBConnector {
    private FirebaseFirestore db;

    /*
    Function returns the current instance of the FirebaseFirestore database
    returns:
        FirebaseFirestore: current instance of the FirebaseFirestore
    */
    public FirebaseFirestore getDB() {

        return FirebaseFirestore.getInstance();
    }
}
