package com.example.pygmyhippo.organizer;

/*
This class is the specialized handler for the event fragment
Purposes:
    - To provide server connectivity to the eventFragment
Issues:
    - None
 */

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBHandler;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
     * @param newEvent The event getting posted to the database
     * @param listener What gets notified when the update is successful or unsuccessful
     * @author James
     */
    public void addEvent(@NonNull Event newEvent, DBOnCompleteListener<Event> listener) {
        DocumentReference docRef = db.collection("Events").document();
        newEvent.setEventID(docRef.getId());
        docRef.set(newEvent)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("DB", String.format("Successfully added Event %s", newEvent.getEventID()));

                    // Add the translated documents to a list to pass to the listener
                    ArrayList<Event> newEventList = new ArrayList<>();
                    newEventList.add(newEvent);
                    listener.OnCompleteDB(newEventList, 0, DBOnCompleteFlags.SUCCESS.value);
                } else {
                    // Notify the listeners of errors
                    Log.d("DB", String.format("Unsuccessfully in adding Event %s", newEvent.getEventID()));
                    listener.OnCompleteDB(new ArrayList<>(), 0, DBOnCompleteFlags.ERROR.value);
                }
            });
    }


    /**
     * This method returns the matching event
     * @param eventID The ID used to retrieve the matching event from the database
     * @param listener What gets notified of results
     * @author Kori
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
                            listener.OnCompleteDB(events, 1, DBOnCompleteFlags.NO_DOCUMENTS.value);
                        } else if (events.size() == 1) {
                            Log.d("DB", String.format("Found event (%s) with event ID (%s)", events.get(0).getEventID(), eventID));
                            listener.OnCompleteDB(events, 1, DBOnCompleteFlags.SINGLE_DOCUMENT.value);
                        } else {
                            Log.d("DB", String.format("Found %d events  with event ID (%s)", events.size(), eventID));
                            listener.OnCompleteDB(events, 1, DBOnCompleteFlags.MULTIPLE_DOCUMENTS.value);
                        }
                    } else {
                        Log.d("DB", String.format("Could not get event with event ID (%s).", eventID));
                        listener.OnCompleteDB(new ArrayList<>(), 1, DBOnCompleteFlags.ERROR.value);
                    }
                });
    }

    public void updateEvent(Event event, DBOnCompleteListener<Event> listener) {
        db.collection("Events")
                .document(event.getEventID())
                .set(event)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ArrayList<Event> events = new ArrayList<>();
                        if (task.isSuccessful()) {
                            events.add(event);
                            Log.d("DB", String.format("Successfully updated event with ID (%s).", event.getEventID()));
                            listener.OnCompleteDB(events, 2, DBOnCompleteFlags.SUCCESS.value);
                        } else {
                            Log.d("DB", String.format("Error: Could not update event with ID (%s).", event.getEventID()));
                            listener.OnCompleteDB(events, 2, DBOnCompleteFlags.ERROR.value);
                        }
                    }
                });
    }
}
