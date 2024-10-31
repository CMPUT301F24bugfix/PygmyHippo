package com.example.pygmyhippo.organizer;

import androidx.annotation.NonNull;

import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBHandler;

/**
 * Post Event Database handler.
 *
 * Adds new Event to Firestore when an organiser creates it from PostEventFragment.
 */
public class PostEventDB extends DBHandler {
    public void addEvent(@NonNull Event newEvent, String organiserID) {
//        Map<String, Object> eventMap = newEvent.toMap();
//        eventMap.put("eventID", null);
//        eventMap.put("entrants", null);
//        eventMap.put("organiserID", organiserID);
//        db.collection("Events").document().set(eventMap);
    }
}
