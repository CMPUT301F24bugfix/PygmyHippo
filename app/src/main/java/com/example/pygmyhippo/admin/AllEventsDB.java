package com.example.pygmyhippo.admin;

/*
This is the specified handler for the AllEventsFragment
Purposes:
    - To fetch events from the database
    - These events populate the list in which the Admin can browse
Issues:
    - None
 */

import android.util.Log;

import com.example.pygmyhippo.database.DBHandler;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBOnCompleteListener;



/**
 * DBHandler for AllEventsFragment
 * @author James
 * Handles getting events from Firestore.
 */
public class AllEventsDB extends DBHandler {

    /**
     * Returns a limited amount of events from the whole event collection from the database
     * @param limit
     *          The max amount of events we want
     * @param listener
     *          The DB listener for data callback
     */
    public void getEvents(int limit, DBOnCompleteListener<Event> listener) {
        db.collection("Events")
            .limit(limit)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Get all the docs
                    QuerySnapshot docs = task.getResult();
                    Log.d("DB", String.format("Found %d Events", docs.size()));

                    // Convert each doc to an event and store it
                    ArrayList<Event> eventList = new ArrayList<>();
                    docs.forEach(doc -> {
                        eventList.add(doc.toObject(Event.class));
                    });

                    // Call the listener
                    listener.OnComplete(eventList, 0, DBOnCompleteFlags.SUCCESS.value);
                } else {
                    Log.d("DB", "Could not get Events");
                    listener.OnComplete(new ArrayList<>(), 0, DBOnCompleteFlags.ERROR.value);
                }
            });
    }
}
