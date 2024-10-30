package com.example.pygmyhippo.database;

import com.google.firebase.firestore.FirebaseFirestore;


// DBConnector Class
// Initializes the connection to the database
public class DBConnector {
    private FirebaseFirestore db;


    /*
   Function initializes the current instance of the DB
   */
    public void DBConnect(){
        this.db = FirebaseFirestore.getInstance();
    }

    /*
    Function returns the current instance of the FirebaseFirestore database
    returns:
        FirebaseFirestore: current instance of the FirebaseFirestore
    */
    public FirebaseFirestore getDB() {
        return this.db;
    }
}
