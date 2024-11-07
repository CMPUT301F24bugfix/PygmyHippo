package com.example.pygmyhippo.organizer;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBHandler;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Post Event Database handler.
 *
 * Adds and search Event to Firestore when an organiser creates it from PostEventFragment.
 */
public class EventDB extends DBHandler {
    /**
     * This posts event to database
     * @param newEvent
     * @param listener
     * @author james
     */
    public void addEvent(@NonNull Event newEvent, DBOnCompleteListener<Event> listener) {
        DocumentReference docRef = db.collection("Events").document();
        newEvent.setEventID(docRef.getId());
        docRef.set(newEvent)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("DB", String.format("Successfully added Event %s", newEvent.getEventID()));
                    ArrayList<Event> newEventList = new ArrayList<>();
                    newEventList.add(newEvent);
                    listener.OnComplete(newEventList, 0, DBOnCompleteFlags.SUCCESS.value);
                } else {
                    Log.d("DB", String.format("Unsuccessfully in adding Event %s", newEvent.getEventID()));
                    listener.OnComplete(new ArrayList<>(), 0, DBOnCompleteFlags.ERROR.value);
                }
            });
    }


    /**
     * This method returns the matching event
     * @param eventID
     * @param listener
     * @author kori
     */
    public void getEvent(String eventID, DBOnCompleteListener<Event> listener) {
        db.collection("Events")
                .whereEqualTo("eventID", eventID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot queryResult = task.getResult();
                        ArrayList<Event> events = new ArrayList<>();
                        queryResult.forEach(doc -> events.add(doc.toObject(Event.class)));
                        if (events.size() == 0) {
                            Log.d("DB", String.format("Found no events with event ID (%s)", eventID));
                            listener.OnComplete(events, 1, DBOnCompleteFlags.NO_DOCUMENTS.value);
                        } else if (events.size() == 1) {
                            Log.d("DB", String.format("Found event (%s) with event ID (%s)", events.get(0).getEventID(), eventID));
                            listener.OnComplete(events, 1, DBOnCompleteFlags.SINGLE_DOCUMENT.value);
                        } else {
                            Log.d("DB", String.format("Found %d events  with event ID (%s)", events.size(), eventID));
                            listener.OnComplete(events, 1, DBOnCompleteFlags.MULTIPLE_DOCUMENTS.value);
                        }
                    } else {
                        Log.d("DB", String.format("Could not get event with event ID (%s).", eventID));
                        listener.OnComplete(new ArrayList<>(), 1, DBOnCompleteFlags.ERROR.value);
                    }
                });
    }
}
